package app.opposite.ui

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.opposite.data.Settings
import app.opposite.sync.GitHubSyncRepository
import app.opposite.ui.theme.C
import app.opposite.ui.theme.Sh
import app.opposite.ui.theme.T

@Composable
fun CreatorMirrorDialog(
    settings: Settings,
    acts: Acts,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    var pinEntered by remember { mutableStateOf("") }
    var isAuthenticated by remember { mutableStateOf(settings.previewMode) }
    var pinError by remember { mutableStateOf(false) }

    val progress by GitHubSyncRepository.progress.collectAsState()

    Box(
        Modifier
            .fillMaxSize()
            .background(Color(0xE610050A))
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            Modifier
                .padding(20.dp)
                .fillMaxWidth()
                .shadow(24.dp, Sh.Lg)
                .clip(Sh.Lg)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF2E0E19),
                            Color(0xFF1B080F),
                            Color(0xFF12050A),
                        )
                    )
                )
                .border(2.dp, C.warm, Sh.Lg)
                .clickable(enabled = false) {}
                .padding(20.dp),
        ) {
            if (!isAuthenticated) {
                // PIN Verification Screen
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("🌙", fontSize = 42.sp)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Panel Espejo del Creador",
                        style = T.Title.copy(color = C.warm, fontSize = 20.sp, fontWeight = FontWeight.Bold),
                    )
                    Text(
                        "Ingresa el PIN de Elvis (0611 o 2703)",
                        style = T.Muted.copy(color = C.dim, fontSize = 12.sp),
                        modifier = Modifier.padding(top = 4.dp, bottom = 16.dp),
                    )

                    OutlinedTextField(
                        value = pinEntered,
                        onValueChange = {
                            pinEntered = it.take(4)
                            pinError = false
                            if (pinEntered == "0611" || pinEntered == "2703") {
                                isAuthenticated = true
                            }
                        },
                        label = { Text("PIN Secreto", color = C.dim) },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        isError = pinError,
                        modifier = Modifier.fillMaxWidth(0.7f),
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = C.ink,
                            unfocusedTextColor = C.ink,
                            focusedContainerColor = Color(0xFF220A13),
                            unfocusedContainerColor = Color(0xFF220A13),
                            focusedIndicatorColor = C.warm,
                            unfocusedIndicatorColor = C.line,
                        ),
                    )

                    Spacer(Modifier.height(16.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        WarmButton("Verificar") {
                            if (pinEntered == "0611" || pinEntered == "2703") {
                                isAuthenticated = true
                            } else {
                                pinError = true
                                acts.toast("PIN incorrecto")
                            }
                        }
                        GhostButton("Cancelar") { onDismiss() }
                    }
                }
            } else {
                // Authenticated Creator & Mirror Dashboard
                Column(
                    Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                ) {
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Column {
                            Text(
                                "👑 Panel Espejo de Elvis",
                                style = T.Title.copy(color = C.warm, fontSize = 18.sp, fontWeight = FontWeight.Bold),
                            )
                            Text(
                                "Progreso de Francesca sincronizado",
                                style = T.Eyebrow.copy(color = C.raspberry, fontSize = 11.sp),
                            )
                        }
                        Text(
                            "✕",
                            style = T.Title.copy(color = C.dim, fontSize = 20.sp),
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable(onClick = onDismiss)
                                .padding(4.dp),
                        )
                    }

                    Spacer(Modifier.height(14.dp))

                    // Preview Mode Switch for Elvis
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .clip(Sh.Md)
                            .background(Color(0xFF3B1522))
                            .border(1.dp, C.red, Sh.Md)
                            .padding(12.dp),
                    ) {
                        Row(
                            Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text(
                                    "Modo Creador / Preview",
                                    style = T.Name.copy(color = C.ink, fontSize = 14.sp, fontWeight = FontWeight.Bold),
                                )
                                Text(
                                    "Desbloquea las 24 ventanas para que puedas probar todo sin alterar las fechas de Francesca.",
                                    style = T.Muted.copy(color = C.dim, fontSize = 11.sp),
                                )
                            }
                            Switch(
                                checked = settings.previewMode,
                                onCheckedChange = {
                                    acts.store.togglePreviewMode()
                                    acts.toast(if (!settings.previewMode) "Modo Preview ACTIVADO" else "Modo Preview DESACTIVADO")
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = C.warm,
                                    checkedTrackColor = C.red,
                                ),
                            )
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    // Solar Atmosphere & Time Simulator
                    val simHour by app.opposite.data.location.LocationTimeProvider.simulatedHour.collectAsState()
                    val atmosphere by app.opposite.data.location.LocationTimeProvider.currentAtmosphere.collectAsState()

                    Box(
                        Modifier
                            .fillMaxWidth()
                            .clip(Sh.Md)
                            .background(Color(0xFF2B0E1B))
                            .border(1.dp, Color(0xFFFFD700).copy(alpha = 0.5f), Sh.Md)
                            .padding(12.dp),
                    ) {
                        Column {
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "🌤️ Simulación Solar en Vivo",
                                    style = T.Name.copy(color = Color(0xFFFFD700), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    if (simHour != null) {
                                        val h = simHour!!.toInt()
                                        val m = ((simHour!! - h) * 60).toInt()
                                        String.format("%02d:%02d (Simulada)", h, m)
                                    } else "Hora Real (Perú)",
                                    style = T.Eyebrow.copy(color = Color(0xFFFFCAD4), fontSize = 11.sp)
                                )
                            }

                            Text(
                                "Fase actual: ${atmosphere.phase.name} · Sol: ${(atmosphere.sunProgress * 100).toInt()}% · Nieve/Luces activas",
                                style = T.Muted.copy(color = Color(0xFFB5838D), fontSize = 10.sp),
                                modifier = Modifier.padding(top = 2.dp, bottom = 6.dp)
                            )

                            // Slider 00:00 - 23:59
                            var sliderVal by remember(simHour) {
                                mutableStateOf<Float>(simHour ?: 18.25f)
                            }
                            androidx.compose.material3.Slider(
                                value = sliderVal,
                                onValueChange = {
                                    sliderVal = it
                                    app.opposite.data.location.LocationTimeProvider.setSimulatedHour(it)
                                },
                                valueRange = 0f..23.99f,
                                modifier = Modifier.fillMaxWidth(),
                                colors = androidx.compose.material3.SliderDefaults.colors(
                                    thumbColor = Color(0xFFFFD700),
                                    activeTrackColor = Color(0xFFC81D31),
                                    inactiveTrackColor = Color(0xFF4A1828)
                                )
                            )

                            // Quick phase buttons
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "🌅 Alba",
                                    fontSize = 11.sp,
                                    color = Color(0xFFFFD700),
                                    modifier = Modifier.clickable {
                                        app.opposite.data.location.LocationTimeProvider.setQuickPhase(app.opposite.domain.astronomy.SolarPhase.SUNRISE)
                                        acts.toast("Amanecer en Ica 🌅")
                                    }
                                )
                                Text(
                                    "☀️ Día",
                                    fontSize = 11.sp,
                                    color = Color(0xFFFFD700),
                                    modifier = Modifier.clickable {
                                        app.opposite.data.location.LocationTimeProvider.setQuickPhase(app.opposite.domain.astronomy.SolarPhase.MORNING)
                                        acts.toast("Mañana luminosa ☀️")
                                    }
                                )
                                Text(
                                    "🌤️ Tarde",
                                    fontSize = 11.sp,
                                    color = Color(0xFFFFD700),
                                    modifier = Modifier.clickable {
                                        app.opposite.data.location.LocationTimeProvider.setQuickPhase(app.opposite.domain.astronomy.SolarPhase.MIDDAY_AFTERNOON)
                                        acts.toast("Tarde cálida 🌤️")
                                    }
                                )
                                Text(
                                    "🌇 Carmesí",
                                    fontSize = 11.sp,
                                    color = Color(0xFFFF85A1),
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.clickable {
                                        app.opposite.data.location.LocationTimeProvider.setQuickPhase(app.opposite.domain.astronomy.SolarPhase.GOLDEN_SUNSET)
                                        acts.toast("Atardecer Carmesí & Dorado 🌇❤️")
                                    }
                                )
                                Text(
                                    "🌙 Noche",
                                    fontSize = 11.sp,
                                    color = Color(0xFFC8E2F5),
                                    modifier = Modifier.clickable {
                                        app.opposite.data.location.LocationTimeProvider.setQuickPhase(app.opposite.domain.astronomy.SolarPhase.DEEP_NIGHT)
                                        acts.toast("Noche profunda estrellada 🌙")
                                    }
                                )
                                Text(
                                    "⏱️ Real",
                                    fontSize = 11.sp,
                                    color = Color(0xFFFFFFFF),
                                    modifier = Modifier.clickable {
                                        app.opposite.data.location.LocationTimeProvider.resetToRealTime()
                                        acts.toast("Sincronizado con Hora Real")
                                    }
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    // Francesca's Progress Section
                    Text(
                        "ESTADO EN VIVO DE PRINCIPESSA",
                        style = T.Eyebrow.copy(color = C.warm, fontSize = 11.sp, letterSpacing = 1.sp),
                    )
                    Spacer(Modifier.height(6.dp))

                    ProgressInfoCard(
                        title = "Ventanas Abiertas",
                        value = if (progress.openedWindows.isEmpty()) "Aún no ha abierto ventanas" else progress.openedWindows.sorted().joinToString(", ") { "#$it" },
                    )
                    ProgressInfoCard(
                        title = "Vales Canjeados",
                        value = if (progress.redeemedVouchers.isEmpty()) "Ninguno canjeado aún" else progress.redeemedVouchers.joinToString("\n"),
                    )
                    ProgressInfoCard(
                        title = "Sospechoso que tiene en mente",
                        value = progress.currentSuspect ?: "Aún no ha señalado a nadie",
                    )
                    ProgressInfoCard(
                        title = "Reacciones a Cartas",
                        value = if (progress.reactions.isEmpty()) "Sin reacciones registradas" else progress.reactions.entries.joinToString("\n") { "• ${it.key}: ${it.value}" },
                    )

                    Spacer(Modifier.height(14.dp))

                    // Sync action
                    WarmButton("Sincronizar ahora con GitHub", Modifier.fillMaxWidth()) {
                        GitHubSyncRepository.syncFromGitHub(context)
                        acts.toast("Sincronizando progreso en tiempo real...")
                    }

                    Spacer(Modifier.height(10.dp))

                    // Update Checker Action
                    WarmButton("🔍 Comprobar Actualización de App", Modifier.fillMaxWidth()) {
                        acts.toast("Consultando GitHub...")
                        app.opposite.sync.AppUpdateChecker.checkForUpdates(context, notifySystem = true) { hasUpdate, msg ->
                            acts.toast(msg)
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    // Reset for Francesca Action
                    GhostButton("🧹 Reiniciar Progreso de Fábrica (Listo para Francesca)", Modifier.fillMaxWidth()) {
                        acts.store.resetAllProgressForFrancesca()
                        acts.toast("¡Progreso reiniciado a 0! Listo para Francesca ❤️")
                    }

                    Spacer(Modifier.height(8.dp))

                    GhostButton("📖 Abrir Nuestro Cuaderno Compartido", Modifier.fillMaxWidth()) {
                        onDismiss()
                        acts.open(Overlay.OurNotebook)
                    }

                    Spacer(Modifier.height(10.dp))

                    Text(
                        "Versión: v${app.opposite.sync.AppUpdateChecker.getCurrentVersionName(context)} (${app.opposite.sync.AppUpdateChecker.getCurrentVersionCode(context)}) · GitHub Sync Activo",
                        style = T.Eyebrow.copy(color = Color(0xFF888888), fontSize = 10.sp),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ProgressInfoCard(title: String, value: String) {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(Sh.Sm)
            .background(Color(0xFF220A13))
            .border(1.dp, C.line, Sh.Sm)
            .padding(10.dp),
    ) {
        Column {
            Text(title, style = T.Eyebrow.copy(color = C.raspberry, fontSize = 10.sp))
            Text(value, style = T.Body.copy(color = C.ink, fontSize = 12.sp), modifier = Modifier.padding(top = 2.dp))
        }
    }
}
