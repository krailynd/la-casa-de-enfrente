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
    val locate = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { ok ->
        if (ok) lastPlace(context)?.let { (la, lo) -> acts.settings { it.copy(lat = la, lon = lo) } }
    }
    val notify = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { ok -> acts.settings { it.copy(notify = ok) }; Dusk.schedule(context) }
    Column(Modifier.fillMaxSize().background(C.bg).verticalScroll(rememberScrollState()).statusBarsPadding().navigationBarsPadding()) {
        Row(Modifier.padding(horizontal = 8.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("←", style = T.Title, modifier = Modifier.clip(Sh.Sm).clickable { acts.open(null) }.padding(horizontal = 10.dp, vertical = 4.dp))
            Text(t("Настройки", "Settings"), style = T.Name)
        }
        Eyebrow(t("Язык", "Language"), Modifier.padding(start = 18.dp, top = 4.dp))
        val lang = androidx.appcompat.app.AppCompatDelegate.getApplicationLocales().toLanguageTags().substringBefore(',').substringBefore('-')
        Row(Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
            for ((i, pair) in listOf("" to t("Системный", "System"), "ru" to "Русский", "en" to "English").withIndex()) {
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
        Item(t("Подарки за окнами", "Presents behind the windows"), t("Для себя или для близкого, как в обычном адвент-календаре", "For you or someone dear, as in an ordinary advent calendar")) { acts.open(Overlay.Gifts) }
        Item(
            t("Напоминание на закате", "Sunset reminder"),
            if (settings.notify) t("Включено: «в доме напротив зажглось окно»", "On: “a window lit up across the yard”") else t("Выключено", "Off"),
        ) {
            if (!settings.notify && android.os.Build.VERSION.SDK_INT >= 33) notify.launch(Manifest.permission.POST_NOTIFICATIONS)
            else { acts.settings { it.copy(notify = !it.notify) }; Dusk.schedule(context) }
        }
        Item(
            t("Закат", "Sunset"),
            (if (settings.lat != null) t("По вашему месту. ", "For your place. ") else t("По часовому поясу. Тапните, чтобы считать по месту. ", "From the time zone. Tap to use your place. ")) + t("Сегодня в ${clock(evening.sunset(evening.today))}", "Today at ${clock(evening.sunset(evening.today))}"),
        ) { locate.launch(Manifest.permission.ACCESS_COARSE_LOCATION) }
        Item(t("Новый сезон", "New season"), t("Новый дом-загадка, новый Тайный Дед. Нынешняя ёлка уйдёт на антресоль.", "A new mystery, a new Secret Grandfather. This tree goes to the shelf.")) { restart = !restart }
        if (restart) Row(Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
            fun go(from: java.time.LocalDate) {
                val s = evening.season
                acts.settings { st ->
                    val shelf = if (s.litCount > 0) st.shelf + app.opposite.data.Past(s.startDate.year, s.culprit, s.lit.keys.sorted(), s.rare.sorted(), s.over) else st.shelf
                    st.copy(shelf = shelf, season = Season.new(from).copy(gifts = if (s.litCount == 0) s.gifts else emptyMap()))
                }
                acts.open(null); acts.tab(Tab.HOUSE)
            }
            WarmButton(t("С сегодня", "From tonight")) { go(evening.today) }
            Spacer(Modifier.width(8.dp))
            GhostButton(t("С 1 декабря", "From Dec 1")) { go(Season.advent(evening.today.plusDays(if (evening.today.monthValue == 12) 30 else 0))) }
        }
        Column(Modifier.padding(16.dp).fillMaxWidth().clip(Sh.Md).border(1.dp, C.line, Sh.Md).padding(14.dp)) {
            Text(t("Как это устроено", "How it works"), style = T.Name)
            Text(
                t("Окно загорается после настоящего заката, когда вы сделали огонёк. В каждой комнате есть улика о Тайном Деде; улики сначала расплывчатые, а точные появляются ближе к концу, так что загадка решается к 21–23 декабря. Пропущенные окна можно зажечь свечой: свеча прибавляется за огонёк в воскресенье. Всё хранится только на телефоне.",
                    "A window lights after the real sunset, once you've done the spark. Each room holds a clue to the Secret Grandfather; clues start vague and sharpen near the end, so the mystery resolves around the 21st–23rd. Missed windows can be lit with a candle; you earn one for a Sunday spark. Everything stays on the phone."),
                style = T.Muted, modifier = Modifier.padding(top = 6.dp),
            )
        }
        Eyebrow(t("Сообщество", "Community"), Modifier.padding(start = 18.dp, top = 4.dp))
        Text(
            t("Приложение делается вместе с теми, кто им пользуется. Всё идёт через бота StillCraft в Telegram.", "The app is made together with the people who use it. It all goes through the StillCraft bot on Telegram."),
            style = T.Muted, modifier = Modifier.padding(horizontal = 18.dp, vertical = 4.dp),
        )
        for ((icon, label, url) in listOf(
            Triple("🐞", t("Сообщить об ошибке", "Report a bug"), Community.BUG),
            Triple("💡", t("Предложить идею", "Suggest an idea"), Community.IDEA),
            Triple("🔔", t("Подписаться на обновления", "Follow updates"), Community.FOLLOW),
            Triple("💬", t("Чат StillCraft", "StillCraft chat"), Community.CHAT),
        )) Item("$icon  $label", null) {
            runCatching { context.startActivity(android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url)).addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)) }
        }
        Spacer(Modifier.height(12.dp))
        Text("${t("версия", "version")} ${BuildConfig.VERSION_NAME}", style = T.Muted, modifier = Modifier.padding(horizontal = 18.dp))
        Spacer(Modifier.height(24.dp))
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
