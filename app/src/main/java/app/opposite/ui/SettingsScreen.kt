package app.opposite.ui

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import app.opposite.BuildConfig
import app.opposite.data.Settings
import app.opposite.domain.Evening
import app.opposite.domain.Season
import app.opposite.ui.theme.C
import app.opposite.ui.theme.Sh
import app.opposite.ui.theme.T
import app.opposite.work.Dusk

/** The StillCraft bot: deep links file a bug or an idea for this app, or follow its releases. */
object Community {
    private const val BOT = "https://t.me/ArcaneWhisperBot?start="
    const val SLUG = "opposite"
    const val BUG = "${BOT}bug_$SLUG"
    const val IDEA = "${BOT}wish_$SLUG"
    const val FOLLOW = "${BOT}f_$SLUG"
    const val CHAT = "https://t.me/+Q4gFEDk8O842OGVi"
}

@Composable
fun SettingsScreen(settings: Settings, evening: Evening, acts: Acts) {
    val context = LocalContext.current
    var restart by remember { mutableStateOf(false) }
    var showCreatorMirror by remember { mutableStateOf(false) }
    val locate = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { ok ->
        if (ok) lastPlace(context)?.let { (la, lo) -> acts.settings { it.copy(lat = la, lon = lo) } }
    }
    val notify = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { ok -> acts.settings { it.copy(notify = ok) }; Dusk.schedule(context) }
    Column(Modifier.fillMaxSize().background(C.bg).verticalScroll(rememberScrollState()).statusBarsPadding().navigationBarsPadding()) {
        Row(Modifier.padding(horizontal = 8.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("←", style = T.Title, modifier = Modifier.clip(Sh.Sm).clickable { acts.open(null) }.padding(horizontal = 10.dp, vertical = 4.dp))
            Text(t("Настройки", "Ajustes"), style = T.Name)
        }
        Eyebrow(t("Язык", "Idioma"), Modifier.padding(start = 18.dp, top = 4.dp))
        val lang = androidx.appcompat.app.AppCompatDelegate.getApplicationLocales().toLanguageTags().substringBefore(',').substringBefore('-')
        Row(Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
            for ((i, pair) in listOf("" to t("Системный", "Sistema"), "es" to "Español", "ru" to "Русский", "en" to "English").withIndex()) {
                val (tag, label) = pair
                if (i > 0) Spacer(Modifier.width(8.dp))
                val pick = {
                    androidx.appcompat.app.AppCompatDelegate.setApplicationLocales(
                        if (tag.isEmpty()) androidx.core.os.LocaleListCompat.getEmptyLocaleList() else androidx.core.os.LocaleListCompat.forLanguageTags(tag),
                    )
                }
                LangChip(label, lang == tag, Modifier.weight(1f), pick)
            }
        }
        Item("Mis Vales y Sorpresas", "Ver cupones de tiempo juntos y detalles caseros") { acts.open(Overlay.Vouchers) }
        Item("Nuestro Cuaderno Compartido", "Espacio privado para metas, películas, recetas y notas") { acts.open(Overlay.OurNotebook) }
        Item("Tutorial de Bienvenida", "Volver a ver la guía interactiva de Nuestro Refugio") {
            acts.open(null)
            acts.settings { it.copy(onboarded = false) }
        }
        Item(
            "Recordatorio al atardecer",
            if (settings.notify) "Activado: «una ventana roja y dorada se encendió enfrente»" else "Desactivado",
        ) {
            if (!settings.notify && android.os.Build.VERSION.SDK_INT >= 33) notify.launch(Manifest.permission.POST_NOTIFICATIONS)
            else { acts.settings { it.copy(notify = !it.notify) }; Dusk.schedule(context) }
        }
        Item(
            "Puesta de sol en Ica",
            (if (settings.lat != null) "Según tu ubicación. " else "Según Ica, Perú (-14.0678, -75.7286). ") + "Hoy a las ${clock(evening.sunset(evening.today))}",
        ) { locate.launch(Manifest.permission.ACCESS_COARSE_LOCATION) }
        Item(
            "Modo Creador / Preview",
            if (settings.previewMode) "ACTIVADO (Todas las ventanas abiertas para prueba)" else "Desactivado (Solo abre a su fecha real)",
        ) {
            acts.store.togglePreviewMode()
            acts.toast(if (!settings.previewMode) "Modo Preview ACTIVADO" else "Modo Preview desactivado")
        }
        Item(
            "👑 Panel Espejo de Elvis & Simulador Solar",
            "Control de cielo en vivo (Alba, Día, Carmesí, Noche) y sincronización con Francesca",
        ) {
            showCreatorMirror = true
        }
        Column(Modifier.padding(16.dp).fillMaxWidth().clip(Sh.Md).border(1.dp, C.line, Sh.Md).padding(14.dp)) {
            Text("Nuestra Historia", style = T.Name.copy(color = C.warm))
            Text(
                "Una aplicación creada exclusivamente por Elvis con todo su amor para Francesca (Principessa, Luna, Frambuesa, Mora).\n\nCada atardecer de diciembre, una ventana cobrará vida con un minijuego, un recuerdo y una pista para descubrir al admirador secreto. Las ventanas pasadas pueden recuperarse con las velas de los domingos.",
                style = T.Muted, modifier = Modifier.padding(top = 6.dp),
            )
        }
        Eyebrow("Dedicatoria", Modifier.padding(start = 18.dp, top = 4.dp))
        Text(
            "Hecho a mano en Ica, Perú para Francesca. Reescribiendo nuestra historia juntos desde el 6 de noviembre de 2026 a las 6:18 PM. Te ama tu Rata Aspirable ❤️",
            style = T.Muted, modifier = Modifier.padding(horizontal = 18.dp, vertical = 4.dp),
        )
        Eyebrow("Contacto & krailynd", Modifier.padding(start = 18.dp, top = 4.dp))
        for ((icon, label, sub, url) in listOf(
            listOf(
                "💬",
                "Escribir a Elvis por WhatsApp",
                "Chat directo con tu novio (907163498)",
                "https://wa.me/51907163498?text=Hola%20Elvis%20mi%20amor%20%E2%9D%A4%EF%B8%8F"
            ),
            listOf(
                "💡",
                "Proponer una idea para Nuestro Refugio",
                "Escríbele a Elvis qué te gustaría agregar a la app",
                "https://wa.me/51907163498?text=Hola%20Elvis,%20tengo%20una%20idea%20bonita%20para%20la%20app:%20"
            ),
            listOf(
                "🐞",
                "Informar de un error o detalle",
                "Avisa a Elvis si algo no se ve bien en tu teléfono",
                "https://wa.me/51907163498?text=Hola%20Elvis,%20encontr%C3%A9%20este%20detalle%20en%20la%20app:%20"
            ),
            listOf(
                "✨",
                "Repositorio Oficial (krailynd)",
                "La Casa de Enfrente en GitHub",
                "https://github.com/krailynd/la-casa-de-enfrente"
            ),
        )) Item("$icon  $label", sub) {
            runCatching {
                context.startActivity(
                    android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url))
                        .addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                )
            }
        }
        Spacer(Modifier.height(12.dp))
        Text("${t("версия", "versión")} ${BuildConfig.VERSION_NAME}", style = T.Muted, modifier = Modifier.padding(horizontal = 18.dp))
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
private fun Item(title: String, sub: String?, onClick: () -> Unit) {
    Column(Modifier.padding(horizontal = 16.dp, vertical = 5.dp).fillMaxWidth().clip(Sh.Md).background(C.bg2).border(1.dp, C.line, Sh.Md).clickable(onClick = onClick).padding(14.dp)) {
        Text(title, style = T.Body)
        if (sub != null) Text(sub, style = T.Muted)
    }
}

@Composable
private fun LangChip(label: String, on: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Box(
        modifier.clip(Sh.Sm).then(if (on) Modifier.background(C.warm) else Modifier.border(1.dp, C.line, Sh.Sm))
            .clickable(onClick = onClick).padding(horizontal = 6.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center,
    ) { Text(label, style = T.Button, color = if (on) C.onWarm else C.ink, maxLines = 1, softWrap = false) }
}
