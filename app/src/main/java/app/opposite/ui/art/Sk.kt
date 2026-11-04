package app.opposite.ui.art

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.vector.PathParser
import kotlin.random.Random

/** Colour from 0xAARRGGBB. */
fun c(v: Long) = Color(v)

/** Lighter (k > 0) or darker (k < 0), like the prototype's shade(). */
fun Color.shade(k: Float): Color {
    fun f(v: Float) = if (k < 0) v * (1 + k) else v + (1 - v) * k
    return Color(f(red).coerceIn(0f, 1f), f(green).coerceIn(0f, 1f), f(blue).coerceIn(0f, 1f), alpha)
}

/** Hue/saturation/lightness, for glass ornaments. */
fun hsl(h: Float, s: Float, l: Float): Color = Color.hsl(((h % 360) + 360) % 360, s, l)

/**
 * A tiny SVG-shaped drawing kit over a [DrawScope]: coordinates are the picture's own (a «viewBox»),
 * paths are SVG path strings, so pictures read like the HTML prototype they came from.
 */
class Sk(val ds: DrawScope) {
    fun path(d: String, fill: Color, alpha: Float = 1f, evenOdd: Boolean = false) =
        ds.drawPath(p(d, evenOdd), fill, alpha = alpha)

    fun path(d: String, brush: Brush, alpha: Float = 1f) = ds.drawPath(p(d), brush, alpha = alpha)

    fun stroke(d: String, color: Color, w: Float, alpha: Float = 1f, cap: StrokeCap = StrokeCap.Round, dash: FloatArray? = null) =
        ds.drawPath(p(d), color, alpha = alpha, style = Stroke(w, cap = cap, pathEffect = dash?.let { PathEffect.dashPathEffect(it) }))

    fun rect(x: Float, y: Float, w: Float, h: Float, color: Color, alpha: Float = 1f, rx: Float = 0f) =
        if (rx > 0) ds.drawRoundRect(color, Offset(x, y), Size(w, h), CornerRadius(rx), alpha = alpha) else ds.drawRect(color, Offset(x, y), Size(w, h), alpha = alpha)

    fun rect(x: Float, y: Float, w: Float, h: Float, brush: Brush, alpha: Float = 1f, rx: Float = 0f) =
        if (rx > 0) ds.drawRoundRect(brush, Offset(x, y), Size(w, h), CornerRadius(rx), alpha = alpha) else ds.drawRect(brush, Offset(x, y), Size(w, h), alpha = alpha)

    fun circle(cx: Float, cy: Float, r: Float, color: Color, alpha: Float = 1f) = ds.drawCircle(color, r, Offset(cx, cy), alpha = alpha)
    fun circle(cx: Float, cy: Float, r: Float, brush: Brush, alpha: Float = 1f) = ds.drawCircle(brush, r, Offset(cx, cy), alpha = alpha)
    fun ring(cx: Float, cy: Float, r: Float, color: Color, w: Float, alpha: Float = 1f) = ds.drawCircle(color, r, Offset(cx, cy), alpha = alpha, style = Stroke(w))

    fun ellipse(cx: Float, cy: Float, rx: Float, ry: Float, color: Color, alpha: Float = 1f) =
        ds.drawOval(color, Offset(cx - rx, cy - ry), Size(rx * 2, ry * 2), alpha = alpha)

    fun ellipse(cx: Float, cy: Float, rx: Float, ry: Float, brush: Brush, alpha: Float = 1f) =
        ds.drawOval(brush, Offset(cx - rx, cy - ry), Size(rx * 2, ry * 2), alpha = alpha)

    fun line(x1: Float, y1: Float, x2: Float, y2: Float, color: Color, w: Float, alpha: Float = 1f, cap: StrokeCap = StrokeCap.Round) =
        ds.drawLine(color, Offset(x1, y1), Offset(x2, y2), w, cap, alpha = alpha)

    /** A soft glow: a radial gradient fading to nothing (instead of an SVG blur). */
    fun glow(cx: Float, cy: Float, r: Float, color: Color, alpha: Float = 1f) =
        ds.drawCircle(Brush.radialGradient(listOf(color.copy(alpha = color.alpha * alpha), color.copy(alpha = 0f)), Offset(cx, cy), r), r, Offset(cx, cy))

    fun group(tx: Float = 0f, ty: Float = 0f, scale: Float = 1f, rotate: Float = 0f, block: Sk.() -> Unit) {
        ds.withTransform({
            translate(tx, ty)
            if (rotate != 0f) rotate(rotate, Offset.Zero)
            if (scale != 1f) scale(scale, scale, Offset.Zero)
        }) { block() }
    }

    fun clip(d: String, block: Sk.() -> Unit) = ds.clipPath(p(d)) { block() }

    /** Paper grain over a rectangle. */
    fun grain(x: Float, y: Float, w: Float, h: Float, alpha: Float = .12f) =
        ds.drawRect(ShaderBrush(ImageShader(noise, TileMode.Repeated, TileMode.Repeated)), Offset(x, y), Size(w, h), alpha = alpha)

    companion object {
        private val cache = object : LinkedHashMap<String, Path>(512, .75f, true) {
            override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, Path>?) = size > 6000
        }

        fun p(d: String, evenOdd: Boolean = false): Path = synchronized(cache) {
            val key = if (evenOdd) "E$d" else d
            cache.getOrPut(key) { PathParser().parsePathString(d).toPath().also { if (evenOdd) it.fillType = PathFillType.EvenOdd } }
        }

        val noise: ImageBitmap by lazy {
            val n = 96
            val bmp = android.graphics.Bitmap.createBitmap(n, n, android.graphics.Bitmap.Config.ARGB_8888)
            val r = Random(7)
            for (y in 0 until n) for (x in 0 until n) {
                val v = r.nextInt(256)
                val dark = v < 128
                val a = (kotlin.math.abs(v - 128) * 1.6f).toInt().coerceAtMost(255)
                bmp.setPixel(x, y, android.graphics.Color.argb(a, if (dark) 40 else 255, if (dark) 28 else 246, if (dark) 18 else 225))
            }
            bmp.asImageBitmap()
        }
    }
}

/** Formats a float for a path string without trailing noise. */
fun Float.f(): String = if (this == this.toInt().toFloat()) this.toInt().toString() else String.format(java.util.Locale.US, "%.2f", this)
