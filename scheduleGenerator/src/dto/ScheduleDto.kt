package dto

import java.time.LocalDate

class ScheduleDto(val date: LocalDate) {
    val names: List<String> = ArrayList()
}