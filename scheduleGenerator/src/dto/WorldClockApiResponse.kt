package dto

import java.time.LocalDateTime

data class WorldClockApiResponse(
    val abbreviation: String,
    val clientIp: String,
    val datetime: LocalDateTime,
    val dayOfWeek: Int,
    val dayOfYear: Int,
    val dst: Boolean,
    val dstFrom: String?,
    val dstOffset: Int,
    val dstUntil: String?,
    val rawOffset: Int,
    val timezone: String,
    val unixtime: Long,
    val utcDatetime: LocalDateTime,
    val utcOffset: String,
    val weekNumber: Int
)