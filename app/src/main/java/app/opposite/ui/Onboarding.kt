package app.opposite.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.opposite.data.Settings
import app.opposite.domain.Season
import app.opposite.domain.WinState
import app.opposite.ui.art.FacadeLook
import app.opposite.ui.art.HOUSE_H
import app.opposite.ui.art.HOUSE_W
import app.opposite.ui.art.facade
import app.opposite.ui.theme.C
import app.opposite.ui.theme.Sh
import app.opposite.ui.theme.T
import app.opposite.work.Dusk
import java.time.LocalDate

/** Coarse last known position, if we're allowed it; quietly nothing otherwise. */
@SuppressLint("MissingPermission")
fun lastPlace(context: Context): Pair<Double, Double>? = try {
    val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    lm.getProviders(true).mapNotNull { lm.getLastKnownLocation(it) }.maxByOrNull { it.time }?.let { it.latitude to it.longitude }
} catch (_: Exception) { null }

@Composable
fun Onboarding(settings: Settings, update: ((Settings) -> Settings) -> Unit) {
    val context = LocalContext.current
    var page by remember { mutableIntStateOf(0) }
    val time by rememberSeconds()
    val today = LocalDate.now()
    val advent = Season.advent(today)
    val located = settings.lat != null
    val locate = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { ok ->
        if (ok) lastPlace(context)?.let { (la, lo) -> update { it.copy(lat = la, lon = lo) } }
    }
    val notify = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { ok -> update { it.copy(notify = ok) } }
    fun start(from: LocalDate) {
        update { it.copy(onboarded = true, season = it.season ?: Season.new(from)) }
        Dusk.schedule(context)
    }

    Column(Modifier.fillMaxSize().background(C.bg).verticalScroll(rememberScrollState()).navigationBarsPadding()) {
        Box {
            Picture(HOUSE_W, HOUSE_H) {
                facade(FacadeLook(dusk = true, t = time, state = { d -> if (page == 0) (if (d in setOf(3, 8, 11, 16, 20)) WinState.LIT else WinState.FUTURE) else if (d == 1) WinState.TODAY else WinState.FUTURE }))
            }
            Snow(Modifier.matchParentSize())
        }
        Column(Modifier.padding(20.dp)) {
            when (page) {
                0 -> {
                    Text(t("Дом напротив", "The House Opposite"), style = T.Title.copy(fontSize = 28.sp))
                    Text(
                        t("Декабрь. Из своего окна вы смотрите на старый дом через двор. В нём 24 тёмных окна. Каждый вечер, после настоящего заката, загорается одно, и за ним — чья-то жизнь.",
                            "December. From your window you look at an old building across the yard. It has 24 dark windows. Every evening, after the real sunset, one of them lights up, and behind it is somebody's life."),
                        style = T.Body, modifier = Modifier.padding(top = 10.dp),
                    )
                    Spacer(Modifier.height(16.dp))
                    WarmButton(t("Дальше", "Next"), Modifier.fillMaxWidth()) { page = 1 }
                }
                1 -> {
                    Text(t("Как это устроено", "How it works"), style = T.Title)
                    for ((ru, en) in listOf(
                        "Окно зажигает огонёк — маленькое настоящее дело: позвонить, выйти во двор, сказать спасибо. Проверять никто не будет." to "A window is lit by a spark — a small real deed: call someone, step outside, say thank you. Nobody checks.",
                        "За окном комната соседа. Тапайте по вещам: где-то блестит улика." to "Behind it is a neighbour's room. Tap things: somewhere a clue glints.",
                        "Кто-то по ночам оставляет подарки под дверями. Каждый год это другой сосед. Назвать его можно 24-го." to "Someone leaves presents at the doors at night. Each year it's a different neighbour. You can name them on the 24th.",
                        "Каждое окно вешает игрушку на вашу ёлку. Пропустили вечер — не страшно, есть свечи." to "Every window hangs an ornament on your tree. Missed an evening? No matter, there are candles.",
                        "Как в обычном адвент-календаре, за окна можно спрятать настоящие подарки — для себя или для близкого." to "As in an ordinary advent calendar, you can hide real presents behind the windows — for yourself or for someone dear.",
                    )) Text("•  " + t(ru, en), style = T.Body, modifier = Modifier.padding(top = 8.dp))
                    Spacer(Modifier.height(16.dp))
                    WarmButton(t("Дальше", "Next"), Modifier.fillMaxWidth()) { page = 2 }
                }
                else -> {
                    Text(t("Когда начнём?", "When shall we start?"), style = T.Title)
                    val days = (advent.toEpochDay() - today.toEpochDay()).toInt()
                    Option(
                        t("С 1 декабря", "From December 1"),
                        if (days == 0 || today.monthValue == 12) t("Настоящий адвент: 24 вечера до Сочельника.", "A real advent: 24 evenings to Christmas Eve.") else t("Настоящий адвент. До первого окна $days ${plural(days, "день", "дня", "дней")}; пока можно спрятать подарки.", "A real advent. $days days to the first window; time to hide presents."),
                    ) { start(advent) }
                    Option(t("Сегодня", "Tonight"), t("24 вечера подряд, начиная с сегодняшнего заката.", "24 evenings in a row, from tonight's sunset.")) { start(today) }
                    Spacer(Modifier.height(10.dp))
                    Text(
                        if (located) t("Закат считаем по вашему месту.", "Sunset is computed for where you are.") else t("Закат считаем по часовому поясу. Можно точнее, по месту — разрешите примерное местоположение (один раз, никуда не отправляется).", "Sunset comes from your time zone. For your exact place, allow approximate location (once; it never leaves the phone)."),
                        style = T.Muted,
                    )
                    if (!located) GhostButton(t("Уточнить закат по месту", "Use my place for sunset"), Modifier.padding(top = 8.dp)) { locate.launch(Manifest.permission.ACCESS_COARSE_LOCATION) }
                    if (Build.VERSION.SDK_INT >= 33) GhostButton(t("Напоминать на закате", "Remind me at sunset"), Modifier.padding(top = 8.dp)) { notify.launch(Manifest.permission.POST_NOTIFICATIONS) }
                }
            }
        }
    }
}

@Composable
private fun Option(title: String, sub: String, onClick: () -> Unit) {
    Column(Modifier.padding(top = 10.dp).fillMaxWidth().clip(Sh.Md).background(C.card).border(1.dp, C.line, Sh.Md).clickable(onClick = onClick).padding(14.dp)) {
        Text(title, style = T.Name)
        Text(sub, style = T.Muted, modifier = Modifier.padding(top = 2.dp))
    }
}
