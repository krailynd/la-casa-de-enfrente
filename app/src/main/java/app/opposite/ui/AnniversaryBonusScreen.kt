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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.opposite.domain.AdventData
import app.opposite.domain.CoupleStory
import app.opposite.ui.theme.C
import app.opposite.ui.theme.Sh
import app.opposite.ui.theme.T
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember

@Composable
fun AnniversaryBonusScreen(acts: Acts) {
    val day25 = AdventData.get(25)
    val time by rememberSeconds()
    val liveDuration = remember(time) { CoupleStory.calculateDuration() }

    Column(
        Modifier
            .fillMaxSize()
            .background(C.bg)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
    ) {
        // Top bar
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
            Column {
                Text("Nuestro Nuevo Comienzo", style = T.Name.copy(color = C.warm, fontSize = 20.sp, fontWeight = FontWeight.Bold))
                Text(liveDuration.subtitle, style = T.Eyebrow.copy(color = C.raspberry, fontSize = 11.sp))
            }
        }

        // Golden Chest Banner
        Box(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clip(Sh.Lg)
                .background(Brush.linearGradient(listOf(Color(0xFF3B1822), Color(0xFF1F0C14))))
                .border(2.dp, C.warm, Sh.Lg)
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("✨👑✨", fontSize = 42.sp)
                Spacer(Modifier.height(8.dp))
                Text("El Cofre de Nuestra Segunda Oportunidad", style = T.Title.copy(color = C.warm, fontSize = 20.sp, textAlign = TextAlign.Center))
                Text(
                    "Elegirnos con más madurez, paz y amor verdadero",
                    style = T.Name.copy(color = C.ink, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center),
                    modifier = Modifier.padding(top = 4.dp)
                )
                Text(
                    liveDuration.formattedSummary,
                    style = T.Eyebrow.copy(color = C.warm, fontSize = 13.sp, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
        }

        // Heartfelt Love Letter
        Box(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .clip(Sh.Md)
                .background(C.paper)
                .border(1.dp, C.line, Sh.Md)
                .padding(18.dp)
        ) {
            Column {
                Text("Para Francesca (Mio Principessa):", style = T.Name.copy(color = C.paperInk, fontSize = 18.sp, fontWeight = FontWeight.Bold))
                Spacer(Modifier.height(10.dp))
                Text(
                    day25.anniversaryLetter ?: "",
                    style = T.Hand.copy(color = C.paperInk, fontSize = 20.sp, lineHeight = 26.sp)
                )
            }
        }

        Spacer(Modifier.height(10.dp))
        Box(Modifier.padding(horizontal = 16.dp).fillMaxWidth()) {
            WarmButton("Volver a Nuestro Refugio", Modifier.fillMaxWidth()) {
                acts.open(null)
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}
