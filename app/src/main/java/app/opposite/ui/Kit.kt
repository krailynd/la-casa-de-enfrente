package app.opposite.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.opposite.BuildConfig
import app.opposite.domain.Tx
import app.opposite.ui.art.Sk
import app.opposite.ui.theme.C
import app.opposite.ui.theme.Sh
import app.opposite.ui.theme.T
import kotlin.math.sin
import kotlin.random.Random

val LocalRu = compositionLocalOf { true }

/** Inline bilingual text. */
@Composable
fun t(ru: String, en: String) = if (LocalRu.current) ru else en

@Composable
fun Tx.s() = this(LocalRu.current)

/** Pictures stop moving for screenshots and tests. */
object Still { var on = BuildConfig.SHOTS }

/** Seconds since the screen appeared, ticking every frame (frozen when [Still.on]). */
@Composable
fun rememberSeconds(): State<Float> {
    val s = remember { mutableFloatStateOf(0f) }
    if (!Still.on) LaunchedEffect(Unit) {
        val start = withFrameNanos { it }
        while (true) withFrameNanos { s.floatValue = (it - start) / 1e9f }
    }
    return s
}

/** Draws a picture made in its own w×h units, scaled to the width; taps come back in picture units. */
@Composable
fun Picture(
    w: Float,
    h: Float,
    modifier: Modifier = Modifier,
    onTap: ((Float, Float) -> Unit)? = null,
    onDoubleTap: ((Float, Float) -> Unit)? = null,
    draw: Sk.(DrawScope) -> Unit
) {
    var m = modifier.fillMaxWidth().aspectRatio(w / h)
    if (onTap != null || onDoubleTap != null) m = m.pointerInput(w, h) {
        detectTapGestures(
            onDoubleTap = if (onDoubleTap != null) { o -> val k = size.width / w; onDoubleTap(o.x / k, o.y / k) } else null,
            onTap = if (onTap != null) { o -> val k = size.width / w; onTap(o.x / k, o.y / k) } else null
        )
    }
    Canvas(m) {
        val k = size.width / w
        withTransform({ scale(k, k, Offset.Zero) }) { Sk(this).draw(this) }
    }
}

/** Slow falling snow over a picture. */
@Composable
fun Snow(modifier: Modifier = Modifier, flakes: Int = 70) {
    if (Still.on) return
    val time = rememberSeconds()
    val seeds = remember { val r = Random(3); List(flakes) { floatArrayOf(r.nextFloat(), r.nextFloat(), .5f + r.nextFloat() * 1.6f, .25f + r.nextFloat() * .5f, r.nextFloat() * 6f) } }
    Canvas(modifier.fillMaxSize()) {
        val t = time.value
        for (f in seeds) {
            val y = ((f[1] * size.height + t * f[3] * 38f) % (size.height + 8f)) - 4f
            val x = (f[0] * size.width + sin(t * .6f + f[4]) * 9f)
            drawCircle(Color.White.copy(alpha = .85f), f[2] * density * .9f, Offset(x, y))
        }
    }
}

@Composable
fun WarmButton(text: String, modifier: Modifier = Modifier, enabled: Boolean = true, onClick: () -> Unit) {
    Box(
        modifier.clip(Sh.Sm).background(if (enabled) C.warm else C.warm.copy(alpha = .35f)).clickable(enabled = enabled, onClick = onClick).padding(horizontal = 18.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center,
    ) { Text(text, style = T.Button, color = C.onWarm, textAlign = TextAlign.Center) }
}

@Composable
fun GhostButton(text: String, modifier: Modifier = Modifier, dark: Boolean = true, onClick: () -> Unit) {
    Box(
        modifier.clip(Sh.Sm).border(1.dp, if (dark) C.line else Color(0xFFC9B99D), Sh.Sm).clickable(onClick = onClick).padding(horizontal = 16.dp, vertical = 11.dp),
        contentAlignment = Alignment.Center,
    ) { Text(text, style = T.Button, color = if (dark) C.ink else C.paperInk, textAlign = TextAlign.Center) }
}

/** A cream bottom sheet over a dimmed screen; tap outside to close. */
@Composable
fun PaperSheet(visible: Boolean, onDismiss: () -> Unit, content: @Composable ColumnScope.() -> Unit) {
    AnimatedVisibility(visible, enter = fadeIn(), exit = fadeOut()) {
        Box(Modifier.fillMaxSize().background(Color(0x8C03060E)).clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onDismiss))
    }
    AnimatedVisibility(visible, enter = slideInVertically { it / 3 } + fadeIn(), exit = slideOutVertically { it / 3 } + fadeOut()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
            Column(
                Modifier.fillMaxWidth().clip(Sh.Sheet).background(C.paper)
                    .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {}
                    .verticalScroll(rememberScrollState()).navigationBarsPadding().padding(20.dp),
                content = content,
            )
        }
    }
}

@Composable
fun Eyebrow(text: String, modifier: Modifier = Modifier) = Text(text.uppercase(), style = T.Eyebrow, modifier = modifier)
