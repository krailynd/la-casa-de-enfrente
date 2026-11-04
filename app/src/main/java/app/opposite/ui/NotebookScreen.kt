package app.opposite.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.remember
import kotlin.math.roundToInt
import androidx.compose.ui.unit.sp
import app.opposite.domain.Evening
import app.opposite.domain.House
import app.opposite.domain.Mystery
import app.opposite.domain.Rules
import app.opposite.ui.art.portrait
import app.opposite.ui.art.c
import app.opposite.ui.theme.C
import app.opposite.ui.theme.Sh
import app.opposite.ui.theme.T

/** Height of one ruled line, in whole pixels so many lines never drift off the rules. */
@Composable
fun rulePx(): Int = with(LocalDensity.current) { 30.dp.roundToPx() }

/** The rule sits this far above the bottom of each line; text baselines land on it. */
private val RuleGap = 7.dp

/**
 * A school notebook page: blue rules, a red margin. Every child starts on a line, so handwriting
 * written with [RuledText] sits on the rules however long the page grows.
 */
@Composable
fun NotePage(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    val step = rulePx()
    val top = with(LocalDensity.current) { 12.dp.roundToPx() }
    Layout(
        content,
        modifier.padding(horizontal = 14.dp, vertical = 8.dp).fillMaxWidth().shadow(8.dp, Sh.Sm).clip(Sh.Sm).background(C.paper)
            .drawBehind {
                var y = top + step - RuleGap.toPx()
                while (y < size.height) { drawLine(C.paperLine, Offset(0f, y), Offset(size.width, y), 1.dp.toPx()); y += step }
                drawLine(c(0xFFE3A2A2), Offset(28.dp.toPx(), 0f), Offset(28.dp.toPx(), size.height), 1.5.dp.toPx())
            }
            .padding(start = 40.dp, end = 16.dp)
            .padding(top = with(LocalDensity.current) { top.toDp() }, bottom = with(LocalDensity.current) { (step - RuleGap.roundToPx()).toDp() }),
    ) { items, c ->
        val placed = items.map { it.measure(c.copy(minHeight = 0)) }
        var y = 0
        val at = placed.map { p -> y.also { y += (p.height + step - 1) / step * step } }
        layout(c.maxWidth, y) { placed.forEachIndexed { i, p -> p.place(0, at[i]) } }
    }
}

/** Text written on the rules: one line per rule, the baseline exactly on it. */
@Composable
fun RuledText(text: String, style: TextStyle, modifier: Modifier = Modifier) {
    val d = LocalDensity.current
    val step = rulePx()
    val st = style.copy(
        lineHeight = with(d) { step.toSp() },
        lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.None),
    )
    val measurer = rememberTextMeasurer()
    val base = remember(st, d) { measurer.measure("Ая", st).firstBaseline }
    val shift = (step - with(d) { RuleGap.toPx() } - base - with(d) { 1.dp.toPx() }).roundToInt()
    Text(text, style = st, modifier = modifier.offset { IntOffset(0, shift) })
}

@Composable
fun NotebookScreen(evening: Evening, acts: Acts) {
    val s = evening.season
    val found = s.clues.count { Mystery.slots.containsKey(it) }
    val total = Mystery.slots.size
    val can = s.lit.containsKey(24) && !s.over
    Column(Modifier.verticalScroll(rememberScrollState())) {
        Header(evening, acts)
        Eyebrow(t("Кто носит подарки?", "Who brings the presents?"), Modifier.padding(start = 18.dp, top = 6.dp))
        NotePage {
            RuledText(t("Дело о Тайном Деде · улик $found из $total", "The Secret Grandfather case · $found of $total clues"), T.Name.copy(color = C.paperInk, fontSize = 17.sp))
            RuledText(t("Кто-то оставляет подарки под дверями. Никто не видел кто. Подозреваемых десять, у каждого свои привычки.", "Someone leaves presents at the doors. Nobody has seen who. There are ten suspects, each with their own habits."), T.Hand.copy(color = c(0xFF8B5A3A)))
            for (d in Mystery.slots.keys.sorted()) {
                val clue = if (d in s.clues) s.clue(d) else null
                if (clue != null) {
                    val tag = clue.attr?.let { t(it.ru, it.en) } ?: t("алиби", "alibi")
                    RuledText("$d · $tag", T.Muted.copy(color = C.paperMuted, fontSize = 12.sp))
                    RuledText(clue.text.s(), T.Hand)
                } else RuledText(
                    "$d.  " + if (s.lit.containsKey(d)) t("окно горит, но улику вы ещё не нашли", "the window is lit, but you haven't found the clue yet") else "· · ·",
                    T.Hand.copy(color = c(0xFFB6A993)),
                    if (s.lit.containsKey(d)) Modifier.clickable { acts.open(Overlay.Room(d)) } else Modifier,
                )
            }
        }
        val left = Mystery.suspects.count { it.id !in s.struck }
        NotePage {
            RuledText(t("Подозреваемые · осталось $left", "Suspects · $left left"), T.Name.copy(color = C.paperInk, fontSize = 17.sp))
            RuledText(t("Тапните, чтобы открыть досье и сверить с уликами.", "Tap to open a file and check it against the clues."), T.Hand.copy(fontSize = 19.sp))
            Column(Modifier.padding(top = 8.dp)) {
                for (row in Mystery.suspects.chunked(5)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(bottom = 6.dp)) {
                        for (p in row) SuspectCard(p, struck = p.id in s.struck, chosen = s.guess == p.id, Modifier.weight(1f)) { acts.sheet(Sheet.Dossier(p.id)) }
                    }
                }
            }
            val guess = s.guess?.let { Mystery.suspect(it) }
            RuledText(
                when {
                    s.over -> t("Дело закрыто: ${Mystery.suspect(s.culprit).name.ru}.", "Case closed: ${Mystery.suspect(s.culprit).name.en}.")
                    can && guess != null -> t("Подозреваемый: ${guess.name.ru}.", "Suspect: ${guess.name.en}.")
                    can -> t("Окно 24 горит. Откройте досье и выберите, кого подозреваете.", "Window 24 is lit. Open the files and choose whom you suspect.")
                    else -> t("Назвать можно будет 24-го, когда загорится последнее окно.", "You can name them on the 24th, when the last window lights.")
                },
                T.Hand.copy(fontSize = 20.sp),
            )
            if (can) {
                WarmButton(t("Назвать", "Name them"), enabled = guess != null) {
                    val id = guess!!.id
                    acts.season { Rules.accuse(it, id, System.currentTimeMillis()) }
                    if (id == s.culprit) acts.open(Overlay.Finale) else acts.sheet(Sheet.Wrong(id))
                }
            }
            if (s.over) { WarmButton(t("Финал", "The ending")) { acts.open(Overlay.Finale) } }
        }
        Spacer(Modifier.height(20.dp))
    }
}

@Composable
private fun SuspectCard(p: Mystery.Suspect, struck: Boolean, chosen: Boolean, modifier: Modifier, onClick: () -> Unit) {
    val look = House.resident(p.day).people.first()
    Column(
        modifier.alpha(if (struck) .38f else 1f).clip(Sh.Sm).background(c(0xFFFFF8E7))
            .border(if (chosen) 2.dp else 1.dp, if (chosen) C.red else c(0xFFD6C6A6), Sh.Sm).clickable(onClick = onClick).padding(vertical = 6.dp, horizontal = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Picture(48f, 52f, Modifier.width(44.dp).clip(Sh.Sm)) { portrait(look, c(House.resident(p.day).wall)) }
        FitWords(p.name.s(), T.Muted.copy(color = C.paperInk, fontSize = 11.5.sp, lineHeight = 13.sp, textDecoration = if (struck) TextDecoration.LineThrough else null), Modifier.padding(top = 4.dp))
        FitWords(p.sub.s(), T.Muted.copy(color = C.paperMuted, fontSize = 9.5.sp, lineHeight = 11.sp))
    }
}

/** Centred text that never breaks inside a word: shrinks until the longest word fits the width. */
@Composable
private fun FitWords(text: String, style: TextStyle, modifier: Modifier = Modifier) {
    val measurer = rememberTextMeasurer()
    BoxWithConstraints(modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        val room = constraints.maxWidth.toFloat()
        val widest = remember(text, style) { text.split(' ').maxOf { measurer.measure(it, style, softWrap = false).size.width }.toFloat() }
        val k = if (widest > room) room / widest * .98f else 1f
        Text(text, style = style.copy(fontSize = style.fontSize * k, lineHeight = style.lineHeight * k), textAlign = TextAlign.Center)
    }
}
