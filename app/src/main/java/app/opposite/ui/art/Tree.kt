package app.opposite.ui.art

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.lerp
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.pow
import kotlin.math.sin
import kotlin.random.Random

const val TREE_W = 320f
const val TREE_H = 460f

/**
 * The season's tree, grown branch by branch: nine whorls of branches around the trunk, each branch a
 * drooping stem with needles on both sides and side twigs, back branches in shadow and front ones in
 * lamplight; then silver rain, fluffy tinsel, a lantern-bulb garland, the Soviet spire on top, cotton
 * snow, Grandfather Frost and the Snow Maiden underneath. The heavy part ([base]) is drawn once into a
 * bitmap; bulbs and ornaments ([live]) are drawn over it every frame.
 */
object TreeArt {
    private const val CX = 160f
    private const val TOP = 46f
    private const val BOTTOM = 368f
    private const val WHORLS = 10

    class Branch(val ax: Float, val ay: Float, val tx: Float, val ty: Float, val depth: Float, val len: Float, val whorl: Int) {
        val mx get() = ax + (tx - ax) * .5f
        val my get() = ay - 3f + (ty - ay) * .25f
        fun at(t: Float): Offset {
            val u = 1 - t
            return Offset(u * u * ax + 2 * u * t * mx + t * t * tx, u * u * ay + 2 * u * t * my + t * t * ty)
        }
        fun tangent(t: Float): Offset = Offset(2 * (1 - t) * (mx - ax) + 2 * t * (tx - mx), 2 * (1 - t) * (my - ay) + 2 * t * (ty - my))
    }

    class Bulb(val x: Float, val y: Float, val color: Color, val phase: Float)

    private fun halfWidth(y: Float): Float {
        val f = ((y - TOP) / (BOTTOM - TOP)).coerceIn(0f, 1f)
        return 10f + f.pow(.95f) * 138f
    }

    val branches: List<Branch> by lazy {
        val r = Random(1971)
        val out = mutableListOf<Branch>()
        for (i in 0 until WHORLS) {
            val f = (i + .55f) / WHORLS
            val ay = TOP + 6f + f.pow(1.08f) * (BOTTOM - TOP - 34f)
            val len = 16f + f * 132f
            val n = 7 + i * 2
            for (k in 0 until n) {
                val phi = (k + r.nextFloat() * .6f) / n * 2 * PI.toFloat()
                val side = cos(phi)
                val depth = sin(phi)
                val l = len * (.85f + r.nextFloat() * .25f)
                val tx = CX + side * l * .98f
                val ty = ay + l * .3f + depth * l * .12f + r.nextFloat() * 4f
                out += Branch(CX + side * 2f, ay, tx, ty, depth, l, i)
            }
        }
        out.sortedBy { it.depth }
    }

    /** Where the 24 ornaments hang: tips of front branches, kept apart. */
    val slots: List<Offset> by lazy {
        val r = Random(24)
        val tips = branches.filter { it.depth > .05f && it.whorl >= 1 }.map { it.at(.82f + r.nextFloat() * .1f) }.shuffled(r)
        val pick = mutableListOf<Offset>()
        for (p in tips) { if (pick.all { hypot(it.x - p.x, it.y - p.y) > 31f }) pick += p; if (pick.size == 24) break }
        for (p in tips) { if (pick.size >= 24) break; if (p !in pick) pick += p }
        pick.sortedBy { it.y }.let { sorted -> List(24) { sorted[(it * 7) % 24] } }
    }

    private val BULB_COLORS = listOf(0xFFFF5A4A, 0xFFFFD24A, 0xFF5AD67A, 0xFF5AA8FF, 0xFFFF8AD8).map { c(it) }

    private fun swag(y: Float, tilt: Float, sag: Float, span: Float = .8f): (Float) -> Offset {
        val hw = halfWidth(y) * span
        val x0 = CX - hw; val x1 = CX + hw
        val y0 = y - tilt; val y1 = y + tilt
        val my = (y0 + y1) / 2 + sag * 2
        return { t -> val u = 1 - t; Offset(u * u * x0 + 2 * u * t * CX + t * t * x1, u * u * y0 + 2 * u * t * my + t * t * y1) }
    }

    private val garlands = listOf(Triple(112f, 5f, 8f), Triple(178f, -7f, 11f), Triple(246f, 6f, 13f), Triple(318f, -5f, 15f))
    private val tinsels = listOf(Triple(146f, -6f, 9f, ) to c(0xFFE8ECF2), Triple(214f, 7f, 12f) to c(0xFFE8C66A), Triple(286f, -6f, 14f) to c(0xFFE8ECF2))

    val bulbs: List<Bulb> by lazy {
        val out = mutableListOf<Bulb>()
        garlands.forEachIndexed { gi, (y, tilt, sag) ->
            val f = swag(y, tilt, sag)
            val n = (halfWidth(y) * 1.6f / 17f).toInt().coerceAtLeast(3)
            for (b in 0..n) { val p = f(b / n.toFloat()); out += Bulb(p.x, p.y, BULB_COLORS[(b + gi * 2) % 5], (b * 1.7f + gi) % 6.28f) }
        }
        out
    }

    fun nearestBulb(p: Offset): Color = bulbs.minBy { hypot(it.x - p.x, it.y - p.y) }.color

    private fun needleColor(depth: Float, lightSide: Float, r: Random): Color {
        val shadow = c(0xFF0C2615)
        val mid = c(0xFF2A6339)
        val lit = c(0xFF5FAE69)
        val k = ((depth + 1) / 2 * .75f + lightSide * .35f + (r.nextFloat() - .5f) * .22f).coerceIn(0f, 1f)
        val base = if (k < .5f) lerp(shadow, mid, k * 2) else lerp(mid, lit, (k - .5f) * 2)
        return if (r.nextFloat() < .12f) lerp(base, c(0xFF3F7A6A), .5f) else base
    }

    /** Room, tree, rain, tinsel, spire, snow, figures, gifts — everything that doesn't move. */
    fun base(sk: Sk) = with(sk) {
        val r = Random(2026)
        room()
        // ствол и тёмная сердцевина, чтобы между ветками не просвечивало
        rect(CX - 8, 330f, 16f, 70f, Brush.horizontalGradient(listOf(c(0xFF8A5A34), c(0xFF3A2414)), CX - 8, CX + 8))
        val core = StringBuilder("M$CX ${TOP - 4}")
        var y = TOP
        while (y <= BOTTOM - 10) { core.append(" L${CX - halfWidth(y) * .72f} $y"); y += 8f }
        y = BOTTOM - 10
        while (y >= TOP) { core.append(" L${CX + halfWidth(y) * .72f} $y"); y -= 8f }
        core.append("Z")
        path(core.toString(), Brush.horizontalGradient(listOf(c(0xFF163A22), c(0xFF0A1F12)), CX - 120, CX + 120))

        for (b in branches) branch(b, r)

        // дождик
        for (k in 0 until 70) {
            val b = branches.filter { it.depth > 0f }.let { it[r.nextInt(it.size)] }
            val p = b.at(.45f + r.nextFloat() * .5f)
            val len = 14f + r.nextFloat() * 26f
            val sway = (r.nextFloat() - .5f) * 4f
            stroke("M${p.x} ${p.y} q$sway ${len / 2} ${sway * .3f} $len", c(0xFFDDE4EE), .55f, .55f + r.nextFloat() * .3f)
            if (r.nextFloat() < .5f) circle(p.x + sway * .4f, p.y + len * (.3f + r.nextFloat() * .6f), .9f, Color.White, .9f)
        }
        // мишура
        for ((swg, col) in tinsels) {
            val f = swag(swg.first, swg.second, swg.third, .74f)
            var t = 0f
            while (t <= 1f) {
                val p = f(t)
                for (q in 0 until 5) {
                    val a = r.nextFloat() * 2 * PI.toFloat()
                    val l = 2.5f + r.nextFloat() * 3.5f
                    val shade = if (r.nextFloat() < .3f) col.shade(-.35f) else if (r.nextFloat() < .2f) Color.White else col
                    line(p.x, p.y, p.x + cos(a) * l, p.y + sin(a) * l, shade, .7f, .9f)
                }
                if (r.nextFloat() < .12f) sparkle(p.x + (r.nextFloat() - .5f) * 6, p.y + (r.nextFloat() - .5f) * 6, 2.6f, .9f)
                t += .007f
            }
        }
        // провод гирлянды
        for ((yy, tilt, sag) in garlands) {
            val f = swag(yy, tilt, sag)
            val sb = StringBuilder()
            for (i in 0..40) { val p = f(i / 40f); sb.append(if (i == 0) "M${p.x} ${p.y}" else " L${p.x} ${p.y}") }
            stroke(sb.toString(), c(0xFF142414), .9f)
        }
        spire()
        under(r)
        grain(0f, 0f, TREE_W, TREE_H, .07f)
    }

    private fun Sk.branch(b: Branch, r: Random) {
        val depthK = (b.depth + 1) / 2
        val stem = lerp(c(0xFF2A1C10), c(0xFF5A4A2A), depthK)
        val steps = (b.len / 2.1f).toInt().coerceAtLeast(6)
        // стебель
        stroke("M${b.ax} ${b.ay} Q${b.mx} ${b.my} ${b.tx} ${b.ty}", stem, 1.6f - (1 - depthK) * .4f, .9f)
        for (s in 1..steps) {
            val t = s / steps.toFloat()
            val p = b.at(t)
            val tg = b.tangent(t)
            val ang = atan2(tg.y, tg.x)
            val nl = (8.5f - t * 3.5f) * (.85f + r.nextFloat() * .3f) * (.75f + b.len / 280f)
            for (side in listOf(-1f, 1f)) {
                repeat(2) {
                    val spread = (.55f + r.nextFloat() * .55f) * side
                    val a = ang + spread + .25f
                    val ex = p.x + cos(a) * nl
                    val ey = p.y + sin(a) * nl
                    val lightSide = if (side < 0) .9f else .2f
                    line(p.x, p.y, ex, ey, needleColor(b.depth, lightSide * (if (b.tx < CX) 1.1f else .8f), r), 1.05f + r.nextFloat() * .35f, cap = StrokeCap.Round)
                }
            }
            // боковые веточки
            if (s % (steps / 3).coerceAtLeast(2) == 0 && t in .25f..0.85f) {
                for (side in listOf(-1f, 1f)) {
                    val a = ang + side * .55f
                    val tl = b.len * .22f * (1 - t * .5f)
                    for (q in 1..6) {
                        val u = q / 6f
                        val sx = p.x + cos(a) * tl * u
                        val sy = p.y + sin(a) * tl * u + u * u * 2
                        for (sd in listOf(-1f, 1f)) {
                            val na = a + sd * (.7f + r.nextFloat() * .3f) + .2f
                            val l2 = (5.5f - u * 2) * (.8f + r.nextFloat() * .3f)
                            line(sx, sy, sx + cos(na) * l2, sy + sin(na) * l2, needleColor(b.depth - .05f, if (sd < 0) .8f else .15f, r), 1f)
                        }
                    }
                }
            }
        }
        // светлые молодые побеги на кончике
        if (b.depth > -.3f) {
            val p = b.at(1f); val tg = b.tangent(1f); val ang = atan2(tg.y, tg.x)
            for (q in 0 until 5) {
                val a = ang + (r.nextFloat() - .5f) * 1.4f
                val l = 3.5f + r.nextFloat() * 3f
                line(p.x, p.y, p.x + cos(a) * l, p.y + sin(a) * l, c(0xFF7CC47A), 1f, .7f + depthK * .3f)
            }
        }
    }

    private fun Sk.room() {
        rect(0f, 0f, TREE_W, TREE_H, Brush.verticalGradient(listOf(c(0xFF2A2236), c(0xFF46354A)), 0f, 400f))
        var x = 0f
        while (x < TREE_W) { rect(x, 0f, 10f, 400f, Color.White, .025f); x += 24f }
        var y = 24f
        var row = 0
        while (y < 390f) {
            var fx = 12f + (row % 2) * 12f
            while (fx < TREE_W) { path("M$fx ${y - 4} l3 4 l-3 4 l-3 -4Z", c(0xFFC9A25A), .12f); fx += 24f }
            y += 34f; row++
        }
        // окно со снегом
        rect(14f, 34f, 66f, 110f, Brush.verticalGradient(listOf(c(0xFF0B1530), c(0xFF1A2A55)), 34f, 144f))
        val r = Random(5)
        repeat(26) { circle(16f + r.nextFloat() * 62, 36f + r.nextFloat() * 100, .6f + r.nextFloat() * 1.1f, Color.White, .8f) }
        path("M14 130 q20 -8 34 -2 q16 -6 32 0 v14 h-66Z", c(0xFFC9D4EA))
        stroke("M12 32 h70 v114 h-70Z", c(0xFFE4E6EC), 4f)
        stroke("M47 34 v110 M14 72 h66", c(0xFFE4E6EC), 3f, cap = StrokeCap.Butt)
        rect(8f, 144f, 78f, 6f, c(0xFFD8DCE4))
        path("M8 26 h36 q-8 60 4 126 h-40Z", c(0xFF7A2A30), .92f)
        stroke("M18 26 q-4 60 2 126 M30 26 q-6 60 4 126", c(0xFF5A1A20), 2f, .5f)
        // пол и ковёр
        rect(0f, 398f, TREE_W, 62f, Brush.verticalGradient(listOf(c(0xFF3A2618), c(0xFF6A4A30)), 398f, 460f))
        for (i in 0 until 11) line(i * 36f - 10, 398f, i * 36f - 34, 460f, c(0xFF2A1A10), 1f, .5f)
        rect(0f, 394f, TREE_W, 5f, c(0xFF2A1A10))
        ellipse(CX, 430f, 140f, 20f, c(0xFF6A2028))
        ds.drawOval(c(0xFFD8A84A), Offset(CX - 124, 415f), androidx.compose.ui.geometry.Size(248f, 30f), style = androidx.compose.ui.graphics.drawscope.Stroke(1.5f, pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(6f, 3f))))
        glow(CX, 250f, 200f, c(0xFFFFCF7A), .35f)
    }

    private fun Sk.spire() {
        val sp = Brush.horizontalGradient(listOf(c(0xFFFF9A8A), c(0xFFD42A2A), c(0xFF6A0A10)), CX - 10, CX + 10)
        glow(CX, 30f, 30f, c(0xFFFF6A5A), .45f)
        path("M$CX -4 Q${CX + 3} 12 ${CX + 4.5f} 22 L${CX - 4.5f} 22 Q${CX - 3} 12 $CX -4Z", sp)
        circle(CX, 21f, 5f, sp)
        ellipse(CX, 33f, 10.5f, 10f, sp)
        rect(CX - 5, 42f, 10f, 8f, Brush.horizontalGradient(listOf(c(0xFF8A6A20), c(0xFFFFE9A0), c(0xFF9A7420)), CX - 5, CX + 5))
        stroke("M${CX - 10} 33 Q$CX 38 ${CX + 10} 33 M${CX - 9} 29 Q$CX 33 ${CX + 9} 29", c(0xFFFFD877), 1.2f)
        for (x in listOf(-6f, -2f, 2f, 6f)) circle(CX + x, 31f + abs(x) * .2f, .9f, c(0xFFFFF0B0))
        stroke("M${CX - 4.5f} 21 h9", c(0xFFFFD877), 1.4f)
        group(CX - 4f, 28f, rotate = -25f) { ellipse(0f, 0f, 2.2f, 4.6f, Color.White, .65f) }
        line(CX - .8f, 2f, CX - .2f, 17f, Color.White, .9f, .6f)
        sparkle(CX + 7, 6f, 4f, .9f)
    }

    private fun Sk.under(r: Random) {
        // вата
        ellipse(CX, 400f, 70f, 12f, c(0xFFE8ECF4))
        for (i in 0 until 16) circle(CX - 66 + i * 8.8f, 396f + sin(i * 1.3f) * 2.5f, 7f + r.nextFloat() * 3f, c(0xFFF7F9FC))
        for (i in 0 until 14) circle(CX - 60 + r.nextFloat() * 120, 392f + r.nextFloat() * 12, .8f, c(0xFFBFD8FF), .9f)
        // подарки
        gift(196f, 388f, 40f, 28f, c(0xFF2F5A8A), c(0xFFE8C24A))
        gift(232f, 398f, 30f, 20f, c(0xFFC8483C), c(0xFFF4ECD9))
        gift(172f, 402f, 26f, 16f, c(0xFFE8C24A), c(0xFFC8483C))
        gift(56f, 404f, 28f, 18f, c(0xFF4A7A3A), c(0xFFF4ECD9))
        // ватные Дед Мороз и Снегурочка
        cottonFigure(108f, 418f, c(0xFFC8302C), beard = true)
        cottonFigure(132f, 420f, c(0xFF5A9AD8), beard = false)
    }

    private fun Sk.cottonFigure(x: Float, y: Float, coat: Color, beard: Boolean) = group(x, y, if (beard) 1f else .85f) {
        ellipse(0f, 2f, 17f, 4f, Color.Black, .3f)
        path("M-15 0 Q-12 -30 0 -40 Q12 -30 15 0Z", coat)
        path("M5 -36 Q12 -28 15 0 L8 0 Q8 -24 2 -38Z", Color.Black, .18f)
        path("M-15 0 Q0 4 15 0 L14 -4 Q0 0 -14 -4Z", Color.White)
        line(-1f, -38f, -1f, 0f, Color.White, 2.5f)
        for (yy in listOf(-30f, -20f, -10f)) circle(-1f, yy, .9f, c(0xFFE8C24A))
        circle(0f, -44f, 7f, c(0xFFF2C8A8))
        if (beard) path("M-7 -44 Q-8 -32 0 -26 Q8 -32 7 -44 Q4 -40 0 -41 Q-4 -40 -7 -44Z", Color.White)
        else { path("M-7 -46 Q-9 -36 -6 -28 L-4 -30 Q-6 -38 -5 -44Z", c(0xFFE8C470)); path("M-4 -28 q0 10 2 18", c(0xFFE8C470)); stroke("M-4 -30 q-1 10 1 20", c(0xFFE8C470), 2.6f) }
        path("M-7.5 -46 Q-8 -56 0 -57 Q8 -56 7.5 -46Z", coat)
        stroke("M-8 -46 h16", Color.White, 2.6f)
        circle(-2.5f, -44.5f, .9f, c(0xFF222222)); circle(2.5f, -44.5f, .9f, c(0xFF222222))
        circle(0f, -42f, 1.4f, c(0xFFE8806E))
        circle(-4.5f, -42f, 1.6f, c(0xFFE8806E), .4f); circle(4.5f, -42f, 1.6f, c(0xFFE8806E), .4f)
        if (beard) {
            line(-14f, -22f, -19f, -12f, coat, 5f)
            line(-19f, -12f, -21f, -36f, c(0xFFC9B07A), 1.6f)
            circle(-21f, -37f, 2.2f, c(0xFF9AD0FF))
        }
        for (k in 0 until 6) circle(-10f + k * 4, -6f - (k % 2) * 3, .7f, c(0xFFBFD8FF), .9f)
    }

    /** Ornaments of lit windows and the twinkling bulbs, over [base]. [t] is seconds. */
    fun live(sk: Sk, ornaments: List<Pair<Int, Boolean>>, t: Float) = with(sk) {
        for (b in bulbs) {
            val a = .55f + .45f * sin(t * 1.7f + b.phase)
            glow(b.x, b.y + 4, 15f, b.color, .38f * a)
        }
        for ((d, rare) in ornaments) {
            val p = slots[d - 1]
            line(p.x, p.y - 2, p.x, p.y + 6, c(0xFFD8D8D8), .6f)
            group(p.x, p.y + 20, .85f) { ornament(d, rare, nearestBulb(p)) }
        }
        for (b in bulbs) {
            val a = .55f + .45f * sin(t * 1.7f + b.phase)
            rect(b.x - 1.4f, b.y - 1.2f, 2.8f, 2.6f, c(0xFF2A2A2A))
            path("M${b.x} ${b.y + 1.2f} q-3.2 2.6 -2.2 5 q.8 2 2.2 2 q1.4 0 2.2 -2 q1 -2.4 -2.2 -5Z", b.color, .55f + .45f * a)
            ellipse(b.x - .7f, b.y + 3.6f, .7f, 1.4f, Color.White, .75f)
        }
    }

    /** Which ornament (window) is at tree point (x, y), among [days]. */
    fun hit(x: Float, y: Float, days: Collection<Int>): Int? =
        days.minByOrNull { val p = slots[it - 1]; hypot(p.x - x, p.y + 20 - y) }?.takeIf { val p = slots[it - 1]; hypot(p.x - x, p.y + 20 - y) < 22f }
}
