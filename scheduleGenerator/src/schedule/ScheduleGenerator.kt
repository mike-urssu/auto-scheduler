package schedule

import dto.MidDateDto
import dto.RestDateDto
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

    val week = 1

    val midDates = arrayListOf<MidDateDto>()       // 미드 날짜
    val midDateDto = MidDateDto("2021-03-30", "솜사탕")
    midDates.add(midDateDto)

    val restDates = arrayListOf<RestDateDto>()     // 휴무 날짜
    val restDateDto = RestDateDto("2021-03-31", "초콜릿")
    restDates.add(restDateDto)

    /**
     * Schedule 생성
     */
    val scheduler = Scheduler(employees, date, week)
    scheduler.init()
    scheduler.setMidDates(midDates)
    scheduler.setRestDates(restDates)

    scheduler.printScheduler()

}