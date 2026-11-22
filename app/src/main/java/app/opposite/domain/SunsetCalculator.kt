package app.opposite.domain

import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin
import kotlin.math.tan

/**
 * Real solar sunset calculator using the NOAA Solar Position Algorithm.
 * Default location: Ica, Peru (lat: -14.0678, lon: -75.7286, Zone: America/Lima UTC-5).
 * In December in Ica, sunset is around 18:24 - 18:35.
 */
object SunsetCalculator {
    const val DEFAULT_LAT = -14.0678
    const val DEFAULT_LON = -75.7286
    val DEFAULT_ZONE: ZoneId = ZoneId.of("America/Lima")

    /**
     * Calculates the exact sunset time (hour and minute) for the given [date], [lat], and [lon].
     */
    fun calculateSunset(date: LocalDate, lat: Double = DEFAULT_LAT, lon: Double = DEFAULT_LON): LocalTime {
        val n = date.dayOfYear
        val lngHour = lon / 15.0

        // Approximate sunset time in days
        val t = n + ((18.0 - lngHour) / 24.0)

        // Sun's mean anomaly in degrees
        val m = (0.9856 * t) - 3.289

        // Sun's true longitude
        val mRad = Math.toRadians(m)
        var l = m + (1.916 * sin(mRad)) + (0.020 * sin(2 * mRad)) + 282.634
        l = (l % 360.0 + 360.0) % 360.0

        // Sun's right ascension
        val lRad = Math.toRadians(l)
        var ra = Math.toDegrees(kotlin.math.atan(0.91764 * tan(lRad)))
        ra = (ra % 360.0 + 360.0) % 360.0

        // Right ascension value must be in the same quadrant as L
        val lQuadrant = floor(l / 90.0) * 90.0
        val raQuadrant = floor(ra / 90.0) * 90.0
        ra += (lQuadrant - raQuadrant)
        ra /= 15.0 // Convert to hours

        // Sun's declination
        val sinDec = 0.39782 * sin(lRad)
        val cosDec = cos(asin(sinDec))

        // Sun's local hour angle for sunset (zenith = 90.8333 degrees for civil sunset with refraction)
        val zenithRad = Math.toRadians(90.8333)
        val latRad = Math.toRadians(lat)
        val cosH = (cos(zenithRad) - (sinDec * sin(latRad))) / (cosDec * cos(latRad))

        // Clamp in case of extreme polar latitudes
        val clampedCosH = cosH.coerceIn(-1.0, 1.0)
        val h = Math.toDegrees(acos(clampedCosH)) / 15.0

        // Local mean time of sunset
        val tSunset = h + ra - (0.06571 * t) - 6.622

        // UTC time of sunset
        var ut = tSunset - lngHour
        ut = (ut % 24.0 + 24.0) % 24.0

        // Convert UTC decimal hours to target time in zone
        val hours = floor(ut).toInt()
        val minutes = floor((ut - hours) * 60.0).toInt()
        val seconds = floor(((ut - hours) * 60.0 - minutes) * 60.0).toInt()

        val utcZoned = ZonedDateTime.of(date, LocalTime.of(hours, minutes, seconds.coerceIn(0, 59)), ZoneId.of("UTC"))
        val localZoned = utcZoned.withZoneSameInstant(DEFAULT_ZONE)
        return localZoned.toLocalTime()
    }

    /**
     * Returns the sunset as epoch milliseconds for the given date.
     */
    fun sunsetEpochMillis(date: LocalDate, lat: Double = DEFAULT_LAT, lon: Double = DEFAULT_LON, zone: ZoneId = DEFAULT_ZONE): Long {
        val time = calculateSunset(date, lat, lon)
        return ZonedDateTime.of(date, time, zone).toInstant().toEpochMilli()
    }
}
