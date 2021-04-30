package schedule

import dataIO.RequestIO
import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun main() {
    val kst = RequestIO.getKSTServerTime()
    val expiration = "2021-05-01"
    val expireAt = LocalDate.parse(expiration.subSequence(0, expiration.length), DateTimeFormatter.ISO_DATE)
    if (kst > expireAt) {
        println("***********************************")
        println("***    사용기간이 만료되었습니다    ***")
        println("***    사용기간을 갱신해주십시오    ***")
        println("***********************************")
        return
    }

    println("프로그램을 실행합니다.\n")
    Thread.sleep(1000)
    println("excel 파일로부터 데이터를 로딩하고 있습니다.\n")
    Thread.sleep(1000)
    val scheduler = ScheduleGenerator()
    scheduler.loadData()
    println("한달 스케줄을 생성중입니다.\n")
    Thread.sleep(1000)
    scheduler.setSchedules()
    scheduler.setTotalWorkCount()
    println("한달 스케줄을 excel 파일로 추출중입니다.\n")
    Thread.sleep(1000)
    scheduler.printMonthSchedule()
    println("스케줄 작성이 완료되었습니다.\n")
    Thread.sleep(1000)
    println("프로그램을 종료합니다.\n")
}