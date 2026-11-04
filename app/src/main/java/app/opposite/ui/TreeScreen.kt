package app.opposite.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import app.opposite.data.Settings
import app.opposite.domain.Evening
import app.opposite.domain.House
import app.opposite.domain.Mystery
import app.opposite.ui.art.Sk
import app.opposite.ui.art.TREE_H
import app.opposite.ui.art.TREE_W
import app.opposite.ui.art.TreeArt
import app.opposite.ui.art.c
import app.opposite.ui.theme.C
import app.opposite.ui.theme.Sh
import app.opposite.ui.theme.T
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** The tree's still part, drawn once per process at three times the picture size. */
object TreeBitmap {
    @Volatile private var cached: ImageBitmap? = null

    fun get(): ImageBitmap = cached ?: synchronized(this) {
        cached ?: run {
            val k = 3f
            val img = ImageBitmap((TREE_W * k).toInt(), (TREE_H * k).toInt())
            CanvasDrawScope().draw(Density(1f), LayoutDirection.Ltr, Canvas(img), Size(TREE_W * k, TREE_H * k)) {
                withTransform({ scale(k, k, Offset.Zero) }) { TreeArt.base(Sk(this)) }
            }
            img.also { cached = it }
        }
    }
}

@Composable
fun TreeScreen(evening: Evening, settings: Settings, acts: Acts) {
    val s = evening.season
    val time by rememberSeconds()
    val base by produceState<ImageBitmap?>(null) { value = withContext(Dispatchers.Default) { TreeBitmap.get() } }
    var picked by remember { mutableStateOf<Int?>(null) }
    val ornaments = s.lit.keys.sorted().map { it to (it in s.rare) }
    Column(Modifier.verticalScroll(rememberScrollState())) {
        Header(evening, acts)
        Eyebrow(t("Ёлка сезона · ${ornaments.size} ${plural(ornaments.size, "игрушка", "игрушки", "игрушек")}", "This season's tree · ${ornaments.size} ornaments"), Modifier.padding(start = 18.dp, top = 6.dp, bottom = 6.dp))
        val img = base
        if (img == null) Box(Modifier.fillMaxWidth().aspectRatio(TREE_W / TREE_H).background(Brush.verticalGradient(listOf(c(0xFF2A2236), c(0xFF46354A)))), contentAlignment = Alignment.Center) {
            Text(t("Ставим ёлку…", "Putting up the tree…"), style = T.Muted)
        } else Picture(TREE_W, TREE_H, onTap = { x, y -> picked = TreeArt.hit(x, y, s.lit.keys) }) {
            ds.drawImage(img, IntOffset.Zero, IntSize(img.width, img.height), IntOffset.Zero, IntSize(TREE_W.toInt(), TREE_H.toInt()), filterQuality = FilterQuality.High)
            TreeArt.live(this, ornaments, time)
        }
        val p = picked
        Column(Modifier.padding(horizontal = 16.dp, vertical = 10.dp).fillMaxWidth().clip(Sh.Md).background(C.paper).padding(14.dp)) {
            if (p == null) Text(t("Каждое зажжённое окно вешает на ёлку игрушку. Найденный мандарин делает её «бабушкиной». Тапните по игрушке.", "Every lit window hangs an ornament on the tree. A tangerine found makes it a “granny's” one. Tap an ornament."), style = T.Hand)
            else {
                val r = House.resident(p)
                Text("${dateText(s.date(p))}${if (p in s.rare) t(" · бабушкина", " · granny's") else ""}".uppercase(), style = T.Eyebrow.copy(color = C.paperMuted))
                Text("${r.name.s()}: «${r.task.s()}»", style = T.Hand)
            }
        }
        Eyebrow(t("Венок · свечей в запасе: ${s.candles}", "Wreath · candles in reserve: ${s.candles}"), Modifier.padding(start = 18.dp, top = 10.dp))
        Wreath(s.candles)
        Text(
            t("Огонёк в воскресенье зажигает свечу. Свечой можно зажечь пропущенное окно: ни стриков, ни штрафов.", "A Sunday spark lights a candle. A candle can light a missed window: no streaks, no penalties."),
            style = T.Muted, modifier = Modifier.padding(horizontal = 24.dp),
        )
        Eyebrow(t("Антресоль", "Top shelf"), Modifier.padding(start = 18.dp, top = 18.dp, bottom = 4.dp))
        if (settings.shelf.isEmpty()) Text(t("Здесь будут ёлки прошлых лет. Пока пусто.", "Trees from past years will be kept here. Empty so far."), style = T.Muted, modifier = Modifier.padding(horizontal = 18.dp))
        for (past in settings.shelf.sortedByDescending { it.year }) Text(
            "${past.year} · ${past.ornaments.size} ${t("игрушек", "ornaments")}${if (past.rare.isNotEmpty()) t(", бабушкиных ${past.rare.size}", ", ${past.rare.size} granny's") else ""} · ${t("Тайный Дед", "Secret Grandfather")}: ${if (past.solved) Mystery.suspect(past.culprit).name.s() else "?"}",
            style = T.Body, modifier = Modifier.padding(horizontal = 18.dp, vertical = 3.dp),
        )
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun Wreath(candles: Int) {
    val time by rememberSeconds()
    Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.Center) {
        Canvas(Modifier.size(240.dp, 70.dp)) {
            val sk = Sk(this)
            val k = size.width / 240f
            withTransform({ scale(k, k, Offset.Zero) }) {
                with(sk) {
                    ellipse(120f, 56f, 104f, 12f, c(0xFF1E4A2A))
                    for (i in 0 until 40) {
                        val a = i / 40f * 6.283f
                        line(120f + kotlin.math.cos(a) * 100f, 56f + kotlin.math.sin(a) * 11f, 120f + kotlin.math.cos(a + .2f) * 92f, 52f + kotlin.math.sin(a + .2f) * 9f, c(0xFF3F8A4E), 2f)
                    }
                    for (i in 0 until 4) {
                        val x = 48f + i * 48f
                        val on = i < candles.coerceAtMost(4)
                        rect(x - 7, 20f, 14f, 34f, if (i == 2) c(0xFFE8C4D0) else c(0xFFC8483C), if (on) 1f else .45f, rx = 2f)
                        if (on) {
                            val fl = 1f + .08f * kotlin.math.sin(time * 9f + i)
                            glow(x, 12f, 16f, c(0xFFFFC463), .6f)
                            path("M$x ${18 - 12 * fl} q${6 * fl} 7 0 ${12 * fl} q${-6 * fl} -5 0 ${-12 * fl}Z", c(0xFFFFC463))
                            line(x, 18f, x, 21f, c(0xFF3A2A20), 1f)
                        }
                    }
                    for ((x, col) in listOf(30f to 0xFFC8302C, 96f to 0xFFE8C24A, 144f to 0xFFC8302C, 210f to 0xFFE8C24A)) circle(x, 60f, 3.5f, c(col))
                }
            }
        }
    }
}
