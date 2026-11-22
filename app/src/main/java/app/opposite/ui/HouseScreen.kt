package app.opposite.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.opposite.data.Settings
import app.opposite.domain.AdventData
import app.opposite.domain.CoupleStory
import app.opposite.domain.Evening
import app.opposite.domain.Season
import app.opposite.domain.WinState
import app.opposite.ui.art.FacadeLook
import app.opposite.ui.art.HOUSE_H
import app.opposite.ui.art.HOUSE_W
import app.opposite.ui.art.SceneryTarget
import app.opposite.ui.art.facade
import app.opposite.ui.art.facadeHit
import app.opposite.ui.art.sceneryHit
import app.opposite.ui.theme.C
import app.opposite.ui.theme.Sh
import app.opposite.ui.theme.T
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun dateText(d: LocalDate): String = if (LocalRu.current) d.format(DateTimeFormatter.ofPattern("d MMMM", Locale.forLanguageTag("ru"))) else d.format(DateTimeFormatter.ofPattern("d 'de' MMMM", Locale.forLanguageTag("es")))

fun clock(ms: Long): String = Instant.ofEpochMilli(ms).atZone(ZoneId.systemDefault()).toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"))

fun plural(n: Int, one: String, few: String, many: String): String {
    val a = n % 100; val b = n % 10
    return when { a in 11..19 -> many; b == 1 -> one; b in 2..4 -> few; else -> many }
}

@Composable
fun Header(evening: Evening, settings: Settings, acts: Acts) {
    val context = LocalContext.current
    val s = evening.season
    val time by rememberSeconds()
    val liveDuration = remember(time) { CoupleStory.calculateDuration() }

    val musicOn = SoundManager.isPlaying()

    Row(
        Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(start = 16.dp, end = 8.dp, top = 12.dp, bottom = 6.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        Column(Modifier.weight(1f)) {
            Text("Nuestro Refugio", style = T.Title.copy(color = C.ink, fontSize = 22.sp))
            Text(
                liveDuration.formattedSummary,
                style = T.Eyebrow.copy(color = C.raspberry, fontSize = 11.sp, letterSpacing = 0.05.sp, fontWeight = FontWeight.Bold),
            )
            Text(
                liveDuration.subtitle,
                style = T.Muted.copy(color = C.dim, fontSize = 10.sp),
            )
        }

        // Music Toggle
        Text(
            if (musicOn) "🎵" else "🔇",
            style = T.Title.copy(fontSize = 18.sp),
            modifier = Modifier
                .clip(Sh.Sm)
                .clickable {
                    val active = SoundManager.toggle(context)
                    acts.store.toggleMusic()
                    acts.toast(if (active) "Música suave de fondo activada 🍃🎶" else "Música silenciada")
                }
                .padding(6.dp)
        )

        Spacer(Modifier.width(4.dp))
        Text("${s.litCount} de ${Season.DAYS}", style = T.Count.copy(color = C.warm), modifier = Modifier.padding(end = 4.dp))
        Text("⚙", style = T.Title.copy(color = C.dim), modifier = Modifier.clip(Sh.Sm).clickable { acts.open(Overlay.Settings) }.padding(6.dp))
    }
}

@Composable
fun HouseScreen(evening: Evening, settings: Settings, acts: Acts, igniting: Int, ignite: Float) {
    val time by rememberSeconds()
    val s = evening.season
    val ru = LocalRu.current
    val numbers = androidx.compose.ui.text.rememberTextMeasurer()

    var moonTaps by remember { mutableIntStateOf(0) }
    var snowmanToggle by remember { mutableStateOf(false) }
    var showCreatorMirror by remember { mutableStateOf(false) }

    Column(Modifier.verticalScroll(rememberScrollState())) {
        Header(evening, settings, acts)

        Box {
            Picture(
                HOUSE_W,
                HOUSE_H,
                onTap = { x, y ->
                    val hitWindow = facadeHit(x, y)
                    if (hitWindow != null) {
                        tapWindow(hitWindow, evening, acts, ru)
                    } else {
                        when (sceneryHit(x, y)) {
                            SceneryTarget.MOON -> {
                                moonTaps++
                                if (moonTaps >= 7) {
                                    moonTaps = 0
                                    showCreatorMirror = true
                                } else {
                                    acts.toast("Tú eres mi Luna favorita, Principessa 🌙")
                                }
                            }
                            SceneryTarget.STARS -> {
                                acts.toast("Un deseo bajo las estrellas para nosotros ✨")
                            }
                            SceneryTarget.SNOWMAN -> {
                                snowmanToggle = !snowmanToggle
                                acts.toast(if (snowmanToggle) "Modo rata activado 🐀✨" else "El Sol manda saludos ☀️")
                            }
                            SceneryTarget.STREETLIGHT -> {
                                acts.toast("Luz dorada para alumbrar tu camino, mi niña 💡")
                            }
                            SceneryTarget.DOOR -> {
                                acts.toast("Toc, toc... Solo se abre una ventana por noche, Mio Principessa 🚪❤️")
                            }
                            null -> {}
                        }
                    }
                }
            ) {
                facade(
                    FacadeLook(
                        dusk = evening.dusk,
                        state = evening::state,
                        hasGift = { s.gifts.containsKey(it) },
                        t = time,
                        igniting = igniting,
                        ignite = ignite,
                        numbers = numbers,
                    )
                )
            }
            Snow(Modifier.matchParentSize())
        }

        // Preview Mode indicator if active
        if (settings.previewMode) {
            Box(
                Modifier
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .fillMaxWidth()
                    .clip(Sh.Sm)
                    .background(C.red.copy(alpha = 0.2f))
                    .border(1.dp, C.red, Sh.Sm)
                    .padding(8.dp)
            ) {
                Text(
                    "🔧 MODO PREVIEW ACTIVO: Puedes abrir y probar cualquier ventana y minijuego sin esperar a diciembre.",
                    style = T.Eyebrow.copy(color = C.warm, fontSize = 11.sp),
                )
            }
        }

        TodayCard(evening, acts)

        // Vouchers Button
        VouchersBanner(acts)

        // Bonus Chest on Dec 27 (or in preview mode)
        val isDec27 = (evening.today.monthValue == 12 && evening.today.dayOfMonth >= 27) || settings.previewMode
        if (isDec27) {
            AnniversaryBanner(acts)
        }

        Spacer(Modifier.height(24.dp))
    }

    if (showCreatorMirror) {
        CreatorMirrorDialog(
            settings = settings,
            acts = acts,
            onDismiss = { showCreatorMirror = false }
        )
    }
}

@Composable
private fun VouchersBanner(acts: Acts) {
    Box(
        Modifier
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .fillMaxWidth()
            .clip(Sh.Lg)
            .background(C.card)
            .border(1.dp, C.line, Sh.Lg)
            .clickable { acts.open(Overlay.Vouchers) }
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("🎁", fontSize = 28.sp, modifier = Modifier.padding(end = 12.dp))
            Column(Modifier.weight(1f)) {
                Text("Mis Vales y Sorpresas", style = T.Name.copy(color = C.ink, fontSize = 16.sp, fontWeight = FontWeight.Bold))
                Text("Cupones de tiempo juntos, cartas y detalles caseros", style = T.Muted.copy(color = C.dim, fontSize = 12.sp))
            }
            Text("›", style = T.Title.copy(color = C.dim, fontSize = 20.sp))
        }
    }
}

@Composable
private fun AnniversaryBanner(acts: Acts) {
    Box(
        Modifier
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .fillMaxWidth()
            .clip(Sh.Lg)
            .background(Brush.linearGradient(listOf(Color(0xFF4A1F2D), Color(0xFF241118))))
            .border(1.5.dp, C.warm, Sh.Lg)
            .clickable { acts.open(Overlay.Minigame(25)) }
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("👑", fontSize = 32.sp, modifier = Modifier.padding(end = 12.dp))
            Column(Modifier.weight(1f)) {
                Text("Nuestro Nuevo Comienzo · Carta Especial", style = T.Name.copy(color = C.warm, fontSize = 16.sp, fontWeight = FontWeight.Bold))
                Text("Segunda oportunidad desde el 06/11 · Toca para abrir ❤️", style = T.Body.copy(color = C.ink, fontSize = 12.sp))
            }
        }
    }
}

private fun tapWindow(d: Int, evening: Evening, acts: Acts, ru: Boolean) {
    val s = evening.season
    if (s.over && !s.lit.containsKey(d)) return

    when (evening.state(d)) {
        WinState.LIT -> acts.open(Overlay.Room(d))
        WinState.FUTURE -> {
            val n = d - evening.day
            acts.toast(
                if (evening.beforeStart && d == 1) "La primera ventana se encenderá el 1 de diciembre al caer el atardecer."
                else "La ventana $d se abrirá en $n ${if (n == 1) "noche" else "noches"}, Principessa."
            )
        }
        WinState.WAITING_DUSK -> acts.toast("Aún hay luz en Ica. La ventana se encenderá tras el atardecer, a las ${clock(evening.sunset(evening.today))}.")
        WinState.TODAY -> acts.sheet(Sheet.Task(d))
        WinState.MISSED -> acts.sheet(Sheet.Missed(d))
    }
}

@Composable
private fun TodayCard(evening: Evening, acts: Acts) {
    val s = evening.season
    val d = if (evening.previewMode && evening.day < 1) 1 else evening.day
    val box = Modifier
        .padding(horizontal = 16.dp, vertical = 8.dp)
        .fillMaxWidth()
        .clip(Sh.Lg)
        .background(Brush.linearGradient(listOf(C.card, C.bg2)))
        .border(1.dp, C.line, Sh.Lg)
        .padding(16.dp)

    Column(box) {
        when {
            s.over -> {
                Eyebrow("Misterio Resuelto")
                Text("¡Descubriste a tu admirador secreto! El Músico de Medianoche (Elvis) siempre pensó en ti.", style = T.Body, modifier = Modifier.padding(top = 6.dp))
                Spacer(Modifier.height(10.dp))
                WarmButton("Ver desenlace de nuevo") { acts.open(Overlay.Finale) }
            }
            evening.beforeStart -> {
                val n = 1 - d
                Eyebrow("Falta poco para iniciar")
                Text("$n días para encender la primera ventana. El edificio espera por ti con velitas listas.", style = T.Body, modifier = Modifier.padding(top = 6.dp))
            }
            d > Season.DAYS -> {
                Eyebrow("Nochebuena cumplida")
                Text("Todas las ventanas están encendidas. Es hora de hacer tu acusación final en el cuaderno.", style = T.Body, modifier = Modifier.padding(top = 6.dp))
            }
            s.lit.containsKey(d) -> {
                Eyebrow("Esta noche")
                val resident = AdventData.get(d)
                Text("La ventana $d está encendida. La historia de hoy te espera.", style = T.Body, modifier = Modifier.padding(top = 6.dp))
                Spacer(Modifier.height(10.dp))
                WarmButton("Entrar a la habitación $d") { acts.open(Overlay.Room(d)) }
            }
            else -> {
                val state = evening.state(d)
                if (state == WinState.WAITING_DUSK) {
                    Eyebrow("Atardeciendo en Ica")
                    Text("La ventana $d cobrará vida en cuanto caiga el sol, a las ${clock(evening.sunset(evening.today))}.", style = T.Body, modifier = Modifier.padding(top = 6.dp))
                } else {
                    Eyebrow("Ventana de hoy lista")
                    Text("Hay una chispa y una nueva historia esperando por ti detrás de la ventana $d.", style = T.Body, modifier = Modifier.padding(top = 6.dp))
                    Spacer(Modifier.height(10.dp))
                    WarmButton("Abrir ventana $d") { acts.sheet(Sheet.Task(d)) }
                }
            }
        }
    }
}
