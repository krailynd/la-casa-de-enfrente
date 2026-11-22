package app.opposite.data

import app.opposite.domain.Gift
import app.opposite.domain.Season
import java.time.LocalDate
import kotlin.random.Random

/** The demo build: a season ten evenings in, a few clues found, a tangerine or two, some presents. */
object DemoSeed {
    fun season(today: LocalDate = LocalDate.now(), culprit: String = "lev"): Season {
        val start = today.minusDays(9)
        val base = Season.new(start, culprit, Random(11))
        val day = 10
        val lit = (1 until day).filter { it != 4 }.associateWith { d -> start.plusDays(d - 1L).atTime(18, 10).atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli() }
        return base.copy(
            lit = lit,
            clues = setOf(1, 2, 3, 5, 6, 8),
            rare = setOf(2, 7),
            candles = 1,
            gifts = mapOf(
                3 to Gift("Chocolatina en la cajita n.º 3", "treat"),
                10 to Gift("Mira en el bolsillo del abrigo de invierno", "thing"),
                14 to Gift("Por la tarde — pista de patinaje, juntos", "deed"),
                24 to Gift("Una carta bajo la almohada", "note"),
            ),
            giftsFrom = "",
        )
    }

    fun seedIfEmpty(store: Store) {
        if (store.now.season != null) return
        store.update { it.copy(onboarded = true, season = season()) }
    }
}
