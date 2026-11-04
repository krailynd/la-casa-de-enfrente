package app.opposite.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.opposite.data.Settings
import app.opposite.domain.Evening
import app.opposite.domain.Rules
import app.opposite.domain.Season
import app.opposite.store
import app.opposite.sunset
import app.opposite.ui.theme.C
import app.opposite.ui.theme.Sh
import app.opposite.ui.theme.T
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.ZoneId

enum class Tab { HOUSE, NOTES, TREE }

sealed interface Overlay {
    data class Room(val day: Int) : Overlay
    data object Finale : Overlay
    data object Gifts : Overlay
    data object Settings : Overlay
}

sealed interface Sheet {
    data class Task(val day: Int) : Sheet
    data class Missed(val day: Int) : Sheet
    data class Dossier(val id: String) : Sheet
    data class Wrong(val id: String) : Sheet
}

/** Everything a screen may ask of the app, so screens stay plain. */
class Acts(
    val season: ((Season) -> Season) -> Unit,
    val settings: ((Settings) -> Settings) -> Unit,
    val open: (Overlay?) -> Unit,
    val sheet: (Sheet?) -> Unit,
    val toast: (String) -> Unit,
    val tab: (Tab) -> Unit,
    val ignite: (Int) -> Unit,
)

@Composable
fun Root(settings: Settings, fixedNow: Long? = null, startTab: Tab = Tab.HOUSE, startOverlay: Overlay? = null, startSheet: Sheet? = null) {
    val context = LocalContext.current
    val season = settings.season
    if (!settings.onboarded || season == null) {
        Onboarding(settings) { change -> context.store.update(change) }
        return
    }
    var now by remember { mutableLongStateOf(fixedNow ?: System.currentTimeMillis()) }
    if (fixedNow == null) LaunchedEffect(Unit) { while (true) { delay(20_000); now = System.currentTimeMillis() } }
    val zone = ZoneId.systemDefault()
    val evening = Evening(season, now, zone, settings.sunset(zone))

    var tab by remember { mutableStateOf(startTab) }
    var overlay by remember { mutableStateOf(startOverlay) }
    var sheet by remember { mutableStateOf(startSheet) }
    var toast by remember { mutableStateOf<String?>(null) }
    var toastId by remember { mutableLongStateOf(0L) }
    val ignite = remember { Animatable(0f) }
    var igniting by remember { mutableStateOf(0) }
    val scope = rememberCoroutineScope()

    val acts = Acts(
        season = { change -> context.store.season(change) },
        settings = { change -> context.store.update(change) },
        open = { overlay = it },
        sheet = { sheet = it },
        toast = { toast = it; toastId++ },
        tab = { tab = it },
        ignite = { d ->
            sheet = null
            overlay = null
            tab = Tab.HOUSE
            scope.launch {
                igniting = d
                ignite.snapTo(0f)
                ignite.animateTo(1f, tween(1400))
                context.store.season { Rules.light(it, d, System.currentTimeMillis(), zone) }
                igniting = 0
                delay(250)
                overlay = Overlay.Room(d)
                toast = null
            }
        },
    )
    LaunchedEffect(toastId) { if (toast != null) { delay(3300); toast = null } }

    BackHandler(sheet != null || overlay != null || tab != Tab.HOUSE) {
        when {
            sheet != null -> sheet = null
            overlay != null -> overlay = null
            else -> tab = Tab.HOUSE
        }
    }

    Box(Modifier.fillMaxSize().background(C.bg)) {
        Column(Modifier.fillMaxSize()) {
            Box(Modifier.weight(1f)) {
                when (tab) {
                    Tab.HOUSE -> HouseScreen(evening, settings, acts, igniting, ignite.value)
                    Tab.NOTES -> NotebookScreen(evening, acts)
                    Tab.TREE -> TreeScreen(evening, settings, acts)
                }
            }
            BottomBar(tab) { tab = it }
        }
        when (val o = overlay) {
            is Overlay.Room -> RoomScreen(evening, o.day, acts)
            Overlay.Finale -> FinaleScreen(evening, acts)
            Overlay.Gifts -> GiftsScreen(evening, acts)
            Overlay.Settings -> SettingsScreen(settings, evening, acts)
            null -> {}
        }
        Sheets(sheet, evening, acts)
        AnimatedVisibility(toast != null, enter = fadeIn(), exit = fadeOut(), modifier = Modifier.align(Alignment.TopCenter)) {
            Text(
                toast ?: "", style = T.Body.copy(color = C.paperInk), textAlign = TextAlign.Center,
                modifier = Modifier.statusBarsPadding().padding(top = 64.dp, start = 24.dp, end = 24.dp).widthIn(max = 380.dp)
                    .shadow(10.dp, Sh.Md).clip(Sh.Md).background(C.paper).padding(horizontal = 16.dp, vertical = 11.dp),
            )
        }
    }
}

@Composable
private fun BottomBar(tab: Tab, onTab: (Tab) -> Unit) {
    Row(Modifier.fillMaxWidth().background(C.bg2).navigationBarsPadding().padding(top = 4.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
        for ((tb, glyph, label) in listOf(Triple(Tab.HOUSE, "▦", t("Дом", "House")), Triple(Tab.NOTES, "✎", t("Блокнот", "Notebook")), Triple(Tab.TREE, "▲", t("Ёлка", "Tree")))) {
            val on = tb == tab
            Column(Modifier.weight(1f).clickable { onTab(tb) }.padding(vertical = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(glyph, style = T.Body, color = if (on) C.warm else C.dim)
                Spacer(Modifier.height(1.dp))
                Text(label, style = T.Muted, color = if (on) C.warm else C.dim)
            }
        }
    }
}
