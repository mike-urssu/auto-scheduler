package dto

import java.time.LocalDate
import java.time.format.DateTimeFormatter

class RestDateDto(fixedRestDate: String, val name: String) {
    val date: LocalDate =
        LocalDate.parse(fixedRestDate.subSequence(0, fixedRestDate.length), DateTimeFormatter.ISO_DATE)
}