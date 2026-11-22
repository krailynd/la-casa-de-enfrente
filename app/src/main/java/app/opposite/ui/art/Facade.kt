package app.opposite.ui.art

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.drawText
import app.opposite.domain.House
import app.opposite.domain.Resident
import app.opposite.domain.WinState

const val HOUSE_W = 360f
const val HOUSE_H = 540f

/** Window rectangle for a cell (floor×10 + column). */
fun winRect(cell: Int): Rect {
    val f = cell / 10
    val col = cell % 10
    val x = 48f + col * 56f
    val y = 80f + (5 - f) * 76f + 16f
    return Rect(x, y, x + 40f, y + 50f)
}

class FacadeLook(
    val dusk: Boolean,
    val state: (Int) -> WinState,
    val hasGift: (Int) -> Boolean = { false },
    val allLit: Boolean = false,
    /** Seconds, for the flicker of today's window and the garland. */
    val t: Float = 0f,
    /** A window being lit right now, and how far along (0..1). */
    val igniting: Int = 0,
    val ignite: Float = 0f,
    /** Draws the chalk day numbers on dark windows, when given. */
    val numbers: androidx.compose.ui.text.TextMeasurer? = null,
)

private val FRAME = c(0xFFE8E8EE)
private val WARM_TOP = c(0xFFF6C667)
private val WARM_BOT = c(0xFFC81D31)

enum class SceneryTarget { MOON, STARS, SNOWMAN, STREETLIGHT, DOOR }

/** Detects interactive touch targets in the outdoor scenery. */
fun sceneryHit(x: Float, y: Float): SceneryTarget? {
    val pt = Offset(x, y)
    val moonDist = (pt - Offset(300f, 40f)).getDistance()
    val snowmanDist = (pt - Offset(232f, 490f)).getDistance()
    return when {
        moonDist < 30f -> SceneryTarget.MOON
        snowmanDist < 36f -> SceneryTarget.SNOWMAN
        y in 0f..70f && x < 260f -> SceneryTarget.STARS
        Rect(10f, 400f, 52f, 480f).contains(pt) -> SceneryTarget.STREETLIGHT
        Rect(150f, 385f, 210f, 470f).contains(pt) -> SceneryTarget.DOOR
        else -> null
    }
}

/** The five-storey house across the yard at dusk (or by day), with its 24 windows. */
fun Sk.facade(look: FacadeLook) {
    val dusk = look.dusk || look.allLit
    val sky = if (dusk) listOf(c(0xFF0B1430), c(0xFF1D2B55), c(0xFF4A3D6B)) else listOf(c(0xFF8FA6C4), c(0xFFB9C6D6), c(0xFFD9D6D0))
    rect(0f, 0f, HOUSE_W, HOUSE_H, Brush.verticalGradient(0f to sky[0], .6f to sky[1], 1f to sky[2], startY = 0f, endY = HOUSE_H))
    if (dusk) {
        for (i in 0 until 26) circle(((i * 137) % 360).toFloat(), ((i * 71) % 70 + 6).toFloat(), if (i % 5 != 0) .7f else 1.2f, Color.White, .7f)
        circle(300f, 40f, 13f, c(0xFFF4ECD0)); circle(306f, 36f, 12f, sky[0])
    }
    val far = if (dusk) c(0xFF141F3D) else c(0xFF8392AA)
    path("M0 120 h20 v-30 h40 v30 h10 v-50 v460 H0Z", far)
    path("M340 140 h20 v400 h-30 v-380 h10Z", far)
    if (dusk) listOf(8f to 100f, 34f to 105f, 46f to 130f, 8f to 150f, 34f to 190f, 345f to 170f, 345f to 230f).forEachIndexed { i, (x, y) -> if (i % 3 != 1) rect(x, y, 7f, 9f, c(0xFFE9B45A), .55f) }

    // дом
    rect(30f, 72f, 300f, 392f, if (dusk) c(0xFF3B4A6E) else c(0xFFB3A596))
    for (k in 0 until 10) rect(30f + k * 30f, 72f, 1f, 392f, Color.Black, .05f)
    rect(26f, 66f, 308f, 8f, if (dusk) c(0xFF2B3757) else c(0xFF8D8173))
    rect(26f, 62f, 308f, 5f, c(0xFFEEF3FA))
    stroke("M90 66 v-26 M80 46 h20 M84 52 h12 M250 66 v-20 M242 50 h16", if (dusk) c(0xFF1C2645) else c(0xFF5A5248), 1.6f)
    for (f in 1..4) line(30f, 80f + f * 76f, 330f, 80f + f * 76f, if (dusk) c(0xFF34416A) else c(0xFFA39584), 1f)
    // сосульки
    var ix = 34
    while (ix < 330) { val h = 4 + (ix * 13) % 9; path("M$ix 74 l2 $h l2 -${h}Z", c(0xFFE8F2FF), .85f); ix += 9 + (ix * 7) % 5 }
    rect(326f, 74f, 5f, 390f, if (dusk) c(0xFF2B3757) else c(0xFF8D8173))
    // подъезд
    rect(160f, 400f, 40f, 64f, if (dusk) c(0xFF24304F) else c(0xFF6F5F4F))
    rect(164f, 404f, 32f, 60f, if (dusk) c(0xFF1A2440) else c(0xFF57483A))
    path("M152 398 h56 l-6 -10 h-44Z", if (dusk) c(0xFF2B3757) else c(0xFF7B6D5F))
    line(152f, 388f, 208f, 388f, c(0xFFEEF3FA), 3f)
    if (dusk) { glow(180f, 394f, 26f, c(0xFFFFCF73), .5f); circle(180f, 394f, 3f, c(0xFFFFE7A6)) }

    for (r in House.residents) window(r, look, dusk)
    balconies(dusk)

    // двор
    path("M0 464 Q90 456 180 466 T360 462 V540 H0Z", if (dusk) c(0xFFC9D4EA) else c(0xFFF2F4F8))
    path("M0 482 Q120 474 230 486 T360 480 V540 H0Z", if (dusk) c(0xFFDDE5F4) else Color.White)
    val dark = if (dusk) c(0xFF2B3757) else c(0xFF5F5A52)
    val pine = if (dusk) c(0xFF22324D) else c(0xFF4F6A52)
    rect(60f, 470f, 3f, 40f, dark); path("M40 478 l22 -34 l22 34Z", pine); path("M44 490 l18 -26 l18 26Z", pine)
    rect(270f, 488f, 44f, 4f, dark); rect(274f, 492f, 3f, 10f, dark); rect(307f, 492f, 3f, 10f, dark)
    stroke("M268 488 q24 -6 48 0", Color.White, 3f)
    for (i in 0 until 9) { val t = i / 8f; ellipse(40 + t * 130 + (if (i % 2 == 1) 4 else -4), 528 - t * 60, 3f, 1.6f, if (dusk) c(0xFF9FB0D0) else c(0xFFC9D2E2)) }
    // фонарь
    rect(18f, 420f, 3f, 90f, dark)
    stroke("M19 420 q0 -12 14 -12", dark, 3f)
    path("M27 408 h12 l-3 6 h-6Z", dark)
    if (dusk) { path("M28 414 h10 l26 96 h-62Z", c(0xFFFFE7A6), .14f); glow(33f, 415f, 18f, c(0xFFFFCF73), .7f); circle(33f, 415f, 4f, c(0xFFFFF3C0)) }
    // снеговик
    group(232f, 506f) {
        ellipse(0f, 6f, 16f, 3f, Color.Black, .12f)
        circle(0f, -6f, 13f, c(0xFFF4F7FC)); circle(0f, -24f, 9f, c(0xFFF4F7FC)); circle(0f, -37f, 6.5f, c(0xFFF4F7FC))
        path("M0 -36 l8 2 l-8 1Z", c(0xFFF2862A))
        circle(-2.5f, -39f, .9f, c(0xFF222222)); circle(2f, -39f, .9f, c(0xFF222222))
        rect(-6f, -50f, 12f, 7f, c(0xFFC8483C))
        stroke("M-8 -30 q8 4 16 0", c(0xFFC8483C), 3f)
        stroke("M-9 -25 l-12 -8 M9 -25 l11 -10", c(0xFF6A4A2A), 1.4f)
        circle(0f, -24f, 1f, c(0xFF333333)); circle(0f, -19f, 1f, c(0xFF333333))
    }
    if (look.allLit) {
        stroke("M0 330 Q180 420 360 330", c(0xFF2A2A2A), 1f)
        val cols = listOf(0xFFFF6B6B, 0xFFFFD166, 0xFF6BDC8F, 0xFF6BB5FF)
        for (i in 1 until 24) {
            val t = i / 24f
            val x = 360 * t
            val y = (1 - t) * (1 - t) * 330 + 2 * (1 - t) * t * 420 + t * t * 330
            val a = .55f + .45f * kotlin.math.sin(look.t * (2.2f + (i % 4) * .4f) + i)
            glow(x, y + 4, 9f, c(cols[i % 4]), .6f * a)
            circle(x, y + 4, 3.2f, c(cols[i % 4]), .4f + .6f * a)
        }
    }
    grain(0f, 0f, HOUSE_W, HOUSE_H, .08f)
}

private fun Sk.window(r: Resident, look: FacadeLook, dusk: Boolean) {
    val g = winRect(r.cell)
    val d = r.day
    val st = if (look.allLit) WinState.LIT else look.state(d)
    val lit = st == WinState.LIT
    if (lit) glow(g.center.x, g.center.y, 38f, c(0xFFFFCF73), .45f)
    rect(g.left - 2, g.top - 2, g.width + 4, g.height + 4, FRAME)
    if (lit) litInside(r, g)
    else {
        rect(g.left, g.top, g.width, g.height, if (dusk) c(0xFF16203B) else c(0xFF6D7D96))
        line(g.left + 4, g.bottom - 4, g.right - 6, g.top + 6, Color.White, 1f, if (dusk) .08f else .3f)
        if (st == WinState.TODAY) {
            val fl = .25f + .3f * (kotlin.math.sin(look.t * 2.4f) * .5f + .5f) + .15f * kotlin.math.sin(look.t * 7.3f)
            rect(g.left, g.top, g.width, g.height, c(0xFFFFC463), fl.coerceIn(.1f, .75f))
        }
        if (look.igniting == d) rect(g.left, g.top, g.width, g.height, Brush.verticalGradient(listOf(WARM_TOP, WARM_BOT), g.top, g.bottom), igniteAlpha(look.ignite))
        if (st == WinState.MISSED) path("M${g.left} ${g.top} l8 0 l-8 8Z M${g.right} ${g.bottom} l-8 0 l8 -8Z", c(0xFFCFE3FF), .5f)
        look.numbers?.let { tm ->
            val today = st == WinState.TODAY
            val col = when { today -> c(0xFFFFD27A); st == WinState.MISSED -> c(0xFF8FA4C8); dusk -> c(0xFFC7D0E4); else -> c(0xFFF4F4F4) }
            val size = if (today) 24f else 20f
            val style = androidx.compose.ui.text.TextStyle(fontFamily = app.opposite.ui.theme.Hand, fontSize = androidx.compose.ui.unit.TextUnit(size / ds.density / ds.fontScale, androidx.compose.ui.unit.TextUnitType.Sp), color = col.copy(alpha = if (today) 1f else .78f))
            val layout = tm.measure("$d", style)
            val cx = g.center.x + 1f; val cy = g.center.y + 4f
            ds.drawText(layout, topLeft = Offset(cx - layout.size.width / 2f, cy - layout.size.height / 2f))
            if (today) stroke("M${cx - 14} $cy a14,14 0 1,0 28,0 a14,14 0 1,0 -28,0", c(0xFFFFD27A), 1.3f, dash = floatArrayOf(3f, 3f))
        }
    }
    stroke("M${g.center.x} ${g.top} v${g.height} M${g.left} ${g.top + 16} h${g.width}", FRAME, 2f, cap = androidx.compose.ui.graphics.StrokeCap.Butt)
    rect(g.left - 4, g.bottom + 1, g.width + 8, 4f, if (dusk) c(0xFFC9D0DC) else c(0xFFD8D8D8))
    path("M${g.left - 3} ${g.bottom + 1} q${(g.width + 6) / 2} -5 ${g.width + 6} 0Z", Color.White)
    if (!lit && look.hasGift(d)) bow(g.right - 3, g.top + 4)
}

/** A paper bow on the frame: there's a present behind this window. */
private fun Sk.bow(x: Float, y: Float) {
    path("M$x $y q-7 -6 -7 0 q0 5 7 0 q7 -6 7 0 q0 5 -7 0Z", c(0xFFD8484A))
    path("M$x $y l-3 7 M$x $y l3 7", c(0xFFD8484A))
    stroke("M$x $y l-3 7 M$x $y l3 7", c(0xFFD8484A), 1.6f)
    circle(x, y, 1.6f, c(0xFFA82A2A))
}

private fun igniteAlpha(p: Float): Float = when {
    p < .2f -> p / .2f * .6f
    p < .3f -> .2f
    p < .55f -> .9f
    p < .65f -> .5f
    else -> .5f + (p - .65f) / .35f * .5f
}.coerceIn(0f, 1f)

private fun Sk.litInside(r: Resident, g: Rect) {
    val d = r.day
    clip("M${g.left} ${g.top} h${g.width} v${g.height} h${-g.width}Z") {
        rect(g.left, g.top, g.width, g.height, c(r.wall).shade(.15f))
        var x = g.left + 3
        while (x < g.right) { rect(x, g.top, 3f, g.height, Color.White, .12f); x += 7 }
        rect(g.left, g.bottom - 7, g.width, 7f, c(0xFF8A5A38))
        val lx = g.center.x + (d % 3 - 1) * 7
        glow(lx, g.top + 10, 18f, c(0xFFFFF2B0), .7f)
        line(lx, g.top, lx, g.top + 5, c(0xFF3A2A20), .6f)
        path("M${lx - 6} ${g.top + 10} L${lx + 6} ${g.top + 10} L${lx + 3.5f} ${g.top + 5} L${lx - 3.5f} ${g.top + 5}Z", c(listOf(0xFFE8A03A, 0xFFC8483C, 0xFF5A8A6A, 0xFFF4ECD9)[d % 4]))
        if (r.things.any { it.type == "tree" }) group(g.left + (if (d % 2 == 1) 9 else 31), g.bottom - 6, .32f) {
            path("M0 -70 l30 40 h-12 l24 34 h-14 l22 38 h-100 l22 -38 h-14 l24 -34 h-12Z", c(0xFF2F5A34))
            circle(-10f, -30f, 5f, c(0xFFFFD64A)); circle(12f, -12f, 5f, c(0xFFD64A3A)); circle(-14f, 10f, 5f, c(0xFF5AB0FF)); circle(16f, 18f, 5f, c(0xFFFFD64A))
        }
        val list = r.people.take(2)
        list.forEachIndexed { i, p ->
            val px = if (list.size == 1) g.left + (if (d % 2 == 1) 24 else 16) else g.left + 12 + i * 16
            group(px, g.bottom - 3, if (p.kid) .34f / .7f * .7f else .31f) { person(p.copy(behind = false, stool = false), mini = true) }
        }
        rect(g.left, g.top, g.width, g.height, Brush.verticalGradient(listOf(WARM_TOP, WARM_BOT), g.top, g.bottom), .38f)
    }
    val cur = c(r.curtain)
    val cd = cur.shade(-.3f)
    path("M${g.left} ${g.top} h12 q-4 ${g.height / 2} 3 ${g.height} h-15Z", cur)
    stroke("M${g.left + 4} ${g.top} q-1 ${g.height / 2} 2 ${g.height} M${g.left + 8} ${g.top} q-2 ${g.height / 2} 2 ${g.height}", cd, .8f)
    path("M${g.right} ${g.top} h-12 q4 ${g.height / 2} -3 ${g.height} h15Z", cur)
    stroke("M${g.right - 4} ${g.top} q1 ${g.height / 2} -2 ${g.height} M${g.right - 8} ${g.top} q2 ${g.height / 2} -2 ${g.height}", cd, .8f)
    rect(g.left, g.top, g.width, 2.5f, cd)
}

private fun Sk.balconies(dusk: Boolean) {
    val rail = if (dusk) c(0xFF8A96B4) else c(0xFF5A5248)
    for ((cell, thing) in listOf(20 to "sled", 40 to "jars", 24 to "skis", 44 to "tree", 30 to "line")) {
        val g = winRect(cell)
        val y = g.bottom + 6
        val x0 = g.left - 8
        val x1 = g.right + 8
        val w = x1 - x0
        rect(x0, y, w, 4f, if (dusk) c(0xFF56648A) else c(0xFF9A8C7C))
        path("M$x0 $y q${w / 2} -4 $w 0Z", Color.White)
        when (thing) {
            "sled" -> { stroke("M${x1 - 14} ${y - 2} v-16 M${x1 - 6} ${y - 2} v-16", c(0xFFC8483C), 2f); line(x1 - 16, y - 10, x1 - 4, y - 10, c(0xFFA8743A), 2.5f) }
            "jars" -> for (i in 0..2) rect(x0 + 3 + i * 6, y - 8, 5f, 8f, c(listOf(0xFFC8483C, 0xFFE8A03A, 0xFF5A8A3A)[i]), .85f, rx = 1f)
            "skis" -> stroke("M${x1 - 4} $y L${x1 - 10} ${y - 34} M${x1 - 1} $y L${x1 - 7} ${y - 34}", c(0xFFD8B04A), 2f)
            "tree" -> { path("M${x0 + 10} $y l-6 -2 l6 -22 l6 22Z", c(0xFF2F5A34)); stroke("M${x0 + 5} ${y - 12} l10 -3 M${x0 + 5} ${y - 6} l10 -3", c(0xFFD8C8A0), 1f) }
            "line" -> { stroke("M$x0 ${y - 16} q${w / 2} 5 $w 0", c(0xFFCCCCCC), .6f); rect(x0 + 12, y - 14, 7f, 9f, c(0xFFC8483C)); rect(x0 + 24, y - 13, 9f, 6f, c(0xFF5A8AD6)) }
        }
        rect(x0, y - 18, w, 1.6f, rail)
        for (i in 0..8) rect(x0 + 1 + i * (w - 2) / 8, y - 17, 1f, 17f, rail)
    }
}

/** Which window (its evening) is at facade point (x, y), with a little slack. */
fun facadeHit(x: Float, y: Float): Int? = House.residents.firstOrNull { winRect(it.cell).inflate(4f).contains(Offset(x, y)) }?.day
