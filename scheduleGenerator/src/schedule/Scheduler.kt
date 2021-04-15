package schedule

import dto.MidScheduleDto
import dto.RestScheduleDto
import java.time.LocalDate
import kotlin.random.Random

class Scheduler(
    private val names: List<String>,
    private val employees: HashMap<String, Employee>,
    private val date: LocalDate,
    private val week: Int
) {
    private lateinit var schedules: LinkedHashMap<LocalDate, TodaySchedule>

    fun init() {
        schedules = linkedMapOf()
        for (i in 0 until week * 7) {
            schedules[date.plusDays(i.toLong())] = TodaySchedule()
        }
    }

    fun setSchedules(midSchedules: List<MidScheduleDto>, restSchedules: List<RestScheduleDto>) {
        this.setMidDates(midSchedules)
        this.setRestDates(restSchedules)
        this.setMonthSchedules()
    }

    private fun setMidDates(midSchedules: List<MidScheduleDto>) {
        for (mid in midSchedules) {
            val schedule = schedules[mid.date]!!
            schedule.mid = mid.name
        }
    }

    private fun setRestDates(restSchedules: List<RestScheduleDto>) {
        for (rest in restSchedules) {
            val schedule = schedules[rest.date]!!
            schedule.rest = rest.name
        }
    }

    private fun setMonthSchedules() {
        for (index in 0 until week) {
            while (true) {
                if (setWeekSchedules(index))
                    break
            }
        }
    }

    private fun setWeekSchedules(week: Int): Boolean {
        resetEmployeesWorkCount()

        var close = ""
        val random = Random

        for (j in 0 until 7) {
//            print("j: $j    ")
            if (j == 4) {
                if (isCountEquals(0)) {
                    println()
                    return false
                }
            }
            if (j == 5) {
                if (isCountEquals(1) || isCountEquals(2)) {
                    println()
                    return false
                }
            }
            if (j == 6) {
                if (isCountEquals(3)) {
                    println()
                    return false
                }
            }

            val today = schedules[date.plusDays((week * 7 + j).toLong())]!!
            if (today.mid.isNotEmpty())
                employees[today.mid]!!.count++
            if (today.rest.isNotEmpty())
                employees[today.rest]!!.used = true
            setTodayOpen(today, random, close)
            if (close.isNotEmpty()) {
                employees[schedules[date.plusDays((week * 7 + j - 1).toLong())]!!.close]!!.used = false
            }
            close = setTodayClose(today, random)
            activateEmployees(today)

//            println("${date.plusDays((week * 7 + j).toLong())}  open: ${today.open}   mid: ${today.mid}    close: ${today.close}    rest: ${today.rest}")
        }
        return true
    }

    private fun resetEmployeesWorkCount() {
        for (name in employees.keys) {
            employees[name]!!.count = 0
            employees[name]!!.used = false
        }
    }

    private fun setTodayOpen(today: TodaySchedule, random: Random, close: String): String {
        var r: Int
        while (true) {
            r = random.nextInt(employees.size)
            if (close.isNotEmpty()) {
                if (employees[names[r]] != employees[close]!! && !employees[names[r]]!!.used && employees[names[r]]!!.count < 5 && today.rest != names[r]) {
                    today.open = employees[names[r]]!!.name
                    employees[names[r]]!!.used = true
                    employees[names[r]]!!.count++
                    break
                }
            } else {
                if (!employees[names[r]]!!.used && employees[names[r]]!!.count < 5) {
                    today.open = employees[names[r]]!!.name
                    employees[names[r]]!!.used = true
                    employees[names[r]]!!.count++
                    break
                }
            }
        }
        return names[r]
    }

    private fun setTodayClose(today: TodaySchedule, random: Random): String {
        var r: Int
        while (true) {
            r = random.nextInt(employees.size)
            if (!employees[names[r]]!!.used && employees[names[r]]!!.count < 5 && today.rest != names[r]) {
                today.close = employees[names[r]]!!.name
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

    fun printScheduler() {
        for (i in 0 until week) {
            for (j in 0 until 7) {
                val today = date.plusDays((i * 7 + j).toLong())
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