package app.opposite.domain

import java.time.LocalDate
import java.time.ZoneId

/**
 * Sunset calculation using NOAA Solar Position Algorithm.
 * Default coordinates: Ica, Peru (-14.0678, -75.7286) where sunset in December is ~18:18 - 18:35.
 */
object Sun {
    fun sunset(date: LocalDate, lat: Double, lon: Double, zone: ZoneId): Long {
        return SunsetCalculator.sunsetEpochMillis(date, lat, lon, zone)
    }

    fun guess(zone: ZoneId, date: LocalDate): Pair<Double, Double> {
        return SunsetCalculator.DEFAULT_LAT to SunsetCalculator.DEFAULT_LON
    }
}
