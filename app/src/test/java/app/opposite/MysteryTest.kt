package app.opposite

import app.opposite.domain.House
import app.opposite.domain.Mystery
import app.opposite.domain.Rules
import app.opposite.domain.Season
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId
import kotlin.random.Random

class MysteryTest {
    private fun left(culprit: String, plan: Map<Int, Mystery.Pick>, upTo: Int) =
        Mystery.suspects.filter { p -> (1..upTo).mapNotNull { Mystery.clue(it, culprit, plan) }.all { it.allows(p) } }

    @Test fun everyCulpritIsSolvableAndPacedSlowly() {
        for (c in Mystery.suspects) repeat(60) { seed ->
            val plan = Mystery.plan(c.id, Random(seed))
            val all = left(c.id, plan, 23)
            assertEquals("${c.id} seed $seed", listOf(c.id), all.map { it.id })
            assertTrue("${c.id} seed $seed solved by day 10", left(c.id, plan, 10).size >= 3)
            assertTrue("${c.id} seed $seed solved by day 15", left(c.id, plan, 15).size >= 2)
        }
    }

    @Test fun everyClueHasTextInBothLanguages() {
        for (c in Mystery.suspects) {
            val plan = Mystery.plan(c.id, Random(1))
            for (d in 1..23) {
                val clue = Mystery.clue(d, c.id, plan)!!
                assertTrue(clue.text.ru.isNotBlank() && clue.text.en.isNotBlank())
                assertTrue("no Latin in RU: ${clue.text.ru}", !Regex("[A-Za-z]{3,}").containsMatchIn(clue.text.ru))
            }
        }
    }

    @Test fun planSurvivesSaving() {
        val plan = Mystery.plan("olga", Random(5))
        assertEquals(plan, Mystery.decode(Mystery.encode(plan)))
    }

    @Test fun houseHasTwentyFourWindowsInDistinctCells() {
        assertEquals(24, House.residents.size)
        assertEquals(24, House.residents.map { it.cell }.toSet().size)
        assertTrue(House.residents.none { it.cell == 12 })
        for (s in Mystery.suspects) assertTrue(House.resident(s.day).people.isNotEmpty())
        for (r in House.residents) if (r.day < 24) assertEquals("one clue thing in ${r.day}", 1, r.things.count { it.clue })
    }

    @Test fun candlesAndSundays() {
        val zone = ZoneId.of("Europe/Moscow")
        val start = LocalDate.of(2026, 12, 1) // Tuesday
        var s = Season.new(start, "petr", Random(1))
        fun at(d: LocalDate) = d.atTime(18, 0).atZone(zone).toInstant().toEpochMilli()
        // Day 6 is Sunday 6 Dec: lighting on time adds a candle.
        s = Rules.light(s, 6, at(start.plusDays(5)), zone)
        assertEquals(2, s.candles)
        // A missed window costs one.
        s = Rules.light(s, 2, at(start.plusDays(7)), zone)
        assertEquals(1, s.candles)
        s = Rules.light(s, 3, at(start.plusDays(7)), zone)
        assertEquals(0, s.candles)
        val again = Rules.light(s, 4, at(start.plusDays(7)), zone)
        assertTrue("no candles — stays dark", !again.lit.containsKey(4))
    }

    @Test fun accusing() {
        var s = Season.new(LocalDate.of(2026, 12, 1), "zoya", Random(2))
        s = (1..23).fold(s) { acc, d -> Rules.findClue(acc, d) }
        assertEquals(listOf("zoya"), Rules.possible(s).map { it.id })
        assertTrue(Rules.contradiction(s, "petr") != null)
        s = Rules.accuse(s, "petr", 1)
        assertEquals(1, s.tries); assertTrue(!s.over)
        s = Rules.accuse(s, "zoya", 2)
        assertTrue(s.over)
    }
}
