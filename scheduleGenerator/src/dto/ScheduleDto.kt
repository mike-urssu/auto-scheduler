package dto

import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ScheduleDto(val name: String, midDate: String) {
    val date: LocalDate = LocalDate.parse(midDate.subSequence(0, midDate.length), DateTimeFormatter.ISO_DATE)
}