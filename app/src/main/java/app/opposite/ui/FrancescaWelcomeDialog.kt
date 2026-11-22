package app.opposite.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import app.opposite.ui.theme.C
import app.opposite.ui.theme.Sh
import app.opposite.ui.theme.T

data class TourStep(
    val icon: String,
    val title: String,
    val description: String,
    val tip: String,
)

/**
 * Romantic and guided interactive welcome tour for Francesca.
 */
@Composable
fun FrancescaWelcomeDialog(onDismiss: () -> Unit) {
    val steps = remember {
        listOf(
            TourStep(
                icon = "✨",
                title = "Bienvenida a Nuestro Refugio",
                description = "Hola mi Principessa hermosa. Creé este espacio privado exclusivamente para ti, para celebrar nuestra nueva etapa y acompañarte cada día con detalles hechos con todo mi corazón.",
                tip = "Un regalo sincero de tu Rata Aspirable 🐀❤️",
            ),
            TourStep(
                icon = "🏠",
                title = "1. La Casa de Adviento",
                description = "Una fachada invernal con 24 ventanas. Cada tarde a las 6:00 PM se encenderá una nueva ventana. Complétala encendiendo su luz para descubrir su historia y sorpresa diaria.",
                tip = "Toca cada ventana encendida para entrar a su habitación.",
            ),
            TourStep(
                icon = "🔍",
                title = "2. El Cuaderno del Admirador Secreto",
                description = "Un misterio en el edificio: alguien te deja notas, frambuesas y melodías. En cada ventana encontrarás una pista nueva. En tu cuaderno irás anotando todo para desenmascarar al culpable.",
                tip = "¿Quién será ese admirador secreto que no deja de pensar en ti? ✎",
            ),
            TourStep(
                icon = "🎧",
                title = "3. El Refugio Musical",
                description = "Nuestras 13 canciones y melodías organizadas en playlists (Dulce, Amor, Navideña y Pasión) con reproductor offline para que las escuches donde quieras sin gastar tus datos.",
                tip = "La música ambiental se silencia sola cuando le das play a un tema.",
            ),
            TourStep(
                icon = "💌",
                title = "4. La Galería de Cartas",
                description = "Cartas íntimas con sello de lacre rojo carmesí. Algunas están protegidas por acertijos cómplices que solo tú puedes descifrar para romper el sello y leerlas.",
                tip = "Puedes reaccionar a cada carta para dejarme saber qué sentiste 🫂",
            ),
            TourStep(
                icon = "📖",
                title = "5. Nuestro Cuaderno Compartido",
                description = "Un bloc de notas tipo Notion al final de tu Cuaderno: un espacio para nuestras metas, lista de películas para ver abrazados, recetas caseras y recuerdos.",
                tip = "Puedes marcar tareas completadas y crear nuevas notas juntas.",
            ),
        )
    }

    var currentStepIndex by remember { mutableIntStateOf(0) }
    val step = steps[currentStepIndex]
    val isLast = currentStepIndex == steps.size - 1

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .background(Color(0xDD0B0306))
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF2E0C17), Color(0xFF19060C), Color(0xFF100307))
                        )
                    )
                    .border(1.5.dp, C.warm, RoundedCornerShape(24.dp))
                    .padding(24.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Header Skip button
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "PASO ${currentStepIndex + 1} DE ${steps.size}",
                            style = T.Eyebrow.copy(color = C.raspberry, fontSize = 11.sp, letterSpacing = 1.sp)
                        )
                        Text(
                            "Omitir tour",
                            style = T.Body.copy(color = C.paperMuted, fontSize = 12.sp),
                            modifier = Modifier
                                .clip(Sh.Sm)
                                .clickable { onDismiss() }
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(Modifier.height(18.dp))

                    // Step Icon Circle
                    Box(
                        Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF421524))
                            .border(1.5.dp, C.warm, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(step.icon, fontSize = 38.sp)
                    }

                    Spacer(Modifier.height(16.dp))

                    Text(
                        step.title,
                        style = T.Title.copy(color = C.warm, fontSize = 20.sp, textAlign = TextAlign.Center),
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(10.dp))

                    Text(
                        step.description,
                        style = T.Body.copy(color = C.ink, fontSize = 14.sp, lineHeight = 21.sp, textAlign = TextAlign.Center),
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(14.dp))

                    // Tip box
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .clip(Sh.Sm)
                            .background(Color(0xFF1F0911))
                            .border(1.dp, Color(0x33F7EFE2), Sh.Sm)
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            "💡 ${step.tip}",
                            style = T.Hand.copy(color = C.warm, fontSize = 13.sp, textAlign = TextAlign.Center),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(Modifier.height(20.dp))

                    // Progress indicator dots
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for (i in steps.indices) {
                            val active = i == currentStepIndex
                            Box(
                                Modifier
                                    .size(if (active) 10.dp else 6.dp)
                                    .clip(CircleShape)
                                    .background(if (active) C.warm else C.dim)
                            )
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    // Navigation buttons
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (currentStepIndex > 0) {
                            GhostButton("← Atrás", modifier = Modifier.weight(1f)) {
                                currentStepIndex--
                            }
                        }
                        WarmButton(
                            if (isLast) "¡Comenzar nuestro viaje! ❤️" else "Siguiente →",
                            modifier = Modifier.weight(1f)
                        ) {
                            if (isLast) {
                                onDismiss()
                            } else {
                                currentStepIndex++
                            }
                        }
                    }
                }
            }
        }
    }
}
