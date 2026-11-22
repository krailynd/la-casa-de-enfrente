package app.opposite.ui.ambient

import android.content.Context
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import app.opposite.data.messages.AmbientMessagesRepository
import app.opposite.domain.astronomy.SkyAtmosphere
import kotlin.math.PI
import kotlin.math.sin

enum class FairyLightMode {
    COZY_FLICKER,
    FESTIVE_WAVE,
    ROMANTIC_RED_GOLD,
    STEADY
}

@Composable
fun LivingStreetCanvas(
    atmosphere: SkyAtmosphere,
    onEntityTapped: (speaker: String, message: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val infiniteTransition = rememberInfiniteTransition(label = "living_street")

    // Reloj continuo para movimiento suave
    val animTime by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(28000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "street_loop"
    )

    // Pulso para luces navideñas
    val lightPulse by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "light_pulse"
    )

    var fairyMode by remember { mutableStateOf(FairyLightMode.ROMANTIC_RED_GOLD) }
    var wreathTaps by remember { mutableIntStateOf(0) }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val w = size.width
                    val h = size.height

                    // 1. Tejado / Guirnaldas de luces
                    if (offset.y in (h * 0.12f)..(h * 0.18f)) {
                        fairyMode = when (fairyMode) {
                            FairyLightMode.COZY_FLICKER -> FairyLightMode.FESTIVE_WAVE
                            FairyLightMode.FESTIVE_WAVE -> FairyLightMode.ROMANTIC_RED_GOLD
                            FairyLightMode.ROMANTIC_RED_GOLD -> FairyLightMode.STEADY
                            FairyLightMode.STEADY -> FairyLightMode.COZY_FLICKER
                        }
                        val (spk, msg) = AmbientMessagesRepository.getMessageForEntity(
                            context, "guirnalda", atmosphere.isDayTime, atmosphere.isSunset
                        )
                        onEntityTapped(spk, msg)
                        return@detectTapGestures
                    }

                    // 2. Corona Navideña en la puerta principal (centro abajo de fachada)
                    if (offset.x in (w * 0.42f)..(w * 0.58f) && offset.y in (h * 0.68f)..(h * 0.78f)) {
                        wreathTaps++
                        val (spk, msg) = AmbientMessagesRepository.getMessageForEntity(
                            context, "corona", atmosphere.isDayTime, atmosphere.isSunset
                        )
                        onEntityTapped(spk, msg)
                        return@detectTapGestures
                    }

                    // 3. Rata Aspirable 🐭🎸 (junto a la puerta o en la banca)
                    if (offset.x in (w * 0.60f)..(w * 0.74f) && offset.y in (h * 0.73f)..(h * 0.82f)) {
                        val (spk, msg) = AmbientMessagesRepository.getMessageForEntity(
                            context, "rata_aspirable", atmosphere.isDayTime, atmosphere.isSunset
                        )
                        onEntityTapped(spk, msg)
                        return@detectTapGestures
                    }

                    // 4. Muñeco de Nieve (abajo a la derecha)
                    if (offset.x in (w * 0.55f)..(w * 0.75f) && offset.y in (h * 0.74f)..(h * 0.86f)) {
                        val (spk, msg) = AmbientMessagesRepository.getMessageForEntity(
                            context, "muñeco", atmosphere.isDayTime, atmosphere.isSunset
                        )
                        onEntityTapped(spk, msg)
                        return@detectTapGestures
                    }

                    // 5. Farola Cálida (abajo a la izquierda)
                    if (offset.x in (w * 0.05f)..(w * 0.18f) && offset.y in (h * 0.65f)..(h * 0.85f)) {
                        val (spk, msg) = AmbientMessagesRepository.getMessageForEntity(
                            context, "farola", atmosphere.isDayTime, atmosphere.isSunset
                        )
                        onEntityTapped(spk, msg)
                        return@detectTapGestures
                    }

                    // 6. Pino Nevado (abajo izquierda)
                    if (offset.x in (w * 0.12f)..(w * 0.28f) && offset.y in (h * 0.72f)..(h * 0.88f)) {
                        val (spk, msg) = AmbientMessagesRepository.getMessageForEntity(
                            context, "pino", atmosphere.isDayTime, atmosphere.isSunset
                        )
                        onEntityTapped(spk, msg)
                        return@detectTapGestures
                    }

                    // 7. Calle nevada inferior (Coche vintage rojo o peatones paseando)
                    if (offset.y > h * 0.84f) {
                        val (spk, msg) = AmbientMessagesRepository.getMessageForEntity(
                            context, "coche", atmosphere.isDayTime, atmosphere.isSunset
                        )
                        onEntityTapped(spk, msg)
                        return@detectTapGestures
                    }

                    // 8. Sol / Luna / Cielo general
                    if (offset.y < h * 0.35f) {
                        val tag = if (atmosphere.isNight) "luna" else "sol"
                        val (spk, msg) = AmbientMessagesRepository.getMessageForEntity(
                            context, tag, atmosphere.isDayTime, atmosphere.isSunset
                        )
                        onEntityTapped(spk, msg)
                    }
                }
            }
    ) {
        val w = size.width
        val h = size.height

        // A. Sombras Dinámicas Reales en el Suelo y Objetos
        if (atmosphere.isDayTime && atmosphere.shadowLengthFactor > 0.05f) {
            val sDx = atmosphere.shadowDx
            val sLen = atmosphere.shadowLengthFactor

            // Sombra del muñeco de nieve
            drawOval(
                color = atmosphere.shadowColor,
                topLeft = Offset(w * 0.65f + sDx * 0.5f - 24f * sLen, h * 0.83f),
                size = Size(48f * sLen, 16f)
            )

            // Sombra del pino
            drawOval(
                color = atmosphere.shadowColor,
                topLeft = Offset(w * 0.18f + sDx * 0.7f - 38f * sLen, h * 0.84f),
                size = Size(76f * sLen, 20f)
            )

            // Sombra de la banca
            drawOval(
                color = atmosphere.shadowColor,
                topLeft = Offset(w * 0.80f + sDx * 0.4f - 28f * sLen, h * 0.82f),
                size = Size(56f * sLen, 14f)
            )
        }

        // B. Luces Cálidas y Conos de Luz Nocturnos (Farola y Faros)
        if (atmosphere.lightsIntensity > 0.05f) {
            val lampIntensity = atmosphere.lightsIntensity
            val lx = w * 0.09f
            val ly = h * 0.70f

            // Cono trapezoidal de luz de la farola
            val lampCone = Path().apply {
                moveTo(lx - 6f, ly)
                lineTo(lx + 6f, ly)
                lineTo(lx + 55f, ly + 95f)
                lineTo(lx - 45f, ly + 95f)
                close()
            }
            drawPath(
                path = lampCone,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0x99FFE08A).copy(alpha = 0.55f * lampIntensity),
                        Color(0x44FFE08A).copy(alpha = 0.22f * lampIntensity),
                        Color.Transparent
                    ),
                    startY = ly,
                    endY = ly + 95f
                )
            )

            // Óvalo iluminado en la nieve
            drawOval(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0x88FFE08A).copy(alpha = 0.45f * lampIntensity),
                        Color.Transparent
                    ),
                    center = Offset(lx + 5f, ly + 95f),
                    radius = 45f
                ),
                topLeft = Offset(lx - 40f, ly + 82f),
                size = Size(90f, 26f)
            )
        }

        // C. Decoraciones Navideñas en el Tejado y Balcones (Guirnaldas Festivas)
        val roofY = h * 0.15f
        drawFairyLightsRow(w, roofY, lightPulse, fairyMode, atmosphere.lightsIntensity)

        // D. Chimenea con Humo Animado en el Tejado
        val chimX = w * 0.22f
        val chimY = h * 0.14f
        // Chimenea de ladrillo
        drawRect(
            color = Color(0xFF6B1825),
            topLeft = Offset(chimX - 10f, chimY - 14f),
            size = Size(20f, 14f)
        )
        // Partículas de humo blanco elevándose
        for (i in 0 until 4) {
            val smokePhase = (animTime * 14f + i * 0.7f) % 3.0f
            val sy = chimY - 16f - smokePhase * 18f
            val sx = chimX + sin(smokePhase * 2f) * (6f + smokePhase * 4f)
            val sRadius = 4f + smokePhase * 3.5f
            drawCircle(
                color = Color.White.copy(alpha = (1f - (smokePhase / 3.0f)).coerceIn(0f, 0.45f)),
                radius = sRadius,
                center = Offset(sx, sy)
            )
        }

        // E. Coche Vintage Rojo con Árbol pasando periódicamente por la calle
        val carProgress = (animTime * 2.2f) % 1.0f // Pasa cada ~13 segundos
        if (carProgress in 0.05f..0.95f) {
            val cx = w * 1.15f - (carProgress / 0.90f) * (w * 1.30f) // De derecha a izquierda
            val cy = h * 0.92f

            drawVintageCar(cx, cy, atmosphere.lightsIntensity > 0.3f)
        }

        // F. Rata Aspirable 🐭🎸 (Pequeño ratón con bufanda roja y mini guitarra)
        val ratX = w * 0.68f
        val ratY = h * 0.78f
        drawRataAspirableIcon(ratX, ratY, animTime)

        // G. Gatito Esponjoso en la Banca (abajo derecha)
        val catX = w * 0.84f
        val catY = h * 0.81f
        drawCatOnBench(catX, catY, lightPulse)

        // H. Pareja Caminando Tomada de la Mano (Elvis & Francesca)
        val coupleProgress = (animTime * 0.8f) % 1.0f
        if (coupleProgress in 0.1f..0.9f) {
            val cpx = w * 0.05f + (coupleProgress / 0.8f) * (w * 0.75f)
            val cpy = h * 0.88f
            drawWalkingCouple(cpx, cpy, lightPulse)
        }
    }
}

private fun DrawScope.drawFairyLightsRow(
    w: Float,
    y: Float,
    pulse: Float,
    mode: FairyLightMode,
    nightIntensity: Float
) {
    val count = 22
    val step = (w * 0.84f) / count
    val startX = w * 0.08f

    for (i in 0 until count) {
        val bx = startX + i * step
        val by = y + sin(i * 0.6f) * 4f

        val bulbColor = when (mode) {
            FairyLightMode.ROMANTIC_RED_GOLD -> if (i % 2 == 0) Color(0xFFC81D31) else Color(0xFFFFD700)
            FairyLightMode.FESTIVE_WAVE -> when (i % 3) {
                0 -> Color(0xFFC81D31)
                1 -> Color(0xFFFFD700)
                else -> Color(0xFFFFF8EE)
            }
            FairyLightMode.COZY_FLICKER -> if (sin(pulse * 3f + i) > 0) Color(0xFFFFD700) else Color(0xFFE85D75)
            FairyLightMode.STEADY -> Color(0xFFFFE08A)
        }

        val glowAlpha = (0.55f + sin(pulse + i * 0.5f) * 0.35f).coerceIn(0.2f, 1f)

        // Halo de luz
        drawCircle(
            color = bulbColor.copy(alpha = glowAlpha * (0.4f + nightIntensity * 0.6f)),
            radius = 6.5f,
            center = Offset(bx, by)
        )
        // Núcleo bombillita
        drawCircle(
            color = Color.White.copy(alpha = 0.9f),
            radius = 2.5f,
            center = Offset(bx, by)
        )
    }
}

private fun DrawScope.drawVintageCar(cx: Float, cy: Float, lightsOn: Boolean) {
    // Sombra del coche
    drawOval(
        color = Color(0x331A0810),
        topLeft = Offset(cx - 40f, cy + 12f),
        size = Size(80f, 12f)
    )

    // Carrocería roja cereza
    drawRoundRect(
        color = Color(0xFFB8142C),
        topLeft = Offset(cx - 36f, cy - 6f),
        size = Size(72f, 18f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(5f, 5f)
    )

    // Cabina
    drawRoundRect(
        color = Color(0xFF7A0B1B),
        topLeft = Offset(cx - 18f, cy - 16f),
        size = Size(36f, 12f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
    )
    // Ventanita
    drawRoundRect(
        color = Color(0xFFC8E2F5),
        topLeft = Offset(cx - 14f, cy - 14f),
        size = Size(14f, 8f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(2f, 2f)
    )

    // Arbolito navideño verde amarrado arriba
    drawOval(
        color = Color(0xFF226638),
        topLeft = Offset(cx - 20f, cy - 22f),
        size = Size(40f, 9f)
    )

    // Ruedas negras con centro cromado
    for (wx in listOf(cx - 22f, cx + 22f)) {
        drawCircle(Color(0xFF2B282C), radius = 6f, center = Offset(wx, cy + 12f))
        drawCircle(Color(0xFFE8EEF5), radius = 2.5f, center = Offset(wx, cy + 12f))
    }

    // Faros delanteros encendidos proyectando cono de luz
    if (lightsOn) {
        val fx = cx - 36f
        val fy = cy + 2f
        drawCircle(Color(0xFFFFEA88), radius = 4f, center = Offset(fx, fy))
        // Cono de luz hacia adelante
        val cone = Path().apply {
            moveTo(fx, fy)
            lineTo(fx - 65f, fy - 15f)
            lineTo(fx - 65f, fy + 22f)
            close()
        }
        drawPath(
            cone,
            brush = Brush.horizontalGradient(
                listOf(Color(0x88FFE088), Color.Transparent),
                startX = fx,
                endX = fx - 65f
            )
        )
    }
}

private fun DrawScope.drawRataAspirableIcon(rx: Float, ry: Float, animTime: Float) {
    val hop = (sin(animTime * 16f) * 2f).coerceAtLeast(0f)
    val cy = ry - hop

    // Sombra
    drawOval(Color(0x331A0810), topLeft = Offset(rx - 8f, ry + 8f), size = Size(16f, 5f))

    // Cuerpo gris perla
    drawOval(Color(0xFFC8C2CA), topLeft = Offset(rx - 7f, cy - 5f), size = Size(14f, 13f))
    // Orejitas rosadas
    drawCircle(Color(0xFFF5B5C5), radius = 3.5f, center = Offset(rx - 5f, cy - 7f))
    drawCircle(Color(0xFFF5B5C5), radius = 3.5f, center = Offset(rx + 5f, cy - 7f))
    // Ojos
    drawCircle(Color(0xFF2B0A14), radius = 1.2f, center = Offset(rx - 2.5f, cy - 2f))
    drawCircle(Color(0xFF2B0A14), radius = 1.2f, center = Offset(rx + 2.5f, cy - 2f))

    // Bufanda roja carmesí ondeando
    drawRoundRect(
        Color(0xFFC81D31),
        topLeft = Offset(rx - 6f, cy + 1f),
        size = Size(12f, 4f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(2f, 2f)
    )
    drawRect(
        Color(0xFFC81D31),
        topLeft = Offset(rx + 4f, cy + 2f),
        size = Size(5f, 7f)
    )

    // Mini guitarra marrón
    drawOval(Color(0xFFB56525), topLeft = Offset(rx - 10f, cy + 2f), size = Size(6f, 9f))
}

private fun DrawScope.drawCatOnBench(cx: Float, cy: Float, pulse: Float) {
    val tailWiggle = sin(pulse * 2f) * 3f
    // Cuerpo naranja acurrucado
    drawOval(Color(0xFFEB9545), topLeft = Offset(cx - 8f, cy - 5f), size = Size(16f, 10f))
    // Pecho blanco
    drawOval(Color(0xFFFFF8EE), topLeft = Offset(cx - 4f, cy - 2f), size = Size(8f, 6f))
    // Cabeza
    drawCircle(Color(0xFFEB9545), radius = 5f, center = Offset(cx + 6f, cy - 5f))
    // Orejitas
    drawCircle(Color(0xFFB85025), radius = 2f, center = Offset(cx + 4f, cy - 9f))
    drawCircle(Color(0xFFB85025), radius = 2f, center = Offset(cx + 8f, cy - 9f))
    // Cola
    drawLine(
        Color(0xFFEB9545),
        start = Offset(cx - 7f, cy),
        end = Offset(cx - 14f, cy - 4f + tailWiggle),
        strokeWidth = 2.5f
    )
}

private fun DrawScope.drawWalkingCouple(px: Float, py: Float, pulse: Float) {
    val step = sin(pulse * 4f) * 2f
    // Elvis (abrigo azul noche)
    drawRoundRect(
        Color(0xFF223652),
        topLeft = Offset(px - 10f, py - 18f + step),
        size = Size(9f, 16f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(3f, 3f)
    )
    drawCircle(Color(0xFFFFDFD2), radius = 4f, center = Offset(px - 5.5f, py - 22f + step))

    // Francesca (abrigo rojo rubí)
    drawRoundRect(
        Color(0xFFB8142C),
        topLeft = Offset(px, py - 16f - step),
        size = Size(8f, 15f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(3f, 3f)
    )
    drawCircle(Color(0xFFFFDFD2), radius = 3.8f, center = Offset(px + 4f, py - 20f - step))

    // Manos unidas en el centro
    drawCircle(Color(0xFFFFDFD2), radius = 2f, center = Offset(px, py - 8f))

    // Corazoncito rojo flotando sobre ellos
    if (sin(pulse * 2f) > 0.3f) {
        val hy = py - 32f - sin(pulse * 2f) * 4f
        drawCircle(Color(0xFFC81D31), radius = 3f, center = Offset(px - 1.5f, hy))
        drawCircle(Color(0xFFC81D31), radius = 3f, center = Offset(px + 1.5f, hy))
    }
}
