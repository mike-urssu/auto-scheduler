package schedule

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun main() {
    val today = LocalDateTime.now().toLocalDate()
    val expiration = "2021-05-01"
    val expireAt = LocalDate.parse(expiration.subSequence(0, expiration.length), DateTimeFormatter.ISO_DATE)

    if (today > expireAt) {
        println("***********************************")
        println("***    사용기간이 만료되었습니다    ***")
        println("***    사용기간을 갱신해주십시오    ***")
        println("***********************************")
        return
    }

    val scheduler = ScheduleGenerator()
    scheduler.loadData()
    scheduler.setSchedules()
    scheduler.setTotalWorkCount()
    scheduler.printMonthSchedule()
}