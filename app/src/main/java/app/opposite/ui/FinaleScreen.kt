package app.opposite.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.opposite.data.Past
import app.opposite.domain.Evening
import app.opposite.domain.Mystery
import app.opposite.domain.Season
import app.opposite.domain.WinState
import app.opposite.ui.art.FacadeLook
import app.opposite.ui.art.HOUSE_H
import app.opposite.ui.art.HOUSE_W
import app.opposite.ui.art.facade
import app.opposite.ui.theme.C
import app.opposite.ui.theme.T

@Composable
fun FinaleScreen(evening: Evening, acts: Acts) {
    val s = evening.season
    val c = Mystery.suspect(s.culprit)
    val time by rememberSeconds()
    val ru = LocalRu.current
    Column(Modifier.fillMaxSize().background(C.bg).verticalScroll(rememberScrollState()).navigationBarsPadding()) {
        Box {
            Picture(HOUSE_W, HOUSE_H) { facade(FacadeLook(dusk = true, state = { WinState.LIT }, allLit = true, t = time)) }
            Snow(Modifier.matchParentSize())
            Text("←", style = T.Title, modifier = Modifier.statusBarsPadding().padding(8.dp).clickable { acts.open(null) }.padding(8.dp), color = C.ink)
        }
        Column(Modifier.padding(horizontal = 20.dp, vertical = 14.dp)) {
            Eyebrow(t("Тайный Дед этого года", "El Abuelo Secreto de este año"))
            Text(c.name.s(), style = T.Title.copy(fontSize = 26.sp), modifier = Modifier.padding(top = 4.dp, bottom = 8.dp))
            Text(c.finale.s(), style = T.Body)
            Spacer(Modifier.height(10.dp))
            Text(
                t("Сегодня весь подъезд собрался в пятой квартире. На столе лежит двадцать четвёртый свёрток, и он ${Mystery.phrase.getValue(Mystery.Attr.WRAP).getValue(c.traits.getValue(Mystery.Attr.WRAP)).ru}. На нём ваше имя.",
                    "Esta noche todo el portal se reunió en el piso 5. Sobre la mesa descansa el vigésimo cuarto paquete, ${Mystery.phrase.getValue(Mystery.Attr.WRAP).getValue(c.traits.getValue(Mystery.Attr.WRAP)).en}. Lleva tu nombre."),
                style = T.Body,
            )
            Text(c.sign.s(), style = T.Hand.copy(color = C.warm, fontSize = 24.sp), modifier = Modifier.padding(vertical = 12.dp))
            val n = s.litCount
            Text(
                (if (s.tries == 0) t("Разгадано с первой попытки. ", "¡Resuelto al primer intento! ") else t("Разгадано с ${s.tries + 1}-й попытки. ", "Resuelto al intento n.º ${s.tries + 1}. ")) +
                    t("Ёлка переедет на антресоль: $n ${plural(n, "игрушка", "игрушки", "игрушек")}${if (s.rare.isNotEmpty()) ", бабушкиных: ${s.rare.size}" else ""}. В следующем декабре Тайным Дедом может оказаться кто угодно.",
                        "El árbol sube al estante: $n adornos${if (s.rare.isNotEmpty()) ", ${s.rare.size} de la abuela" else ""}. El próximo diciembre, cualquiera podría ser el Abuelo Secreto."),
                style = T.Muted,
            )
            Spacer(Modifier.height(16.dp))
            Row {
                WarmButton(t("Убрать ёлку на антресоль", "Guardar el árbol en el estante")) {
                    val year = s.startDate.year
                    acts.settings { st ->
                        st.copy(
                            shelf = st.shelf + Past(year, s.culprit, s.lit.keys.sorted(), s.rare.sorted(), s.over),
                            season = Season.new(Season.advent(evening.today.plusDays(1))),
                        )
                    }
                    acts.open(null)
                    acts.tab(Tab.TREE)
                    acts.toast(if (ru) "Ёлка на антресоли. До следующего декабря!" else "¡El árbol está en el estante. Hasta el próximo diciembre!")
                }
                Spacer(Modifier.width(10.dp))
                GhostButton(t("Во двор", "Volver al patio")) { acts.open(null) }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}
