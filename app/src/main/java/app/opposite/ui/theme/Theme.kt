package app.opposite.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import app.opposite.R

/**
 * Romantic Nocturnal Palette for Francesca.
 * Dominated by her favorite color RED (wine, ruby crimson, raspberry, warm parchment, and candle gold).
 */
object C {
    val bg = Color(0xFF160B10)           // Deep warm midnight burgundy
    val bg2 = Color(0xFF241118)          // Surface wine
    val card = Color(0xFF2E1620)         // Slightly elevated wine card
    val line = Color(0xFF4A1F2D)         // Subtle wine border
    val ink = Color(0xFFF7EFE2)          // Warm cream text
    val dim = Color(0xFFB89A98)          // Muted warm rose
    val faint = Color(0xFF7D5F64)        // Faint border / inactive
    val warm = Color(0xFFF6C667)         // Candlelight gold & stars
    val onWarm = Color(0xFF2A1A08)
    val frost = Color(0xFFE2C2C6)        // Frost with gentle rose hue
    val paper = Color(0xFFF7EFE2)        // Smooth warm parchment
    val paperLine = Color(0xFFE5D7C5)    // Notebook faint grid line
    val paperInk = Color(0xFF2C1810)     // Warm dark espresso ink
    val paperMuted = Color(0xFF8B6A5B)   // Soft sepia ink
    val red = Color(0xFFC81D31)          // Francesca's Ruby Crimson Primary
    val raspberry = Color(0xFFE04F5F)    // Raspberry glow
    val green = Color(0xFF387B4E)        // Soft emerald for matches
    val clue = Color(0xFFFFF4C9)
}

val Lora = FontFamily(
    Font(R.font.lora_regular, FontWeight.Normal),
    Font(R.font.lora_medium, FontWeight.SemiBold),
    Font(R.font.lora_italic, FontWeight.Normal, FontStyle.Italic),
)

val Hand = FontFamily(Font(R.font.caveat, FontWeight.Normal))

object Sh {
    val Lg = RoundedCornerShape(18.dp)
    val Md = RoundedCornerShape(14.dp)
    val Sm = RoundedCornerShape(10.dp)
    val Sheet = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
}

object T {
    val Title = TextStyle(fontFamily = Lora, fontWeight = FontWeight.SemiBold, fontSize = 22.sp, lineHeight = 28.sp, color = C.ink)
    val Name = TextStyle(fontFamily = Lora, fontWeight = FontWeight.SemiBold, fontSize = 19.sp, lineHeight = 24.sp, color = C.ink)
    val Eyebrow = TextStyle(fontFamily = Lora, fontSize = 12.sp, letterSpacing = .12.em, color = C.dim)
    val Body = TextStyle(fontFamily = Lora, fontSize = 15.sp, lineHeight = 23.sp, color = C.ink)
    val Muted = TextStyle(fontFamily = Lora, fontSize = 13.sp, lineHeight = 19.sp, color = C.dim)
    val Hand = TextStyle(fontFamily = app.opposite.ui.theme.Hand, fontSize = 22.sp, lineHeight = 26.sp, color = C.paperInk)
    val HandBig = TextStyle(fontFamily = app.opposite.ui.theme.Hand, fontSize = 27.sp, lineHeight = 31.sp, color = C.paperInk)
    val Button = TextStyle(fontFamily = Lora, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
    val Count = TextStyle(fontFamily = app.opposite.ui.theme.Hand, fontSize = 24.sp, color = C.warm)
}

@Composable
fun OppositeTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = C.red,
            onPrimary = Color.White,
            secondary = C.warm,
            onSecondary = C.onWarm,
            background = C.bg,
            surface = C.bg2,
            onSurface = C.ink,
            onBackground = C.ink,
            outline = C.line,
        ),
        typography = MaterialTheme.typography.copy(bodyLarge = T.Body, bodyMedium = T.Body, labelLarge = T.Button, titleLarge = T.Title),
        content = content,
    )
}
