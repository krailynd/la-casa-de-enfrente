package app.opposite.ui.art

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import app.opposite.domain.Resident
import kotlin.math.round

const val ROOM_W = 360f
const val ROOM_H = 250f

private val WALL_ITEMS = setOf("shelf", "clock", "frame", "garland", "coat")
private val SHADOW = mapOf("piano" to 66f, "bed" to 66f, "table" to 56f, "desk" to 62f, "sewing" to 60f, "aquarium" to 54f, "armchair" to 50f, "tree" to 50f, "chess" to 46f, "radio" to 38f, "tv" to 32f, "gifts" to 40f, "easel" to 36f, "rocket" to 34f, "boots" to 30f)

/**
 * A neighbour's room seen from outside, through their window: wallpaper, floor, furniture, the
 * people, a sparkle on the clue until it's found, the hidden tangerine, lamplight, grain, then the
 * window frame, curtains and frost on top. [t] is seconds, for the sparkle.
 */
fun Sk.room(r: Resident, clueFound: Boolean, mandarinFound: Boolean, t: Float) {
    val wall = c(r.wall)
    val wallD = wall.shade(-.18f)
    val wallL = wall.shade(.12f)
    val lampX = r.things.firstOrNull { it.type == "lamp" }?.x ?: 180f
    rect(0f, 0f, ROOM_W, ROOM_H, Brush.verticalGradient(0f to wallD, .7f to wall, 1f to wallD, startY = 0f, endY = ROOM_H))
    var x = 0f
    while (x < ROOM_W) { rect(x, 0f, 11f, 198f, wallL, .35f); x += 26f }
    var y = 20f
    while (y < 190f) {
        var fx = 18f + (round(y / 30f).toInt() % 2) * 13f
        while (fx < ROOM_W) {
            for ((dx, dy) in listOf(0f to -2.5f, 0f to 2.5f, -2.5f to 0f, 2.5f to 0f)) circle(fx + dx, y + dy, 2f, wallD, .28f)
            circle(fx, y, 1.2f, wallL, .6f)
            fx += 26f
        }
        y += 30f
    }
    rect(0f, 176f, ROOM_W, 22f, wallD, .35f); rect(0f, 174f, ROOM_W, 3f, wallL, .5f)
    rect(0f, 198f, ROOM_W, 52f, Brush.verticalGradient(listOf(c(0xFF5A3C26), c(0xFF8A603E)), 198f, 250f))
    var bx = -20f
    while (bx < 400f) { stroke("M$bx 198 L${bx - 26} 250", c(0xFF4A3020), 1.2f, .6f); bx += 34f }
    rect(0f, 194f, ROOM_W, 5f, c(0xFF4A3020)); rect(0f, 194f, ROOM_W, 1.2f, c(0xFFA07850), .6f)
    // ковёр
    ellipse(180f, 226f, 118f, 17f, c(0xFF7A2E30))
    ds.drawOval(c(0xFFD8A84A), Offset(76f, 213f), androidx.compose.ui.geometry.Size(208f, 26f), style = androidx.compose.ui.graphics.drawscope.Stroke(1.4f, pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(5f, 3f))))
    ellipse(180f, 226f, 70f, 8f, c(0xFF9A3E3A)); ellipse(180f, 226f, 30f, 3.5f, c(0xFFD8A84A), .6f)

    for (th in r.things) if (!th.small && th.type !in WALL_ITEMS) ellipse(th.x, 199f, SHADOW[th.type] ?: 22f, 5f, Brush.radialGradient(listOf(Color.Black.copy(alpha = .3f), Color.Transparent), Offset(th.x, 199f), SHADOW[th.type] ?: 22f))
    for (p in r.people.filter { it.behind }) group(p.x, if (p.kid) 192f else 186f) { person(p) }
    for (th in r.things) if (!th.small) thing(th.type, th.x, th.y)
    for (p in r.people.filter { !it.behind }) group(p.x, if (p.sit) 196f else 198f) {
        ellipse(0f, 1f, if (p.kid) 14f else 20f, 3.5f, Color.Black, .22f)
        person(p)
    }
    for (th in r.things) if (th.small) thing(th.type, th.x, th.y)

    if (!clueFound) r.things.firstOrNull { it.clue }?.let { th ->
        val cy = if (th.small) th.y else 150f
        for ((i, d) in listOf(Triple(-14f, -12f, 0f), Triple(16f, -4f, .6f), Triple(-4f, 14f, 1.2f)).withIndex()) {
            val a = ((kotlin.math.sin((t - d.third) * 3.4f) + 1) / 2).coerceIn(0f, 1f)
            sparkle(th.x + d.first, cy + d.second, 5f + i, a)
        }
    }
    if (!mandarinFound) r.mandarin?.let { (mx, my) ->
        circle(mx, my, 6.5f, c(0xFFF2862A)); circle(mx - 2, my - 2, 2.5f, c(0xFFFFB060), .7f)
        path("M$mx ${my - 6} q4 -5 8 -2 q-4 2 -8 2Z", c(0xFF3A7A3A))
    }

    // свет лампы, виньетка, зерно
    rect(0f, 0f, ROOM_W, ROOM_H, Brush.radialGradient(0f to c(0xFFFFD890).copy(alpha = .42f), .55f to c(0xFFFFB060).copy(alpha = .08f), 1f to c(0xFF1A0A20).copy(alpha = .45f), center = Offset(lampX, 75f), radius = 290f))
    grain(0f, 0f, ROOM_W, ROOM_H, .1f)

    frame(r)
}

/** The window we look through: white frame with a top light, folded curtains, frost in the corners, snow on the sill. */
private fun Sk.frame(r: Resident) {
    val fr = c(0xFFE4E6EC)
    path("M0 0 h360 v250 h-360Z M16 16 v218 h328 v-218Z", fr, evenOdd = true)
    path("M16 16 h328 v4 h-324 v214 h-4Z", Color.Black, .12f)
    rect(176f, 16f, 8f, 218f, fr); rect(16f, 78f, 328f, 7f, fr)
    rect(183f, 16f, 1.5f, 218f, Color.Black, .12f)
    val cur = c(r.curtain)
    val cd = cur.shade(-.3f)
    val cl = cur.shade(.18f)
    path("M16 16 h48 q-18 110 12 218 h-60Z", cur)
    for (x in listOf(24f, 36f, 48f)) stroke("M$x 16 q${-6 + x / 12} 110 ${x / 4 + 4} 218", cd, 2.5f, .55f)
    stroke("M58 16 q-16 110 12 218", cl, 2f)
    path("M344 16 h-48 q18 110 -12 218 h60Z", cur)
    for (x in listOf(336f, 324f, 312f)) stroke("M$x 16 q${6 - (360 - x) / 12} 110 ${-(360 - x) / 4 - 4} 218", cd, 2.5f, .55f)
    stroke("M302 16 q16 110 -12 218", cl, 2f)
    rect(16f, 16f, 328f, 6f, cd)
    path("M16 22 c10 2 18 6 22 14 c-6 -2 -12 0 -16 6 c0 -8 -2 -14 -6 -20Z M16 22 h26 l-26 28Z", c(0xFFE6F0FF), .55f)
    path("M344 234 c-10 -2 -20 -8 -24 -18 c8 4 16 2 20 -6 c0 10 2 18 4 24Z M344 234 h-40 l40 -40Z", c(0xFFE6F0FF), .5f)
    rect(0f, 234f, 360f, 16f, c(0xFFC9CFDA))
    path("M2 234 q30 -10 60 -6 q40 -8 80 -2 q60 -10 110 -3 q50 -8 106 2Z", Color.White)
}

fun Sk.sparkle(x: Float, y: Float, r: Float, a: Float) {
    if (a <= .02f) return
    glow(x, y, r * 2.2f, c(0xFFFFF6C0), a * .6f)
    path("M$x ${y - r} L${x + r * .25f} ${y - r * .25f} L${x + r} $y L${x + r * .25f} ${y + r * .25f} L$x ${y + r} L${x - r * .25f} ${y + r * .25f} L${x - r} $y L${x - r * .25f} ${y - r * .25f}Z", c(0xFFFFF6C0), a)
}

/** What was tapped at room point (x, y): a thing's index, -2 for the tangerine, or null. Small things win. */
fun roomHit(r: Resident, x: Float, y: Float, mandarinFound: Boolean): Int? {
    if (!mandarinFound) r.mandarin?.let { (mx, my) -> if ((mx - x) * (mx - x) + (my - y) * (my - y) < 18 * 18) return -2 }
    val order = r.things.indices.sortedBy { if (r.things[it].small) 0 else 1 }
    return order.firstOrNull { hitBox(r.things[it].type, r.things[it].x, r.things[it].y).contains(Offset(x, y)) }
}
