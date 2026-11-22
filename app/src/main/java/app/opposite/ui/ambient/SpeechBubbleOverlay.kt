package app.opposite.ui.ambient

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

data class ActiveBubble(
    val speaker: String,
    val message: String,
    val xOffset: Float = 0.5f,
    val yOffset: Float = 0.35f,
    val timestamp: Long = System.currentTimeMillis()
)

@Composable
fun SpeechBubbleOverlay(
    bubble: ActiveBubble?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(bubble) {
        if (bubble != null) {
            visible = true
            delay(4200) // 4.2 segundos de permanencia
            visible = false
            delay(300)
            onDismiss()
        } else {
            visible = false
        }
    }

    AnimatedVisibility(
        visible = visible && bubble != null,
        enter = fadeIn(spring(stiffness = Spring.StiffnessMediumLow)) +
                scaleIn(spring(dampingRatio = 0.65f, stiffness = Spring.StiffnessMediumLow)),
        exit = fadeOut(spring(stiffness = Spring.StiffnessMedium)) +
                scaleOut(spring(stiffness = Spring.StiffnessMedium)),
        modifier = modifier
    ) {
        if (bubble == null) return@AnimatedVisibility

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp)
                .clickable {
                    visible = false
                    onDismiss()
                },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .widthIn(max = 340.dp)
                    .shadow(12.dp, RoundedCornerShape(18.dp), spotColor = Color(0xFF6E0718))
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFFFFF9F2), // Crema cálido
                                Color(0xFFFDF0E2)
                            )
                        )
                    )
                    .border(
                        width = 1.5.dp,
                        brush = Brush.horizontalGradient(
                            listOf(
                                Color(0xFFFFD700), // Oro
                                Color(0xFFB8142C), // Carmesí
                                Color(0xFFFFD700)
                            )
                        ),
                        shape = RoundedCornerShape(18.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = bubble.speaker,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF8B1E3F),
                            fontFamily = FontFamily.Serif
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = "✨",
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = bubble.message,
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        color = Color(0xFF1E0A12),
                        fontWeight = FontWeight.Normal,
                        fontFamily = FontFamily.Serif
                    )
                }
            }
        }
    }
}
