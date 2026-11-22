package app.opposite.ui

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.opposite.data.Settings
import app.opposite.domain.Voucher
import app.opposite.domain.Vouchers
import app.opposite.ui.theme.C
import app.opposite.ui.theme.Sh
import app.opposite.ui.theme.T
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Screen showing Francesca's unlocked vouchers and redemption history,
 * with mystery cards ('?') and romantic clues for future days.
 */
@Composable
fun VouchersScreen(settings: Settings, acts: Acts) {
    var voucherToRedeem by remember { mutableStateOf<Voucher?>(null) }
    val redeemedMap = settings.redeemedVouchers
    val litDays = settings.season?.lit?.keys ?: emptySet()
    val isPreview = settings.previewMode

    Column(
        Modifier
            .fillMaxSize()
            .background(C.bg)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Header
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "←",
                style = T.Title.copy(fontSize = 24.sp),
                modifier = Modifier
                    .clip(Sh.Sm)
                    .clickable { acts.open(null) }
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            )
            Column(Modifier.weight(1f)) {
                Text("Mis Vales y Sorpresas 🎁", style = T.Name.copy(color = C.warm, fontSize = 20.sp))
                Text("Detalles caseros y momentos juntos para Principessa", style = T.Muted.copy(color = C.paperMuted, fontSize = 12.sp))
            }
        }

        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(Vouchers.ALL) { v ->
                val isUnlocked = isPreview || (v.day in litDays)
                val redeemedAt = redeemedMap[v.id]
                val isRedeemed = redeemedAt != null

                if (isUnlocked) {
                    // UNLOCKED VOUCHER CARD
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .clip(Sh.Md)
                            .background(if (isRedeemed) Color(0xFF1B1216) else C.card)
                            .border(1.5.dp, if (isRedeemed) C.dim else C.red, Sh.Md)
                            .padding(16.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(v.icon, fontSize = 32.sp, modifier = Modifier.padding(end = 12.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(
                                        v.title,
                                        style = T.Name.copy(color = C.warm, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        v.description,
                                        style = T.Body.copy(color = C.ink, fontSize = 13.sp)
                                    )
                                }
                            }

                            Spacer(Modifier.height(10.dp))
                            Text(v.note, style = T.Eyebrow.copy(color = C.raspberry, fontSize = 11.sp))
                            Spacer(Modifier.height(12.dp))

                            if (isRedeemed) {
                                val dateStr = Instant.ofEpochMilli(redeemedAt!!)
                                    .atZone(ZoneId.systemDefault())
                                    .format(DateTimeFormatter.ofPattern("d 'de' MMMM, HH:mm"))
                                Box(
                                    Modifier
                                        .clip(Sh.Sm)
                                        .background(Color(0xFF1E3A24))
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        "✓ CANJEADO el $dateStr",
                                        style = T.Eyebrow.copy(color = Color(0xFF8CE49E), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    )
                                }
                            } else {
                                WarmButton("Canjear con Elvis ahora 🎁") {
                                    voucherToRedeem = v
                                }
                            }
                        }
                    }
                } else {
                    // MYSTERY / LOCKED VOUCHER CARD (CON '?' Y PISTA MISTERIOSA)
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .clip(Sh.Md)
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFF220B13), Color(0xFF15070C))
                                )
                            )
                            .border(1.dp, C.warm.copy(alpha = 0.4f), Sh.Md)
                            .padding(16.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF38121E))
                                        .border(1.dp, C.warm, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("❓", fontSize = 22.sp)
                                }
                                Spacer(Modifier.width(14.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(
                                        "Vale Secreto Nº ${v.day}",
                                        style = T.Name.copy(color = C.warm, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                                    )
                                    Text(
                                        "Se desbloqueará al encender la ventana ${v.day} de Diciembre ❄️",
                                        style = T.Muted.copy(color = C.paperMuted, fontSize = 11.sp)
                                    )
                                }
                            }

                            Spacer(Modifier.height(10.dp))

                            // Mystery Hint in Handwriting style
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .clip(Sh.Sm)
                                    .background(Color(0xFF18080E))
                                    .border(1.dp, Color(0x33F7EFE2), Sh.Sm)
                                    .padding(10.dp)
                            ) {
                                Column {
                                    Text("💡 PISTA MISTERIOSA:", style = T.Eyebrow.copy(color = C.raspberry, fontSize = 10.sp, letterSpacing = 1.sp))
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        "«${v.mysteryHint}»",
                                        style = T.Hand.copy(color = C.warm, fontSize = 14.sp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
            item { Spacer(Modifier.height(24.dp)) }
        }
    }

    // Confirmation Alert
    voucherToRedeem?.let { v ->
        AlertDialog(
            onDismissRequest = { voucherToRedeem = null },
            title = { Text("¿Canjear ${v.title}?", style = T.Title.copy(fontSize = 18.sp, color = C.paperInk)) },
            text = {
                Text(
                    "Al confirmar, este vale se marcará como usado con la fecha y hora actual para que tu Rata Aspirable te lo cumpla con todo el amor.",
                    style = T.Body.copy(color = C.paperMuted, fontSize = 14.sp)
                )
            },
            confirmButton = {
                WarmButton("Sí, canjear") {
                    acts.store.redeemVoucher(v.id, System.currentTimeMillis())
                    voucherToRedeem = null
                    acts.toast("¡Vale canjeado! Notificado a tu Rata Aspirable ✨")
                }
            },
            dismissButton = {
                GhostButton("Cancelar", dark = false) {
                    voucherToRedeem = null
                }
            },
            containerColor = C.paper,
        )
    }
}
