package schedule

import dto.MidScheduleDto
import dto.RestScheduleDto
import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun main() {
    /**
     * 직원, 시작날짜, 기간, 미드, 휴무 데이터를 엑셀 파일로부터 가져오기
     */
    val names = listOf("솜사탕", "초콜릿", "달고나")
    val employees = HashMap<String, Employee>()
    for (name in names) {
        val employee = Employee(name)
        employees[name] = employee
    }

    val startDate = "2021-03-28"
    val date = LocalDate.parse(startDate.subSequence(0, startDate.length), DateTimeFormatter.ISO_DATE)

    val week = 4

    val midSchedules = arrayListOf<MidScheduleDto>()       // 미드 날짜
    val midDateDto = MidScheduleDto("2021-03-30", "솜사탕")
    val midDateDto1 = MidScheduleDto("2021-04-06", "초콜릿")
    val midDateDto2 = MidScheduleDto("2021-04-13", "달고나")
    val midDateDto3 = MidScheduleDto("2021-04-20", "솜사탕")
    midSchedules.add(midDateDto)
    midSchedules.add(midDateDto1)
    midSchedules.add(midDateDto2)
    midSchedules.add(midDateDto3)

    val restSchedules = arrayListOf<RestScheduleDto>()     // 휴무 날짜
    val restDateDto = RestScheduleDto("2021-03-31", "초콜릿")
    val restDateDto1 = RestScheduleDto("2021-04-01", "솜사탕")
    restSchedules.add(restDateDto)
    restSchedules.add(restDateDto1)

    /**
     * Scheduler 생성
     */
    val scheduler = Scheduler(names, employees, date, week)
    scheduler.init()
    scheduler.setSchedules(midSchedules, restSchedules)

    /**
     * Scheduler 출력
     */
    scheduler.printScheduler()
}