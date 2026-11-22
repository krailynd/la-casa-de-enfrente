package app.opposite.domain

import java.time.ZoneId
import java.time.ZonedDateTime

object CoupleStory {
    // Viernes 6 de noviembre de 2026 a las 06:18:35 PM (Hora Perú UTC-5)
    val NEW_STAGE_START_ZONED: ZonedDateTime = ZonedDateTime.of(
        2026, 11, 6, 18, 18, 35, 0, ZoneId.of("America/Lima")
    )
    val NEW_STAGE_START_EPOCH_MS: Long = NEW_STAGE_START_ZONED.toInstant().toEpochMilli()

    data class LiveDuration(
        val days: Long,
        val hours: Long,
        val minutes: Long,
        val seconds: Long,
        val formattedSummary: String,
        val subtitle: String = "Reescribiendo nuestra historia desde el 06 Nov 2026 · 6:18:35 PM",
    )

    fun calculateDuration(nowEpochMs: Long = System.currentTimeMillis()): LiveDuration {
        val diffMs = (nowEpochMs - NEW_STAGE_START_EPOCH_MS).coerceAtLeast(0)
        val totalSec = diffMs / 1000
        val days = totalSec / (24 * 3600)
        val hours = (totalSec % (24 * 3600)) / 3600
        val minutes = (totalSec % 3600) / 60
        val seconds = totalSec % 60
        val summary = "$days días, ${hours}h ${minutes}m ${seconds}s juntos"
        return LiveDuration(days, hours, minutes, seconds, summary)
    }
}
