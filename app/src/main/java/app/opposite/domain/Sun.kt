package app.opposite.domain

import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.math.PI
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan

/** Sunset by the NOAA approximation: good to a minute or two, which is plenty for lighting a window. */
object Sun {
    /** Sunset in epoch millis for [date] at [lat]/[lon]; at polar night, half past three in the afternoon. */
    fun sunset(date: LocalDate, lat: Double, lon: Double, zone: ZoneId): Long {
        val n = date.dayOfYear
        val g = 2 * PI / 365.0 * (n - 1 + 0.5)
        val eqTime = 229.18 * (0.000075 + 0.001868 * cos(g) - 0.032077 * sin(g) - 0.014615 * cos(2 * g) - 0.040849 * sin(2 * g))
        val decl = 0.006918 - 0.399912 * cos(g) + 0.070257 * sin(g) - 0.006758 * cos(2 * g) + 0.000907 * sin(2 * g) - 0.002697 * cos(3 * g) + 0.00148 * sin(3 * g)
        val latR = Math.toRadians(lat)
        val cosHa = cos(Math.toRadians(90.833)) / (cos(latR) * cos(decl)) - tan(latR) * tan(decl)
        val midnight = date.atStartOfDay(zone)
        if (cosHa > 1) return midnight.plusHours(15).plusMinutes(30).toInstant().toEpochMilli()
        if (cosHa < -1) return midnight.plusHours(21).toInstant().toEpochMilli()
        val ha = Math.toDegrees(acos(cosHa))
        val utcMinutes = 720 - 4 * (lon - ha) - eqTime
        val utc = ZonedDateTime.of(date.atStartOfDay(), ZoneId.of("UTC")).plusSeconds((utcMinutes * 60).toLong())
        return utc.toInstant().toEpochMilli()
    }

    /** Without a location: the longitude the time zone implies, and the latitude of Moscow. */
    fun guess(zone: ZoneId, date: LocalDate): Pair<Double, Double> {
        val offset = zone.rules.getStandardOffset(date.atStartOfDay(zone).toInstant()).totalSeconds / 3600.0
        return 55.75 to offset * 15.0
    }
}
