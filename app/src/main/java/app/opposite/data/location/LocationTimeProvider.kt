package app.opposite.data.location

import app.opposite.domain.astronomy.AstronomyEngine
import app.opposite.domain.astronomy.SkyAtmosphere
import app.opposite.domain.astronomy.SolarPhase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

object LocationTimeProvider {
    // Si es null, usa la hora real del dispositivo en zona Perú (America/Lima)
    private val _simulatedHour = MutableStateFlow<Float?>(null)
    val simulatedHour: StateFlow<Float?> = _simulatedHour.asStateFlow()

    private val _currentAtmosphere = MutableStateFlow(
        AstronomyEngine.calculateAtmosphere(LocalDateTime.now())
    )
    val currentAtmosphere: StateFlow<SkyAtmosphere> = _currentAtmosphere.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.Default)

    init {
        scope.launch {
            while (isActive) {
                updateAtmosphere()
                delay(30000) // Recalcular cada 30 segundos en reposo
            }
        }
    }

    fun setSimulatedHour(hour: Float?) {
        _simulatedHour.value = hour?.coerceIn(0f, 23.99f)
        updateAtmosphere()
    }

    fun setQuickPhase(phase: SolarPhase) {
        val targetHour = when (phase) {
            SolarPhase.SUNRISE -> 5.75f          // 05:45
            SolarPhase.MORNING -> 9.5f           // 09:30
            SolarPhase.MIDDAY_AFTERNOON -> 13.5f // 13:30
            SolarPhase.GOLDEN_SUNSET -> 18.25f   // 18:15 (¡Hora dorada carmesí!)
            SolarPhase.DUSK -> 19.0f             // 19:00
            SolarPhase.DEEP_NIGHT -> 22.5f       // 22:30
        }
        setSimulatedHour(targetHour)
    }

    fun resetToRealTime() {
        _simulatedHour.value = null
        updateAtmosphere()
    }

    fun getEffectiveDateTime(): LocalDateTime {
        val sim = _simulatedHour.value
        val zone = java.time.ZoneId.of("America/Lima")
        val now = java.time.ZonedDateTime.now(zone).toLocalDateTime()
        return if (sim != null) {
            val totalMinutes = (sim * 60f).toInt()
            val hour = (totalMinutes / 60) % 24
            val min = totalMinutes % 60
            LocalDateTime.of(now.toLocalDate(), LocalTime.of(hour, min))
        } else {
            now
        }
    }

    private fun updateAtmosphere() {
        val effectiveDt = getEffectiveDateTime()
        _currentAtmosphere.value = AstronomyEngine.calculateAtmosphere(effectiveDt)
    }
}
