package schedule

import dto.MidDateDto
import dto.RestDateDto
import java.time.LocalDate

class Scheduler(
    private val employees: HashMap<String, Employee>,
    private val date: LocalDate,
    private val week: Int
) {
    lateinit var schedules: LinkedHashMap<LocalDate, TodayEmployees>

    fun init() {
        schedules = linkedMapOf()
        for (i in 0 until week * 7) {
            schedules[date.plusDays(i.toLong())] = TodayEmployees()
        }
    }

    fun setMidDates(fixedMidDates: List<MidDateDto>) {
        for (mid in fixedMidDates) {
            schedules[mid.date]!!.mid = mid.name
        }
    }

    fun setRestDates(fixedRestDates: List<RestDateDto>) {
        for (rest in fixedRestDates) {
            schedules[rest.date]!!.rest = rest.name
        }
    }

    fun printScheduler() {
        for (i in 0 until week) {
            for (j in 0 until 7) {
                val today = date.plusDays((i * 7 + j).toLong())
                println("date: $today")
                val todayEmployee = schedules[today]!!
                print("open: ${todayEmployee.open}   mid: ${todayEmployee.mid}    close: ${todayEmployee.close}    rest: ${todayEmployee.rest}")
            }
        }
    }
}