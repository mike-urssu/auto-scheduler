package schedule

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun main() {
    val today = LocalDateTime.now().toLocalDate()
    val expiration = "2021-05-01"
    val expireAt = LocalDate.parse(expiration.subSequence(0, expiration.length), DateTimeFormatter.ISO_DATE)

    if (today > expireAt) {
        println("사용기간이 종료되었습니다.")
        println("관리자에게 문의하세요.")
        return
    }

    val scheduler = Scheduler()
    scheduler.loadData()
    scheduler.setSchedules()
    scheduler.printScheduler()
}