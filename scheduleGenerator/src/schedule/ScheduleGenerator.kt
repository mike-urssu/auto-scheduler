package schedule

import excel.DataIO
import java.time.LocalDate
import kotlin.random.Random

class ScheduleGenerator {
    private var dataIO = DataIO()
    private val names = dataIO.getNames()
    private var employees = HashMap<String, Employee>()
    private var startDate = dataIO.getStartDate()
    private var week = dataIO.getWeek()

    private var schedules: LinkedHashMap<LocalDate, Schedule>
    private var yesterdayClose = ""

    init {
        for (name in names)
            employees[name] = Employee(name)
        schedules = linkedMapOf()
        for (i in 0 until week * 7)
            schedules[startDate.plusDays(i.toLong())] = Schedule()
    }

    fun loadData() {
        setDefaultDates(4, Time.MID)
        setDefaultDates(6, Time.REST)
    }

    private fun setDefaultDates(rowIndex: Int, time: Time) {
        val defaultSchedules = dataIO.getSchedules(rowIndex)
        for (defaultSchedule in defaultSchedules) {
            val todaySchedule = schedules[defaultSchedule.date]!!
            when (time) {
                Time.OPEN -> todaySchedule.open = defaultSchedule.name
                Time.MID -> todaySchedule.mid = defaultSchedule.name
                Time.CLOSE -> todaySchedule.close = defaultSchedule.name
                else -> todaySchedule.rest = defaultSchedule.name
            }
        }
    }

    fun setSchedules() {
        for (i in 0 until week)
            while (!setWeekSchedules(i));
    }

    private fun setWeekSchedules(week: Int): Boolean {
        resetEmployeesWorkCount(week)

        for (i in 0 until 7) {
            if (!isValid(i)) {
                rollback(week)
                return false
            }

            val today = startDate.plusDays((week * 7 + i).toLong())
            val todaySchedule = schedules[today]!!
            println("date: $today")

            /**
             * 휴무 true
             */
            if (todaySchedule.rest.isNotEmpty())
                employees[todaySchedule.rest]!!.used = true

            /**
             * 오픈 설정
             */
            setTodayOpen(todaySchedule, week)
            println("open: ${todaySchedule.open}")
            printStatus()

            /**
             * 오픈 설정 후 close used = false
             */
            if (yesterdayClose.isNotEmpty())
                employees[yesterdayClose]!!.used = false

            /**
             * 미드 설정
             */
            if (todaySchedule.mid.isNotEmpty())
                employees[todaySchedule.mid]!!.count++
            println("mid: ${todaySchedule.mid}")
            printStatus()

            /**
             * 마감 설정
             */
            setTodayClose(todaySchedule, week)
            println("close: ${todaySchedule.close}")
            printStatus()

            /**
             * 마감 설정 후 open used = false
             */
            employees[todaySchedule.open]!!.used = false

            /**
             * 휴무 false
             */
            if (todaySchedule.rest.isNotEmpty())
                employees[todaySchedule.rest]!!.used = false
            println("rest: ${todaySchedule.rest}")
            printStatus()
        }
        return true
    }

    private fun resetEmployeesWorkCount(week: Int) {
        for (name in employees.keys) {
            employees[name]!!.count = 0
            employees[name]!!.used = false
        }
        yesterdayClose = ""

        if (week != 0) {
            val lastSaturday = startDate.plusDays((week * 7 - 1).toLong())
            val lastSaturdaySchedule = schedules[lastSaturday]!!
            yesterdayClose = lastSaturdaySchedule.close
            employees[lastSaturdaySchedule.close]!!.used = true
        }
    }

    private fun isValid(i: Int): Boolean {
        return when (i) {
            4 -> !isCountEquals(0) && !isCountEquals(1)     // (0, 4, 5), {(1, 4, 4), (1, 3, 5)}
            5 -> !isCountEquals(2) // && !isCountEquals(3)     // (2, 4, 5), (3, 3, 5)
            6 -> !isCountEquals(3)
            else -> true
        }
    }

    private fun rollback(week: Int) {
        println("rollback week: $week")
        resetEmployeesWorkCount(week)

        for (i in 0 until 7) {
            val date = startDate.plusDays((week * 7 + i).toLong())
            val schedule = schedules[date]!!
            schedule.open = ""
            schedule.mid = ""
            schedule.close = ""
            schedule.rest = ""
        }
        loadData()
    }

    private fun setTodayOpen(schedule: Schedule, week: Int) {
        val startAt = System.currentTimeMillis()
        while (true) {
            val endAt = System.currentTimeMillis()
            if (endAt - startAt > 1000)
                rollback(week)
            val r = Random.nextInt(employees.size)
            val name = names[r]
            val employee = employees[name]!!
            if (!employee.used && employee.count < 5 && name != yesterdayClose) {
                employee.used = true
                employee.count++
                schedule.open = name
                break
            }
        }
    }

    private fun setTodayClose(schedule: Schedule, week: Int) {
        val startAt = System.currentTimeMillis()
        while (true) {
            val endAt = System.currentTimeMillis()
            if (endAt - startAt > 1000)
                rollback(week)
            val r = Random.nextInt(employees.size)
            val name = names[r]
            val employee = employees[name]!!
            if (!employee.used && employee.count < 5) {
                employee.used = true
                employee.count++
                schedule.close = name
                yesterdayClose = name
                break
            }
        }
    }

    private fun isCountEquals(count: Int): Boolean {
        for (name in employees.keys) {
            if (employees[name]!!.count == count)
                return true
        }
        return false
    }

    fun printScheduler() {
        dataIO.readyToPrint()
        for (i in 0 until week) {
            dataIO.printDate(startDate, i)
            dataIO.printOpenSchedules(schedules, i, startDate)
            dataIO.printMidSchedules(schedules, i, startDate)
            dataIO.printCloseSchedules(schedules, i, startDate)
            dataIO.printRestSchedules(schedules, i, startDate)
        }
        dataIO.printScheduleFile()
    }

    private fun printStatus() {
        for (name in employees.keys) {
            println("$name ${employees[name]!!.used} ${employees[name]!!.count}")
        }
        println()
    }
}