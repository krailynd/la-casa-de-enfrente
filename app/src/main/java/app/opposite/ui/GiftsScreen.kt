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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import app.opposite.domain.Evening
import app.opposite.domain.Gift
import app.opposite.domain.House
import app.opposite.domain.Season
import app.opposite.ui.theme.C
import app.opposite.ui.theme.Sh
import app.opposite.ui.theme.T

/**
 * Presents behind the windows, as in an ordinary advent calendar. Optional. When someone else fills
 * them in («sealed»), unopened presents stay hidden until the one making the calendar unlocks the list.
 */
@Composable
fun GiftsScreen(evening: Evening, acts: Acts) {
    val s = evening.season
    var unlocked by remember { mutableStateOf(!s.giftsSealed) }
    var confirm by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxSize().background(C.bg).statusBarsPadding().navigationBarsPadding().imePadding()) {
        Row(Modifier.padding(horizontal = 8.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("←", style = T.Title, modifier = Modifier.clip(Sh.Sm).clickable { acts.open(null) }.padding(horizontal = 10.dp, vertical = 4.dp))
            Text(t("Подарки за окнами", "Presents behind the windows"), style = T.Name)
        }
        LazyColumn(Modifier.weight(1f)) {
            item {
                Text(
                    t("Как в обычном адвент-календаре: за окном можно спрятать что-то настоящее. Шоколадку в коробочке №7, записку, «загляни в карман пальто», дело на вечер. Подарок видно, только когда окно загорится.",
                        "As in an ordinary advent calendar, something real can wait behind a window: a chocolate in box 7, a note, “look in your coat pocket”, something to do together. It shows only when the window lights."),
                    style = T.Muted, modifier = Modifier.padding(horizontal = 18.dp, vertical = 4.dp),
                )
                Row(Modifier.padding(horizontal = 16.dp, vertical = 10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Chip(t("Для себя", "For me"), !s.giftsSealed, Modifier.weight(1f)) { acts.season { it.copy(giftsSealed = false) }; unlocked = true }
                    Chip(t("Готовлю для другого", "Making it for someone"), s.giftsSealed, Modifier.weight(1f)) { acts.season { it.copy(giftsSealed = true) } }
                }
                if (s.giftsSealed) {
                    Field(s.giftsFrom, t("От кого (увидит получатель)", "From (the recipient will see it)"), Modifier.padding(horizontal = 16.dp)) { v -> acts.season { it.copy(giftsFrom = v.take(40)) } }
                    if (!unlocked) Column(Modifier.padding(16.dp).fillMaxWidth().clip(Sh.Md).border(1.dp, C.line, Sh.Md).padding(14.dp)) {
                        Text(t("Список спрятан, чтобы никто не подсмотрел заранее. Подарков: ${s.gifts.size}.", "The list is hidden so nobody peeks ahead. Presents: ${s.gifts.size}."), style = T.Body)
                        Spacer(Modifier.height(10.dp))
                        if (!confirm) GhostButton(t("Я готовлю подарки — открыть", "I'm the one preparing — open")) { confirm = true }
                        else Row {
                            WarmButton(t("Да, показать", "Yes, show")) { unlocked = true }
                            Spacer(Modifier.width(8.dp))
                            GhostButton(t("Не надо", "No")) { confirm = false }
                        }
                    }
                }
            }
            if (unlocked) items((1..Season.DAYS).toList()) { d -> GiftRow(d, s, evening, acts) }
            item { Spacer(Modifier.height(30.dp)) }
        }
    }
}

@Composable
private fun GiftRow(d: Int, s: Season, evening: Evening, acts: Acts) {
    val g = s.gifts[d]
    val r = House.resident(d)
    Column(Modifier.padding(horizontal = 16.dp, vertical = 5.dp).fillMaxWidth().clip(Sh.Md).background(C.bg2).border(1.dp, if (g != null) C.red.copy(alpha = .5f) else C.line, Sh.Md).padding(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("$d", style = T.Count, modifier = Modifier.width(34.dp))
            Column(Modifier.weight(1f)) {
                Text(dateText(s.date(d)), style = T.Body)
                Text("${t("окно", "window")}: ${r.name.s()}${if (s.lit.containsKey(d)) t(" · уже открыто", " · opened") else ""}", style = T.Muted)
            }
            if (g != null) Text("✕", style = T.Body.copy(color = C.dim), modifier = Modifier.clip(Sh.Sm).clickable { acts.season { it.copy(gifts = it.gifts - d) } }.padding(8.dp))
        }
        Field(g?.text ?: "", t("Что за этим окном? Например: «шоколадка в коробочке №$d»", "What's behind it? E.g. “a chocolate in box $d”"), Modifier.padding(top = 8.dp)) { v ->
            acts.season { it.copy(gifts = if (v.isBlank()) it.gifts - d else it.gifts + (d to Gift(v.take(160), it.gifts[d]?.kind ?: "thing"))) }
        }
        if (g != null) Row(Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            for (k in Gift.KINDS) {
                val (icon, label) = giftKind(k)
                Chip("$icon $label", g.kind == k, Modifier.weight(1f)) { acts.season { it.copy(gifts = it.gifts + (d to g.copy(kind = k))) } }
            }
        }
    }
}

@Composable
private fun Chip(text: String, on: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier.clip(Sh.Sm).background(if (on) C.warm else C.bg2).border(1.dp, if (on) C.warm else C.line, Sh.Sm).clickable(onClick = onClick).padding(horizontal = 10.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) { Text(text, style = T.Muted.copy(color = if (on) C.onWarm else C.ink)) }
}

@Composable
private fun Field(value: String, hint: String, modifier: Modifier = Modifier, onChange: (String) -> Unit) {
    var text by remember(value.isEmpty()) { mutableStateOf(value) }
    Box(modifier.fillMaxWidth().clip(Sh.Sm).background(C.paper).padding(horizontal = 12.dp, vertical = 10.dp)) {
        if (text.isEmpty()) Text(hint, style = T.Hand.copy(color = C.paperMuted, fontSize = T.Hand.fontSize * .9f))
        BasicTextField(text, { text = it; onChange(it) }, textStyle = T.Hand, cursorBrush = SolidColor(C.paperInk), modifier = Modifier.fillMaxWidth())
    }
}
