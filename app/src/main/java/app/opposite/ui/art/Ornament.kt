package app.opposite.ui.art

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * A glass ornament for window [d], centred at (0, 0), about 30 units tall, hanging from a cap at y = -17.
 * Eight shapes in turn: ball, fir cone, twisted icicle, star, little house, rocket, ribbed lantern, bell.
 * A «granny's» ornament ([rare]) has a gold cap and gold dust. [reflect] tints a highlight by a nearby bulb.
 */
fun Sk.ornament(d: Int, rare: Boolean, reflect: Color? = null) {
    val h = (d * 53 + 20) % 360f
    val col = hsl(h, .64f, .46f)
    val light = hsl(h, .8f, .78f)
    val dark = hsl(h, .7f, .22f)
    val gold = c(0xFFE2B84A)
    val radial = Brush.radialGradient(0f to light, .45f to col, 1f to dark, center = Offset(-4f, -5f), radius = 17f)
    val linear = Brush.horizontalGradient(0f to light, .5f to col, 1f to dark, startX = -8f, endX = 8f)
    val capBrush = Brush.horizontalGradient(
        if (rare) listOf(c(0xFF8A6A20), c(0xFFFFE9A0), c(0xFF9A7420)) else listOf(c(0xFF7A7F88), c(0xFFF4F6FA), c(0xFF80858E)), -4f, 4f)

    stroke("M0 -17 q-3 -5 0 -8 q3 3 0 8", c(0xFFBBBBBB), .8f)
    rect(-4f, -17.5f, 8f, 5.5f, capBrush, rx = 1.2f)
    for (x in listOf(-2.5f, -.5f, 1.5f)) line(x, -17f, x, -12.5f, Color.Black, .5f, .2f)

    fun shine(x: Float, y: Float, w: Float = 3f, hh: Float = 5f) {
        group(x, y, rotate = -30f) { ellipse(0f, 0f, w, hh, Color.White, .55f) }
        circle(x + 1.5f, y + 6, .9f, Color.White, .7f)
    }

    when (d % 8) {
        0 -> {
            circle(0f, 0f, 12.5f, radial)
            path("M-12.2 1 Q0 7.5 12.2 1 L12.4 -2.5 Q0 4 -12.4 -2.5Z", if (rare) gold else c(0xFFF4E4B0), .9f)
            for (x in listOf(-9f, -4.5f, 0f, 4.5f, 9f)) circle(x, 2.2f + cos(x / 12) * 1.4f, .9f, dark)
            stroke("M3 -8 v7 M-.5 -4.5 h7 M.5 -6.5 l5 5 M.5 -1.5 l5 -5", Color.White, .8f, .75f)
            shine(-5f, -6f)
            ring(0f, 0f, 12.5f, dark, .7f, .5f)
        }
        1 -> {
            ellipse(0f, 2f, 8.5f, 15f, radial)
            val rows = listOf(-9f, -4f, 1f, 6f, 11f)
            rows.forEachIndexed { ri, y ->
                val n = 4 - kotlin.math.abs(ri - 2) + 1
                for (j in 0 until n) {
                    val x = (j - (n - 1) / 2f) * 4.6f
                    path("M${x - 2.6f} $y q2.6 4 5.2 0 q-2.6 1.4 -5.2 0Z", dark, .55f)
                    stroke("M${x - 2} ${y + .4f} q2 2 4 0", light, .7f)
                }
            }
            shine(-3.5f, -5f, 1.6f, 4.5f)
            circle(0f, 15f, 1.3f, Color.White, .8f)
        }
        2 -> {
            path("M-6.5 -12 L6.5 -12 L0 28 Z", linear)
            for (i in 0..4) stroke("M${-5.5f + i * 1.2f} ${-9 + i * 7} Q0 ${-6 + i * 7} ${5.5f - i * 1.2f} ${-12 + i * 7}", Color.White, 1.1f - i * .15f, .65f)
            line(-2f, -12f, 0f, 24f, Color.White, .6f, .5f)
        }
        3 -> {
            val pts = (0 until 10).map { i -> val a = -PI / 2 + i * PI / 5; val r = if (i % 2 == 1) 6.0 else 14.5; Offset((r * cos(a)).toFloat(), (r * sin(a) + 2).toFloat()) }
            for (i in 0 until 10) {
                val p = pts[i]; val q = pts[(i + 1) % 10]
                path("M0 2 L${p.x} ${p.y} L${q.x} ${q.y}Z", if (i % 2 == 1) dark else col)
            }
            path("M0 2 L${pts[8].x} ${pts[8].y} L${pts[9].x} ${pts[9].y}Z M0 2 L${pts[9].x} ${pts[9].y} L${pts[0].x} ${pts[0].y}Z", light, .7f)
            circle(0f, 2f, 2.4f, if (rare) gold else Color.White, .9f)
        }
        4 -> {
            path("M-10 -1 L0 -12 L10 -1 L10 13 L-10 13Z", linear)
            path("M-12 0 L0 -13.5 L12 0 L10 1 L0 -10 L-10 1Z", Color.White)
            stroke("M-11 -.5 q2 3 4 .5 q2 3 4 -1", Color.White, 1.6f)
            rect(-6.5f, 2f, 5.5f, 5f, c(0xFFFFD877))
            stroke("M-3.8 2 v5 M-6.5 4.5 h5.5", dark, .6f)
            path("M2 13 v-7 q2.5 -2.5 5 0 v7Z", dark)
            rect(4f, -11f, 3f, 5f, dark)
        }
        5 -> {
            path("M0 -13 Q9 -3 6.5 13 L-6.5 13 Q-9 -3 0 -13Z", c(0xFFE6E9EF))
            path("M0 -13 Q9 -3 6.5 13 L2 13 Q4 -2 0 -13Z", c(0xFF9AA2B0), .5f)
            path("M-6.5 5 L-12 15 L-6.5 13Z M6.5 5 L12 15 L6.5 13Z", col)
            path("M0 -13 Q3 -9 4 -6 L-4 -6 Q-3 -9 0 -13Z", col)
            circle(0f, 0f, 3.6f, radial); ring(0f, 0f, 3.6f, c(0xFFC8CCD4), 1.2f)
            rect(-4.5f, 7f, 9f, 2f, c(0xFFA8202A), .8f)
            path("M-4 13 L0 21 L4 13Z", c(0xFFFFB347)); path("M-2 13 L0 17 L2 13Z", c(0xFFFFF3A0))
        }
        6 -> {
            ellipse(0f, 1f, 10f, 12.5f, radial)
            for (x in listOf(-6f, -3f, 0f, 3f, 6f)) stroke("M$x -11 Q${x * 1.7f} 1 $x 13", dark, .8f, .6f)
            rect(-5f, -12f, 10f, 2.5f, capBrush); rect(-5f, 12f, 10f, 2.5f, capBrush)
            line(0f, 14.5f, 0f, 18.5f, gold, 1.2f); circle(0f, 19.5f, 1.4f, gold)
            shine(-4f, -4f, 1.8f, 4f)
        }
        else -> {
            path("M0 -12 Q9 -12 9 2 Q9 9 13 12 L-13 12 Q-9 9 -9 2 Q-9 -12 0 -12Z", radial)
            path("M-13 12 L13 12 L12 14.5 L-12 14.5Z", if (rare) gold else light)
            circle(0f, 16.5f, 2.4f, gold)
            stroke("M-9 4 Q0 7 9 4", if (rare) gold else Color.White, 1.2f, .8f)
            shine(-4f, -4f, 1.8f, 4.5f)
        }
    }
    reflect?.let { circle(5f, -4f, 1.6f, it, .85f); glow(5f, -4f, 4f, it, .5f) }
    if (rare) for ((i, pt) in listOf(-6f to 4f, 5f to -3f, 2f to 8f, -3f to -7f, 7f to 6f, -8f to -2f, 0f to 0f, 4f to 3f).withIndex())
        circle(pt.first, pt.second, if (i % 3 != 0) .7f else 1.1f, c(0xFFFFF6C8))
}
