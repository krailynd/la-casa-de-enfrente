package app.opposite.domain

import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import kotlin.random.Random

/**
 * Something real behind a window, as in an ordinary advent calendar.
 */
data class Gift(val text: String, val kind: String = "thing") {
    companion object {
        val KINDS = listOf("thing", "treat", "note", "deed")
    }
}

/**
 * Advent calendar season for Francesca.
 * Days 1..24 lead to Christmas Eve, and Day 25 represents the special 27th of December (9 months).
 */
data class Season(
    val start: Long,
    val culprit: String,
    val plan: Map<Int, Mystery.Pick>,
    val lit: Map<Int, Long> = emptyMap(),
    val clues: Set<Int> = emptySet(),
    val rare: Set<Int> = emptySet(),
    val candles: Int = 1,
    val struck: Set<String> = emptySet(),
    val guess: String? = null,
    val tries: Int = 0,
    val solved: Long? = null,
    val gifts: Map<Int, Gift> = emptyMap(),
    val giftsSealed: Boolean = false,
    val giftsFrom: String = "Elvis",
) {
    fun day(today: LocalDate): Int = (today.toEpochDay() - start + 1).toInt()
    val startDate: LocalDate get() = LocalDate.ofEpochDay(start)
    fun date(d: Int): LocalDate = startDate.plusDays(d - 1L)
    val litCount get() = lit.size
    val over get() = solved != null

    fun clue(d: Int) = Mystery.clue(d, culprit, plan)
    fun foundClues() = clues.sorted().mapNotNull { clue(it) }

    companion object {
        const val DAYS = 24

        fun new(start: LocalDate, culprit: String? = null, random: Random = Random.Default): Season {
            val c = culprit ?: "elvis" // Elvis is the admirer for Francesca
            return Season(start.toEpochDay(), c, Mystery.plan(c, random))
        }

        fun advent(today: LocalDate): LocalDate {
            val dec1 = LocalDate.of(today.year, 12, 1)
            return if (today.monthValue == 12 && today.dayOfMonth <= 24) dec1 else if (today.isAfter(dec1)) dec1.plusYears(1) else dec1
        }
    }
}

enum class WinState { FUTURE, WAITING_DUSK, TODAY, MISSED, LIT }

class Evening(
    val season: Season,
    val now: Long,
    val zone: ZoneId,
    val sunset: (LocalDate) -> Long,
    val previewMode: Boolean = false,
) {
    val today: LocalDate = Instant.ofEpochMilli(now).atZone(zone).toLocalDate()
    val localTime = Instant.ofEpochMilli(now).atZone(zone).toLocalTime()
    val day = season.day(today)
    // Es noche si es de madrugada (antes del amanecer ~5:40 AM) o después del atardecer (>= sunset)
    val isNight: Boolean get() {
        val minuteOfDay = localTime.hour * 60 + localTime.minute
        return minuteOfDay < 340 || now >= sunset(today)
    }
    val dusk: Boolean get() = isNight

    fun state(d: Int): WinState = when {
        previewMode -> if (season.lit.containsKey(d)) WinState.LIT else WinState.TODAY
        season.lit.containsKey(d) -> WinState.LIT
        d > day -> WinState.FUTURE
        d == day -> if (dusk) WinState.TODAY else WinState.WAITING_DUSK
        else -> WinState.MISSED
    }

    val beforeStart get() = if (previewMode) false else day < 1
    val missed get() = if (previewMode) 0 else (1 until day.coerceAtMost(Season.DAYS + 1)).count { !season.lit.containsKey(it) }
}

object Rules {
    fun light(s: Season, d: Int, now: Long, zone: ZoneId): Season {
        if (s.lit.containsKey(d)) return s
        val today = Instant.ofEpochMilli(now).atZone(zone).toLocalDate()
        val late = d < s.day(today)
        val sunday = !late && today.dayOfWeek == DayOfWeek.SUNDAY
        return s.copy(
            lit = s.lit + (d to now),
            candles = (s.candles - (if (late) 1 else 0) + (if (sunday) 1 else 0)).coerceAtLeast(0),
        )
    }

    fun findClue(s: Season, d: Int) = if (Mystery.slots.containsKey(d)) s.copy(clues = s.clues + d) else s
    fun findMandarin(s: Season, d: Int) = s.copy(rare = s.rare + d)
    fun strike(s: Season, id: String) = s.copy(struck = if (id in s.struck) s.struck - id else s.struck + id, guess = if (s.guess == id) null else s.guess)
    fun suspect(s: Season, id: String) = s.copy(guess = id, struck = s.struck - id)

    fun contradiction(s: Season, id: String): Mystery.Clue? {
        val p = Mystery.suspect(id)
        return s.foundClues().firstOrNull { !it.allows(p) }
    }

    fun accuse(s: Season, id: String, now: Long): Season =
        if (id == s.culprit) s.copy(guess = id, solved = now) else s.copy(tries = s.tries + 1, guess = id)

    fun possible(s: Season) = Mystery.suspects.filter { p -> s.foundClues().all { it.allows(p) } }
}
