package schedule

import excel.DataReader
import java.time.LocalDate
import kotlin.random.Random

class Scheduler {
    private var dataReader: DataReader = DataReader()
    private val names = dataReader.getNames()
    private var employees = HashMap<String, Employee>()
    private var startDate = dataReader.getStartDate()
    private var week = dataReader.getWeek()

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

    fun setSchedules() {
        this.setMidDates()
        this.setRestDates()
        this.setMonthSchedules()
    }

    private fun setMidDates() {
        val midSchedules = dataReader.getSchedules(3)
        for (midSchedule in midSchedules) {
            val schedule = schedules[midSchedule.date]!!
            schedule.mid = midSchedule.name
        }
    }

    private fun setRestDates() {
        val restSchedules = dataReader.getSchedules(4)
        for (restSchedule in restSchedules) {
            val schedule = schedules[restSchedule.date]!!
            schedule.rest = restSchedule.name
        }
    }

    private fun setMonthSchedules() {
        for (i in 0 until week)
            while (!setWeekSchedules(i));
    }

//    private fun setSchedule(date: Int) {
//        val random = Random
//        val today = startDate.plusDays(date.toLong())
//        println("today: $today")
//        printEmployeesStatus()
//        val schedule = schedules[today]!!
//
//        if (schedule.mid.isNotEmpty())
//            employees[schedule.mid]!!.used = true
//        if (schedule.rest.isNotEmpty())
//            employees[schedule.rest]!!.used = true
//
//        setTodayOpen(schedule, random)
//
//        setTodayClose(schedule, random)
//        println()
//    }

    private fun setWeekSchedules(week: Int): Boolean {
        resetEmployeesWorkCount()

//        printEmployeesStatus()
//        println()

        for (i in 0 until 7) {
            if (!isValid(i)) {
                rollback()
                return false
            }

            val date = startDate.plusDays((week * 7 + i).toLong())

            val schedule = schedules[date]!!
            if (schedule.mid.isNotEmpty())
                employees[schedule.mid]!!.count++
            if (schedule.rest.isNotEmpty())
                employees[schedule.rest]!!.used = true

            setTodayOpen(schedule)

            if (close.isNotEmpty())
                employees[schedules[date.plusDays((1).toLong())]!!.close]!!.used = false

            setTodayClose(schedule)

            employees[schedule.open]!!.used = false
            if (schedule.rest.isNotEmpty())
                employees[schedule.rest]!!.used = false

            print("i: $i    ")
            printEmployeesStatus()
        }
        return true
    }


    private fun resetEmployeesWorkCount() {
        for (name in employees.keys) {
            employees[name]!!.count = 0
//            employees[name]!!.used = false
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

    private fun rollback() {
        resetEmployeesWorkCount()
        for (name in employees.keys)
            employees[name]!!.used = false

        if (week != 0) {
            val lastSaturday = startDate.plusDays((week * 7 - 1).toLong())
            val lastSaturdaySchedule = schedules[lastSaturday]!!
            employees[lastSaturdaySchedule.close]!!.used = true
        }
    }

    private fun setTodayOpen(schedule: TodaySchedule) {
        while (true) {
            val r = Random.nextInt(employees.size)
            val name = names[r]

            if (!employees[name]!!.used && name != close && employees[name]!!.count < 5) {
                employees[name]!!.used = true
                employees[name]!!.count++
                schedule.open = name
                println("open: $name")
                break
            }
        }
        if (close.isNotEmpty())
            employees[close]!!.used = false
    }

    private fun setTodayClose(schedule: TodaySchedule) {
        while (true) {
            val r = Random.nextInt(employees.size)
            val name = names[r]
            if (!employees[name]!!.used && employees[name]!!.count < 5) {
                employees[name]!!.used = true
                employees[name]!!.count++
                schedule.close = name
                close = name
                println("close: $close")
                break
            }
        }
        employees[schedule.open]!!.used = false
        if (schedule.rest.isNotEmpty())
            employees[schedule.rest]!!.used = false
    }

    private fun isCountEquals(count: Int): Boolean {
        for (name in employees.keys) {
            if (employees[name]!!.count == count)
                return true
        }
        return false
    }

    private fun printEmployeesStatus() {
        for (name in employees.keys)
            println("$name      used: ${employees[name]!!.used}     count: ${employees[name]!!.count}")
        println()
        println()
    }

    fun printScheduler() {
        for (i in 0 until week) {
            for (j in 0 until 7) {
                val today = startDate.plusDays((i * 7 + j).toLong())
                print("date: $today     ")
                val todayEmployee = schedules[today]!!
                print("open: ${todayEmployee.open}   mid: ${todayEmployee.mid}    close: ${todayEmployee.close}    rest: ${todayEmployee.rest}")
                println()
            }

//            for (name in employees.keys) {
//                print("${employees[name]!!.name}: ${employees[name]!!.count}    ")
//            }
            println()
            println()
        }
        println()
    }
}