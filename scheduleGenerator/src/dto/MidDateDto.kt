package dto

import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MidDateDto(fixedMidDate: String, val name: String) {
    val date: LocalDate = LocalDate.parse(fixedMidDate.subSequence(0, fixedMidDate.length), DateTimeFormatter.ISO_DATE)
}