package app.opposite.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import app.opposite.domain.Evening
import app.opposite.domain.House
import app.opposite.domain.Mystery
import app.opposite.domain.Rules
import app.opposite.ui.theme.C
import app.opposite.ui.theme.T

@Composable
fun Sheets(sheet: Sheet?, evening: Evening, acts: Acts) {
    // Keep the last sheet's content on screen while it slides away.
    var last by remember { mutableStateOf(sheet) }
    if (sheet != null) last = sheet
    PaperSheet(sheet != null, onDismiss = { acts.sheet(null) }) {
        when (val s = last) {
            is Sheet.Task -> TaskSheet(s.day, evening, acts, late = false)
            is Sheet.Missed -> TaskSheet(s.day, evening, acts, late = true)
            is Sheet.Dossier -> DossierSheet(s.id, evening, acts)
            is Sheet.Wrong -> WrongSheet(s.id, evening, acts)
            null -> {}
        }
    }
}

private val paperBody = T.Body.copy(color = C.paperInk)
private val paperMuted = T.Muted.copy(color = C.paperMuted)

@Composable
private fun TaskSheet(d: Int, evening: Evening, acts: Acts, late: Boolean) {
    val r = House.resident(d)
    val candles = evening.season.candles
    Text("${dateText(evening.season.date(d))} · ${r.flat.s()}", style = paperMuted)
    Text(if (late) t("Это окно так и осталось тёмным", "This window stayed dark") else t("Огонёк на сегодня", "Tonight's spark"), style = T.Name.copy(color = C.paperInk), modifier = Modifier.padding(top = 2.dp))
    Text(r.task.s(), style = T.HandBig, modifier = Modifier.padding(vertical = 16.dp))
    Text(
        if (late) t("Ничего страшного: дом подождёт. Сделайте этот огонёк сегодня и зажгите окно свечой. Свечи даёт воскресный огонёк.", "No harm done: the house will wait. Do this spark today and light the window with a candle. Candles come from Sunday sparks.")
        else t("Сделайте это, когда сможете, хоть до полуночи. Проверять никто не будет.", "Do it when you can, even near midnight. Nobody checks."),
        style = if (late) paperBody else paperMuted,
    )
    Spacer(Modifier.height(16.dp))
    Row {
        if (late) WarmButton(t("Зажечь свечой ($candles)", "Light with a candle ($candles)"), enabled = candles > 0) { acts.ignite(d) }
        else WarmButton(t("Сделано, зажечь окно", "Done — light the window")) { acts.ignite(d) }
        Spacer(Modifier.width(10.dp))
        GhostButton(t("Позже", "Later"), dark = false) { acts.sheet(null) }
    }
}

@Composable
private fun DossierSheet(id: String, evening: Evening, acts: Acts) {
    val p = Mystery.suspect(id)
    val s = evening.season
    val found = s.foundClues()
    val struck = id in s.struck
    Text("${t("досье", "file")} · ${p.sub.s()}", style = paperMuted)
    Text(p.name.s(), style = T.Name.copy(color = C.paperInk))
    Text(p.intro.s(), style = paperBody, modifier = Modifier.padding(top = 6.dp, bottom = 8.dp))
    for (a in Mystery.Attr.entries) {
        val rel = found.filter { it.attr == a }
        val bad = rel.any { !it.allows(p) }
        val ok = rel.isNotEmpty() && !bad
        Text(
            "•  " + Mystery.habit.getValue(a).getValue(p.traits.getValue(a)).s(),
            style = paperBody.copy(
                color = if (bad) C.paperMuted else if (ok) C.green else C.paperInk,
                fontWeight = if (ok) FontWeight.SemiBold else FontWeight.Normal,
                textDecoration = if (bad) TextDecoration.LineThrough else null,
            ),
            modifier = Modifier.padding(vertical = 1.dp),
        )
    }
    if (found.any { it.alibiOf == id }) Text("•  " + t("Есть алиби (улика 22).", "Has an alibi (clue 22)."), style = paperBody.copy(color = C.paperMuted, textDecoration = TextDecoration.LineThrough))
    Text(t("Зелёным отмечено то, что совпало с найденными уликами, зачёркнуто то, что не совпало.", "Green matches the clues found so far; struck through contradicts them."), style = paperMuted, modifier = Modifier.padding(top = 10.dp))
    Spacer(Modifier.height(14.dp))
    Row {
        if (!s.over) {
            WarmButton(t("Подозреваю", "Suspect")) { acts.season { Rules.suspect(it, id) }; acts.sheet(null) }
            Spacer(Modifier.width(10.dp))
            GhostButton(if (struck) t("Вернуть в список", "Put back") else t("Вычеркнуть", "Cross out"), dark = false) { acts.season { Rules.strike(it, id) }; acts.sheet(null) }
        } else GhostButton(t("Закрыть", "Close"), dark = false) { acts.sheet(null) }
    }
}

@Composable
private fun WrongSheet(id: String, evening: Evening, acts: Acts) {
    val p = Mystery.suspect(id)
    val c = Rules.contradiction(evening.season, id)
    Text(t("${p.name.ru}? Не сходится", "${p.name.en}? It doesn't fit"), style = T.Name.copy(color = C.paperInk))
    Spacer(Modifier.height(8.dp))
    if (c == null) Text(t("Против этого человека пока мало улик. Найдите ещё: в каждом горящем окне что-то блестит.", "There's too little against this person yet. Find more: something glints in every lit window."), style = paperBody)
    else {
        Text(t("Улика ${c.day}:", "Clue ${c.day}:"), style = paperMuted)
        Text(c.text.s(), style = T.Hand, modifier = Modifier.padding(vertical = 6.dp))
        if (c.attr != null) Text(t("А про ${p.name.ru} известно другое: ", "But about ${p.name.en} we know otherwise: ") + Mystery.habit.getValue(c.attr).getValue(p.traits.getValue(c.attr)).s().replaceFirstChar { it.lowercase() }, style = paperBody)
    }
    Spacer(Modifier.height(14.dp))
    WarmButton(t("Ещё подумаю", "I'll think again")) { acts.sheet(null) }
}
