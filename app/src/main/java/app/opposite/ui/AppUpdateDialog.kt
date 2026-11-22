package app.opposite.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import app.opposite.sync.AppUpdateChecker
import app.opposite.sync.AppUpdateInfo

@Composable
fun AppUpdateBanner(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val updateInfo by AppUpdateChecker.updateAvailable.collectAsState()
    val dismissedVersion by AppUpdateChecker.dismissedVersion.collectAsState()
    var showDetailDialog by remember { mutableStateOf(false) }

    val isVisible = updateInfo != null && (updateInfo!!.isMandatory || updateInfo!!.versionCode > dismissedVersion)

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier
    ) {
        val info = updateInfo ?: return@AnimatedVisibility

        // Animación de pulso para el regalo
        val infiniteTransition = rememberInfiniteTransition(label = "pulse")
        val scale by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.15f,
            animationSpec = infiniteRepeatable(
                animation = tween(900, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "gift_scale"
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            Color(0xFF8B1E3F), // Rubí profundo
                            Color(0xFF5E1228), // Vino cálido
                            Color(0xFF8B1E3F)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    brush = Brush.horizontalGradient(
                        listOf(
                            Color(0xFFFFD700).copy(alpha = 0.7f),
                            Color(0xFFFF85A1).copy(alpha = 0.5f),
                            Color(0xFFFFD700).copy(alpha = 0.7f)
                        )
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
                .clickable { showDetailDialog = true }
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF2B0A14))
                        .border(1.dp, Color(0xFFFFD700).copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🎁",
                        fontSize = 20.sp,
                        modifier = Modifier.scale(scale)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "¡Actualización disponible!",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFE8D6)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFFFD700))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "v${info.versionName}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF3A0007)
                            )
                        }
                    }
                    Text(
                        text = "Nuevos detalles de tu Rata Aspirable ❤️ Toca para ver",
                        fontSize = 12.sp,
                        color = Color(0xFFFFCAD4),
                        maxLines = 1
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Ver ✨",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFD700)
                )
            }
        }
    }

    if (showDetailDialog && updateInfo != null) {
        AppUpdateDialog(
            info = updateInfo!!,
            onDismiss = { showDetailDialog = false },
            onUpdate = {
                showDetailDialog = false
                AppUpdateChecker.openDownloadUrl(context, updateInfo!!.downloadUrl)
            },
            onRemindLater = {
                showDetailDialog = false
                AppUpdateChecker.dismissCurrentUpdate(context, updateInfo!!.versionCode)
            }
        )
    }
}

@Composable
fun AppUpdateDialog(
    info: AppUpdateInfo,
    onDismiss: () -> Unit,
    onUpdate: () -> Unit,
    onRemindLater: () -> Unit
) {
    val currentCode = AppUpdateChecker.getCurrentVersionCode(LocalContext.current)
    val currentName = AppUpdateChecker.getCurrentVersionName(LocalContext.current)

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp)),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E0A12)),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = Brush.verticalGradient(
                    listOf(
                        Color(0xFFFFD700).copy(alpha = 0.8f),
                        Color(0xFF8B1E3F).copy(alpha = 0.6f)
                    )
                ),
                width = 1.5.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🏠✨",
                    fontSize = 36.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = info.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFE8D6),
                    fontFamily = FontFamily.Serif
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Actual: v$currentName ($currentCode)",
                        fontSize = 11.sp,
                        color = Color(0xFFB5838D)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "➔",
                        fontSize = 12.sp,
                        color = Color(0xFFFFD700)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Nueva: v${info.versionName} (${info.versionCode})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFD700)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = info.message,
                    fontSize = 13.sp,
                    color = Color(0xFFFFCAD4),
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Cuadro con notas de la versión
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF12050A))
                        .border(1.dp, Color(0xFF4A1525), RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    Column {
                        Text(
                            text = "🎁 ¿Qué hay de nuevo?",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD700)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = info.releaseNotes,
                            fontSize = 12.sp,
                            color = Color(0xFFF4ACB7),
                            lineHeight = 18.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onUpdate,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFD700)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Descargar e Instalar (GitHub)",
                        color = Color(0xFF330007),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                if (!info.isMandatory) {
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = onRemindLater,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFFB5838D)
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = Brush.linearGradient(
                                listOf(Color(0xFF4A1525), Color(0xFF4A1525))
                            )
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Recordar más tarde",
                            fontSize = 12.sp,
                            color = Color(0xFFB5838D)
                        )
                    }
                }
            }
        }
    }
}
