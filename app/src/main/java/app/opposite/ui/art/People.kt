package app.opposite.ui.art

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import app.opposite.domain.Look

/**
 * A neighbour, feet at (0, 0), about 125 units tall (a child is 0.7 of that, with a bigger head).
 * [mini] skips the face details for the tiny people seen in facade windows.
 */
fun Sk.person(p: Look, mini: Boolean = false) {
    val s = if (p.kid) .7f else 1f
    group(scale = s) { body(p, mini) }
}

private val PAPER = c(0xFFF4ECD9)
private val SHOE = c(0xFF2A1E18)
private val INK = c(0xFF2A1A14)

private fun Sk.body(p: Look, mini: Boolean) {
    val skin = c(p.skin)
    val top = c(p.top)
    val bot = c(p.bottom)
    val hair = c(p.hairColor)
    val topD = top.shade(-.32f)
    val hairD = hair.shade(-.35f)
    val dy = if (p.sit) 10f else 0f
    val hs = if (p.kid) 1.22f else 1f

    // ноги
    if (p.sit) {
        if (p.stool) {
            rect(-17f, -37f, 34f, 5f, c(0xFF7A5232), rx = 2f)
            stroke("M-12 -32 L-15 0 M12 -32 L15 0", c(0xFF5A3A22), 3f)
        }
        path("M-12 -31 L-11.5 -5 L-4 -5 L-4 -31Z M4 -31 L4 -5 L11.5 -5 L12 -31Z", if (p.skirt) c(0xFF6A5050) else bot)
        path("M-16 -43 h32 v9 q0 4 -4 4 h-24 q-4 0 -4 -4Z", bot)
        if (p.skirt) {
            path("M-18 -44 h36 l3 16 q-21 4 -42 0Z", bot)
            path("M2 -44 h16 l3 16 q-10 2 -19 1Z", Color.Black, .1f)
        }
    } else if (p.skirt) {
        stroke("M-9 -18 L-9 -4 M9 -18 L9 -4", c(0xFF5A4A4A), 5.5f, cap = androidx.compose.ui.graphics.StrokeCap.Butt)
        path("M-16 -51 L-22 -14 Q0 -9 22 -14 L16 -51Z", bot)
        path("M4 -51 L16 -51 L22 -14 Q14 -11 8 -11Z", Color.Black, .12f)
    } else {
        path("M-15 -51 L-13.5 -4 L-2.6 -4 L0 -35 L2.6 -4 L13.5 -4 L15 -51Z", bot)
        path("M2.6 -4 L0 -35 L4 -51 L15 -51 L13.5 -4Z", Color.Black, .14f)
    }
    path("M-15 -1 q0 -6 7 -6 q7 0 7 6Z M1 -1 q0 -6 7 -6 q7 0 7 6Z", SHOE)

    group(ty = dy) {
        if (p.hair == "long") head(hs) { path("M-13 -110 Q-18 -86 -15 -77 Q0 -74 15 -77 Q18 -86 13 -110Z", hair) }
        val torso = "M-16 -83 Q-21 -64 -18 -49 L18 -49 Q21 -64 16 -83 Q8 -91 0 -91 Q-8 -91 -16 -83Z"
        path(torso, top)
        clip(torso) {
            when (p.pattern) {
                "nordic" -> {
                    rect(-22f, -82f, 44f, 10f, PAPER)
                    val z = StringBuilder("M-22 -72")
                    var x = -22
                    while (x < 22) { z.append(" l3 -6 l3 6"); x += 6 }
                    z.append(" V-71 H-22Z")
                    path(z.toString(), top)
                    for (i in 0 until 8) circle(-19f + i * 5.5f, -66f, 1.2f, PAPER)
                    rect(-22f, -60f, 44f, 2f, PAPER)
                    rect(-22f, -53f, 44f, 2.4f, topD)
                }
                "stripes" -> { var y = -86f; while (y < -49) { rect(-22f, y, 44f, 3f, PAPER, .6f); y += 7 } }
                "cardigan" -> {
                    path("M-7 -92 L0 -58 L7 -92Z", c(0xFFEFE6D4))
                    stroke("M-7 -92 L0 -58 L0 -49 M7 -92 L0 -58", topD, 1.6f)
                    for (y in listOf(-70f, -62f, -54f)) circle(2.8f, y, 1.3f, topD)
                    stroke("M-15 -62 h8 v7 h-8Z", topD, 1f)
                }
                "hivis" -> { rect(-22f, -68f, 44f, 3.5f, c(0xFFDFE3EA)); rect(-22f, -59f, 44f, 3.5f, c(0xFFDFE3EA)) }
                "hoodie" -> {
                    stroke("M-10 -89 Q0 -80 10 -89", topD, 2f)
                    stroke("M-3 -86 v10 M3 -86 v12", PAPER, 1f)
                    path("M-10 -63 h20 l2 10 h-24Z", topD, .6f)
                }
                "apron" -> {
                    path("M-10 -82 h20 v33 h-20Z", c(0xFFE8DCC0))
                    stroke("M-10 -82 L-15 -89 M10 -82 L15 -89", c(0xFFE8DCC0), 2f)
                    stroke("M-5 -66 h10 v7 h-10Z", c(0xFFB8A888), 1f)
                }
                "scrubs" -> { path("M-6 -91 L0 -80 L6 -91Z", topD, .5f); stroke("M5 -74 h7 v6 h-7Z", topD, 1f) }
            }
            path("M6 -95 Q14 -70 9 -45 L24 -45 L24 -95Z", Color.Black, .16f)
            rect(-22f, -95f, 6f, 50f, Color.White, .07f)
        }
        p.shawl?.let { sc ->
            val sh = c(sc)
            path("M-19 -85 Q0 -60 19 -85 L20 -75 Q0 -46 -20 -75Z", sh)
            for (i in 0..8) {
                val t = i / 8f
                val x = -20 + t * 40
                val y = -75 + kotlin.math.sin(t * Math.PI).toFloat() * 28.5f
                line(x, y, x, y + 3, sh, 1f)
            }
            stroke("M-19 -85 Q0 -60 19 -85", sh.shade(.25f), 1f)
        }
        // шея и голова
        rect(-3.6f, -96f, 7.2f, 8f, skin)
        rect(-3.6f, -92f, 7.2f, 3f, Color.Black, .12f)
        head(hs) {
            ellipse(-11.2f, -103f, 2.6f, 3.6f, skin)
            ellipse(11.2f, -103f, 2.6f, 3.6f, skin)
            ellipse(0f, -104f, 11f, 12.6f, skin)
            path("M5 -115 Q13 -104 6 -93 Q11 -96 11 -104 Q11 -112 5 -115Z", c(0xFF7A3A20), .1f)
            if (p.beard) path("M-10.8 -104 Q-11.5 -88 0 -85 Q11.5 -88 10.8 -104 Q7 -95 0 -96.5 Q-7 -95 -10.8 -104Z", hair)
            if (!mini) face(p, hair, hairD)
            hairFront(p, hair)
        }
        if (p.bow) path("M-6 -93 l6 3 l6 -3 v6 l-6 -3 l-6 3Z", c(0xFFA8202A))
        arms(p, top, skin, mini)
    }
}

private fun Sk.head(scale: Float, block: Sk.() -> Unit) {
    if (scale == 1f) block() else group(0f, -104f, scale) { group(0f, 104f) { block() } }
}

private fun Sk.face(p: Look, hair: Color, hairD: Color) {
    if (p.sleepy) stroke("M-6 -104 q2 1.6 4 0 M2 -104 q2 1.6 4 0", INK, 1.1f)
    else {
        ellipse(-4f, -104f, 1.35f, 1.75f, INK); ellipse(4f, -104f, 1.35f, 1.75f, INK)
        circle(-3.5f, -104.7f, .5f, Color.White); circle(4.5f, -104.7f, .5f, Color.White)
    }
    stroke("M-6.2 -108.6 q2.2 -1.4 4.4 0 M1.8 -108.6 q2.2 -1.4 4.4 0", hairD, 1.1f)
    stroke("M0 -103 q1.7 2.8 -.6 3.5", c(0xFFA8644A), .9f)
    circle(-6.6f, -99.4f, 2.4f, c(0xFFE8806E), .42f); circle(6.6f, -99.4f, 2.4f, c(0xFFE8806E), .42f)
    if (p.freckles) for ((x, y) in listOf(-7f to -101f, -5.5f to -100f, -8f to -99.5f, 7f to -101f, 5.5f to -100f, 8f to -99.5f)) circle(x, y, .45f, c(0xFFA0582A))
    if (p.moustache) path("M-5.6 -98.6 Q-2.6 -101.2 0 -99.6 Q2.6 -101.2 5.6 -98.6 Q2.6 -97.2 0 -98.3 Q-2.6 -97.2 -5.6 -98.6Z", hair)
    stroke("M-2.8 -97 q2.8 2.3 5.6 0", c(0xFF8A3A2A), 1.1f)
    if (p.glasses) {
        circle(-4f, -104f, 3.7f, Color.White, .12f); circle(4f, -104f, 3.7f, Color.White, .12f)
        ring(-4f, -104f, 3.7f, c(0xFF3A2A20), .9f); ring(4f, -104f, 3.7f, c(0xFF3A2A20), .9f)
        stroke("M-.3 -104.5 q.3 -1 .6 0 M-7.7 -104.5 l-3 -1 M7.7 -104.5 l3 -1", c(0xFF3A2A20), .8f)
    }
}

private const val CAP = "M-11.8 -103 Q-12.5 -119 0 -119 Q12.5 -119 11.8 -103 Q9 -112 2 -112 Q-2 -109 -6 -112 Q-10 -110 -11.8 -103Z"

private fun Sk.hairFront(p: Look, hair: Color) {
    val hat = c(p.hat)
    fun shine() = stroke("M-6 -115.5 q4 -2 8 -.8", Color.White, 1f, .25f)
    when (p.hair) {
        "short" -> { path(CAP, hair); shine() }
        "bob", "long" -> { path("M-12.8 -96 Q-15 -119 0 -119 Q15 -119 12.8 -96 L10.6 -97 Q11 -110 3 -112 Q-4 -107 -10.6 -103 L-10.6 -97Z", hair); shine() }
        "bun" -> { circle(0f, -120f, 6.2f, hair); path(CAP, hair); shine() }
        "pigtails" -> {
            circle(-15f, -104f, 4.6f, hair); circle(15f, -104f, 4.6f, hair)
            circle(-12.5f, -107f, 1.7f, c(0xFFD8484A)); circle(12.5f, -107f, 1.7f, c(0xFFD8484A))
            path(CAP, hair); shine()
        }
        "bald" -> { ellipse(-10.6f, -105f, 2.6f, 5f, hair); ellipse(10.6f, -105f, 2.6f, 5f, hair); ellipse(-3f, -113f, 4f, 1.6f, Color.White, .35f) }
        "curly" -> for ((x, y) in listOf(-11f to -106f, -9f to -113f, -3f to -117f, 4f to -117f, 10f to -113f, 12f to -106f, -13f to -99f, 13f to -99f, 0f to -119f, -6f to -115f, 7f to -115f)) circle(x, y, 4.4f, hair)
        "cap" -> {
            path(CAP, hair)
            path("M-12.5 -106 Q-12.5 -124 0 -124 Q12.5 -124 12.5 -106Z", hat)
            for (x in listOf(-8f, -4f, 0f, 4f, 8f)) line(x, -121f, x * 1.1f, -107f, hat.shade(-.2f), .8f)
            rect(-13f, -109f, 26f, 5f, hat.shade(.2f), rx = 2f)
            circle(0f, -125f, 3.8f, PAPER)
        }
        "kerchief" -> {
            path("M-13.6 -100 Q-15 -122 0 -122 Q15 -122 13.6 -100 L10 -97 Q10 -112 0 -113 Q-10 -112 -10 -97Z", hat)
            path("M-4 -90 l4 -3 l4 3 l-2 6 h-4Z", hat)
            for ((x, y) in listOf(-6f to -118f, 0f to -120f, 6f to -118f, -11f to -110f, 11f to -110f)) circle(x, y, 1.1f, Color.White, .8f)
        }
        "beret" -> { path(CAP, hair); ellipse(-2f, -116f, 14f, 5.5f, hat); line(-2f, -121f, -1f, -124f, hat, 2f) }
        "flatcap" -> { path(CAP, hair); path("M-12 -108 Q-11 -122 2 -121 Q13 -120 12 -108Z", hat); ellipse(5f, -108f, 12f, 2.6f, hat.shade(-.25f)) }
    }
}

private fun Sk.arms(p: Look, top: Color, skin: Color, mini: Boolean) {
    fun sleeve(d: String) { stroke(d, Color.Black, 10.5f, .28f); stroke(d, top, 8.6f) }
    fun hand(x: Float, y: Float) = circle(x, y, 3.9f, skin)
    val ld = "M-15 -82 Q-22 -66 -18.5 -52"
    val rd = "M15 -82 Q22 -66 18.5 -52"
    val fl = "M-15 -82 Q-23 -62 -6 -62"
    val fr = "M15 -82 Q23 -62 6 -62"
    when (p.arms) {
        "cup" -> {
            sleeve(ld); hand(-18.5f, -50f); sleeve("M15 -82 Q24 -61 7 -63")
            if (!mini) {
                rect(-1f, -74f, 10f, 10f, PAPER, rx = 2f)
                stroke("M9 -71 q4 0 4 3 q0 3 -4 3", PAPER, 1.6f)
                rect(-1f, -72f, 10f, 2f, c(0xFFC8483C))
                stroke("M2 -78 q-2 -3 0 -6 M6 -78 q2 -3 0 -6", Color.White, 1f, .5f)
            }
            hand(6f, -63f)
        }
        "phone" -> { sleeve(ld); hand(-18.5f, -50f); sleeve("M15 -82 Q28 -87 12.5 -99"); rect(9f, -111f, 5.5f, 11f, c(0xFF2A2A30), rx = 1f); hand(11.5f, -100f) }
        "wave" -> { sleeve(ld); hand(-18.5f, -50f); sleeve("M15 -82 Q28 -92 23 -110"); hand(23f, -113f) }
        "brush" -> {
            sleeve(ld); hand(-18.5f, -50f); sleeve("M15 -82 Q29 -86 26 -100")
            line(26f, -101f, 35f, -118f, c(0xFFA8743A), 2f); line(35f, -118f, 37f, -122f, c(0xFFC8483C), 3f)
            hand(26f, -101f)
        }
        "knit" -> {
            sleeve(fl); sleeve(fr)
            if (!mini) {
                val y = c(p.yarn)
                rect(-9f, -66f, 18f, 10f, y, rx = 2f)
                stroke("M-8 -62 h16 M-8 -59 h16", Color.White, 1f, .35f)
                stroke("M-16 -74 L3 -57 M16 -74 L-3 -57", c(0xFFCFD4DC), 1.3f)
                circle(20f, -44f, 5.5f, y)
                stroke("M16 -46 q4 -3 8 1 M16 -42 q4 -3 8 1", Color.Black, 1f, .2f)
                stroke("M8 -60 Q16 -56 18 -48", y, 1f)
            }
            hand(-5f, -62f); hand(5f, -62f)
        }
        "book" -> {
            sleeve(fl); sleeve(fr)
            path("M-13 -76 L0 -73 L13 -76 L13 -60 L0 -57 L-13 -60Z", c(0xFF8A3B3B))
            path("M-11.5 -75 L0 -72 L11.5 -75 L11.5 -61 L0 -58 L-11.5 -61Z", PAPER)
            if (!mini) {
                line(0f, -72f, 0f, -58f, c(0xFFC9B99D), 1f)
                stroke("M-9 -70 l7 1.5 M-9 -67 l7 1.5 M2 -69 l7 -1.5 M2 -66 l7 -1.5", c(0xFFB8A888), .7f)
            }
            hand(-11f, -62f); hand(11f, -62f)
        }
        "baby" -> {
            sleeve(fl); sleeve(fr)
            ellipse(0f, -69f, 14f, 8.5f, c(0xFFE8EEF8))
            stroke("M-12 -72 Q0 -60 13 -70", c(0xFFC8D4EA), 1f)
            circle(-9f, -73f, 6f, c(0xFFF6D2B4))
            path("M-15 -74 Q-9 -82 -3 -74Z", c(0xFF9AB0D8))
            if (!mini) stroke("M-11 -72 q1 .8 2 0 M-7.6 -72 q1 .8 2 0", INK, .7f)
            hand(-6f, -63f); hand(8f, -64f)
        }
        "hands" -> { sleeve(fl); sleeve(fr); hand(-5f, -62f); hand(5f, -62f) }
        else -> { sleeve(ld); sleeve(rd); hand(-18.5f, -50f); hand(18.5f, -50f) }
    }
}

/** A head-and-shoulders portrait for the suspects' files, in a 48×52 box (origin top-left). */
fun Sk.portrait(p: Look, wall: Color) {
    rect(0f, 0f, 48f, 52f, Brush.verticalGradient(listOf(wall.shade(.15f), wall.shade(-.1f))))
    group(24f, 134f) { person(p.copy(sit = false, kid = false, arms = "down", behind = false, stool = false)) }
    ds.drawRect(Brush.radialGradient(listOf(Color.Transparent, Color.Black.copy(alpha = .25f)), Offset(24f, 22f), 40f), Offset.Zero, androidx.compose.ui.geometry.Size(48f, 52f))
}
