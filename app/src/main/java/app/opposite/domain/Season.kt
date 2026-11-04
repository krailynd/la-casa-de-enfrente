package app.opposite.domain

import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import kotlin.random.Random

/**
 * Something real behind a window, as in an ordinary advent calendar: a chocolate in box 7, «look in your
 * coat pocket», a note, a little treat. Optional; filled in by you or by whoever made the calendar for you.
 */
data class Gift(val text: String, val kind: String = "thing") {
    companion object {
        /** thing — something to find; treat — something sweet; note — words; deed — something to do together. */
        val KINDS = listOf("thing", "treat", "note", "deed")
    }
}

/** One December. Days are counted from [start] (day 1). */
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
    /** Gifts were set by someone else: the list stays sealed so nobody peeks ahead. */
    val giftsSealed: Boolean = false,
    val giftsFrom: String = "",
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
            val c = culprit ?: Mystery.suspects[random.nextInt(Mystery.suspects.size)].id
            return Season(start.toEpochDay(), c, Mystery.plan(c, random))
        }

        /** The next 1 December, or this one if we are in December already (before the 25th). */
        fun advent(today: LocalDate): LocalDate {
            val dec1 = LocalDate.of(today.year, 12, 1)
            return if (today.monthValue == 12 && today.dayOfMonth <= 24) dec1 else if (today.isAfter(dec1)) dec1.plusYears(1) else dec1
        }
    }
}

/** What a window is, right now. */
enum class WinState { FUTURE, WAITING_DUSK, TODAY, MISSED, LIT }

class Evening(val season: Season, val now: Long, val zone: ZoneId, val sunset: (LocalDate) -> Long) {
    val today: LocalDate = Instant.ofEpochMilli(now).atZone(zone).toLocalDate()
    val day = season.day(today)
    val dusk: Boolean get() = now >= sunset(today)

    fun state(d: Int): WinState = when {
        season.lit.containsKey(d) -> WinState.LIT
        d > day -> WinState.FUTURE
        d == day -> if (dusk) WinState.TODAY else WinState.WAITING_DUSK
        else -> WinState.MISSED
    }

    val beforeStart get() = day < 1
    val missed get() = (1 until day.coerceAtMost(Season.DAYS + 1)).count { !season.lit.containsKey(it) }
}

/** The rules: lighting windows, clues, candles, accusing. Pure functions over [Season]. */
object Rules {
    /** Lights window [d]; a missed one costs a candle. Lighting on a Sunday puts a candle on the wreath. */
    fun light(s: Season, d: Int, now: Long, zone: ZoneId): Season {
        if (s.lit.containsKey(d)) return s
        val today = Instant.ofEpochMilli(now).atZone(zone).toLocalDate()
        val late = d < s.day(today)
        if (late && s.candles <= 0) return s
        val sunday = !late && today.dayOfWeek == DayOfWeek.SUNDAY
        return s.copy(lit = s.lit + (d to now), candles = s.candles - (if (late) 1 else 0) + (if (sunday) 1 else 0))
    }

    fun findClue(s: Season, d: Int) = if (Mystery.slots.containsKey(d)) s.copy(clues = s.clues + d) else s
    fun findMandarin(s: Season, d: Int) = s.copy(rare = s.rare + d)
    fun strike(s: Season, id: String) = s.copy(struck = if (id in s.struck) s.struck - id else s.struck + id, guess = if (s.guess == id) null else s.guess)
    fun suspect(s: Season, id: String) = s.copy(guess = id, struck = s.struck - id)

    /** The first found clue that rules [id] out, or null when nothing found contradicts them. */
    fun contradiction(s: Season, id: String): Mystery.Clue? {
        val p = Mystery.suspect(id)
        return s.foundClues().firstOrNull { !it.allows(p) }
    }

    fun accuse(s: Season, id: String, now: Long): Season = if (id == s.culprit) s.copy(guess = id, solved = now) else s.copy(tries = s.tries + 1, guess = id)

    /** Suspects nothing found so far rules out. */
    fun possible(s: Season) = Mystery.suspects.filter { p -> s.foundClues().all { it.allows(p) } }
}
