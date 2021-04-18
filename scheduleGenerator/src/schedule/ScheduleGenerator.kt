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

    println("이번달 스케줄표를 생성합니다.")
    val scheduler = Scheduler()
    println("스케줄표를 생성 중입니다.")
    scheduler.setSchedules()
    println("스케줄표를 출력합니다.")
//    scheduler.printScheduler()
    println("스케줄표를 작성을 완료하였습니다.")
}