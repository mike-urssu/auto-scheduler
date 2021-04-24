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
//        this.setOpenDates()
//        this.setMidDates()
//        this.setCloseDates()
//        this.setRestDates()
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

    private fun setOpenDates() {
        val openSchedules = dataIO.getSchedules(3)
        for (openSchedule in openSchedules) {
            val schedule = schedules[openSchedule.date]!!
            schedule.open = openSchedule.name
        }
    }

    private fun setMidDates() {
        val midSchedules = dataIO.getSchedules(4)
        for (midSchedule in midSchedules) {
            val schedule = schedules[midSchedule.date]!!
            schedule.mid = midSchedule.name
        }
    }

    private fun setCloseDates() {
        val closeSchedules = dataIO.getSchedules(5)
        for (closeSchedule in closeSchedules) {
            val schedule = schedules[closeSchedule.date]!!
            schedule.close = closeSchedule.name
        }
    }

    private fun setRestDates() {
        val restSchedules = dataIO.getSchedules(6)
        for (restSchedule in restSchedules) {
            val schedule = schedules[restSchedule.date]!!
            schedule.rest = restSchedule.name
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

            /**
             * 휴무 true
             */
            if (todaySchedule.rest.isNotEmpty())
                employees[todaySchedule.rest]!!.used = true

            /**
             * 오픈 설정
             */
            setTodayOpen(todaySchedule)

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

            /**
             * 마감 설정
             */
            setTodayClose(todaySchedule)

            /**
             * 마감 설정 후 open used = false
             */
            employees[todaySchedule.open]!!.used = false

            /**
             * 휴무 false
             */
            if (todaySchedule.rest.isNotEmpty())
                employees[todaySchedule.rest]!!.used = false
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
            4 -> !isCountEquals(0)
            5 -> !isCountEquals(1) && !isCountEquals(2)
            6 -> !isCountEquals(3)
            else -> true
        }
    }

    private fun rollback(week: Int) {
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

    private fun setTodayOpen(schedule: Schedule) {
        if (schedule.open.isNotEmpty()) {
            val employee = employees[schedule.open]!!
            employee.used = true
            employee.count++
            return
        }

        while (true) {
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

    private fun setTodayClose(schedule: Schedule) {
        if (schedule.close.isNotEmpty()) {
            val employee = employees[schedule.close]!!
            employee.used = true
            employee.count++
            return
        }

        while (true) {
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

//        for (i in 0 until week) {
//            for (j in 0 until 7) {
//                val today = startDate.plusDays((i * 7 + j).toLong())
//                print("date: $today     ")
//                val todayEmployee = schedules[today]!!
//                print("open: ${todayEmployee.open}   mid: ${todayEmployee.mid}    close: ${todayEmployee.close}    rest: ${todayEmployee.rest}")
//                println()
//            }
//
//            for (name in employees.keys) {
//                print("${employees[name]!!.name}: ${employees[name]!!.count}    ")
//            }
//            println()
//            println()
//        }
//        println()

//        dataIO.printScheduleFile()
    }
}