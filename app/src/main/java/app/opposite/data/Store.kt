package app.opposite.data

import android.content.Context
import app.opposite.domain.Gift
import app.opposite.domain.Mystery
import app.opposite.domain.Season
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.json.JSONArray
import org.json.JSONObject

/** A finished season kept on the shelf: its tree and who it was. */
data class Past(val year: Int, val culprit: String, val ornaments: List<Int>, val rare: List<Int>, val solved: Boolean)

data class Settings(
    val onboarded: Boolean = false,
    val lat: Double? = null,
    val lon: Double? = null,
    val notify: Boolean = true,
    val season: Season? = null,
    val shelf: List<Past> = emptyList(),
)

/** Everything lives in one small JSON document; writes are rare (a few a day). */
class Store(context: Context) {
    private val prefs = context.getSharedPreferences("opposite", Context.MODE_PRIVATE)
    private val _state = MutableStateFlow(read())
    val state: StateFlow<Settings> = _state
    val now: Settings get() = _state.value

    fun update(change: (Settings) -> Settings) {
        val next = change(_state.value)
        _state.value = next
        prefs.edit().putString("state", write(next).toString()).apply()
    }

    fun season(change: (Season) -> Season) = update { s -> s.season?.let { s.copy(season = change(it)) } ?: s }

    private fun read(): Settings = try {
        prefs.getString("state", null)?.let { parse(JSONObject(it)) } ?: Settings()
    } catch (_: Exception) {
        Settings()
    }

    companion object {
        fun write(s: Settings): JSONObject = JSONObject().apply {
            put("onboarded", s.onboarded)
            s.lat?.let { put("lat", it) }
            s.lon?.let { put("lon", it) }
            put("notify", s.notify)
            s.season?.let { put("season", season(it)) }
            put("shelf", JSONArray(s.shelf.map { p ->
                JSONObject().put("year", p.year).put("culprit", p.culprit).put("ornaments", JSONArray(p.ornaments)).put("rare", JSONArray(p.rare)).put("solved", p.solved)
            }))
        }

        private fun season(s: Season) = JSONObject().apply {
            put("start", s.start)
            put("culprit", s.culprit)
            put("plan", Mystery.encode(s.plan))
            put("lit", JSONObject().apply { s.lit.forEach { (d, t) -> put(d.toString(), t) } })
            put("clues", JSONArray(s.clues.toList()))
            put("rare", JSONArray(s.rare.toList()))
            put("candles", s.candles)
            put("struck", JSONArray(s.struck.toList()))
            s.guess?.let { put("guess", it) }
            put("tries", s.tries)
            s.solved?.let { put("solved", it) }
            put("gifts", JSONObject().apply { s.gifts.forEach { (d, g) -> put(d.toString(), JSONObject().put("text", g.text).put("kind", g.kind)) } })
            put("giftsSealed", s.giftsSealed)
            put("giftsFrom", s.giftsFrom)
        }

        private fun ints(a: JSONArray?) = if (a == null) emptyList() else (0 until a.length()).map { a.getInt(it) }
        private fun strs(a: JSONArray?) = if (a == null) emptyList() else (0 until a.length()).map { a.getString(it) }

        fun parse(o: JSONObject): Settings = Settings(
            onboarded = o.optBoolean("onboarded"),
            lat = if (o.has("lat")) o.getDouble("lat") else null,
            lon = if (o.has("lon")) o.getDouble("lon") else null,
            notify = o.optBoolean("notify", true),
            season = o.optJSONObject("season")?.let { j ->
                Season(
                    start = j.getLong("start"),
                    culprit = j.getString("culprit"),
                    plan = Mystery.decode(j.getString("plan")),
                    lit = j.optJSONObject("lit")?.let { l -> l.keys().asSequence().associate { it.toInt() to l.getLong(it) } } ?: emptyMap(),
                    clues = ints(j.optJSONArray("clues")).toSet(),
                    rare = ints(j.optJSONArray("rare")).toSet(),
                    candles = j.optInt("candles", 1),
                    struck = strs(j.optJSONArray("struck")).toSet(),
                    guess = j.optString("guess").takeIf { it.isNotEmpty() },
                    tries = j.optInt("tries"),
                    solved = if (j.has("solved")) j.getLong("solved") else null,
                    gifts = j.optJSONObject("gifts")?.let { g -> g.keys().asSequence().associate { k -> k.toInt() to g.getJSONObject(k).let { Gift(it.getString("text"), it.optString("kind", "thing")) } } } ?: emptyMap(),
                    giftsSealed = j.optBoolean("giftsSealed"),
                    giftsFrom = j.optString("giftsFrom"),
                )
            },
            shelf = o.optJSONArray("shelf")?.let { a ->
                (0 until a.length()).map { a.getJSONObject(it) }.map { p ->
                    Past(p.getInt("year"), p.getString("culprit"), ints(p.optJSONArray("ornaments")), ints(p.optJSONArray("rare")), p.optBoolean("solved"))
                }
            } ?: emptyList(),
        )
    }
}
