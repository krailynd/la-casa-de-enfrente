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

/** A winter evening: deep blue night, warm window light, the cream of a school notebook. */
object C {
    val bg = Color(0xFF0F1A33)
    val bg2 = Color(0xFF16223F)
    val card = Color(0xFF1C2C52)
    val line = Color(0xFF2B3F6C)
    val ink = Color(0xFFE9E3D3)
    val dim = Color(0xFF9AA6C2)
    val faint = Color(0xFF6E7C9E)
    val warm = Color(0xFFFFCB6B)
    val onWarm = Color(0xFF2A1A08)
    val frost = Color(0xFF9FC0EF)
    val paper = Color(0xFFF4ECD9)
    val paperLine = Color(0xFFCDD9E6)
    val paperInk = Color(0xFF3A2F25)
    val paperMuted = Color(0xFF8B7A66)
    val red = Color(0xFFC8483C)
    val green = Color(0xFF2D6A3A)
    val clue = Color(0xFFFFF4C9)
}

val Lora = FontFamily(
    Font(R.font.lora_regular, FontWeight.Normal),
    Font(R.font.lora_medium, FontWeight.SemiBold),
    Font(R.font.lora_italic, FontWeight.Normal, FontStyle.Italic),
)

/** Handwriting: tasks, clues, the notebook. */
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
        colorScheme = darkColorScheme(primary = C.warm, onPrimary = C.onWarm, background = C.bg, surface = C.bg2, onSurface = C.ink, onBackground = C.ink, outline = C.line),
        typography = MaterialTheme.typography.copy(bodyLarge = T.Body, bodyMedium = T.Body, labelLarge = T.Button, titleLarge = T.Title),
        content = content,
    )
}
