package schedule

import excel.DataReader
import java.time.LocalDate
import kotlin.random.Random

class Scheduler {
    private var schedules: LinkedHashMap<LocalDate, TodaySchedule>
    private var dataReader: DataReader = DataReader()
    private val names = dataReader.getNames()
    private var employees = HashMap<String, Employee>()
    private var startDate = dataReader.getStartDate()
    private var week = dataReader.getWeek()

    init {
        for (name in names)
            employees[name] = Employee(name)
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
        for (index in 0 until week)
            while (!setWeekSchedules(index));
    }

    private fun setWeekSchedules(week: Int): Boolean {
        var close = ""
        val random = Random
        resetEmployeesWorkCount(week)
        println("start")
        printEmployeesStatus()

        for (i in 0 until 7) {
            if (!isValid(i))
                return false

            val schedule = schedules[startDate.plusDays((week * 7 + i).toLong())]!!
            if (schedule.mid.isNotEmpty())
                employees[schedule.mid]!!.count++
            if (schedule.rest.isNotEmpty())
                employees[schedule.rest]!!.used = true

            setTodayOpen(schedule, random, close)
            if (close.isNotEmpty())
                employees[schedules[startDate.plusDays((week * 7 + i - 1).toLong())]!!.close]!!.used = false
            printEmployeesStatus()

            close = setTodayClose(schedule, random)
            printEmployeesStatus()
            activateEmployees(schedule)
        }
        return true
    }

    private fun resetEmployeesWorkCount(week: Int) {
        for (name in employees.keys) {
            employees[name]!!.count = 0
            employees[name]!!.used = false
        }

        if (week != 0) {
            val schedule = schedules[startDate.plusDays((week * 7 - 1).toLong())]
            val name = employees[schedule!!.close]!!.name
            employees[name]!!.used = true
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

    private fun setTodayOpen(today: TodaySchedule, random: Random, close: String) {
        while (true) {
            var r = random.nextInt(employees.size)
//            if (close.isNotEmpty()) {
//                if (employees[names[r]] != employees[close]!! && !employees[names[r]]!!.used && employees[names[r]]!!.count < 5 && today.rest != names[r]) {
//                    today.open = employees[names[r]]!!.name
//                    employees[names[r]]!!.used = true
//                    employees[names[r]]!!.count++
//                    break
//                }
//            } else {
            if (!employees[names[r]]!!.used && employees[names[r]]!!.count < 5) {
                today.open = employees[names[r]]!!.name
                println("open: ${today.open}")
                employees[names[r]]!!.used = true
                employees[names[r]]!!.count++
                break
            }
//            }
        }
    }

    private fun setTodayClose(today: TodaySchedule, random: Random): String {
        var r: Int
        while (true) {
            r = random.nextInt(employees.size)
            if (!employees[names[r]]!!.used && employees[names[r]]!!.count < 5 && today.rest != names[r]) {
                today.close = employees[names[r]]!!.name
                println("close: ${today.close}")
                employees[names[r]]!!.used = true
                employees[names[r]]!!.count++
                break
            }
        }
        return names[r]
    }

    private fun activateEmployees(today: TodaySchedule) {
        employees[today.open]!!.used = false
        if (today.rest.isNotEmpty())
            employees[today.rest]!!.used = false
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
            print("$name used: ${employees[name]!!.used}     ")
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

            for (name in employees.keys) {
                print("${employees[name]!!.name}: ${employees[name]!!.count}    ")
            }
            println()
            println()
        }
        println()
        println()
    }
}