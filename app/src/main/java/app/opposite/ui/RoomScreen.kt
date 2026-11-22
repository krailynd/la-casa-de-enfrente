package app.opposite.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.opposite.domain.ActivityType
import app.opposite.domain.AdventData
import app.opposite.domain.Evening
import app.opposite.domain.Gift
import app.opposite.domain.House
import app.opposite.domain.Rules
import app.opposite.domain.Vouchers
import app.opposite.ui.art.ROOM_H
import app.opposite.ui.art.ROOM_W
import app.opposite.ui.art.room
import app.opposite.ui.art.roomHit
import app.opposite.ui.theme.C
import app.opposite.ui.theme.Sh
import app.opposite.ui.theme.T

private sealed interface Said {
    data class Note(val text: String) : Said
    data class Clue(val text: String, val first: Boolean) : Said
    data object Mandarin : Said
}

@Composable
fun RoomScreen(evening: Evening, d: Int, acts: Acts) {
    val r = House.resident(d)
    val s = evening.season
    val time by rememberSeconds()
    var said by remember(d) { mutableStateOf<Said?>(null) }
    val ru = LocalRu.current
    val clueFound = d in s.clues
    val dayData = AdventData.get(d)

    Column(
        Modifier
            .fillMaxSize()
            .background(C.bg)
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Top bar
        Row(Modifier.padding(horizontal = 8.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("←", style = T.Title, modifier = Modifier.clip(Sh.Sm).clickable { acts.open(null) }.padding(horizontal = 10.dp, vertical = 4.dp))
            Column {
                Text(r.name.s(), style = T.Name)
                Text("ventana $d · ${r.flat.s()}", style = T.Muted)
            }
        }

        // Room scene
        Picture(ROOM_W, ROOM_H, onTap = { x, y ->
            when (val hit = roomHit(r, x, y, d in s.rare)) {
                null -> {}
                -2 -> {
                    acts.season { Rules.findMandarin(it, d) }
                    said = Said.Mandarin
                }
                else -> {
                    val th = r.things[hit]
                    if (th.clue && s.clue(d) != null) {
                        val first = d !in s.clues
                        acts.season { Rules.findClue(it, d) }
                        said = Said.Clue(s.clue(d)!!.text(ru), first)
                        if (first) acts.toast("✎ Pista anotada en tu cuaderno")
                    } else {
                        said = Said.Note(th.note?.invoke(ru) ?: "…")
                    }
                }
            }
        }) { room(r, clueFound, d in s.rare, time) }

        // Story
        Text(r.story.s(), style = T.Body, modifier = Modifier.padding(start = 18.dp, end = 18.dp, top = 12.dp))

        // Bubble for speech / items
        val bubble = said
        val isClue = bubble is Said.Clue || bubble is Said.Mandarin
        Column(
            Modifier
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .fillMaxWidth()
                .heightIn(min = 54.dp)
                .clip(Sh.Md)
                .background(if (isClue) C.clue else C.paper)
                .then(if (isClue) Modifier.border(2.dp, C.warm, Sh.Md) else Modifier)
                .padding(horizontal = 14.dp, vertical = 10.dp),
        ) {
            when (bubble) {
                is Said.Clue -> {
                    Text(if (bubble.first) "PISTA PARA EL CUADERNO" else "PISTA", style = T.Eyebrow.copy(color = C.red))
                    Text(bubble.text, style = T.Hand)
                }
                Said.Mandarin -> {
                    Text("UN DETALLE ESPECIAL", style = T.Eyebrow.copy(color = C.red))
                    Text("¡Encontraste una mandarina! El adorno n.º $d del árbol ahora brilla con destellos dorados.", style = T.Hand)
                }
                is Said.Note -> Text(bubble.text, style = T.Hand)
                null -> Text(
                    if (s.clue(d) != null && !clueFound) "Toca los objetos de la habitación. Algo importante está brillando."
                    else "Toca los objetos de la habitación para explorar.",
                    style = T.Hand,
                )
            }
        }

        // Integrated Minigame / Activity for today
        when (dayData.activityType) {
            ActivityType.COLORING -> {
                ColoringGame(dayData.coloringTheme ?: "luna_estrellas") {
                    acts.season { Rules.findClue(it, d) }
                    acts.toast("¡Dibujo terminado y colgado en tu Árbol! 🎄✨")
                }
            }
            ActivityType.SCRATCH_VOUCHER -> {
                val voucher = Vouchers.forDay(d)
                if (voucher != null) {
                    ScratchCardGame(voucher) {
                        acts.store.redeemVoucher(voucher.id, System.currentTimeMillis())
                        acts.season { Rules.findClue(it, d) }
                        acts.toast("¡Vale guardado en tu buzón de sorpresas! 🎁")
                    }
                }
            }
            ActivityType.TRIVIA -> {
                if (dayData.trivia != null) {
                    TriviaGame(dayData.trivia) {
                        acts.season { Rules.findClue(it, d) }
                        acts.toast("¡Pista descubierta y anotada en tu cuaderno! ✎")
                    }
                }
            }
            ActivityType.MEMORAMA -> {
                MemoramaGame(dayData.memoryPairs ?: listOf("Luna", "Sol", "Frambuesa", "Mora", "Guitarra", "Carta")) {
                    acts.season { Rules.findClue(it, d) }
                    acts.toast("¡Recuerdo completado y pista anotada! ✎")
                }
            }
            ActivityType.CONSTELLATION -> {
                ConstellationGame {
                    acts.season { Rules.findClue(it, d) }
                    acts.toast("¡Constelación revelada y pista anotada! ✎")
                }
            }
            ActivityType.RIDDLE -> {
                RiddleGame {
                    acts.season { Rules.findClue(it, d) }
                    acts.toast("¡Acertijo resuelto y pista anotada! ✎")
                }
            }
            ActivityType.PROMISE_JAR -> {
                PromiseJarGame {
                    acts.season { Rules.findClue(it, d) }
                    acts.toast("¡Promesa guardada en tu corazón! 🫙✨")
                }
            }
            ActivityType.LETTER -> {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clip(Sh.Md)
                        .background(C.card)
                        .border(1.dp, C.red, Sh.Md)
                        .padding(14.dp)
                ) {
                    Column {
                        Text("💌 Detalle para Principessa", style = T.Name.copy(color = C.warm, fontSize = 16.sp))
                        Spacer(Modifier.height(6.dp))
                        Text(dayData.clueNote, style = T.Hand.copy(color = C.ink, fontSize = 18.sp))
                    }
                }
            }
            ActivityType.FINALE -> {
                Spacer(Modifier.height(10.dp))
                WarmButton("Ir al Cuaderno y Nombrar al Admirador Secreto →", Modifier.padding(horizontal = 16.dp)) {
                    acts.open(null)
                    acts.tab(Tab.NOTES)
                }
            }
            ActivityType.ANNIVERSARY_BONUS -> {}
        }

        s.gifts[d]?.let { GiftCard(it, s.giftsFrom) }

        Spacer(Modifier.height(28.dp))
    }
}

@Composable
fun giftKind(kind: String) = when (kind) {
    "treat" -> "🍬" to "dulce"
    "note" -> "💌" to "nota"
    "deed" -> "✨" to "detalle"
    else -> "🎁" to "encontrar"
}

@Composable
private fun GiftCard(g: Gift, from: String) {
    val (icon, label) = giftKind(g.kind)
    Box(Modifier.padding(horizontal = 16.dp, vertical = 8.dp).fillMaxWidth().clip(Sh.Md).background(C.paper).border(1.5.dp, C.red.copy(alpha = .6f), Sh.Md)) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(icon, style = T.Title)
                Column(Modifier.padding(start = 10.dp)) {
                    Text("UN REGALO TRAS ESTA VENTANA", style = T.Eyebrow.copy(color = C.red))
                    Text(label + if (from.isNotBlank()) " · de: $from" else "", style = T.Muted.copy(color = C.paperMuted))
                }
            }
            Text(g.text, style = T.HandBig.copy(fontSize = 25.sp), modifier = Modifier.padding(top = 8.dp))
        }
        Box(Modifier.align(Alignment.TopEnd).padding(10.dp).clip(Sh.Sm).background(Color(0x22C8483C)).padding(horizontal = 6.dp, vertical = 2.dp)) {
            Text("✦", style = T.Muted.copy(color = C.red))
        }
    }
}
