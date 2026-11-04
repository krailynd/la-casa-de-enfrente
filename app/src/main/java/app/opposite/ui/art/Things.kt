package app.opposite.ui.art

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color

/** Furniture and small things for the rooms, in room units (floor line at y = 198). */
fun Sk.thing(type: String, x: Float, y: Float) {
    when (type) {
        "lamp" -> {
            glow(x, 95f, 70f, c(0xFFFFE5A0), .6f)
            line(x, 198f, x, 90f, c(0xFF3A2A20), 3f)
            ellipse(x, 198f, 14f, 3f, c(0xFF3A2A20))
            path("M${x - 20} 92 L${x + 20} 92 L${x + 12} 66 L${x - 12} 66Z", c(0xFFF4C66D))
            stroke("M${x - 20} 92 L${x + 20} 92 L${x + 12} 66 L${x - 12} 66Z", c(0xFFC48A3A), 1f)
        }
        "tree" -> {
            rect(x - 5, 182f, 10f, 16f, c(0xFF6A4A2A))
            path("M$x 70 l30 40 h-12 l24 34 h-14 l22 38 h-100 l22 -38 h-14 l24 -34 h-12Z", c(0xFF2F5A34))
            path("M$x 70 l30 40 h-12 l24 34 h-14 l22 38 h-50Z", Color.Black, .15f)
            for ((dx, yy, col) in listOf(Triple(-14f, 120f, 0xFFD64A3A), Triple(12f, 135f, 0xFFE8C24A), Triple(-22f, 165f, 0xFF4A8AD6), Triple(22f, 170f, 0xFFD64A3A), Triple(0f, 155f, 0xFFE8C24A), Triple(-6f, 100f, 0xFF4A8AD6))) {
                circle(x + dx, yy, 4f, c(col)); circle(x + dx - 1.2f, yy - 1.2f, 1.2f, Color.White, .6f)
            }
            path("M$x 58 l4 9 h9 l-7 6 l3 9 l-9 -6 l-9 6 l3 -9 l-7 -6 h9Z", c(0xFFFFD64A))
        }
        "cat" -> {
            stroke("M${x + 30} 190 q22 -4 18 -30", c(0xFFD98A3A), 6f)
            ellipse(x + 10, 180f, 30f, 18f, c(0xFFE09440))
            path("M${x + 10} 162 q28 0 30 18 q-14 8 -30 0Z", c(0xFFC07A30), .5f)
            circle(x - 18, 160f, 15f, c(0xFFE09440))
            path("M${x - 30} 152 l2 -14 l9 8Z M${x - 6} 152 l-2 -14 l-9 8Z", c(0xFFE09440))
            circle(x - 23, 158f, 2f, c(0xFF222222)); circle(x - 13, 158f, 2f, c(0xFF222222))
            stroke("M${x - 18} 165 l-2 2 M${x - 18} 165 l2 2", c(0xFF222222), 1f)
            stroke("M${x - 28} 170 q10 8 20 0", c(0xFFB8A070), 2.5f)
        }
        "table" -> {
            rect(x - 50, 148f, 5f, 50f, c(0xFF6A4A2A)); rect(x + 45, 148f, 5f, 50f, c(0xFF6A4A2A))
            path("M${x - 54} 136 h108 l4 44 q-58 6 -116 0Z", c(0xFFF1E4D0))
            path("M${x + 20} 136 h34 l4 44 q-18 2 -34 2Z", Color.Black, .08f)
            for (i in 0 until 12) circle(x - 56 + i * 10.3f, 180f + kotlin.math.sin(i.toFloat()) * .8f, 3f, c(0xFFF8F0E0))
            stroke("M${x - 50} 160 h100", c(0xFFC8483C), 1.5f, .6f, dash = floatArrayOf(4f, 3f))
            path("M${x + 18} 136 q0 -16 13 -16 q13 0 13 16Z", c(0xFFC8483C))
            stroke("M${x + 18} 130 q-6 0 -8 -6", c(0xFFC8483C), 3f)
            circle(x + 31, 119f, 2.6f, c(0xFFC8483C))
            stroke("M${x + 24} 128 q3 3 6 0", Color.White, 1f)
            ellipse(x - 34, 135f, 9f, 2f, Color.White)
            rect(x - 39, 126f, 10f, 9f, Color.White, rx = 2f)
            stroke("M${x - 29} 128 q4 0 4 3 q0 3 -4 3", Color.White, 1.5f)
            ellipse(x, 135f, 12f, 2.5f, c(0xFFD8A84A))
            circle(x - 4, 132f, 3f, c(0xFFC98A3A)); circle(x + 3, 132f, 3f, c(0xFFB8742A))
        }
        "shelf" -> {
            rect(x - 42, 96f, 84f, 5f, c(0xFF6A4A2A)); rect(x - 42, 56f, 84f, 5f, c(0xFF6A4A2A))
            val cols = listOf(0xFF8A3B3B, 0xFF3B5A8A, 0xFFC9A25A, 0xFF4A6A4A, 0xFF6A3A6A, 0xFFA8743A, 0xFF2F4F4F, 0xFFB05050)
            for (r in 0..1) for (i in 0 until 8) {
                val h = 30f - (i % 3) * 3
                val by = if (r == 1) 101f else 61f
                rect(x - 40 + i * 10, by - h, 8f, h, c(cols[(i + r * 3) % 8]))
                rect(x - 40 + i * 10, by - h + 4, 8f, 2f, Color.White, .2f)
            }
        }
        "tv" -> {
            rect(x - 28, 168f, 56f, 30f, c(0xFF6A4A2A))
            rect(x - 26, 122f, 52f, 44f, c(0xFF3A3A3A), rx = 4f)
            rect(x - 21, 127f, 36f, 34f, c(0xFF8FC0E0), rx = 5f)
            rect(x - 21, 127f, 36f, 12f, Color.White, .15f, rx = 5f)
            circle(x + 20, 134f, 2.5f, c(0xFF888888)); circle(x + 20, 144f, 2.5f, c(0xFF888888))
            stroke("M${x - 8} 122 l-10 -16 M${x + 4} 122 l10 -16", c(0xFF555555), 1f)
        }
        "plant" -> {
            path("M${x - 12} 172 h24 l-4 26 h-16Z", c(0xFFB5653A))
            stroke("M$x 172 q-20 -20 -12 -40 M$x 172 q18 -22 14 -42 M$x 172 q2 -30 0 -48", c(0xFF3A6A3A), 3f)
            for ((dx, yy) in listOf(-12f to 132f, 14f to 130f, 0f to 124f, -16f to 148f, 16f to 150f)) circle(x + dx, yy, 7f, c(0xFF4A7A3A))
            for ((dx, yy) in listOf(-10f to 128f, 2f to 121f, 14f to 127f)) circle(x + dx, yy, 3f, c(0xFFD6394A))
        }
        "radio" -> {
            rect(x - 34, 160f, 68f, 38f, c(0xFF7A5A3A))
            rect(x - 30, 128f, 60f, 32f, c(0xFF9A6A3A), rx = 4f)
            rect(x - 25, 133f, 30f, 22f, c(0xFFD8C79A), rx = 2f)
            stroke("M${x - 22} 140 h24 M${x - 22} 146 h24", c(0xFF8A7A5A), 1f)
            circle(x + 17, 144f, 6f, c(0xFF4A3A2A))
        }
        "piano" -> {
            rect(x - 60, 100f, 120f, 98f, c(0xFF2A1A14))
            rect(x - 60, 138f, 120f, 10f, c(0xFF3A2A20))
            rect(x - 56, 148f, 112f, 9f, c(0xFFF4EFE2))
            for (i in 0 until 16) line(x - 56 + i * 7, 148f, x - 56 + i * 7, 157f, c(0xFF999999), .6f)
            circle(x - 40, 94f, 4f, c(0xFFFFD877)); rect(x - 42, 94f, 4f, 8f, c(0xFFE8E2D0))
            glow(x - 40, 92f, 18f, c(0xFFFFE5A0), .6f)
        }
        "aquarium" -> {
            rect(x - 50, 160f, 100f, 38f, c(0xFF4A3A2A))
            rect(x - 50, 104f, 100f, 56f, c(0xFF6FB6C9), .8f)
            path("M${x - 48} 150 q20 -6 40 0 t56 0 v8 h-96Z", c(0xFFC9B07A))
            stroke("M${x - 30} 150 q-4 -20 4 -36 M${x + 30} 150 q6 -18 -2 -34", c(0xFF3A8A4A), 3f)
            for ((dx, yy, col) in listOf(Triple(-10f, 124f, 0xFFFF8A3A), Triple(14f, 136f, 0xFFFFD23A), Triple(-26f, 140f, 0xFFFF5A7A)))
                path("M${x + dx} $yy q6 -5 12 0 q-6 5 -12 0Z M${x + dx} $yy l-5 -4 v8Z", c(col))
            stroke("M${x - 50} 104 h100 v56 h-100Z", c(0xFF2A3A3A), 3f)
        }
        "sewing" -> {
            rect(x - 54, 147f, 108f, 42f, c(0xFF6A4A30))
            path("M${x - 30} 186 h24 l-4 -6 h-16Z", c(0xFF3A3A3A))
            rect(x - 56, 140f, 112f, 7f, c(0xFF8A6A4A))
            path("M${x - 30} 140 v-30 h50 v12 h-34 v18Z", c(0xFF2A2A2A))
            circle(x + 22, 116f, 7f, c(0xFF8A8A8A))
            rect(x - 6, 122f, 3f, 12f, c(0xFFBBBBBB))
        }
        "armchair" -> {
            rect(x - 40, 120f, 80f, 60f, c(0xFF7A3B3B), rx = 14f)
            rect(x - 48, 140f, 18f, 44f, c(0xFF6A2F2F), rx = 8f)
            rect(x + 30, 140f, 18f, 44f, c(0xFF6A2F2F), rx = 8f)
            rect(x - 32, 160f, 64f, 20f, c(0xFF8A4848), rx = 6f)
            rect(x - 42, 184f, 5f, 14f, c(0xFF3A2A20)); rect(x + 37, 184f, 5f, 14f, c(0xFF3A2A20))
        }
        "clock" -> {
            circle(x, 62f, 18f, c(0xFFF4ECD9)); ring(x, 62f, 18f, c(0xFF6A4A2A), 4f)
            stroke("M$x 62 v-11 M$x 62 l8 4", c(0xFF3A2A20), 2f)
            line(x, 80f, x, 110f, c(0xFF6A4A2A), 1f)
            circle(x, 112f, 4f, c(0xFFC9A25A))
        }
        "frame" -> {
            rect(x - 30, 50f, 60f, 44f, c(0xFFC9A25A))
            rect(x - 25, 55f, 50f, 34f, c(0xFFE9DCC0))
            path("M${x - 25} 89 l12 -14 l8 8 l12 -16 l18 22Z", c(0xFF7A8A9A))
            for (i in 0 until 6) circle(x - 18 + i * 7, 80f, 2f, c(0xFF4A3A2A))
        }
        "bed" -> {
            rect(x - 60, 150f, 120f, 30f, c(0xFFD6C3A3), rx = 5f)
            rect(x - 60, 160f, 120f, 22f, c(0xFF8A4A4A))
            rect(x - 64, 122f, 8f, 76f, c(0xFF6A4A2A)); rect(x + 56, 140f, 8f, 58f, c(0xFF6A4A2A))
            rect(x - 54, 140f, 28f, 14f, Color.White, rx = 6f)
            for (i in 0..3) stroke("M${x - 30 + i * 22} 170 l6 -6 l6 6", c(0xFFF4ECD9), 1f)
        }
        "gifts" -> {
            gift(x - 34, 170f, 30f, 28f, c(0xFFC8483C), c(0xFFFFD64A))
            gift(x, 178f, 28f, 20f, c(0xFF3B6D8F), c(0xFFF4ECD9))
            gift(x - 14, 156f, 20f, 14f, c(0xFF4A7A3A), c(0xFFFFD64A))
        }
        "rocket" -> {
            path("M$x 50 q30 40 26 148 h-52 q-4 -108 26 -148Z", c(0xFFC9B69A))
            path("M$x 50 q30 40 26 148 h-14 q2 -100 -12 -148Z", Color.Black, .12f)
            circle(x, 110f, 12f, c(0xFF9AC0D8)); ring(x, 110f, 12f, c(0xFF888888), 3f)
            path("M${x - 26} 160 l-18 38 h18Z M${x + 26} 160 l18 38 h-18Z", c(0xFFC8483C))
        }
        "desk" -> {
            rect(x - 58, 147f, 116f, 44f, c(0xFF6A4A30))
            rect(x + 14, 150f, 40f, 40f, c(0xFF7A5638))
            stroke("M${x + 14} 163 h40 M${x + 14} 176 h40", c(0xFF5A3A22), 1f)
            for (yy in listOf(157f, 170f, 183f)) circle(x + 34, yy, 1.5f, c(0xFFD8B04A))
            rect(x - 60, 140f, 120f, 7f, c(0xFF8A6A4A))
            rect(x - 26, 112f, 44f, 28f, c(0xFF2A2F3A), rx = 2f)
            rect(x - 23, 115f, 38f, 22f, c(0xFF5A7AA0))
            rect(x + 28, 128f, 9f, 12f, c(0xFFF4ECD9), rx = 2f)
        }
        "chess" -> {
            rect(x - 44, 140f, 88f, 7f, c(0xFF8A6A4A))
            rect(x - 4, 147f, 8f, 51f, c(0xFF6A4A2A)); rect(x - 30, 198f, 60f, 4f, c(0xFF6A4A2A))
            for (i in 0 until 4) for (j in 0 until 8) rect(x - 24 + j * 6, 128f + i * 3, 6f, 3f, if ((i + j) % 2 == 1) c(0xFF3A2A20) else c(0xFFF1E4D0))
            path("M${x - 10} 128 v-8 h4 v8Z M${x + 8} 128 l2 -10 l2 10Z", c(0xFF111111))
        }
        "easel" -> {
            stroke("M${x - 30} 198 L$x 40 L${x + 30} 198 M$x 40 L$x 198", c(0xFF8A6A4A), 4f)
            rect(x - 40, 60f, 80f, 80f, c(0xFFF4ECD9)); stroke("M${x - 40} 60 h80 v80 h-80Z", c(0xFF6A4A2A), 2f)
            rect(x - 26, 76f, 52f, 64f, c(0xFF5A6A8A))
            for (f in 0..3) for (k in 0..2) rect(x - 20 + k * 16, 82f + f * 14, 8f, 8f, if (f == 3 && k == 0) c(0xFFFFD06B) else c(0xFF2A3550))
            rect(x - 44, 140f, 88f, 5f, c(0xFF6A4A2A))
        }
        "garland" -> {
            stroke("M0 30 Q180 44 360 30", c(0xFF6A4A2A), 1f)
            val cols = listOf(0xFFF4ECD9, 0xFF9AC0D8, 0xFFF4ECD9, 0xFFFFD877)
            for (i in 0 until 13) path("M${10 + i * 27} 34 l8 0 l-4 12Z", c(cols[i % 4]))
        }
        "envelope" -> {
            rect(x - 13, y - 9, 26f, 18f, c(0xFFF8F1DF)); stroke("M${x - 13} ${y - 9} h26 v18 h-26Z", c(0xFFB9A37A), 1f)
            stroke("M${x - 13} ${y - 9} l13 10 l13 -10", c(0xFFB9A37A), 1f)
            circle(x, y + 2, 3.5f, c(0xFFA52A2A))
        }
        "mitten" -> {
            path("M${x - 8} ${y + 10} v-14 q0 -9 9 -9 q9 0 9 9 v14Z", c(0xFFC8302C))
            path("M${x + 10} ${y - 2} q7 -2 6 5 l-6 3Z", c(0xFFC8302C))
            rect(x - 9, y + 7, 19f, 5f, c(0xFFF4ECD9))
            stroke("M${x + 1} ${y - 6} v8 M${x - 3} ${y - 2} h8", Color.White, 1.3f)
        }
        "boots" -> {
            path("M${x - 26} 198 v-36 h14 v26 h12 v10Z", c(0xFF4A4A52))
            path("M${x + 2} 198 v-36 h14 v26 h12 v10Z", c(0xFF55555E))
            rect(x - 26, 160f, 14f, 5f, c(0xFF3A3A40)); rect(x + 2, 160f, 14f, 5f, c(0xFF3A3A40))
        }
        "parcel" -> {
            rect(x - 16, y - 12, 32f, 22f, c(0xFFB98A5A))
            stroke("M$x ${y - 12} v22 M${x - 16} ${y - 1} h32", c(0xFFE8DCC0), 2f)
            stroke("M${x - 4} ${y - 16} q4 4 0 4 q-4 0 0 -4 M${x + 4} ${y - 16} q-4 4 0 4", c(0xFFE8DCC0), 1.5f)
        }
        "key" -> {
            ring(x - 8, y, 6f, c(0xFFC9A25A), 3f)
            stroke("M${x - 2} $y h16 v5 M${x + 9} $y v4", c(0xFFC9A25A), 3f)
        }
        "note" -> group(x, y, rotate = -6f) {
            rect(-12f, -14f, 24f, 28f, c(0xFFFFFDF2))
            for (i in 0..3) line(-8f, -8f + i * 6, 4f + (i % 2) * 4, -8f + i * 6, c(0xFF4A5AA0), 1f)
        }
        "honey" -> {
            rect(x - 11, y - 10, 22f, 22f, c(0xFFE89A2A), .9f, rx = 4f)
            rect(x - 12, y - 15, 24f, 6f, c(0xFFC8483C))
            rect(x - 5, y - 14, 10f, 4f, c(0xFFF4ECD9))
            rect(x - 8, y - 6, 4f, 14f, Color.White, .3f, rx = 2f)
        }
        "coat" -> {
            rect(x - 4, 40f, 8f, 8f, c(0xFF6A4A2A))
            path("M${x - 26} 60 q26 -14 52 0 l6 100 h-64Z", c(0xFF7A7F88))
            path("M$x 52 q26 0 26 8 l6 100 h-32Z", Color.Black, .12f)
            line(x, 52f, x, 160f, c(0xFF5A5F68), 1f)
            for (i in 0..3) circle(x + 5, 72f + i * 20, 3f, c(0xFFD6B04A))
            path("M${x - 20} 108 h14 v10 h-14Z", c(0xFF6A6F78))
            path("M${x - 18} 108 q2 -10 8 -4", c(0xFFC8302C))
        }
    }
}

fun Sk.gift(x: Float, y: Float, w: Float, h: Float, col: Color, ribbon: Color) {
    rect(x, y, w, h, col)
    rect(x + w * .62f, y, w * .38f, h, Color.Black, .2f)
    rect(x, y, w, 3f, Color.White, .18f)
    rect(x + w / 2 - 2, y, 4f, h, ribbon)
    stroke("M${x + w / 2} $y q-9 -9 -9 -2 q0 4 9 2 q9 -9 9 -2 q0 4 -9 2", ribbon, 2.4f)
}

/** Where a thing can be tapped, generously. */
fun hitBox(type: String, x: Float, y: Float): Rect = when (type) {
    "envelope", "mitten", "parcel", "key", "note", "honey" -> Rect(x - 20, y - 20, x + 20, y + 20)
    "lamp" -> Rect(x - 22, 60f, x + 22, 200f)
    "tree" -> Rect(x - 50, 55f, x + 50, 198f)
    "cat" -> Rect(x - 35, 140f, x + 50, 200f)
    "table" -> Rect(x - 56, 115f, x + 56, 198f)
    "shelf" -> Rect(x - 44, 25f, x + 44, 102f)
    "tv" -> Rect(x - 30, 105f, x + 30, 198f)
    "plant" -> Rect(x - 22, 118f, x + 22, 198f)
    "radio" -> Rect(x - 36, 125f, x + 36, 198f)
    "piano" -> Rect(x - 62, 90f, x + 62, 198f)
    "aquarium" -> Rect(x - 52, 100f, x + 52, 198f)
    "sewing" -> Rect(x - 56, 105f, x + 56, 198f)
    "armchair" -> Rect(x - 50, 118f, x + 50, 198f)
    "clock" -> Rect(x - 20, 42f, x + 20, 118f)
    "frame" -> Rect(x - 32, 48f, x + 32, 96f)
    "bed" -> Rect(x - 66, 120f, x + 66, 198f)
    "gifts" -> Rect(x - 36, 150f, x + 30, 198f)
    "rocket" -> Rect(x - 44, 50f, x + 44, 198f)
    "desk" -> Rect(x - 60, 108f, x + 60, 198f)
    "chess" -> Rect(x - 44, 115f, x + 44, 202f)
    "easel" -> Rect(x - 44, 40f, x + 44, 198f)
    "garland" -> Rect(0f, 24f, 360f, 50f)
    "boots" -> Rect(x - 28, 158f, x + 30, 200f)
    "coat" -> Rect(x - 28, 40f, x + 32, 162f)
    else -> Rect(x - 30, y - 30, x + 30, y + 30)
}
