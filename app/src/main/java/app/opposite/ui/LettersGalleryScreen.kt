package app.opposite.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import app.opposite.sync.GitHubSyncRepository
import app.opposite.sync.LetterItem
import app.opposite.ui.theme.C
import app.opposite.ui.theme.Sh
import app.opposite.ui.theme.T
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private enum class LetterFilter { ALL, UNREAD, FAVORITES }

@Composable
fun LettersGalleryScreen(acts: Acts) {
    val context = LocalContext.current
    val letters by GitHubSyncRepository.letters.collectAsState()
    var selectedFilter by remember { mutableStateOf(LetterFilter.ALL) }
    var openedLetter by remember { mutableStateOf<LetterItem?>(null) }
    var riddleLetter by remember { mutableStateOf<LetterItem?>(null) }

    val filteredLetters = remember(letters, selectedFilter) {
        when (selectedFilter) {
            LetterFilter.ALL -> letters
            LetterFilter.UNREAD -> letters.filter { !it.isRead }
            LetterFilter.FAVORITES -> letters.filter { it.isFavorite }
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF1E0A12),
                        Color(0xFF14060C),
                        Color(0xFF0F0408),
                    )
                )
            )
            .statusBarsPadding()
    ) {
        Column(Modifier.fillMaxSize()) {
            // Header
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            "Cartas para Mio Principessa 💌",
                            style = T.Title.copy(color = C.warm, fontSize = 21.sp, fontWeight = FontWeight.Bold),
                        )
                        Text(
                            "Escritas por tu Rata Aspirable · Refugio de amor",
                            style = T.Eyebrow.copy(color = C.raspberry, fontSize = 11.sp),
                        )
                    }

                    // Sync button
                    val isSyncing by GitHubSyncRepository.isSyncing.collectAsState()
                    Box(
                        Modifier
                            .clip(CircleShape)
                            .background(Color(0xFF32131F))
                            .border(1.dp, C.red, CircleShape)
                            .clickable {
                                GitHubSyncRepository.syncFromGitHub(context)
                                acts.toast("Buscando cartas nuevas en nuestro refugio...")
                            }
                            .padding(8.dp),
                    ) {
                        Text(if (isSyncing) "⏳" else "🔄", fontSize = 14.sp)
                    }
                }

                Spacer(Modifier.height(10.dp))

                // Filters Row
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        text = "Todas (${letters.size})",
                        selected = selectedFilter == LetterFilter.ALL,
                        onClick = { selectedFilter = LetterFilter.ALL },
                    )
                    FilterChip(
                        text = "No leídas (${letters.count { !it.isRead }})",
                        selected = selectedFilter == LetterFilter.UNREAD,
                        onClick = { selectedFilter = LetterFilter.UNREAD },
                    )
                    FilterChip(
                        text = "Favoritas (${letters.count { it.isFavorite }})",
                        selected = selectedFilter == LetterFilter.FAVORITES,
                        onClick = { selectedFilter = LetterFilter.FAVORITES },
                    )
                }
            }

            // Letters List
            if (filteredLetters.isEmpty()) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("✉️", fontSize = 42.sp)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "No hay cartas en este buzón",
                            style = T.Name.copy(color = C.dim, fontSize = 15.sp),
                        )
                    }
                }
            } else {
                LazyColumn(
                    Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    item { Spacer(Modifier.height(4.dp)) }
                    items(filteredLetters, key = { it.id }) { letter ->
                        EnvelopeCard(
                            letter = letter,
                            onOpen = {
                                if (letter.isLocked) {
                                    riddleLetter = letter
                                } else {
                                    GitHubSyncRepository.markLetterAsRead(context, letter.id)
                                    openedLetter = letter
                                }
                            },
                            onToggleFavorite = {
                                GitHubSyncRepository.toggleFavorite(context, letter.id)
                            },
                        )
                    }
                    item { Spacer(Modifier.height(90.dp)) }
                }
            }
        }

        // Riddle Dialog for Locked Letters
        riddleLetter?.let { letter ->
            LetterRiddleDialog(
                letter = letter,
                onDismiss = { riddleLetter = null },
                onSolved = {
                    GitHubSyncRepository.unlockLetter(context, letter.id)
                    riddleLetter = null
                    openedLetter = letter.copy(isLocked = false)
                    acts.toast("¡Lacre roto con éxito! La carta es tuya para siempre 💌")
                }
            )
        }

        // Open Letter Reader Overlay
        openedLetter?.let { letter ->
            LetterReadingDialog(
                letter = letter,
                onDismiss = { openedLetter = null },
                acts = acts,
            )
        }
    }
}

@Composable
private fun FilterChip(text: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (selected) C.red else Color(0xFF26101A))
            .border(1.dp, if (selected) C.warm else C.line, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp),
    ) {
        Text(
            text,
            style = T.Eyebrow.copy(
                color = if (selected) Color.White else C.dim,
                fontSize = 11.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            ),
        )
    }
}

@Composable
private fun EnvelopeCard(
    letter: LetterItem,
    onOpen: () -> Unit,
    onToggleFavorite: () -> Unit,
) {
    val haptic = LocalHapticFeedback.current

    Box(
        Modifier
            .fillMaxWidth()
            .shadow(10.dp, Sh.Lg)
            .clip(Sh.Lg)
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF381522),
                        Color(0xFF240E16),
                    )
                )
            )
            .border(1.dp, Brush.linearGradient(listOf(C.warm.copy(alpha = 0.7f), C.red)), Sh.Lg)
            .clickable {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onOpen()
            }
            .padding(16.dp),
    ) {
        Column {
            // Top badges row
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier
                            .clip(Sh.Sm)
                            .background(Color(0xFF4A1828))
                            .border(1.dp, C.red.copy(alpha = 0.6f), Sh.Sm)
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                    ) {
                        Text(letter.category, style = T.Eyebrow.copy(color = C.warm, fontSize = 10.sp))
                    }
                    if (letter.isLocked) {
                        Spacer(Modifier.width(6.dp))
                        Box(
                            Modifier
                                .clip(Sh.Sm)
                                .background(Color(0xFF8A5A1A))
                                .padding(horizontal = 6.dp, vertical = 2.dp),
                        ) {
                            Text("🔒 CON ACERTIJO", style = T.Eyebrow.copy(color = C.warm, fontSize = 9.sp, fontWeight = FontWeight.Bold))
                        }
                    } else if (!letter.isRead) {
                        Spacer(Modifier.width(6.dp))
                        Box(
                            Modifier
                                .clip(Sh.Sm)
                                .background(C.red)
                                .padding(horizontal = 6.dp, vertical = 2.dp),
                        ) {
                            Text("NUEVA", style = T.Eyebrow.copy(color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold))
                        }
                    }
                }

                // Favorite Heart Button
                Text(
                    if (letter.isFavorite) "❤️" else "🤍",
                    fontSize = 16.sp,
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable(onClick = onToggleFavorite)
                        .padding(4.dp),
                )
            }

            Spacer(Modifier.height(12.dp))

            // Center Wax Seal & Title
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Wax Seal Stamp with Lock badge if locked
                Box(contentAlignment = Alignment.Center) {
                    WaxSealStamp(modifier = Modifier.size(54.dp))
                    if (letter.isLocked) {
                        Text("🔒", fontSize = 16.sp)
                    }
                }

                Spacer(Modifier.width(14.dp))

                Column(Modifier.weight(1f)) {
                    Text(
                        letter.title,
                        style = T.Name.copy(color = C.ink, fontSize = 16.sp, fontWeight = FontWeight.Bold),
                    )
                    Text(
                        if (letter.isLocked) "Sello de lacre protegido con enigma" else letter.date,
                        style = T.Muted.copy(color = C.raspberry, fontSize = 11.sp),
                    )
                    letter.reaction?.let { r ->
                        Text(
                            "Tu reacción: $r",
                            style = T.Eyebrow.copy(color = C.green, fontSize = 10.sp),
                            modifier = Modifier.padding(top = 2.dp),
                        )
                    }
                }

                Text(
                    if (letter.isLocked) "Resolver ›" else "Abrir ›",
                    style = T.Name.copy(color = C.warm, fontSize = 13.sp, fontWeight = FontWeight.SemiBold),
                )
            }
        }
    }
}

@Composable
fun WaxSealStamp(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .shadow(6.dp, CircleShape)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    listOf(
                        Color(0xFFE22D42),
                        Color(0xFFC81D31),
                        Color(0xFF7A0E1C),
                    )
                )
            )
            .border(1.5.dp, Color(0xFFF7EFE2).copy(alpha = 0.6f), CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "E ♥ F",
                style = T.Eyebrow.copy(
                    color = Color(0xFFF7EFE2),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                ),
            )
        }
    }
}

/**
 * Riddle Dialog to unlock sealed letters.
 */
@Composable
private fun LetterRiddleDialog(
    letter: LetterItem,
    onDismiss: () -> Unit,
    onSolved: () -> Unit,
) {
    var selectedOption by remember { mutableStateOf<Int?>(null) }
    var showHint by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .background(Color(0xDD0B0306))
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF2E0C17), Color(0xFF19060C))
                        )
                    )
                    .border(1.5.dp, C.warm, RoundedCornerShape(24.dp))
                    .padding(22.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "ENIGMA PARA ABRIR LA CARTA 🔐",
                            style = T.Eyebrow.copy(color = C.raspberry, fontSize = 10.sp, letterSpacing = 1.sp)
                        )
                        Text(
                            "✕",
                            style = T.Title.copy(color = C.paperMuted, fontSize = 18.sp),
                            modifier = Modifier
                                .clip(Sh.Sm)
                                .clickable { onDismiss() }
                                .padding(4.dp)
                        )
                    }

                    Spacer(Modifier.height(14.dp))

                    Text(
                        letter.title,
                        style = T.Title.copy(color = C.warm, fontSize = 18.sp, textAlign = TextAlign.Center),
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(12.dp))

                    Box(
                        Modifier
                            .fillMaxWidth()
                            .clip(Sh.Md)
                            .background(C.paper)
                            .border(1.dp, C.warm, Sh.Md)
                            .padding(14.dp)
                    ) {
                        Text(
                            letter.riddleQuestion ?: "¿Estás lista para leer esta carta?",
                            style = T.HandBig.copy(color = C.paperInk, fontSize = 18.sp, lineHeight = 24.sp)
                        )
                    }

                    if (showHint && !letter.riddleHint.isNullOrBlank()) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "💡 Pista: ${letter.riddleHint}",
                            style = T.Muted.copy(color = C.red, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        )
                    }

                    Spacer(Modifier.height(14.dp))

                    // Riddle Options
                    letter.riddleOptions.forEachIndexed { idx, opt ->
                        val isChosen = selectedOption == idx
                        val isCorrect = idx == letter.riddleCorrectIndex
                        val btnBg = when {
                            selectedOption == null -> Color(0xFF240E16)
                            isChosen && isCorrect -> Color(0xFF1E3A24)
                            isChosen && !isCorrect -> Color(0xFF4A1818)
                            else -> Color(0xFF240E16)
                        }

                        Box(
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(Sh.Sm)
                                .background(btnBg)
                                .border(1.dp, if (isChosen) C.warm else C.line, Sh.Sm)
                                .clickable(enabled = selectedOption == null) {
                                    selectedOption = idx
                                    if (isCorrect) {
                                        onSolved()
                                    } else {
                                        isError = true
                                    }
                                }
                                .padding(12.dp)
                        ) {
                            Text(opt, style = T.Name.copy(color = C.ink, fontSize = 14.sp))
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (!showHint && !letter.riddleHint.isNullOrBlank()) {
                            GhostButton("¿Pista? 💡") { showHint = true }
                        } else {
                            Spacer(Modifier.width(1.dp))
                        }

                        if (isError && selectedOption != null) {
                            GhostButton("Reintentar") {
                                selectedOption = null
                                isError = false
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Reading Dialog with parchment texture, 3D envelope opening animation and reactions.
 */
@Composable
private fun LetterReadingDialog(
    letter: LetterItem,
    onDismiss: () -> Unit,
    acts: Acts,
) {
    val context = LocalContext.current
    var selectedReaction by remember { mutableStateOf(letter.reaction) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .background(Color(0xDD0B0306))
                .clickable(onClick = onDismiss)
                .padding(horizontal = 20.dp, vertical = 32.dp),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .clickable(enabled = false) {}
                    .shadow(24.dp, Sh.Lg)
                    .clip(Sh.Lg)
                    .background(C.paper)
                    .border(2.dp, C.warm, Sh.Lg)
                    .padding(24.dp),
            ) {
                Column {
                    // Top Bar
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(letter.date, style = T.Muted.copy(color = C.paperMuted, fontSize = 11.sp))
                        Text(
                            "✕",
                            style = T.Title.copy(color = C.paperInk, fontSize = 18.sp),
                            modifier = Modifier
                                .clip(Sh.Sm)
                                .clickable(onClick = onDismiss)
                                .padding(4.dp),
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    // Title
                    Text(
                        letter.title,
                        style = T.Title.copy(color = C.paperInk, fontSize = 21.sp, fontWeight = FontWeight.Bold),
                    )

                    Spacer(Modifier.height(14.dp))

                    // Letter Body in Handwriting Font
                    Text(
                        letter.content,
                        style = T.Hand.copy(
                            color = C.paperInk,
                            fontSize = 17.5.sp,
                            lineHeight = 25.sp,
                        ),
                    )

                    Spacer(Modifier.height(20.dp))

                    // Reactions Section
                    Text(
                        "REACCIONAR A TU RATA ASPIRABLE",
                        style = T.Eyebrow.copy(color = C.red, fontSize = 10.sp, letterSpacing = 1.sp),
                    )

                    Spacer(Modifier.height(8.dp))

                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        listOf(
                            "Me hiciste llorar 🥺",
                            "Te amo ❤️",
                            "Un abrazo ya 🫂",
                        ).forEach { react ->
                            val isChosen = selectedReaction?.startsWith(react.take(6)) == true
                            Box(
                                Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (isChosen) C.red else Color(0x11C81D31))
                                    .border(1.dp, C.red, RoundedCornerShape(16.dp))
                                    .clickable {
                                        selectedReaction = react
                                        GitHubSyncRepository.recordReaction(context, letter.id, react)
                                        acts.toast("¡Reacción enviada a tu Rata Aspirable! ❤️")
                                    }
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                            ) {
                                Text(
                                    react,
                                    style = T.Eyebrow.copy(
                                        color = if (isChosen) Color.White else C.paperInk,
                                        fontSize = 11.sp,
                                    ),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
