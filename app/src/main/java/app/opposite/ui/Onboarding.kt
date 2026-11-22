package app.opposite.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.opposite.data.Settings
import app.opposite.domain.Season
import app.opposite.domain.SunsetCalculator
import app.opposite.domain.WinState
import app.opposite.ui.art.FacadeLook
import app.opposite.ui.art.HOUSE_H
import app.opposite.ui.art.HOUSE_W
import app.opposite.ui.art.facade
import app.opposite.ui.theme.C
import app.opposite.ui.theme.Sh
import app.opposite.ui.theme.T
import app.opposite.work.Dusk
import app.opposite.work.SmartSunsetNotificationManager
import kotlinx.coroutines.delay
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@SuppressLint("MissingPermission")
fun lastPlace(context: Context): Pair<Double, Double>? = try {
    val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val p = lm.getProviders(true).firstNotNullOfOrNull { lm.getLastKnownLocation(it) }
    p?.let { it.latitude to it.longitude }
} catch (_: Exception) {
    null
}

@Composable
fun Onboarding(settings: Settings, onDone: (Settings) -> Unit) {
    val context = LocalContext.current
    var page by remember { mutableIntStateOf(0) }
    val time by rememberSeconds()

    var lat by remember { mutableStateOf(settings.lat ?: SunsetCalculator.DEFAULT_LAT) }
    var lon by remember { mutableStateOf(settings.lon ?: SunsetCalculator.DEFAULT_LON) }
    var locationSaved by remember { mutableStateOf(settings.lat != null) }
    var notifActive by remember { mutableStateOf(settings.notify) }

    val locate = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            lastPlace(context)?.let { (la, lo) ->
                lat = la
                lon = lo
            }
        }
        locationSaved = true
    }

    val notify = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        notifActive = granted
        if (granted) {
            SmartSunsetNotificationManager.createChannel(context)
        }
    }

    val today = LocalDate.now()
    val adventStart = LocalDate.of(today.year, 12, 1)

    // Live countdown to Dec 1
    var nowSeconds by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            nowSeconds = System.currentTimeMillis()
        }
    }

    val targetDateTime = adventStart.atTime(SunsetCalculator.calculateSunset(adventStart, lat, lon))
    val diffDuration = Duration.between(LocalDateTime.now(), targetDateTime)
    val daysRemaining = diffDuration.toDays().coerceAtLeast(0)
    val hoursRemaining = (diffDuration.toHours() % 24).coerceAtLeast(0)
    val minsRemaining = (diffDuration.toMinutes() % 60).coerceAtLeast(0)
    val secsRemaining = (diffDuration.seconds % 60).coerceAtLeast(0)

    val sunsetToday = SunsetCalculator.calculateSunset(today, lat, lon).format(DateTimeFormatter.ofPattern("HH:mm"))

    Box(Modifier.fillMaxSize().background(C.bg)) {
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).navigationBarsPadding()) {
            Box {
                Picture(HOUSE_W, HOUSE_H) {
                    facade(FacadeLook(dusk = true, state = { WinState.FUTURE }, t = time, allLit = (page == 0)))
                }
                Snow(Modifier.matchParentSize())
            }

            Column(Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
                // Page 0: Bienvenida Emocional
                AnimatedVisibility(page == 0, enter = fadeIn() + slideInVertically()) {
                    Column {
                        Text("Nuestra Casa de Diciembre", style = T.Title.copy(fontSize = 26.sp, color = C.ink))
                        Spacer(Modifier.height(10.dp))
                        Text(
                            "Para ti, Mio Principessa. Al otro lado de esta pantalla hay 24 ventanas esperando la caída del sol. Cada atardecer de diciembre, una luz roja y dorada se encenderá para ti: detrás guarda una historia, una pista secreta, un juego o un pedacito de nosotros.",
                            style = T.Body.copy(color = C.dim, fontSize = 15.sp, lineHeight = 23.sp),
                        )
                        Spacer(Modifier.height(20.dp))
                        WarmButton("Entrar a nuestro adviento", Modifier.fillMaxWidth()) { page = 1 }
                    }
                }

                // Page 1: Cómo funciona (4 Tarjetas Interactivas)
                AnimatedVisibility(page == 1, enter = fadeIn() + slideInVertically()) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Cómo funciona", style = T.Title.copy(fontSize = 22.sp, color = C.ink))
                        Text("Un refugio diario diseñado solo para ti", style = T.Muted.copy(color = C.dim, fontSize = 13.sp))

                        InfoCard("🌅", "Una ventana cada atardecer", "Cuando el sol se esconda en tu cielo, la ventana del día cobrará vida automáticamente con una luz cálida.")
                        InfoCard("🎨", "Habitación viva y mini-juegos", "Toca los objetos de cada cuarto: podrás colorear recuerdos, raspar sorpresas, escuchar melodías y hallar pistas.")
                        InfoCard("🕵️‍♀️", "El misterio del admirador secreto", "Alguien deja detalles en las puertas cada noche. Reúne las 23 pistas en tu cuaderno y descubre quién es el día 24.")
                        InfoCard("🎄", "Tu árbol y velas de rescate", "Cada ventana cuelga un adorno en tu árbol. Si algún día llegas tarde, las velas dominicales rescatan tu racha sin perder nada.")

                        Spacer(Modifier.height(10.dp))
                        WarmButton("Configurar el inicio", Modifier.fillMaxWidth()) { page = 2 }
                    }
                }

                // Page 2: ¿Cuándo empezamos? (Solo 1 de Diciembre + Ubicación + Notificaciones)
                AnimatedVisibility(page == 2, enter = fadeIn() + slideInVertically()) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("¿Cuándo empezamos?", style = T.Title.copy(fontSize = 22.sp, color = C.ink))

                        // Bloque único de inicio (1 de Diciembre)
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .clip(Sh.Md)
                                .background(C.card)
                                .border(2.dp, C.red, Sh.Md)
                                .padding(16.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(Modifier.size(10.dp).clip(CircleShape).background(C.warm))
                                    Spacer(Modifier.width(8.dp))
                                    Text("1 de Diciembre (Adviento Oficial)", style = T.Name.copy(color = C.ink, fontSize = 16.sp, fontWeight = FontWeight.Bold))
                                }
                                Spacer(Modifier.height(8.dp))

                                if (today.isBefore(adventStart)) {
                                    Text(
                                        "Faltan $daysRemaining días, ${String.format("%02d:%02d:%02d", hoursRemaining, minsRemaining, secsRemaining)} para que se encienda la primera ventana, Principessa.",
                                        style = T.Hand.copy(color = C.warm, fontSize = 19.sp)
                                    )
                                    Text("El tiempo perfecto para que todo esté listo para ti.", style = T.Muted.copy(color = C.dim, fontSize = 12.sp), modifier = Modifier.padding(top = 4.dp))
                                } else {
                                    Text("¡El adviento ya comenzó! Tu ventana de hoy te espera al atardecer.", style = T.Hand.copy(color = C.warm, fontSize = 19.sp))
                                }
                            }
                        }

                        // Botón 1: Ubicación Real
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .clip(Sh.Md)
                                .background(C.bg2)
                                .border(1.dp, C.line, Sh.Md)
                                .padding(14.dp)
                        ) {
                            Column {
                                Text(
                                    if (locationSaved) "Ubicación guardada ✓ Hoy el atardecer en tu cielo es a las $sunsetToday"
                                    else "El atardecer se calcula para Ica, Perú (18:18 - 18:35 en diciembre). Puedes calibrarlo con tu ubicación exacta.",
                                    style = T.Body.copy(fontSize = 13.sp, color = C.ink)
                                )
                                Spacer(Modifier.height(8.dp))
                                GhostButton(if (locationSaved) "Actualizar ubicación" else "Usar mi ubicación para el atardecer") {
                                    locate.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
                                }
                            }
                        }

                        // Botón 2: Notificaciones Inteligentes
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .clip(Sh.Md)
                                .background(C.bg2)
                                .border(1.dp, C.line, Sh.Md)
                                .padding(14.dp)
                        ) {
                            Column {
                                Text(
                                    if (notifActive) "Notificaciones al atardecer activadas ✓ Te avisaré cada noche cuando se encienda tu ventana, Luna."
                                    else "Recibe un aviso cálido cada noche cuando caiga el sol y se ilumine tu habitación.",
                                    style = T.Body.copy(fontSize = 13.sp, color = C.ink)
                                )
                                Spacer(Modifier.height(8.dp))
                                GhostButton(if (notifActive) "Notificaciones activadas ✓" else "Avisarme al atardecer") {
                                    if (Build.VERSION.SDK_INT >= 33) {
                                        notify.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    } else {
                                        notifActive = true
                                        SmartSunsetNotificationManager.createChannel(context)
                                    }
                                }
                            }
                        }

                        Spacer(Modifier.height(10.dp))
                        WarmButton("Comenzar nuestra historia", Modifier.fillMaxWidth()) {
                            val startSeason = Season.new(adventStart)
                            onDone(
                                settings.copy(
                                    onboarded = true,
                                    lat = lat,
                                    lon = lon,
                                    notify = notifActive,
                                    season = startSeason,
                                )
                            )
                            Dusk.schedule(context)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoCard(icon: String, title: String, desc: String) {
    Box(
        Modifier
            .fillMaxWidth()
            .clip(Sh.Md)
            .background(C.card)
            .border(1.dp, C.line, Sh.Md)
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Text(icon, fontSize = 26.sp, modifier = Modifier.padding(end = 12.dp, top = 2.dp))
            Column {
                Text(title, style = T.Name.copy(color = C.ink, fontSize = 16.sp, fontWeight = FontWeight.Bold))
                Spacer(Modifier.height(3.dp))
                Text(desc, style = T.Body.copy(color = C.dim, fontSize = 13.sp, lineHeight = 19.sp))
            }
        }
    }
}
