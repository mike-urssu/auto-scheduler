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
    private var close = ""

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
        for (i in 0 until week * 7)
            setSchedule(i)

//        while (!setWeekSchedules(date));
    }

    private fun setSchedule(date: Int) {

    }

    private fun setWeekSchedules(week: Int): Boolean {
        val random = Random
        resetEmployeesWorkCount(week)

        printEmployeesStatus()
        println()

        for (i in 0 until 7) {
            if (!isValid(i))
                return false
            val date = startDate.plusDays((week * 7 + i).toLong())

            val schedule = schedules[date]!!
            if (schedule.mid.isNotEmpty())
                employees[schedule.mid]!!.count++
            if (schedule.rest.isNotEmpty())
                employees[schedule.rest]!!.used = true

            setTodayOpen(schedule, random)

            if (close.isNotEmpty())
                employees[schedules[date.plusDays((1).toLong())]!!.close]!!.used = false

            setTodayClose(schedule, random)

            employees[schedule.open]!!.used = false
            if (schedule.rest.isNotEmpty())
                employees[schedule.rest]!!.used = false

            print("i: $i    ")
            printEmployeesStatus()
        }
        return true
    }

    private fun resetEmployeesWorkCount(week: Int) {
        for (name in employees.keys) {
            employees[name]!!.count = 0
//            employees[name]!!.used = false
        }

//        if (week != 0) {
//            val schedule = schedules[startDate.plusDays((week * 7 - 1).toLong())]
//            val name = employees[schedule!!.close]!!.name
//            employees[name]!!.used = true
//        }
    }

    private fun isValid(i: Int): Boolean {
        return when (i) {
            4 -> !isCountEquals(0)
            5 -> !isCountEquals(1) && !isCountEquals(2)
            6 -> !isCountEquals(3)
            else -> true
        }
    }

    private fun setTodayOpen(schedule: TodaySchedule, random: Random) {
        while (true) {
            val r = random.nextInt(employees.size)
            val name = names[r]
            if (!employees[name]!!.used && employees[name]!!.count < 5 && name != close) {
                schedule.open = name
                employees[name]!!.used = true
                employees[name]!!.count++
                break
            }
        }
    }

    private fun setTodayClose(schedule: TodaySchedule, random: Random) {
        while (true) {
            val r = random.nextInt(employees.size)
            val name = names[r]
            if (!employees[names[r]]!!.used && employees[name]!!.count < 5) {
                schedule.close = name
                employees[name]!!.used = true
                employees[name]!!.count++
                close = name
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

    private fun printEmployeesStatus() {
        for (name in employees.keys)
            print("$name used: ${employees[name]!!.used} count: ${employees[name]!!.count}    ")
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