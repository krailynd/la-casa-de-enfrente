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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import app.opposite.data.Settings
import app.opposite.domain.Evening
import app.opposite.domain.House
import app.opposite.domain.Season
import app.opposite.domain.WinState
import app.opposite.ui.art.FacadeLook
import app.opposite.ui.art.HOUSE_H
import app.opposite.ui.art.HOUSE_W
import app.opposite.ui.art.facade
import app.opposite.ui.art.facadeHit
import app.opposite.ui.theme.C
import app.opposite.ui.theme.Sh
import app.opposite.ui.theme.T
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun dateText(d: LocalDate): String = if (LocalRu.current) d.format(DateTimeFormatter.ofPattern("d MMMM", Locale.forLanguageTag("ru"))) else d.format(DateTimeFormatter.ofPattern("MMMM d", Locale.ENGLISH))

fun clock(ms: Long): String = Instant.ofEpochMilli(ms).atZone(ZoneId.systemDefault()).toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"))

fun plural(n: Int, one: String, few: String, many: String): String {
    val a = n % 100; val b = n % 10
    return when { a in 11..19 -> many; b == 1 -> one; b in 2..4 -> few; else -> many }
}

@Composable
fun Header(evening: Evening, acts: Acts) {
    val s = evening.season
    Row(Modifier.fillMaxWidth().statusBarsPadding().padding(start = 16.dp, end = 8.dp, top = 12.dp, bottom = 6.dp), verticalAlignment = Alignment.Bottom) {
        Column(Modifier.weight(1f)) {
            Text(t("Дом напротив", "The House Opposite"), style = T.Title)
            Text("${dateText(evening.today)} · ${t("закат в", "sunset")} ${clock(evening.sunset(evening.today))}", style = T.Muted)
        }
        Text("${s.litCount} ${t("из", "of")} ${Season.DAYS}", style = T.Count, modifier = Modifier.padding(end = 6.dp))
        Text("⚙", style = T.Title.copy(color = C.dim), modifier = Modifier.clip(Sh.Sm).clickable { acts.open(Overlay.Settings) }.padding(8.dp))
    }
}

@Composable
fun HouseScreen(evening: Evening, settings: Settings, acts: Acts, igniting: Int, ignite: Float) {
    val time by rememberSeconds()
    val s = evening.season
    val ru = LocalRu.current
    val numbers = androidx.compose.ui.text.rememberTextMeasurer()
    Column(Modifier.verticalScroll(rememberScrollState())) {
        Header(evening, acts)
        Box {
            Picture(HOUSE_W, HOUSE_H, onTap = { x, y -> facadeHit(x, y)?.let { tapWindow(it, evening, acts, ru) } }) {
                facade(FacadeLook(dusk = evening.dusk, state = evening::state, hasGift = { s.gifts.containsKey(it) }, t = time, igniting = igniting, ignite = ignite, numbers = numbers))
            }
            Snow(Modifier.matchParentSize())
        }
        TodayCard(evening, acts)
        GiftsLine(s, acts)
        Spacer(Modifier.height(20.dp))
    }
}

private fun tapWindow(d: Int, evening: Evening, acts: Acts, ru: Boolean) {
    val s = evening.season
    if (s.over && !s.lit.containsKey(d)) return
    when (evening.state(d)) {
        WinState.LIT -> acts.open(Overlay.Room(d))
        WinState.FUTURE -> {
            val n = d - evening.day
            acts.toast(if (evening.beforeStart && d == 1) (if (ru) "Первое окно загорится ${s.startDate.dayOfMonth} ${monthRu(s.startDate)}." else "The first window lights on ${s.startDate.month.name.lowercase().replaceFirstChar { it.uppercase() }} ${s.startDate.dayOfMonth}.")
            else if (ru) "Окно $d откроется через $n ${plural(n, "вечер", "вечера", "вечеров")}." else "Window $d opens in $n ${if (n == 1) "evening" else "evenings"}.")
        }
        WinState.WAITING_DUSK -> acts.toast(if (ru) "Ещё светло. Окно зажжётся после заката, в ${clock(evening.sunset(evening.today))}." else "It's still light. The window lights after sunset, at ${clock(evening.sunset(evening.today))}.")
        WinState.TODAY -> acts.sheet(Sheet.Task(d))
        WinState.MISSED -> acts.sheet(Sheet.Missed(d))
    }
}

fun monthRu(d: LocalDate) = listOf("января", "февраля", "марта", "апреля", "мая", "июня", "июля", "августа", "сентября", "октября", "ноября", "декабря")[d.monthValue - 1]

@Composable
private fun TodayCard(evening: Evening, acts: Acts) {
    val s = evening.season
    val d = evening.day
    val box = Modifier.padding(horizontal = 16.dp, vertical = 10.dp).fillMaxWidth().clip(Sh.Lg)
        .background(Brush.linearGradient(listOf(C.card, C.bg2))).border(1.dp, C.line, Sh.Lg).padding(16.dp)
    Column(box) {
        when {
            s.over -> {
                Eyebrow(t("Сезон закрыт", "Season closed"))
                Text(t("Тайный Дед найден. Ёлка ждёт на антресоли, а дом — следующего декабря.", "The Secret Grandfather is found. The tree waits on the shelf, and the house waits for next December."), style = T.Body, modifier = Modifier.padding(top = 6.dp))
                Spacer(Modifier.height(10.dp))
                WarmButton(t("Финал ещё раз", "See the ending again")) { acts.open(Overlay.Finale) }
            }
            evening.beforeStart -> {
                val n = 1 - d
                Eyebrow(t("До первого окна", "Until the first window"))
                Text(t("${n} ${plural(n, "день", "дня", "дней")}. Пока в доме напротив тихо: шторы задёрнуты, кто-то пьёт чай.", "$n ${if (n == 1) "day" else "days"}. The house opposite is quiet for now: curtains drawn, someone having tea."), style = T.Body, modifier = Modifier.padding(top = 6.dp))
                Text(t("Пока можно положить подарки за окна.", "Meanwhile you can put presents behind the windows."), style = T.Muted, modifier = Modifier.padding(top = 6.dp))
            }
            d > Season.DAYS -> {
                Eyebrow(t("Все вечера позади", "All evenings are behind"))
                Text(t("Двадцать четвёртое окно горит. Загляните в блокнот: пора назвать Тайного Деда.", "The twenty-fourth window is lit. Open the notebook: it's time to name the Secret Grandfather."), style = T.Body, modifier = Modifier.padding(top = 6.dp))
            }
            s.lit.containsKey(d) -> {
                Eyebrow(t("Сегодня", "Tonight"))
                Text(t("Окно $d горит. ${House.resident(d).name.ru} уже знает, что вы заходили.", "Window $d is lit. ${House.resident(d).name.en} knows you dropped by."), style = T.Body, modifier = Modifier.padding(top = 6.dp))
                Text(t("Следующее окно завтра, после заката.", "The next one tomorrow, after sunset."), style = T.Hand.copy(color = C.warm, fontSize = 20.sp_), modifier = Modifier.padding(top = 6.dp))
            }
            !evening.dusk -> {
                Eyebrow(t("Сегодня · окно $d", "Tonight · window $d"))
                Text(t("Дом ещё серый. Окно зажжётся после заката, в ${clock(evening.sunset(evening.today))}.", "The house is still grey. The window lights after sunset, at ${clock(evening.sunset(evening.today))}."), style = T.Body, modifier = Modifier.padding(top = 6.dp))
                Text(House.resident(d).task.s(), style = T.Hand.copy(color = C.warm, fontSize = 20.sp_), modifier = Modifier.padding(top = 8.dp))
            }
            else -> {
                Eyebrow(t("Смеркается · окно $d", "Dusk · window $d"))
                Text(t("В одном окне кто-то включил свет. Тапните по мерцающему окну.", "Someone just switched on a light. Tap the flickering window."), style = T.Body, modifier = Modifier.padding(top = 6.dp))
                Spacer(Modifier.height(10.dp))
                WarmButton(t("Огонёк на сегодня", "Tonight's spark")) { acts.sheet(Sheet.Task(d)) }
            }
        }
        val missed = evening.missed
        if (missed > 0 && !s.over) Text(
            t("Тёмных окон позади: $missed. Свечей: ${s.candles}.", "Dark windows behind: $missed. Candles: ${s.candles}."),
            style = T.Hand.copy(color = C.frost, fontSize = 19.sp_), modifier = Modifier.padding(top = 8.dp),
        )
    }
}

@Composable
private fun GiftsLine(s: Season, acts: Acts) {
    val n = s.gifts.size
    Row(
        Modifier.padding(horizontal = 16.dp).fillMaxWidth().clip(Sh.Md).border(1.dp, C.line, Sh.Md).clickable { acts.open(Overlay.Gifts) }.padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text("🎁", style = T.Body)
        Column(Modifier.weight(1f).padding(start = 10.dp)) {
            Text(t("Подарки за окнами", "Presents behind the windows"), style = T.Body)
            Text(
                if (n == 0) t("Как в обычном адвент-календаре: шоколадка, записка, «загляни в карман». Для себя или для близкого.", "As in an ordinary advent calendar: a chocolate, a note, “look in your pocket”. For you or for someone dear.")
                else if (s.giftsSealed) t("$n ${plural(n, "подарок спрятан", "подарка спрятано", "подарков спрятано")}${if (s.giftsFrom.isNotBlank()) " · от: ${s.giftsFrom}" else ""}", "$n hidden${if (s.giftsFrom.isNotBlank()) " · from ${s.giftsFrom}" else ""}")
                else t("$n ${plural(n, "окно", "окна", "окон")} с подарком", "$n ${if (n == 1) "window" else "windows"} with a present"),
                style = T.Muted,
            )
        }
        Text("›", style = T.Title.copy(color = C.dim))
    }
}

private val Int.sp_ get() = androidx.compose.ui.unit.TextUnit(this.toFloat(), androidx.compose.ui.unit.TextUnitType.Sp)
