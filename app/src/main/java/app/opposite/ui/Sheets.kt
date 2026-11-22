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
    Text(if (late) t("Это окно так и осталось тёмным", "Esta ventana quedó a oscuras") else t("Огонёк на сегодня", "Chispa de hoy"), style = T.Name.copy(color = C.paperInk), modifier = Modifier.padding(top = 2.dp))
    Text(r.task.s(), style = T.HandBig, modifier = Modifier.padding(vertical = 16.dp))
    Text(
        if (late) t("Ничего страшного: дом подождёт. Сделайте этот огонёк сегодня и зажгите окно свечой. Свечи даёт воскресный огонёк.", "No pasa nada: la casa espera. Realiza esta chispa hoy y enciende la ventana con una vela. Las velas se consiguen con las chispas de los domingos.")
        else t("Сделайте это, когда сможете, хоть до полуночи. Проверять никто не будет.", "Hazlo cuando puedas, incluso cerca de medianoche. Nadie te vigila."),
        style = if (late) paperBody else paperMuted,
    )
    Spacer(Modifier.height(16.dp))
    Row {
        if (late) WarmButton(t("Зажечь свечой ($candles)", "Encender con una vela ($candles)"), enabled = candles > 0) { acts.ignite(d) }
        else WarmButton(t("Сделано, зажечь окно", "Hecho — encender ventana")) { acts.ignite(d) }
        Spacer(Modifier.width(10.dp))
        GhostButton(t("Позже", "Más tarde"), dark = false) { acts.sheet(null) }
    }
}

@Composable
private fun DossierSheet(id: String, evening: Evening, acts: Acts) {
    val p = Mystery.suspect(id)
    val s = evening.season
    val found = s.foundClues()
    val struck = id in s.struck
    Text("${t("досье", "expediente")} · ${p.sub.s()}", style = paperMuted)
    Text(p.name.s(), style = T.Name.copy(color = C.paperInk))
    Text(p.intro.s(), style = paperBody, modifier = Modifier.padding(top = 6.dp, bottom = 8.dp))
    for (a in Mystery.Attr.entries) {
        val rel = found.filter { it.attr == a }
        val bad = rel.any { !it.allows(p) }
        val ok = rel.isNotEmpty() && !bad
        Text(
            "•  " + Mystery.cleanLocalizedText(Mystery.habit.getValue(a).getValue(p.traits.getValue(a)).s()),
            style = paperBody.copy(
                color = if (bad) C.paperMuted else if (ok) C.green else C.paperInk,
                fontWeight = if (ok) FontWeight.SemiBold else FontWeight.Normal,
                textDecoration = if (bad) TextDecoration.LineThrough else null,
            ),
            modifier = Modifier.padding(vertical = 1.dp),
        )
    }
    if (found.any { it.alibiOf == id }) Text("•  " + t("Есть алиби (улика 22).", "Tiene coartada (pista 22)."), style = paperBody.copy(color = C.paperMuted, textDecoration = TextDecoration.LineThrough))
    Text(t("Зелёным отмечено то, что совпало с найденными уликами, зачёркнуто то, что не совпало.", "En verde lo que coincide con las pistas halladas; tachado lo que se contradice."), style = paperMuted, modifier = Modifier.padding(top = 10.dp))
    Spacer(Modifier.height(14.dp))
    Row {
        if (!s.over) {
            WarmButton(t("Подозреваю", "Sospechar")) { acts.season { Rules.suspect(it, id) }; acts.sheet(null) }
            Spacer(Modifier.width(10.dp))
            GhostButton(if (struck) t("Вернуть в список", "Restaurar") else t("Вычеркнуть", "Tachar"), dark = false) { acts.season { Rules.strike(it, id) }; acts.sheet(null) }
        } else GhostButton(t("Закрыть", "Cerrar"), dark = false) { acts.sheet(null) }
    }
}

@Composable
private fun WrongSheet(id: String, evening: Evening, acts: Acts) {
    val p = Mystery.suspect(id)
    val c = Rules.contradiction(evening.season, id)
    Text(t("${p.name.ru}? Не сходится", "${p.name.en}? No encaja"), style = T.Name.copy(color = C.paperInk))
    Spacer(Modifier.height(8.dp))
    if (c == null) Text(t("Против этого человека пока мало улик. Найдите ещё: в каждом горящем окне что-то блестит.", "Aún hay muy pocos indicios contra esta persona. Busca más: algo brilla en cada ventana encendida."), style = paperBody)
    else {
        Text(t("Улика ${c.day}:", "Pista ${c.day}:"), style = paperMuted)
        Text(c.text.s(), style = T.Hand, modifier = Modifier.padding(vertical = 6.dp))
        if (c.attr != null) Text(t("А про ${p.name.ru} известно другое: ", "Pero sobre ${p.name.en} sabemos lo contrario: ") + Mystery.habit.getValue(c.attr).getValue(p.traits.getValue(c.attr)).s().replaceFirstChar { it.lowercase() }, style = paperBody)
    }
    Spacer(Modifier.height(14.dp))
    WarmButton(t("Ещё подумаю", "Lo pensaré mejor")) { acts.sheet(null) }
}
