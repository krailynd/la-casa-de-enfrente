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
    val atmosphere: app.opposite.domain.astronomy.SkyAtmosphere? = null,
    val bubble: app.opposite.ui.ambient.ActiveBubble? = null,
    val carProgress: Float = 0f,
)

private val FRAME = c(0xFFE8E8EE)
private val WARM_TOP = c(0xFFF6C667)
private val WARM_BOT = c(0xFFC81D31)

enum class SceneryTarget { MOON, STARS, SNOWMAN, STREETLIGHT, DOOR, RATA_ASPIRABLE, PAREJA, CAR, CAT, PINE }

/** Detects interactive touch targets in the outdoor scenery. */
fun sceneryHit(x: Float, y: Float, carX: Float = -100f): SceneryTarget? {
    val pt = Offset(x, y)
    val moonDist = (pt - Offset(300f, 40f)).getDistance()
    val snowmanDist = (pt - Offset(232f, 490f)).getDistance()
    val rataDist = (pt - Offset(210f, 460f)).getDistance()
    val parejaDist = (pt - Offset(135f, 480f)).getDistance()
    val catDist = (pt - Offset(286f, 484f)).getDistance()
    val carDist = (pt - Offset(carX, 496f)).getDistance()

    return when {
        moonDist < 36f -> SceneryTarget.MOON
        snowmanDist < 30f -> SceneryTarget.SNOWMAN
        rataDist < 26f -> SceneryTarget.RATA_ASPIRABLE
        parejaDist < 32f -> SceneryTarget.PAREJA
        catDist < 24f -> SceneryTarget.CAT
        carDist < 36f -> SceneryTarget.CAR
        y in 0f..70f && x < 260f -> SceneryTarget.STARS
        Rect(10f, 400f, 52f, 480f).contains(pt) -> SceneryTarget.STREETLIGHT
        Rect(150f, 385f, 210f, 470f).contains(pt) -> SceneryTarget.DOOR
        Rect(40f, 440f, 85f, 500f).contains(pt) -> SceneryTarget.PINE
        else -> null
    }
}

/** The five-storey house across the yard at dusk (or by day), with its 24 windows. */
fun Sk.facade(look: FacadeLook) {
    val atmo = look.atmosphere
    val isNight = atmo?.isNight ?: (look.dusk || look.allLit)
    val isSunset = atmo?.isSunset ?: false
    val isDay = atmo?.isDayTime ?: (!isNight && !isSunset)

    // Cielo astronómico sincronizado con hora real de Perú (o simulada)
    val sky = if (atmo != null) {
        listOf(atmo.skyTop, atmo.skyMid, atmo.skyHorizon)
    } else if (isNight) {
        listOf(c(0xFF070E22), c(0xFF141F3D), c(0xFF263255))
    } else if (isSunset) {
        listOf(c(0xFF381528), c(0xFF8B2535), c(0xFFD47348))
    } else {
        listOf(c(0xFF8FA6C4), c(0xFFB9C6D6), c(0xFFD9D6D0))
    }
    rect(0f, 0f, HOUSE_W, HOUSE_H, Brush.verticalGradient(0f to sky[0], .6f to sky[1], 1f to sky[2], startY = 0f, endY = HOUSE_H))

    // Estrellas y luna en noche
    if (isNight) {
        for (i in 0 until 35) {
            val sx = ((i * 137 + 19) % 350 + 5).toFloat()
            val sy = ((i * 71 + 11) % 65 + 5).toFloat()
            val twinkle = 0.4f + 0.6f * kotlin.math.abs(kotlin.math.sin(look.t * 2.5f + i))
            circle(sx, sy, if (i % 6 == 0) 1.4f else 0.8f, Color.White, twinkle)
        }
        // Luna con sombra creciente y brillo suave
        glow(300f, 40f, 22f, c(0xFFF4ECD0), 0.35f)
        circle(300f, 40f, 13f, c(0xFFF4ECD0))
        circle(306f, 36f, 12f, sky[0])
    } else if (isSunset) {
        // Sol poniente carmesí y dorado
        glow(200f, 55f, 32f, c(0xFFF58A42), 0.5f)
        circle(200f, 55f, 16f, c(0xFFFFD27A))
    } else {
        // Sol radiante diurno
        glow(280f, 45f, 36f, c(0xFFFFE082), 0.45f)
        circle(280f, 45f, 15f, c(0xFFFFF9C4))
    }

    // Siluetas de la ciudad lejana
    val far = if (isNight) c(0xFF141F3D) else if (isSunset) c(0xFF4A2535) else c(0xFF8392AA)
    path("M0 120 h20 v-30 h40 v30 h10 v-50 v460 H0Z", far)
    path("M340 140 h20 v400 h-30 v-380 h10Z", far)
    if (isNight || isSunset) {
        listOf(8f to 100f, 34f to 105f, 46f to 130f, 8f to 150f, 34f to 190f, 345f to 170f, 345f to 230f).forEachIndexed { i, (x, y) ->
            if (i % 3 != 1) rect(x, y, 7f, 9f, c(0xFFE9B45A), .55f)
        }
    }

    // Chimenea con humo animado en el tejado
    val chimX = 85f
    val chimY = 66f
    rect(chimX - 8f, chimY - 14f, 16f, 14f, c(0xFF5A1A22))
    for (i in 0 until 4) {
        val sp = (look.t * 1.8f + i * 0.75f) % 3.0f
        val sy = chimY - 16f - sp * 14f
        val sx = chimX + kotlin.math.sin(sp * 2.2f) * (4f + sp * 3f)
        val sRadius = 3f + sp * 2.8f
        circle(sx, sy, sRadius, Color.White, (1f - (sp / 3f)).coerceIn(0f, 0.4f))
    }

    // Fachada del edificio
    val wallColor = if (isNight) c(0xFF283654) else if (isSunset) c(0xFF6B3645) else c(0xFFB3A596)
    rect(30f, 72f, 300f, 392f, wallColor)
    for (k in 0 until 10) rect(30f + k * 30f, 72f, 1f, 392f, Color.Black, .05f)
    rect(26f, 66f, 308f, 8f, if (isNight) c(0xFF1E2842) else if (isSunset) c(0xFF4A2430) else c(0xFF8D8173))
    rect(26f, 62f, 308f, 5f, c(0xFFEEF3FA))
    stroke("M90 66 v-26 M80 46 h20 M84 52 h12 M250 66 v-20 M242 50 h16", if (isNight) c(0xFF141B2D) else c(0xFF5A5248), 1.6f)
    for (f in 1..4) line(30f, 80f + f * 76f, 330f, 80f + f * 76f, if (isNight) c(0xFF24304F) else c(0xFFA39584), 1f)

    // Guirnalda festiva de luces en el tejado
    val lightColors = listOf(0xFFFF3344, 0xFFFFDD44, 0xFF44DD66, 0xFF44AAFF)
    for (i in 0 until 32) {
        val lx = 32f + i * 9.2f
        val ly = 73f
        val pulse = 0.5f + 0.5f * kotlin.math.sin(look.t * 3f + i * 0.8f)
        val col = lightColors[i % 4]
        glow(lx, ly + 2f, 7f, c(col), 0.6f * pulse)
        circle(lx, ly + 2f, 2.2f, c(col))
    }

    // Carámbanos de hielo
    var ix = 34
    while (ix < 330) { val h = 4 + (ix * 13) % 9; path("M$ix 74 l2 $h l2 -${h}Z", c(0xFFE8F2FF), .85f); ix += 9 + (ix * 7) % 5 }
    rect(326f, 74f, 5f, 390f, if (isNight) c(0xFF1E2842) else c(0xFF8D8173))

    // Puerta de entrada
    rect(160f, 400f, 40f, 64f, if (isNight) c(0xFF1A243D) else c(0xFF6F5F4F))
    rect(164f, 404f, 32f, 60f, if (isNight) c(0xFF141B2E) else c(0xFF57483A))
    path("M152 398 h56 l-6 -10 h-44Z", if (isNight) c(0xFF24304F) else c(0xFF7B6D5F))
    line(152f, 388f, 208f, 388f, c(0xFFEEF3FA), 3f)

    // Corona navideña con lazo rojo en la puerta
    glow(180f, 418f, 14f, c(0xFF2A5A35), 0.35f)
    circle(180f, 418f, 7.5f, c(0xFF2D5A38))
    circle(180f, 418f, 3.8f, if (isNight) c(0xFF141B2E) else c(0xFF57483A))
    // Lazo rojo
    circle(180f, 412f, 2.2f, c(0xFFC81D31))
    path("M176 414 L180 412 L184 414 L182 419 L180 413 L178 419 Z", c(0xFFC81D31))

    // Farol de la entrada
    if (isNight || isSunset) {
        glow(180f, 394f, 26f, c(0xFFFFCF73), .5f)
        circle(180f, 394f, 3f, c(0xFFFFE7A6))
    }

    // Ventanas interactivas
    for (r in House.residents) window(r, look, isNight || isSunset)
    balconies(isNight || isSunset)

    // Suelo nevado (colores según noche/atardecer/día)
    val snowFar = if (isNight) c(0xFFBCCEEA) else if (isSunset) c(0xFFE8D0D6) else c(0xFFF2F4F8)
    val snowNear = if (isNight) c(0xFFD4E1F4) else if (isSunset) c(0xFFF7E6EB) else Color.White
    path("M0 464 Q90 456 180 466 T360 462 V540 H0Z", snowFar)
    path("M0 482 Q120 474 230 486 T360 480 V540 H0Z", snowNear)

    val dark = if (isNight) c(0xFF1E2842) else c(0xFF5F5A52)
    val pine = if (isNight) c(0xFF1E3245) else c(0xFF4F6A52)

    // Pino nevado exterior
    rect(60f, 470f, 3f, 40f, dark)
    path("M40 478 l22 -34 l22 34Z", pine)
    path("M44 490 l18 -26 l18 26Z", pine)
    // Nieve sobre el pino
    path("M40 478 Q62 472 84 478 L62 468 Z", Color.White, 0.8f)

    // Banco de madera
    rect(270f, 488f, 44f, 4f, dark)
    rect(274f, 492f, 3f, 10f, dark)
    rect(307f, 492f, 3f, 10f, dark)
    stroke("M268 488 q24 -6 48 0", Color.White, 3f)

    // Farola con cono de luz
    rect(18f, 420f, 3f, 90f, dark)
    stroke("M19 420 q0 -12 14 -12", dark, 3f)
    path("M27 408 h12 l-3 6 h-6Z", dark)
    if (isNight || isSunset) {
        path("M28 414 h10 l26 96 h-62Z", c(0xFFFFE7A6), .14f)
        glow(33f, 415f, 18f, c(0xFFFFCF73), .7f)
        circle(33f, 415f, 4f, c(0xFFFFF3C0))
    }

    // Muñeco de nieve
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

    // ==========================================
    // ENTIDADES VIVAS EN ESCALA PERFECTA 360x540
    // ==========================================
    val carX = 390f - (look.carProgress * 440f)

    // 1. Gatito Naranja en la Banca
    drawLivingCat(286f, 484f, look.t)

    // 2. Rata Aspirable 🐭🎸 (junto a la puerta)
    drawLivingRata(210f, 458f, look.t)

    // 3. Parejita Enamorada (Francesca & Elvis)
    val coupleX = 135f + kotlin.math.sin(look.t * 1.5f) * 10f
    drawLivingCouple(coupleX, 478f, look.t)

    // 4. Coche Vintage Rojo con Árbol
    if (carX in -45f..395f) {
        drawLivingVintageCar(carX, 498f, isNight || isSunset)
    }

    // 5. Viñeta Cómic de Diálogo inteligente directo sobre el personaje
    if (look.bubble != null) {
        drawLivingComicBubble(look.bubble, carX, coupleX, look.numbers)
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

private fun Sk.drawLivingCat(x: Float, y: Float, t: Float) {
    // Sombra
    ellipse(x, y + 3f, 7f, 2f, Color.Black, 0.2f)
    // Cuerpo esponjoso naranja
    ellipse(x, y, 6.5f, 4.5f, c(0xFFE67E22))
    // Pechito blanco
    circle(x + 2f, y + 1f, 2.5f, Color.White, 0.8f)
    // Cabeza
    circle(x + 3.8f, y - 3f, 3.6f, c(0xFFE67E22))
    // Orejitas con centro rosado
    path("M${x+1.5f} ${y-5.5f} L${x+3f} ${y-9f} L${x+4.5f} ${y-6f} Z", c(0xFFE67E22))
    circle(x + 3f, y - 6.5f, 1f, c(0xFFFFB6C1))
    path("M${x+4f} ${y-5.5f} L${x+6f} ${y-8.5f} L${x+6.8f} ${y-5.5f} Z", c(0xFFE67E22))
    circle(x + 5.5f, y - 6.5f, 1f, c(0xFFFFB6C1))
    // Ojos cerrados sonrientes (^ ^)
    stroke("M${x+2.2f} ${y-3f} l1 -1 l1 1 M${x+4.8f} ${y-3f} l1 -1 l1 1", Color(0xFF4A2500), 0.7f)
    // Naricita rosada
    circle(x + 4f, y - 1.6f, 0.6f, c(0xFFFF8B94))
    // Colita meneándose suavemente
    val tailWag = kotlin.math.sin(t * 3f) * 2f
    stroke("M${x-5f} ${y} Q${x-9f} ${y-4f+tailWag} ${x-12f} ${y-2f+tailWag}", c(0xFFE67E22), 1.6f)
    // Collar rojo
    stroke("M${x+1.8f} ${y-1f} h4", c(0xFFC81D31), 1.2f)
}

private fun Sk.drawLivingRata(x: Float, y: Float, t: Float) {
    // Sombra
    ellipse(x, y + 4f, 7f, 2.2f, Color.Black, 0.25f)
    // Cuerpo redondo de ratón gris
    ellipse(x, y, 5.5f, 4.5f, c(0xFF888482))
    // Cabecita
    circle(x - 1f, y - 5f, 4.2f, c(0xFF888482))
    // Orejas grandes con centro rosado
    circle(x - 4f, y - 8.5f, 2.5f, c(0xFF888482))
    circle(x - 4f, y - 8.5f, 1.4f, c(0xFFFFB6C1))
    circle(x + 2f, y - 8.5f, 2.5f, c(0xFF888482))
    circle(x + 2f, y - 8.5f, 1.4f, c(0xFFFFB6C1))
    // Ojos negros brillantes con destello blanco
    circle(x - 2.4f, y - 5.5f, 1f, Color.Black)
    circle(x - 2.8f, y - 5.8f, 0.4f, Color.White)
    circle(x + 0.4f, y - 5.5f, 1f, Color.Black)
    circle(x + 0.1f, y - 5.8f, 0.4f, Color.White)
    // Nariz rosada y bigotes
    circle(x - 1f, y - 3.2f, 0.8f, c(0xFFFF8B94))
    stroke("M${x-5f} ${y-3.5f} h3 M${x-5f} ${y-2.2f} h3 M${x+1f} ${y-3.5f} h3 M${x+1f} ${y-2.2f} h3", Color(0xFF444444), 0.6f)
    // Bufanda roja navideña con flecos
    rect(x - 4f, y - 2f, 8f, 2.5f, c(0xFFC81D31))
    path("M${x+1.5f} ${y-1f} l2 5 h2.5 l-2 -5 Z", c(0xFFC81D31))
    // Mini guitarra acústica en sus patitas
    ellipse(x + 3.5f, y + 1f, 3.2f, 4.2f, c(0xFFB57038))
    circle(x + 3.5f, y + 1f, 1.1f, Color(0xFF3B1D08)) // boca de guitarra
    line(x + 3.5f, y - 2f, x + 3.5f, y - 8f, c(0xFF8A4E1E), 1.2f) // mástil
    // Notas musicales flotando con el tiempo
    val notePhase = (t * 2.2f) % 3.0f
    if (notePhase < 2.0f) {
        val ny = y - 10f - notePhase * 9f
        val nx = x + 4f + kotlin.math.sin(notePhase * 3f) * 3f
        path("M$nx $ny v-4 h3 v4 Z", c(0xFFFFD27A), (1f - notePhase / 2f).coerceIn(0f, 0.9f))
    }
}

private fun Sk.drawLivingCouple(x: Float, y: Float, t: Float) {
    val walkStep = kotlin.math.sin(t * 3f) * 1.5f

    // Sombra compartida
    ellipse(x, y + 7f, 16f, 3f, Color.Black, 0.22f)

    // --- FRANCESCA (a la izquierda) ---
    val fx = x - 6f
    val fy = y + walkStep * 0.5f

    // Abrigo rojo carmesí de Francesca con caída elegante
    path("M${fx-6} ${fy+6} L${fx-2} ${fy-8} L${fx+4} ${fy+6} Z", c(0xFFC81D31))
    // Cuellito de peluche blanco
    ellipse(fx + 1f, fy - 8f, 3.8f, 1.8f, c(0xFFFFF5EB))
    // Rostro tierno y mejillas sonrojadas
    circle(fx + 1f, fy - 14f, 4.6f, c(0xFFFFDFD3))
    circle(fx - 1f, fy - 13f, 1.3f, c(0xFFFF8B94), 0.7f) // mejilla izq
    circle(fx + 3f, fy - 13f, 1.3f, c(0xFFFF8B94), 0.7f) // mejilla der
    // Ojos felices expresivos
    stroke("M${fx-0.8f} ${fy-15f} l1 -0.8 l1 0.8 M${fx+2.2f} ${fy-15f} l1 -0.8 l1 0.8", Color(0xFF331518), 0.8f)
    // Sonrisa dulce
    stroke("M${fx} ${fy-12f} q1 1 2 0", c(0xFFC81D31), 0.8f)
    // Cabello castaño largo
    path("M${fx-3.5f} ${fy-17f} q-4 8 0 16 q4 -6 0 -16 Z", c(0xFF4A2518))
    path("M${fx+5.5f} ${fy-17f} q3 8 0 16 q-3 -6 0 -16 Z", c(0xFF4A2518))
    // Boina / gorrito rojo carmesí con borla
    path("M${fx-3f} ${fy-17f} q4 -4 8 0 Z", c(0xFFC81D31))
    circle(fx + 1f, fy - 19.5f, 1.2f, c(0xFFFFF5EB))
    // Botitas de invierno
    rect(fx - 4f, fy + 5f, 2.5f, 4.5f, c(0xFF2B1D24))
    rect(fx, fy + 5f, 2.5f, 4.5f, c(0xFF2B1D24))

    // --- ELVIS (a la derecha) ---
    val ex = x + 6f
    val ey = y - walkStep * 0.5f

    // Abrigo oscuro moderno de Elvis
    rect(ex - 2f, ey - 8f, 7.5f, 14f, c(0xFF281E24))
    // Bufanda de lana crema
    rect(ex - 3f, ey - 8f, 8.5f, 2.5f, c(0xFFEDE4D8))
    path("M${ex+2f} ${ey-6f} v6 h2.5 v-6 Z", c(0xFFEDE4D8))
    // Rostro de Elvis
    circle(ex + 1.5f, ey - 14f, 4.6f, c(0xFFFFDFD3))
    circle(ex, ey - 13f, 1.2f, c(0xFFFF9AA2), 0.6f)
    circle(ex + 3f, ey - 13f, 1.2f, c(0xFFFF9AA2), 0.6f)
    // Ojos mirando a Francesca con cariño
    circle(ex + 0.2f, ey - 15f, 0.7f, Color(0xFF221115))
    circle(ex + 2.8f, ey - 15f, 0.7f, Color(0xFF221115))
    // Sonrisa
    stroke("M${ex+0.5f} ${ey-12f} q1 1 2 0", Color(0xFF442228), 0.8f)
    // Cabello oscuro moderno
    path("M${ex-2.5f} ${ey-17f} q4 -3 8 0 l-1 3 h-6 Z", c(0xFF1E171C))
    // Correa de guitarra en diagonal
    stroke("M${ex-2f} ${ey-8f} L${ex+5f} ${ey+4f}", c(0xFF8A5A38), 1.2f)
    // Botitas
    rect(ex - 1.5f, ey + 5f, 2.5f, 4.5f, c(0xFF1E171C))
    rect(ex + 2.5f, ey + 5f, 2.5f, 4.5f, c(0xFF1E171C))

    // Manos tomadas en el centro
    circle(x, y - 1f, 1.5f, c(0xFFFFDFD3))

    // Corazón rojo flotante pulsante entre ambos
    val heartPulse = 1f + 0.25f * kotlin.math.sin(t * 4f)
    val heartY = y - 24f + kotlin.math.sin(t * 2.5f) * 2f
    group(x, heartY, heartPulse) {
        glow(0f, 0f, 8f, c(0xFFE02438), 0.5f)
        path("M0 -1 C-2.5 -4.5 -6 -2.5 -6 1 C-6 4 -1 7.5 0 9.5 C1 7.5 6 4 6 1 C6 -2.5 2.5 -4.5 0 -1 Z", c(0xFFE02438))
    }
}

private fun Sk.drawLivingVintageCar(x: Float, y: Float, isNight: Boolean) {
    // Sombra del auto sobre la nieve
    ellipse(x, y + 6f, 26f, 4f, Color.Black, 0.3f)

    // Carrocería redondeada vintage rojo carmesí
    rect(x - 22f, y - 6f, 44f, 11f, c(0xFFC81D31))
    // Techo arqueado vintage
    path("M${x-14} ${y-6} Q${x} ${y-18} ${x+14} ${y-6} Z", c(0xFFC81D31))
    // Ventanas suaves
    path("M${x-11} ${y-6} Q${x} ${y-15} ${x+11} ${y-6} Z", c(0xFFE8F2FF), 0.85f)
    // Parachoques cromados
    rect(x - 24f, y + 1f, 3f, 4f, c(0xFFE0E0E0))
    rect(x + 21f, y + 1f, 3f, 4f, c(0xFFE0E0E0))

    // Árbol de navidad verde amarrado en el techo
    path("M${x-15} ${y-16} L${x+12} ${y-22} L${x+12} ${y-14} Z", c(0xFF265A30))
    // Cuerdas doradas sujetando el árbol
    line(x - 8f, y - 13f, x - 5f, y - 18f, c(0xFFFFD27A), 1f)
    line(x + 4f, y - 13f, x + 7f, y - 18f, c(0xFFFFD27A), 1f)

    // Ruedas negras con rines cromados
    circle(x - 12f, y + 5f, 4.5f, Color(0xFF1E1E1E))
    circle(x - 12f, y + 5f, 2f, c(0xFFCCCCCC))
    circle(x + 12f, y + 5f, 4.5f, Color(0xFF1E1E1E))
    circle(x + 12f, y + 5f, 2f, c(0xFFCCCCCC))

    // Faros delanteros (a la izquierda porque va hacia la izquierda)
    circle(x - 21.5f, y - 1f, 2f, c(0xFFFFF2B0))
    if (isNight) {
        // Cono de luz brillante hacia adelante
        glow(x - 22f, y - 1f, 14f, c(0xFFFFE7A6), 0.7f)
        path("M${x-22} ${y-1} L${x-70} ${y-14} L${x-70} ${y+16} Z", c(0xFFFFE7A6), 0.18f)
    }
}

private fun Sk.drawLivingComicBubble(
    bubble: app.opposite.ui.ambient.ActiveBubble,
    carX: Float,
    coupleX: Float,
    tm: androidx.compose.ui.text.TextMeasurer?
) {
    val (anchorX, anchorY) = when {
        bubble.speaker.contains("Rata", ignoreCase = true) -> 210f to 448f
        bubble.speaker.contains("Pareja", ignoreCase = true) || bubble.speaker.contains("Elvis", ignoreCase = true) -> coupleX to 458f
        bubble.speaker.contains("Coche", ignoreCase = true) || bubble.speaker.contains("Auto", ignoreCase = true) -> carX.coerceIn(50f, 310f) to 480f
        bubble.speaker.contains("Muñeco", ignoreCase = true) || bubble.speaker.contains("Nieve", ignoreCase = true) -> 232f to 472f
        bubble.speaker.contains("Gato", ignoreCase = true) || bubble.speaker.contains("Michi", ignoreCase = true) -> 286f to 472f
        bubble.speaker.contains("Luna", ignoreCase = true) -> 280f to 68f
        bubble.speaker.contains("Sol", ignoreCase = true) -> 180f to 68f
        bubble.speaker.contains("Farola", ignoreCase = true) -> 33f to 395f
        bubble.speaker.contains("Pino", ignoreCase = true) -> 62f to 440f
        else -> 180f to 400f
    }

    val bw = 210f
    val bh = 48f
    val bx = (anchorX - bw / 2f).coerceIn(8f, 360f - bw - 8f)
    val by = (anchorY - bh - 8f).coerceAtLeast(10f)

    // Sombra del bocadillo cómic
    rect(bx + 2f, by + 2f, bw, bh, Color.Black, 0.22f)
    // Fondo crema cálido
    rect(bx, by, bw, bh, c(0xFFFFF9F2))
    // Borde rojo carmesí
    stroke("M$bx $by h$bw v$bh h-$bw Z", c(0xFFC81D31), 1.4f)

    // Cola / flecha de la viñeta cómic apuntando hacia el personaje (▼)
    val tailX = anchorX.coerceIn(bx + 16f, bx + bw - 16f)
    val tailY = by + bh
    path("M${tailX - 6} $tailY L$tailX ${tailY + 7} L${tailX + 6} $tailY Z", c(0xFFFFF9F2))
    stroke("M${tailX - 6} $tailY L$tailX ${tailY + 7} L${tailX + 6} $tailY", c(0xFFC81D31), 1.4f)

    // Texto con TextMeasurer
    tm?.let { measurer ->
        val titleStyle = androidx.compose.ui.text.TextStyle(
            color = c(0xFFC81D31),
            fontSize = androidx.compose.ui.unit.TextUnit(10f / ds.density / ds.fontScale, androidx.compose.ui.unit.TextUnitType.Sp),
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
        )
        val bodyStyle = androidx.compose.ui.text.TextStyle(
            color = Color(0xFF1E0A12),
            fontSize = androidx.compose.ui.unit.TextUnit(9.5f / ds.density / ds.fontScale, androidx.compose.ui.unit.TextUnitType.Sp),
            fontFamily = app.opposite.ui.theme.Hand
        )

        val titleLayout = measurer.measure("✦ ${bubble.speaker}", titleStyle)
        val bodyLayout = measurer.measure(
            bubble.message.take(75) + if (bubble.message.length > 75) "…" else "",
            bodyStyle,
            constraints = androidx.compose.ui.unit.Constraints(maxWidth = (bw * ds.density - 24).toInt())
        )

        ds.drawText(titleLayout, topLeft = Offset(bx + 8f, by + 4f))
        ds.drawText(bodyLayout, topLeft = Offset(bx + 8f, by + 18f))
    }
}
