package app.opposite.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.opposite.domain.DayContent
import app.opposite.domain.TriviaQuestion
import app.opposite.domain.Voucher
import app.opposite.domain.Vouchers
import app.opposite.ui.theme.C
import app.opposite.ui.theme.Hand
import app.opposite.ui.theme.Lora
import app.opposite.ui.theme.Sh
import app.opposite.ui.theme.T
import kotlinx.coroutines.delay

/**
 * 1. COLORING GAME ("Pinta la Noche de Principessa")
 */
@Composable
fun ColoringGame(theme: String, onFinished: () -> Unit) {
    val palette = listOf(
        Color(0xFFC81D31), // Rojo rubí
        Color(0xFFE04F5F), // Frambuesa
        Color(0xFFF6C667), // Dorado luna
        Color(0xFF4A1F2D), // Borgoña vino
        Color(0xFF387B4E), // Verde pino
        Color(0xFFF7EFE2), // Blanco crema
        Color(0xFF160B10), // Fondo noche
    )

    var selectedColor by remember { mutableStateOf(palette[0]) }
    val paths = remember { mutableStateListOf<Pair<Path, Color>>() }
    var currentPath by remember { mutableStateOf<Path?>(null) }
    var isDone by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Pinta la Noche de Principessa", style = T.Title.copy(color = C.ink, fontSize = 20.sp))
        Text("Elige un color y dibuja libremente con tu dedo", style = T.Muted, modifier = Modifier.padding(bottom = 12.dp))

        // Color Palette Selector
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(bottom = 12.dp)) {
            for (color in palette) {
                val isSelected = selectedColor == color
                Box(
                    Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(color)
                        .border(if (isSelected) 3.dp else 1.dp, if (isSelected) C.warm else C.line, CircleShape)
                        .clickable { selectedColor = color }
                )
            }
        }

        // Canvas Board
        Box(
            Modifier
                .fillMaxWidth()
                .height(260.dp)
                .clip(Sh.Md)
                .background(C.card)
                .border(1.dp, C.line, Sh.Md)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            val p = Path().apply { moveTo(offset.x, offset.y) }
                            currentPath = p
                        },
                        onDrag = { change, _ ->
                            currentPath?.lineTo(change.position.x, change.position.y)
                        },
                        onDragEnd = {
                            currentPath?.let { paths.add(it to selectedColor) }
                            currentPath = null
                        }
                    )
                }
        ) {
            Canvas(Modifier.fillMaxSize()) {
                // Outlines based on theme
                when (theme) {
                    "luna_estrellas" -> {
                        drawCircle(C.warm.copy(alpha = 0.25f), radius = 60f, center = Offset(size.width * 0.5f, size.height * 0.45f))
                        drawCircle(C.card, radius = 50f, center = Offset(size.width * 0.53f, size.height * 0.42f))
                        // Stars
                        drawCircle(Color.White, 3f, Offset(size.width * 0.25f, size.height * 0.3f))
                        drawCircle(Color.White, 4f, Offset(size.width * 0.75f, size.height * 0.25f))
                        drawCircle(Color.White, 3.5f, Offset(size.width * 0.35f, size.height * 0.7f))
                    }
                    "frambuesas_moras" -> {
                        // Branch
                        drawLine(C.paperMuted, Offset(size.width * 0.2f, size.height * 0.5f), Offset(size.width * 0.8f, size.height * 0.5f), strokeWidth = 5f)
                        // Berries outline
                        drawCircle(C.raspberry.copy(alpha = 0.3f), radius = 30f, center = Offset(size.width * 0.4f, size.height * 0.58f))
                        drawCircle(Color(0xFF4A1F2D).copy(alpha = 0.3f), radius = 32f, center = Offset(size.width * 0.6f, size.height * 0.58f))
                    }
                    "guitarra_flores" -> {
                        // Guitar shape outline
                        drawCircle(C.warm.copy(alpha = 0.2f), radius = 55f, center = Offset(size.width * 0.5f, size.height * 0.6f))
                        drawCircle(C.warm.copy(alpha = 0.2f), radius = 40f, center = Offset(size.width * 0.5f, size.height * 0.35f))
                        drawLine(C.paperMuted, Offset(size.width * 0.5f, size.height * 0.35f), Offset(size.width * 0.5f, size.height * 0.1f), strokeWidth = 8f)
                    }
                    else -> {
                        // Siluetas al atardecer
                        drawCircle(C.warm.copy(alpha = 0.3f), radius = 50f, center = Offset(size.width * 0.5f, size.height * 0.4f))
                        drawLine(C.red, Offset(0f, size.height * 0.75f), Offset(size.width, size.height * 0.75f), strokeWidth = 4f)
                    }
                }

                // User painted strokes
                for ((path, color) in paths) {
                    drawPath(path, color, style = Stroke(width = 12f, cap = StrokeCap.Round, join = StrokeJoin.Round))
                }
                currentPath?.let {
                    drawPath(it, selectedColor, style = Stroke(width = 12f, cap = StrokeCap.Round, join = StrokeJoin.Round))
                }
            }
        }

        Spacer(Modifier.height(14.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            GhostButton("Borrar todo", dark = true) { paths.clear() }
            WarmButton(if (isDone) "¡Colgado en el Árbol! ✓" else "Colgar en el Árbol") {
                isDone = true
                onFinished()
            }
        }
    }
}

/**
 * 2. SCRATCH CARD GAME ("Raspa y Gana de Escarcha")
 */
@Composable
fun ScratchCardGame(voucher: Voucher, onClaimed: () -> Unit) {
    val scratchedPoints = remember { mutableStateListOf<Offset>() }
    var scratchedRatio by remember { mutableFloatStateOf(0f) }
    var isRevealed by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Raspa la Escarcha", style = T.Title.copy(color = C.ink, fontSize = 20.sp))
        Text("Desliza tu dedo sobre la escarcha para revelar tu vale", style = T.Muted, modifier = Modifier.padding(bottom = 12.dp))

        Box(
            Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(Sh.Md)
                .border(2.dp, C.red, Sh.Md)
        ) {
            // Background Voucher Card
            Column(
                Modifier
                    .fillMaxSize()
                    .background(C.paper)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(voucher.icon, fontSize = 42.sp)
                Spacer(Modifier.height(6.dp))
                Text(voucher.title, style = T.Name.copy(color = C.paperInk, fontSize = 18.sp, fontWeight = FontWeight.Bold))
                Spacer(Modifier.height(4.dp))
                Text(voucher.description, style = T.Body.copy(color = C.paperMuted, fontSize = 13.sp, textAlign = TextAlign.Center))
                Spacer(Modifier.height(4.dp))
                Text(voucher.note, style = T.Eyebrow.copy(color = C.red, fontSize = 11.sp, textAlign = TextAlign.Center))
            }

            // Frost Cover Canvas
            if (!isRevealed) {
                Canvas(
                    Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectDragGestures { change, _ ->
                                scratchedPoints.add(change.position)
                                scratchedRatio = (scratchedPoints.size / 90f).coerceAtMost(1f)
                                if (scratchedRatio >= 0.75f) {
                                    isRevealed = true
                                }
                            }
                        }
                ) {
                    val frostGradient = Brush.verticalGradient(listOf(Color(0xFFE8D0D4), Color(0xFFC89CA5), Color(0xFFF2E6E8)))
                    drawRect(frostGradient)

                    // Draw scratched holes
                    for (pt in scratchedPoints) {
                        drawCircle(Color.Transparent, radius = 42f, center = pt, blendMode = androidx.compose.ui.graphics.BlendMode.Clear)
                    }
                }
            }
        }

        Spacer(Modifier.height(14.dp))
        if (isRevealed || scratchedRatio > 0.4f) {
            WarmButton("Guardar en mi Buzón de Vales") {
                onClaimed()
            }
        } else {
            Text("Raspa un poquito más con tu dedo... ✨", style = T.Eyebrow.copy(color = C.warm))
        }
    }
}

/**
 * 3. TRIVIA GAME ("Trivia Cómplice de Pareja")
 */
@Composable
fun TriviaGame(questions: List<TriviaQuestion>, onCompleted: () -> Unit) {
    var currentIndex by remember { mutableIntStateOf(0) }
    var selectedOption by remember { mutableStateOf<Int?>(null) }
    var answeredCorrectly by remember { mutableStateOf(false) }
    var completedAll by remember { mutableStateOf(false) }

    val q = questions.getOrNull(currentIndex)

    Column(Modifier.fillMaxWidth().padding(16.dp)) {
        Text("Trivia de Nosotros", style = T.Title.copy(color = C.ink, fontSize = 20.sp))
        Text("Pregunta ${currentIndex + 1} de ${questions.size}", style = T.Eyebrow.copy(color = C.raspberry), modifier = Modifier.padding(top = 2.dp, bottom = 12.dp))

        if (q != null && !completedAll) {
            Text(q.question, style = T.Name.copy(color = C.ink, fontSize = 17.sp), modifier = Modifier.padding(bottom = 14.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                q.options.forEachIndexed { idx, opt ->
                    val isChosen = selectedOption == idx
                    val isCorrect = idx == q.correctIndex
                    val btnBg = when {
                        isChosen && isCorrect -> C.green
                        isChosen && !isCorrect -> C.red
                        else -> C.card
                    }

                    Box(
                        Modifier
                            .fillMaxWidth()
                            .clip(Sh.Sm)
                            .background(btnBg)
                            .border(1.dp, if (isChosen) C.warm else C.line, Sh.Sm)
                            .clickable {
                                selectedOption = idx
                                answeredCorrectly = isCorrect
                            }
                            .padding(horizontal = 14.dp, vertical = 12.dp)
                    ) {
                        Text(opt, style = T.Body.copy(color = if (isChosen) Color.White else C.ink, fontSize = 14.sp))
                    }
                }
            }

            if (selectedOption != null) {
                Spacer(Modifier.height(10.dp))
                if (answeredCorrectly) {
                    Text(q.note, style = T.Hand.copy(color = C.warm, fontSize = 18.sp), modifier = Modifier.padding(vertical = 6.dp))
                    WarmButton(if (currentIndex < questions.size - 1) "Siguiente Pregunta →" else "Completar Trivia") {
                        if (currentIndex < questions.size - 1) {
                            currentIndex++
                            selectedOption = null
                            answeredCorrectly = false
                        } else {
                            completedAll = true
                            onCompleted()
                        }
                    }
                } else {
                    Text("¡Casi! Prueba otra opción, Principessa 😉", style = T.Eyebrow.copy(color = C.raspberry))
                }
            }
        } else {
            Text("¡Completaste toda la trivia! Se ha desbloqueado la pista del día en tu cuaderno.", style = T.Hand.copy(color = C.warm, fontSize = 20.sp))
        }
    }
}

/**
 * 4. MEMORAMA GAME ("Memorama de Recuerdos")
 */
@Composable
fun MemoramaGame(pairs: List<String>, onCompleted: () -> Unit) {
    val icons = listOf("🌙", "☀️", "🍓", "🍇", "🎸", "💌")
    val cards = remember { (icons + icons).shuffled() }
    val revealed = remember { mutableStateListOf<Int>() }
    val matched = remember { mutableStateListOf<Int>() }

    LaunchedEffect(revealed.size) {
        if (revealed.size == 2) {
            val (first, second) = revealed
            if (cards[first] == cards[second]) {
                matched.add(first)
                matched.add(second)
                revealed.clear()
                if (matched.size == cards.size) {
                    onCompleted()
                }
            } else {
                delay(900)
                revealed.clear()
            }
        }
    }

    Column(Modifier.fillMaxWidth().padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Memorama de Pareja", style = T.Title.copy(color = C.ink, fontSize = 20.sp))
        Text("Encuentra los 6 pares de símbolos cómplices", style = T.Muted, modifier = Modifier.padding(bottom = 12.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth().height(260.dp)
        ) {
            itemsIndexed(cards) { index, symbol ->
                val isOpen = index in revealed || index in matched
                val rotation by animateFloatAsState(targetValue = if (isOpen) 180f else 0f, animationSpec = tween(350))

                Box(
                    Modifier
                        .aspectRatio(1f)
                        .graphicsLayer {
                            rotationY = rotation
                            cameraDistance = 12f * density
                        }
                        .clip(Sh.Sm)
                        .background(if (isOpen) C.paper else C.card)
                        .border(1.dp, if (isOpen) C.warm else C.line, Sh.Sm)
                        .clickable(enabled = !isOpen && revealed.size < 2) {
                            revealed.add(index)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (rotation >= 90f) {
                        Text(symbol, fontSize = 26.sp, modifier = Modifier.graphicsLayer { rotationY = 180f })
                    } else {
                        Text("❤️", fontSize = 18.sp, color = C.raspberry)
                    }
                }
            }
        }

        if (matched.size == cards.size) {
            Spacer(Modifier.height(10.dp))
            Text("¡Memorama completado! Pista desbloqueada en tu cuaderno ✨", style = T.Hand.copy(color = C.warm, fontSize = 18.sp))
        }
    }
}

/**
 * 5. CONSTELLATION GAME ("Conectar Estrellas")
 */
@Composable
fun ConstellationGame(onCompleted: () -> Unit) {
    val starPoints = remember {
        listOf(
            Offset(100f, 200f),
            Offset(180f, 120f),
            Offset(260f, 200f),
            Offset(220f, 300f),
            Offset(140f, 300f),
        )
    }
    var connectedUpTo by remember { mutableIntStateOf(0) }
    var isDone by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Constelación del Corazón", style = T.Title.copy(color = C.ink, fontSize = 20.sp))
        Text("Toca las estrellas numeradas en orden para trazar la figura", style = T.Muted, modifier = Modifier.padding(bottom = 12.dp))

        Box(
            Modifier
                .fillMaxWidth()
                .height(260.dp)
                .clip(Sh.Md)
                .background(C.bg)
                .border(1.dp, C.line, Sh.Md)
        ) {
            Canvas(Modifier.fillMaxSize()) {
                // Lines between connected stars
                for (i in 0 until connectedUpTo) {
                    val p1 = starPoints[i]
                    val p2 = starPoints[(i + 1) % starPoints.size]
                    drawLine(C.warm, p1, p2, strokeWidth = 3f, cap = StrokeCap.Round)
                }

                // Stars
                starPoints.forEachIndexed { idx, pt ->
                    val isLit = idx <= connectedUpTo
                    drawCircle(if (isLit) C.warm else Color.White.copy(alpha = 0.5f), radius = if (isLit) 8f else 5f, center = pt)
                }
            }

            // Clickable Star targets
            starPoints.forEachIndexed { idx, pt ->
                Box(
                    Modifier
                        .size(44.dp)
                        .graphicsLayer {
                            translationX = pt.x - 22f
                            translationY = pt.y - 22f
                        }
                        .clickable {
                            if (idx == connectedUpTo) {
                                connectedUpTo++
                                if (connectedUpTo >= starPoints.size) {
                                    isDone = true
                                    onCompleted()
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text("${idx + 1}", color = if (idx <= connectedUpTo) C.warm else Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(Modifier.height(10.dp))
        if (isDone) {
            Text("¡Constelación trazada! Se reproduce una armonía suave para ti ✨", style = T.Hand.copy(color = C.warm, fontSize = 18.sp))
        }
    }
}

/**
 * 6. RIDDLE GAME ("Acertijos del Refugio")
 */
data class Riddle(
    val question: String,
    val hint: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String,
)

@Composable
fun RiddleGame(onSolved: () -> Unit) {
    val riddles = remember {
        listOf(
            Riddle(
                question = "A las 18:18 del viernes 6 de noviembre, una 'rata' te hizo una pregunta con el corazón en la mano... ¿Qué comenzó en ese instante?",
                hint = "Piensa en nuestro nuevo comienzo con más madurez y paz.",
                options = listOf("Una simple charla", "Nuestra nueva historia de novios", "Un viaje", "Una broma"),
                correctIndex = 1,
                explanation = "¡Exacto! El 06/11 a las 18:18:35 empezó nuestra nueva y más sana etapa juntos ❤️",
            ),
            Riddle(
                question = "Tengo 6 cuerdas afinadas en Sol sostenido mayor y mi madera guarda el eco de los acordes que compongo para ti... ¿Qué soy?",
                hint = "Elvis la toca en Ableton Live y acústico.",
                options = listOf("Un piano", "La guitarra de Elvis", "Un violín", "Un saxofón"),
                correctIndex = 1,
                explanation = "¡Así es! Mi guitarra acústica siempre tiene notas dedicadas a ti 🎸",
            ),
            Riddle(
                question = "En las noches te llaman Luna, de postre te dicen Frambuesa y Mora, pero en italiano eres su única...",
                hint = "Mio...",
                options = listOf("Ragazza", "Bella", "Principessa", "Stella"),
                correctIndex = 2,
                explanation = "¡Mio Principessa! Tu título real e inmutable para siempre 👑",
            ),
        )
    }

    var currentIndex by remember { mutableIntStateOf(0) }
    var selectedOption by remember { mutableStateOf<Int?>(null) }
    var showHint by remember { mutableStateOf(false) }
    var isSuccess by remember { mutableStateOf(false) }

    val currentRiddle = riddles[currentIndex]

    Column(Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Acertijos del Refugio 📜", style = T.Title.copy(color = C.warm, fontSize = 20.sp))
        Text("Enigma ${currentIndex + 1} de ${riddles.size}", style = T.Eyebrow.copy(color = C.raspberry, fontSize = 11.sp))

        Spacer(Modifier.height(14.dp))

        Box(
            Modifier
                .fillMaxWidth()
                .clip(Sh.Md)
                .background(C.paper)
                .border(1.5.dp, C.warm, Sh.Md)
                .padding(16.dp),
        ) {
            Column {
                Text(currentRiddle.question, style = T.HandBig.copy(color = C.paperInk, fontSize = 20.sp, lineHeight = 26.sp))

                if (showHint) {
                    Spacer(Modifier.height(8.dp))
                    Text("💡 Pista: ${currentRiddle.hint}", style = T.Muted.copy(color = C.red, fontSize = 12.sp, fontWeight = FontWeight.SemiBold))
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        // Options
        for (i in currentRiddle.options.indices) {
            val opt = currentRiddle.options[i]
            val isChosen = selectedOption == i
            val isCorrect = i == currentRiddle.correctIndex
            val bgColor = when {
                selectedOption == null -> C.card
                isChosen && isCorrect -> Color(0xFF1E3A24)
                isChosen && !isCorrect -> Color(0xFF4A1818)
                else -> C.card
            }

            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(Sh.Sm)
                    .background(bgColor)
                    .border(1.dp, if (isChosen) C.warm else C.line, Sh.Sm)
                    .clickable(enabled = selectedOption == null) {
                        selectedOption = i
                        if (i == currentRiddle.correctIndex) {
                            if (currentIndex + 1 >= riddles.size) {
                                isSuccess = true
                                onSolved()
                            }
                        }
                    }
                    .padding(14.dp),
            ) {
                Text(opt, style = T.Name.copy(color = C.ink, fontSize = 14.sp))
            }
        }

        Spacer(Modifier.height(10.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            if (selectedOption == null && !showHint) {
                GhostButton("¿Necesitas una pista? 💡") { showHint = true }
            } else {
                Spacer(Modifier.width(1.dp))
            }

            if (selectedOption != null) {
                if (selectedOption == currentRiddle.correctIndex && currentIndex + 1 < riddles.size) {
                    WarmButton("Siguiente acertijo →") {
                        currentIndex++
                        selectedOption = null
                        showHint = false
                    }
                } else if (selectedOption != currentRiddle.correctIndex) {
                    GhostButton("Reintentar") { selectedOption = null }
                }
            }
        }

        if (isSuccess) {
            Spacer(Modifier.height(12.dp))
            Text("¡Acertijo superado! La verdad siempre estuvo en sus corazones ✨", style = T.Hand.copy(color = C.warm, fontSize = 18.sp))
        }
    }
}

/**
 * 7. PROMISE JAR GAME ("Frasco de Promesas de la Rata Aspirable")
 */
@Composable
fun PromiseJarGame(onPromiseRevealed: () -> Unit) {
    val promises = remember {
        listOf(
            "Cuidar nuestra paz, hablar con el corazón abierto y no permitir que ningún malentendido nos distancie.",
            "Prepararte una cena casera calientita con música suave cada vez que llegues cansada.",
            "Recordarte cada día, con hechos y no con palabras vacías, lo hermosa y valiosa que eres para mí.",
            "Componerte canciones en mi guitarra acústica para arrullarte y que duermas tranquila.",
            "Darte siempre tu lugar como mi única Principessa, con lealtad y respeto incondicional.",
            "Construir juntos esta nueva etapa desde el amor sano, la madurez y la complicidad mutua.",
        )
    }

    var revealedPromise by remember { mutableStateOf<String?>(null) }
    var isShaking by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Frasco de Promesas & Razones 🫙", style = T.Title.copy(color = C.warm, fontSize = 20.sp))
        Text("Toca el frasco para sacar una promesa de tu Rata Aspirable", style = T.Muted, modifier = Modifier.padding(bottom = 14.dp))

        // Jar Icon with animation
        Box(
            Modifier
                .size(110.dp)
                .clip(CircleShape)
                .background(Brush.radialGradient(listOf(Color(0xFF4A1F2D), Color(0xFF1E0A12))))
                .border(2.dp, C.warm, CircleShape)
                .clickable {
                    revealedPromise = promises.random()
                    onPromiseRevealed()
                },
            contentAlignment = Alignment.Center,
        ) {
            Text("🫙✨", fontSize = 48.sp)
        }

        Spacer(Modifier.height(16.dp))

        revealedPromise?.let { promise ->
            Box(
                Modifier
                    .fillMaxWidth()
                    .clip(Sh.Md)
                    .background(C.paper)
                    .border(1.5.dp, C.red, Sh.Md)
                    .padding(18.dp),
            ) {
                Column {
                    Text("PROMESA DE TU RATA ASPIRABLE", style = T.Eyebrow.copy(color = C.red, fontSize = 10.sp, letterSpacing = 1.sp))
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "«$promise»",
                        style = T.Hand.copy(color = C.paperInk, fontSize = 19.sp, lineHeight = 25.sp),
                    )
                }
            }

            Spacer(Modifier.height(10.dp))
            GhostButton("Sacar otra promesa 🫙") {
                revealedPromise = promises.random()
            }
        }
    }
}
