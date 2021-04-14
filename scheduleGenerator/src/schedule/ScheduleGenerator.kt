package schedule

import dto.FixedMidDateDto
import dto.FixedRestDateDto
import dto.ScheduleDto
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder

class ScheduleGenerator(
    private val date: LocalDate,
    private val week: Int,
    private val names: List<String>
) {

    lateinit var schedules: List<ScheduleDto>
    fun initSchedule() {
        schedules = mutableListOf()
        for (i in 0 until week * 7) {
            val scheduleDto = ScheduleDto(date.plusDays(i.toLong()))
            schedules.plus(scheduleDto)
            println("i: $i      date: ${scheduleDto.date}")
        }

    }
}

fun main() {
    val today = "2021-03-28"
    val startDate = LocalDate.parse(today.subSequence(0, today.length), DateTimeFormatter.ISO_DATE)
    val names = listOf("a", "b", "c")

    val mids = arrayListOf<FixedMidDateDto>()
    val fixedMidDateDto = FixedMidDateDto("2021-04-05", "a")
    mids.add(fixedMidDateDto)

    val fixedRestDateDto = FixedRestDateDto("2021-04-05", "a")
    mids.add(fixedMidDateDto)


    val scheduleGenerator = ScheduleGenerator(startDate, 4, names)
    scheduleGenerator.initSchedule()

}

fun String.toLocalDateTime(): LocalDateTime {
    val dateFormatter = DateTimeFormatterBuilder()
        .appendPattern("yyyy-mm-dd")
        .toFormatter()
    return LocalDateTime.parse(this, dateFormatter)
}