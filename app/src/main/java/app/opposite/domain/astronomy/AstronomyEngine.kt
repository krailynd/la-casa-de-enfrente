package app.opposite.domain.astronomy

import androidx.compose.ui.graphics.Color
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.math.PI
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.sin

enum class SolarPhase {
    SUNRISE,            // Amanecer (sunrise - 35min a sunrise + 45min)
    MORNING,            // Mañana (sunrise + 45min a 11:30)
    MIDDAY_AFTERNOON,   // Mediodía / Tarde (11:30 a sunset - 65min)
    GOLDEN_SUNSET,      // Atardecer Carmesí & Dorado (sunset - 65min a sunset + 20min)
    DUSK,               // Anochecer / Crepúsculo (sunset + 20min a sunset + 65min)
    DEEP_NIGHT          // Noche Profunda
}

data class SkyAtmosphere(
    val phase: SolarPhase,
    val skyTop: Color,
    val skyMid: Color,
    val skyHorizon: Color,
    val sunProgress: Float,     // 0f a 1f durante el día
    val moonProgress: Float,    // 0f a 1f durante la noche
    val sunElevationDeg: Float,
    val shadowDx: Float,
    val shadowLengthFactor: Float,
    val shadowColor: Color,
    val sunRadiusMultiplier: Float,
    val sunCenterColor: Color,
    val sunHaloColor: Color,
    val isDayTime: Boolean,
    val isSunset: Boolean,
    val isNight: Boolean,
    val ambientFilter: Color,
    val lightsIntensity: Float  // 0f de día a 1f de noche
)

object AstronomyEngine {
    // Coordenadas fijas por defecto: Ica, Perú
    const val DEFAULT_LAT = -14.0678
    const val DEFAULT_LON = -75.7286

    /**
     * Calcula la atmósfera del cielo en tiempo real o simulado mediante ecuaciones solares.
     */
    fun calculateAtmosphere(dateTime: LocalDateTime, lat: Double = DEFAULT_LAT, lon: Double = DEFAULT_LON): SkyAtmosphere {
        val minuteOfDay = dateTime.hour * 60 + dateTime.minute + dateTime.second / 60f

        // Aproximación solar NOAA para Ica (-14.0678, UTC-5)
        // Amanecer aprox: 05:40 (340 min), Atardecer aprox: 18:25 (1105 min)
        val sunriseMin = 340f
        val sunsetMin = 1105f
        val solarNoonMin = (sunriseMin + sunsetMin) / 2f // 722.5 min (~12:02)

        val isDayTime = minuteOfDay in sunriseMin..sunsetMin
        val sunProgress = if (isDayTime) {
            (minuteOfDay - sunriseMin) / (sunsetMin - sunriseMin)
        } else {
            -1f
        }

        val moonProgress = if (!isDayTime) {
            if (minuteOfDay > sunsetMin) {
                (minuteOfDay - sunsetMin) / (1440f - sunsetMin + sunriseMin)
            } else {
                (1440f - sunsetMin + minuteOfDay) / (1440f - sunsetMin + sunriseMin)
            }
        } else {
            -1f
        }

        // Elevación solar aproximada
        val sunElevationDeg = if (isDayTime) {
            (sin(sunProgress * PI) * 78f).toFloat()
        } else {
            -(sin((minuteOfDay / 1440f) * 2 * PI) * 45f).toFloat()
        }

        // Determinar fase solar
        val phase = when {
            minuteOfDay in (sunriseMin - 35f)..(sunriseMin + 45f) -> SolarPhase.SUNRISE
            minuteOfDay in (sunriseMin + 45f)..690f -> SolarPhase.MORNING
            minuteOfDay in 690f..(sunsetMin - 65f) -> SolarPhase.MIDDAY_AFTERNOON
            minuteOfDay in (sunsetMin - 65f)..(sunsetMin + 20f) -> SolarPhase.GOLDEN_SUNSET
            minuteOfDay in (sunsetMin + 20f)..(sunsetMin + 65f) -> SolarPhase.DUSK
            else -> SolarPhase.DEEP_NIGHT
        }

        val isSunset = phase == SolarPhase.GOLDEN_SUNSET
        val isNight = phase == SolarPhase.DEEP_NIGHT || phase == SolarPhase.DUSK

        // Interpolación continua lerp de paletas de las 6 estaciones
        val (skyTop, skyMid, skyHorizon) = interpolateSkyGradient(minuteOfDay, sunriseMin, sunsetMin)

        // Sombras dinámicas: desplazamiento y longitud
        val shadowDx = if (isDayTime) {
            (-cos(sunProgress * PI) * 36f).toFloat()
        } else 0f

        val shadowLengthFactor = if (isDayTime) {
            (1.4f - sin(sunProgress * PI) * 0.9f).toFloat().coerceIn(0.4f, 1.6f)
        } else 0f

        val shadowColor = when (phase) {
            SolarPhase.MORNING, SolarPhase.SUNRISE -> Color(0x385A6D94) // Lavanda suave
            SolarPhase.GOLDEN_SUNSET -> Color(0x554A1835)               // Vino-púrpura cálido
            SolarPhase.MIDDAY_AFTERNOON -> Color(0x332B1A24)
            else -> Color(0x00000000)
        }

        // Sol y Resplandor
        val sunRadiusMultiplier = if (isSunset) 5.2f else if (phase == SolarPhase.SUNRISE) 4.2f else 3.2f
        val sunCenterColor = if (isSunset) Color(0xFFFF5D3A) else Color(0xFFFFFDF5)
        val sunHaloColor = if (isSunset) Color(0x55E63946) else Color(0x33FFD166)

        val lightsIntensity = when (phase) {
            SolarPhase.DEEP_NIGHT -> 1.0f
            SolarPhase.DUSK -> 0.85f
            SolarPhase.GOLDEN_SUNSET -> 0.5f
            SolarPhase.SUNRISE -> 0.2f
            else -> 0f
        }

        val ambientFilter = when (phase) {
            SolarPhase.GOLDEN_SUNSET -> Color(0x38FF5E3A) // Baño carmesí cálido
            SolarPhase.DEEP_NIGHT -> Color(0x440D0614)   // Borgoña nocturno
            SolarPhase.DUSK -> Color(0x281A1028)
            SolarPhase.SUNRISE -> Color(0x25FFD08A)
            else -> Color(0x00000000)
        }

        return SkyAtmosphere(
            phase = phase,
            skyTop = skyTop,
            skyMid = skyMid,
            skyHorizon = skyHorizon,
            sunProgress = sunProgress.coerceIn(0f, 1f),
            moonProgress = moonProgress.coerceIn(0f, 1f),
            sunElevationDeg = sunElevationDeg,
            shadowDx = shadowDx,
            shadowLengthFactor = shadowLengthFactor,
            shadowColor = shadowColor,
            sunRadiusMultiplier = sunRadiusMultiplier,
            sunCenterColor = sunCenterColor,
            sunHaloColor = sunHaloColor,
            isDayTime = isDayTime,
            isSunset = isSunset,
            isNight = isNight,
            ambientFilter = ambientFilter,
            lightsIntensity = lightsIntensity
        )
    }

    private fun interpolateSkyGradient(min: Float, sunriseMin: Float, sunsetMin: Float): Triple<Color, Color, Color> {
        // Paletas de referencia
        // 1. Amanecer (05:40)
        val pSunrise = Triple(Color(0xFF2B244D), Color(0xFFE87A90), Color(0xFFFFD08A))
        // 2. Mañana (09:00)
        val pMorning = Triple(Color(0xFF5B8EC9), Color(0xFFA8D0F0), Color(0xFFEBF5FF))
        // 3. Tarde (14:00)
        val pAfternoon = Triple(Color(0xFF467BB8), Color(0xFF96C2E8), Color(0xFFF4EBD9))
        // 4. Atardecer Carmesí & Oro (18:00)
        val pSunset = Triple(Color(0xFF2E163B), Color(0xFFB82546), Color(0xFFFFC857))
        // 5. Crepúsculo (18:50)
        val pDusk = Triple(Color(0xFF1A0B18), Color(0xFF1A2142), Color(0xFF631D30))
        // 6. Noche Profunda (23:00)
        val pNight = Triple(Color(0xFF0D060D), Color(0xFF161D36), Color(0xFF261221))

        return when {
            min < 305f -> pNight
            min < sunriseMin -> lerpTriple(pNight, pSunrise, (min - 305f) / (sunriseMin - 305f))
            min < sunriseMin + 60f -> lerpTriple(pSunrise, pMorning, (min - sunriseMin) / 60f)
            min < 690f -> lerpTriple(pMorning, pAfternoon, (min - (sunriseMin + 60f)) / (690f - (sunriseMin + 60f)))
            min < (sunsetMin - 60f) -> pAfternoon
            min < sunsetMin -> lerpTriple(pAfternoon, pSunset, (min - (sunsetMin - 60f)) / 60f)
            min < sunsetMin + 45f -> lerpTriple(pSunset, pDusk, (min - sunsetMin) / 45f)
            min < sunsetMin + 90f -> lerpTriple(pDusk, pNight, (min - (sunsetMin + 45f)) / 45f)
            else -> pNight
        }
    }

    private fun lerpTriple(a: Triple<Color, Color, Color>, b: Triple<Color, Color, Color>, f: Float): Triple<Color, Color, Color> {
        val factor = f.coerceIn(0f, 1f)
        return Triple(
            lerpColor(a.first, b.first, factor),
            lerpColor(a.second, b.second, factor),
            lerpColor(a.third, b.third, factor)
        )
    }

    fun lerpColor(c1: Color, c2: Color, factor: Float): Color {
        val f = factor.coerceIn(0f, 1f)
        return Color(
            red = c1.red + (c2.red - c1.red) * f,
            green = c1.green + (c2.green - c1.green) * f,
            blue = c1.blue + (c2.blue - c1.blue) * f,
            alpha = c1.alpha + (c2.alpha - c1.alpha) * f
        )
    }
}
