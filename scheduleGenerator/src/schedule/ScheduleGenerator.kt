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

    private var schedules: LinkedHashMap<LocalDate, TodaySchedule>
    private var tempEmployees = HashMap<String, Employee>()
    private var close = ""

    init {
        for (name in names) {
            employees[name] = Employee(name)
            tempEmployees[name] = Employee(name)
        }
        schedules = linkedMapOf()
        for (i in 0 until week * 7)
            schedules[startDate.plusDays(i.toLong())] = TodaySchedule()
    }

    fun loadData() {
//        this.setOpenDates()
        this.setMidDates()
//        this.setCloseDates()
        this.setRestDates()
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
        resetEmployeesWorkCount()

        for (i in 0 until 7) {
            if (!isValid(i)) {
                rollback(week)
                return false
            }

            val date = startDate.plusDays((week * 7 + i).toLong())
            val schedule = schedules[date]!!

            if (schedule.rest.isNotEmpty())
                employees[schedule.rest]!!.used = true

            setTodayOpen(schedule)
            printScheduler()

            if (close.isNotEmpty())
                employees[close]!!.used = false

            if (schedule.mid.isNotEmpty())
                employees[schedule.mid]!!.count++
            printScheduler()

            setTodayClose(schedule)
            printScheduler()

            employees[schedule.open]!!.used = false

            if (schedule.rest.isNotEmpty())
                employees[schedule.rest]!!.used = false

            println("Status")
            printScheduler()
        }
        return true
    }

    private fun resetEmployeesWorkCount() {
        for (name in employees.keys)
            employees[name]!!.count = 0
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
        resetEmployeesWorkCount()
        for (name in employees.keys)
            employees[name]!!.used = false
        close = ""

        for (i in 0 until 7) {
            val date = startDate.plusDays((week * 7 + i).toLong())
            val schedule = schedules[date]!!
            schedule.open = ""
            schedule.mid = ""
            schedule.close = ""
            schedule.rest = ""
        }
        loadData()

        if (week != 0) {
            val lastSaturday = startDate.plusDays((week * 7 - 1).toLong())
            close = schedules[lastSaturday]!!.close

            val lastSaturdaySchedule = schedules[lastSaturday]!!
            employees[lastSaturdaySchedule.close]!!.used = true
        }

//        println("rollback week: $week")
//        printScheduler()
//        Thread.sleep(3000)
    }

    private fun setTodayOpen(schedule: TodaySchedule) {
        println("setTodayOpen Start")
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
            if (!employee.used && employee.count < 5 && name != close) {
                employee.used = true
                employee.count++
                schedule.open = name
                break
            }
        }
        println("setTodayOpen End")
    }

    private fun setTodayClose(schedule: TodaySchedule) {
        println("setTodayClose Start")
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
                close = name
                break
            }
        }
        println("setTodayClose End")
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

        for (i in 0 until week) {
            for (j in 0 until 7) {
                val today = startDate.plusDays((i * 7 + j).toLong())
                print("date: $today     ")
                val todayEmployee = schedules[today]!!
                print("open: ${todayEmployee.open}   mid: ${todayEmployee.mid}    close: ${todayEmployee.close}    rest: ${todayEmployee.rest}")
                println()
            }

            for (name in employees.keys) {
                print("${employees[name]!!.name}: ${employees[name]!!.count}    ")
            }
            println()
            println()
        }
        println()

//        dataIO.printScheduleFile()
    }
}