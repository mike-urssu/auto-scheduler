package schedule

import entity.Employee
import entity.Schedule
import entity.Time
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

    /**
     * 엑셀 파일로부터 사용자가 초기에 설정한 미드, 휴무 날짜를 schedules에 초기화
     */
    fun loadData() {
        setDefaultDates(3, Time.MID)
        setDefaultDates(4, Time.REST)
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

    /**
     * 한달 단위 스케줄을 일주일 단위로 생성
     */
    fun setSchedules() {
        for (i in 0 until week)
            while (!setWeekSchedules(i));
    }

    private fun setWeekSchedules(week: Int): Boolean {
        resetEmployeesStatus(week)

        for (i in 0 until 7) {
            if (!isValid(i)) {
                rollback(week)
                return false
            }

            val today = startDate.plusDays((week * 7 + i).toLong())
            val todaySchedule = schedules[today]!!

            if (todaySchedule.rest.isNotEmpty())
                employees[todaySchedule.rest]!!.used = true

            if (!setTodayOpen(todaySchedule, week))
                return false

            if (yesterdayClose.isNotEmpty()) {
                if (yesterdayClose != todaySchedule.rest)
                    employees[yesterdayClose]!!.used = false
            }

            if (todaySchedule.mid.isNotEmpty())
                employees[todaySchedule.mid]!!.count++

            if (!setTodayClose(todaySchedule, week))
                return false

            employees[todaySchedule.open]!!.used = false

            if (todaySchedule.rest.isNotEmpty())
                employees[todaySchedule.rest]!!.used = false
        }
        return true
    }

    /**
     * 직원의 일한 횟수, 근무 가능 여부를 초기화
     */
    private fun resetEmployeesStatus(week: Int) {
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

    /**
     * 일주일 단위 스케줄에서 특정 index에 대해서 유효성 검사
     */
    private fun isValid(i: Int): Boolean {
        return when (i) {
            4 -> !isCountEquals(0) && !isCountEquals(1)     // (0, 4, 5), {(1, 4, 4), (1, 3, 5)}
            5 -> !isCountEquals(2) // && !isCountEquals(3)     // (2, 4, 5), (3, 3, 5)
            6 -> !isCountEquals(3)
            else -> true
        }
    }

    private fun isCountEquals(count: Int): Boolean {
        for (name in employees.keys) {
            if (employees[name]!!.count == count)
                return true
        }
        return false
    }

    /**
     * 해당 주차 스케줄이 유효하지 않으면 해당 주차 스케줄을 초기 상태로 초기화
     */
    private fun rollback(week: Int) {
        resetEmployeesStatus(week)

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

    /**
     * 해당 날짜에 오픈할 직원을 저장
     */
    private fun setTodayOpen(schedule: Schedule, week: Int): Boolean {
        val startAt = System.currentTimeMillis()
        while (true) {
            val endAt = System.currentTimeMillis()
            if (endAt - startAt > 1000) {
                rollback(week)
                return false
            }
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
        return true
    }

    /**
     * 해당 날짜에 마감할 직원을 저장
     */
    private fun setTodayClose(schedule: Schedule, week: Int): Boolean {
        val startAt = System.currentTimeMillis()
        while (true) {
            val endAt = System.currentTimeMillis()
            if (endAt - startAt > 1000) {
                rollback(week)
                return false
            }
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
        return true
    }

    /**
     * 한달동안 각 직원이 오픈, 미드, 마감, 휴무한 횟수를 저장
     */
    fun setTotalWorkCount() {
        for (date in schedules.keys) {
            val today = schedules[date]!!
            employees[today.open]!!.open++
            if (today.mid.isNotEmpty())
                employees[today.mid]!!.mid++
            employees[today.close]!!.close++
            if (today.rest.isNotEmpty())
                employees[today.rest]!!.rest++
        }
    }

    /**
     * 한달 단위 스케줄을 excel 파일로 출력
     */
    fun printMonthSchedule() {
        dataIO.readyToPrint()
        for (i in 0 until week) {
            dataIO.printDate(startDate, i)
            dataIO.printOpenSchedules(schedules, i, startDate)
            dataIO.printMidSchedules(schedules, i, startDate)
            dataIO.printCloseSchedules(schedules, i, startDate)
            dataIO.printRestSchedules(schedules, i, startDate)
        }
        dataIO.printTotalWorkCount(employees)
        dataIO.printScheduleFile()
    }
}