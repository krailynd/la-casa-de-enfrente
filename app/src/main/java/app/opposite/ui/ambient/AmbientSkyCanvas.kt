package app.opposite.ui.ambient

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import app.opposite.domain.astronomy.SkyAtmosphere
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

data class StarParticle(
    val xRatio: Float,
    val yRatio: Float,
    val size: Float,
    val speed: Float,
    val phase: Float
)

data class SnowParticle(
    val xRatio: Float,
    var yRatio: Float,
    val size: Float,
    val speed: Float,
    val driftFreq: Float,
    val layer: Int // 0: fondo, 1: medio, 2: frente
)

@Composable
fun AmbientSkyCanvas(
    atmosphere: SkyAtmosphere,
    modifier: Modifier = Modifier,
    onSkyTap: (() -> Unit)? = null
) {
    val infiniteTransition = rememberInfiniteTransition(label = "ambient_sky")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sky_time"
    )

    val sunbeamRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(40000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sunbeams"
    )

    // Semilla fija de estrellas
    val stars = remember {
        val r = Random(2026)
        List(55) {
            StarParticle(
                xRatio = r.nextFloat(),
                yRatio = r.nextFloat() * 0.45f, // Arriba en el cielo
                size = 1.2f + r.nextFloat() * 2.2f,
                speed = 1.5f + r.nextFloat() * 3.0f,
                phase = r.nextFloat() * 2f * PI.toFloat()
            )
        }
    }

    // Copos de nieve multicapa
    val snowflakes = remember {
        val r = Random(1225)
        List(85) {
            val layer = when {
                it < 40 -> 0 // Fondo
                it < 72 -> 1 // Medio
                else -> 2    // Frente
            }
            SnowParticle(
                xRatio = r.nextFloat(),
                yRatio = r.nextFloat(),
                size = when (layer) {
                    0 -> 1.5f + r.nextFloat() * 1.5f
                    1 -> 2.5f + r.nextFloat() * 2.0f
                    else -> 4.0f + r.nextFloat() * 3.0f
                },
                speed = when (layer) {
                    0 -> 0.08f + r.nextFloat() * 0.06f
                    1 -> 0.16f + r.nextFloat() * 0.10f
                    else -> 0.28f + r.nextFloat() * 0.15f
                },
                driftFreq = 1.0f + r.nextFloat() * 2.5f,
                layer = layer
            )
        }
    }

    var magicBlizzardActive by remember { mutableStateOf(false) }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                magicBlizzardActive = !magicBlizzardActive
                onSkyTap?.invoke()
            }
    ) {
        val w = size.width
        val h = size.height
        val horizonY = h * 0.62f

        // 1. Degradado vertical del cielo según las 6 fases continuas
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    atmosphere.skyTop,
                    atmosphere.skyMid,
                    atmosphere.skyHorizon
                ),
                startY = 0f,
                endY = horizonY
            ),
            size = Size(w, horizonY)
        )

        // 2. Estrellas y Estrella Fugaz (de noche o crepúsculo)
        if (atmosphere.isNight) {
            val starAlphaBase = if (atmosphere.phase.name == "DUSK") 0.45f else 0.85f
            for (s in stars) {
                val twinkle = (sin(time * s.speed + s.phase) * 0.45f + 0.55f) * starAlphaBase
                drawCircle(
                    color = Color.White.copy(alpha = twinkle.coerceIn(0.1f, 1f)),
                    radius = s.size,
                    center = Offset(s.xRatio * w, s.yRatio * horizonY)
                )
            }

            // Estrella fugaz ocasional cruzando en diagonal
            val shootingProgress = (time % (PI.toFloat() * 1.5f)) / (PI.toFloat() * 1.5f)
            if (shootingProgress in 0.1f..0.25f) {
                val sp = (shootingProgress - 0.1f) / 0.15f
                val sx = w * (0.85f - sp * 0.5f)
                val sy = horizonY * (0.05f + sp * 0.35f)
                drawLine(
                    color = Color(0xFFFFD700).copy(alpha = (1f - sp) * 0.85f),
                    start = Offset(sx, sy),
                    end = Offset(sx + 35f, sy - 22f),
                    strokeWidth = 2.0f
                )
            }
        }

        // 3. Sol Procedural (durante el día o atardecer)
        if (atmosphere.isDayTime) {
            val sunProgress = atmosphere.sunProgress
            // Trayectoria parabólica
            val sunX = w * (0.12f + sunProgress * 0.76f)
            val sunY = horizonY * 0.85f - sin(sunProgress * PI.toFloat()) * (horizonY * 0.72f)
            val baseSunR = 24.dp.toPx()

            // A. Halo atmosférico amplio
            val haloRadius = baseSunR * atmosphere.sunRadiusMultiplier
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        atmosphere.sunHaloColor,
                        atmosphere.sunHaloColor.copy(alpha = atmosphere.sunHaloColor.alpha * 0.5f),
                        Color.Transparent
                    ),
                    center = Offset(sunX, sunY),
                    radius = haloRadius
                ),
                radius = haloRadius,
                center = Offset(sunX, sunY)
            )

            // B. Rayos solares rotativos (Sunbeams)
            rotate(degrees = sunbeamRotation, pivot = Offset(sunX, sunY)) {
                val beamCount = 12
                val beamPulse = sin(time * 2f) * 6f
                for (b in 0 until beamCount) {
                    val angle = b * (2f * PI.toFloat() / beamCount)
                    val beamLen = baseSunR * 2.2f + (if (b % 2 == 0) 14f else 4f) + beamPulse
                    val bx = sunX + cos(angle) * beamLen
                    val by = sunY + sin(angle) * beamLen
                    drawLine(
                        brush = Brush.linearGradient(
                            listOf(Color.White.copy(alpha = 0.45f), Color.Transparent),
                            start = Offset(sunX, sunY),
                            end = Offset(bx, by)
                        ),
                        start = Offset(sunX, sunY),
                        end = Offset(bx, by),
                        strokeWidth = 3f
                    )
                }
            }

            // C. Resplandor horizontal en el horizonte (Horizon bloom al atardecer)
            if (atmosphere.isSunset) {
                drawOval(
                    brush = Brush.radialGradient(
                        listOf(Color(0x77FF7A00), Color.Transparent),
                        center = Offset(sunX, horizonY),
                        radius = w * 0.55f
                    ),
                    topLeft = Offset(sunX - w * 0.45f, horizonY - 45f),
                    size = Size(w * 0.9f, 90f)
                )
            }

            // D. Núcleo Solar
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(Color.White, atmosphere.sunCenterColor),
                    center = Offset(sunX, sunY),
                    radius = baseSunR
                ),
                radius = baseSunR,
                center = Offset(sunX, sunY)
            )
        }

        // 4. Luna Procedural (durante la noche o crepúsculo)
        if (atmosphere.isNight) {
            val moonProg = atmosphere.moonProgress
            val moonX = w * (0.82f - moonProg * 0.65f)
            val moonY = horizonY * 0.75f - sin(moonProg * PI.toFloat()) * (horizonY * 0.60f)
            val moonRadius = 20.dp.toPx()

            // Doble halo lunar en crema
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(
                        Color(0x38FFF6D6),
                        Color(0x12FFF6D6),
                        Color.Transparent
                    ),
                    center = Offset(moonX, moonY),
                    radius = moonRadius * 3.6f
                ),
                radius = moonRadius * 3.6f,
                center = Offset(moonX, moonY)
            )

            // Luna creciente luminosa
            drawCircle(
                color = Color(0xFFFFF7DC),
                radius = moonRadius,
                center = Offset(moonX, moonY)
            )
            // Sombra recortando la luna para fase creciente
            drawCircle(
                color = atmosphere.skyTop,
                radius = moonRadius * 0.88f,
                center = Offset(moonX + moonRadius * 0.42f, moonY - moonRadius * 0.15f)
            )
        }

        // 5. Nieve Multicapa y Paralaje
        val snowDensity = if (atmosphere.isNight) 1.25f else 1.0f
        val blizzardFactor = if (magicBlizzardActive) 1.8f else 1.0f

        for (sn in snowflakes) {
            // Desplazamiento vertical continuo
            val yNorm = (sn.yRatio + (time / (2 * PI.toFloat())) * sn.speed * blizzardFactor) % 1.0f
            val sy = yNorm * h
            val windOffset = sin(time * sn.driftFreq + sn.xRatio * 10f) * (if (sn.layer == 2) 16f else 8f)
            val sx = (sn.xRatio * w + windOffset) % w

            val alpha = when (sn.layer) {
                0 -> 0.32f * snowDensity
                1 -> 0.65f * snowDensity
                else -> 0.92f * snowDensity
            }

            drawCircle(
                color = if (magicBlizzardActive && sn.layer == 2) {
                    Color(0xFFFFD700).copy(alpha = alpha) // Nieve dorada mágica al tocar
                } else {
                    Color.White.copy(alpha = alpha)
                },
                radius = sn.size * (if (magicBlizzardActive) 1.3f else 1.0f),
                center = Offset(sx, sy)
            )
        }
    }
}
