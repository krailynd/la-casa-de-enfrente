package app.opposite.sync

import android.content.Context
import android.util.Base64
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class LetterItem(
    val id: String,
    val title: String,
    val category: String, // "Nueva Oportunidad", "Para cuando me extrañes", "Noche de Diciembre", "Promesa", "Cómplice"
    val date: String,
    val content: String,
    val isRead: Boolean = false,
    val isFavorite: Boolean = false,
    val reaction: String? = null,
    val reactionTime: String? = null,
    val isLocked: Boolean = false,
    val riddleQuestion: String? = null,
    val riddleHint: String? = null,
    val riddleOptions: List<String> = emptyList(),
    val riddleCorrectIndex: Int = 0,
)

data class FrancescaProgress(
    val lastSync: String = "",
    val openedWindows: List<Int> = emptyList(),
    val redeemedVouchers: List<String> = emptyList(),
    val reactions: Map<String, String> = emptyMap(),
    val currentSuspect: String? = null,
    val solvedRiddles: List<Int> = emptyList(),
    val completedDrawings: List<Int> = emptyList(),
)

object GitHubSyncRepository {
    private const val GITHUB_API_BASE = "https://api.github.com/repos"
    private const val DEFAULT_OWNER = "krailynd"
    private const val DEFAULT_REPO = "nuestro-refugio"

    private val _letters = MutableStateFlow<List<LetterItem>>(emptyList())
    val letters: StateFlow<List<LetterItem>> = _letters.asStateFlow()

    private val _progress = MutableStateFlow(FrancescaProgress())
    val progress: StateFlow<FrancescaProgress> = _progress.asStateFlow()

    private val _hasNewLetter = MutableStateFlow(false)
    val hasNewLetter: StateFlow<Boolean> = _hasNewLetter.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.IO)

    fun initialize(context: Context) {
        scope.launch {
            loadLocalLetters(context)
            loadLocalProgress(context)
            syncFromGitHub(context)
        }
    }

    private fun lettersFile(context: Context) = File(context.filesDir, "letters_cache.json")
    private fun progressFile(context: Context) = File(context.filesDir, "progress_cache.json")

    private fun loadLocalLetters(context: Context) {
        val file = lettersFile(context)
        val jsonStr = if (file.exists()) {
            file.readText()
        } else {
            try {
                context.assets.open("sync/letters.json").bufferedReader().use { it.readText() }
            } catch (_: Exception) {
                "[]"
            }
        }
        _letters.value = parseLettersJson(jsonStr)
    }

    private fun loadLocalProgress(context: Context) {
        val file = progressFile(context)
        if (file.exists()) {
            try {
                val obj = JSONObject(file.readText())
                val openedList = mutableListOf<Int>()
                val arrWin = obj.optJSONArray("openedWindows")
                if (arrWin != null) {
                    for (i in 0 until arrWin.length()) openedList.add(arrWin.getInt(i))
                }
                val vouchersList = mutableListOf<String>()
                val arrVouch = obj.optJSONArray("redeemedVouchers")
                if (arrVouch != null) {
                    for (i in 0 until arrVouch.length()) vouchersList.add(arrVouch.getString(i))
                }
                val reactMap = mutableMapOf<String, String>()
                val objReact = obj.optJSONObject("reactions")
                if (objReact != null) {
                    for (k in objReact.keys()) reactMap[k] = objReact.getString(k)
                }

                _progress.value = FrancescaProgress(
                    lastSync = obj.optString("lastSync", ""),
                    openedWindows = openedList,
                    redeemedVouchers = vouchersList,
                    reactions = reactMap,
                    currentSuspect = obj.optString("currentSuspect").takeIf { it.isNotBlank() },
                )
            } catch (_: Exception) {
            }
        }
    }

    fun syncFromGitHub(context: Context, owner: String = DEFAULT_OWNER, repo: String = DEFAULT_REPO, token: String? = null) {
        scope.launch {
            _isSyncing.value = true
            try {
                // Intento 1: data/letters.json en rama main
                var remoteJson: String? = null
                val urls = listOf(
                    "https://raw.githubusercontent.com/$owner/$repo/main/data/letters.json",
                    "https://raw.githubusercontent.com/$owner/$repo/main/letters.json"
                )
                for (u in urls) {
                    try {
                        val conn = URL(u).openConnection() as HttpURLConnection
                        conn.connectTimeout = 5000
                        conn.readTimeout = 5000
                        if (conn.responseCode == 200) {
                            remoteJson = conn.inputStream.bufferedReader().use { it.readText() }
                            break
                        }
                    } catch (_: Exception) {}
                }

                if (remoteJson != null) {
                    val remoteLetters = parseLettersJson(remoteJson)
                    if (remoteLetters.isNotEmpty()) {
                        val currentIds = _letters.value.map { it.id }.toSet()
                        val newCount = remoteLetters.count { it.id !in currentIds }
                        if (newCount > 0) {
                            _hasNewLetter.value = true
                        }
                        // Merge con estados locales (isRead, isFavorite, reaction, isLocked)
                        val localMap = _letters.value.associateBy { it.id }
                        val merged = remoteLetters.map { r ->
                            val loc = localMap[r.id]
                            if (loc != null) {
                                r.copy(
                                    isRead = loc.isRead,
                                    isFavorite = loc.isFavorite,
                                    isLocked = loc.isLocked, // Si ya la desbloqueó localmente, mantenerla desbloqueada
                                    reaction = loc.reaction ?: r.reaction,
                                    reactionTime = loc.reactionTime ?: r.reactionTime,
                                )
                            } else r
                        }
                        _letters.value = merged
                        lettersFile(context).writeText(serializeLettersJson(merged))
                    }
                }
            } catch (_: Exception) {
            } finally {
                _isSyncing.value = false
            }
        }
    }

    fun markLetterAsRead(context: Context, letterId: String) {
        val updated = _letters.value.map {
            if (it.id == letterId) it.copy(isRead = true) else it
        }
        _letters.value = updated
        _hasNewLetter.value = updated.any { !it.isRead }
        scope.launch {
            lettersFile(context).writeText(serializeLettersJson(updated))
        }
    }

    fun unlockLetter(context: Context, letterId: String) {
        val updated = _letters.value.map {
            if (it.id == letterId) it.copy(isLocked = false, isRead = true) else it
        }
        _letters.value = updated
        scope.launch {
            lettersFile(context).writeText(serializeLettersJson(updated))
        }
    }

    fun toggleFavorite(context: Context, letterId: String) {
        val updated = _letters.value.map {
            if (it.id == letterId) it.copy(isFavorite = !it.isFavorite) else it
        }
        _letters.value = updated
        scope.launch {
            lettersFile(context).writeText(serializeLettersJson(updated))
        }
    }

    fun recordReaction(context: Context, letterId: String, reaction: String) {
        val now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
        val updated = _letters.value.map {
            if (it.id == letterId) it.copy(reaction = reaction, reactionTime = now) else it
        }
        _letters.value = updated
        val updatedReactions = _progress.value.reactions.toMutableMap()
        updatedReactions[letterId] = "$reaction ($now)"
        _progress.value = _progress.value.copy(reactions = updatedReactions)

        scope.launch {
            lettersFile(context).writeText(serializeLettersJson(updated))
            progressFile(context).writeText(serializeProgressJson(_progress.value))
            pushProgressToGitHub(context, "sync: Principessa reaccionó a carta $letterId con $reaction")
        }
    }

    fun recordWindowOpened(context: Context, day: Int) {
        val now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
        val updatedWindows = (_progress.value.openedWindows + day).distinct().sorted()
        _progress.value = _progress.value.copy(lastSync = now, openedWindows = updatedWindows)
        scope.launch {
            progressFile(context).writeText(serializeProgressJson(_progress.value))
            pushProgressToGitHub(context, "sync: Principessa abrió ventana $day ($now)")
        }
    }

    fun recordVoucherRedeemed(context: Context, voucherId: String) {
        val now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
        val updatedVouchers = (_progress.value.redeemedVouchers + voucherId).distinct()
        _progress.value = _progress.value.copy(lastSync = now, redeemedVouchers = updatedVouchers)
        scope.launch {
            progressFile(context).writeText(serializeProgressJson(_progress.value))
            pushProgressToGitHub(context, "sync: Principessa canjeó vale $voucherId ($now)")
        }
    }

    private suspend fun pushProgressToGitHub(context: Context, commitMessage: String) = withContext(Dispatchers.IO) {
        // Enviar actualización a GitHub API si hay token configurado
        try {
            val progressContent = serializeProgressJson(_progress.value)
            val base64 = Base64.encodeToString(progressContent.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)
            val apiUrl = "$GITHUB_API_BASE/$DEFAULT_OWNER/$DEFAULT_REPO/contents/data/progress_sync.json"

            var sha: String? = null
            try {
                val getConn = URL(apiUrl).openConnection() as HttpURLConnection
                getConn.setRequestProperty("Accept", "application/vnd.github.v3+json")
                if (getConn.responseCode == 200) {
                    val resp = JSONObject(getConn.inputStream.bufferedReader().use { it.readText() })
                    sha = resp.optString("sha")
                }
            } catch (_: Exception) {}

            val putPayload = JSONObject().apply {
                put("message", commitMessage)
                put("content", base64)
                if (!sha.isNullOrBlank()) put("sha", sha)
            }

            val putConn = URL(apiUrl).openConnection() as HttpURLConnection
            putConn.requestMethod = "PUT"
            putConn.doOutput = true
            putConn.setRequestProperty("Accept", "application/vnd.github.v3+json")
            putConn.setRequestProperty("Content-Type", "application/json")
            putConn.outputStream.use { it.write(putPayload.toString().toByteArray(Charsets.UTF_8)) }
            putConn.responseCode
        } catch (_: Exception) {}
    }

    private fun parseLettersJson(json: String): List<LetterItem> {
        val list = mutableListOf<LetterItem>()
        try {
            val arr = JSONArray(json)
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                val opts = mutableListOf<String>()
                val arrOpts = o.optJSONArray("riddleOptions")
                if (arrOpts != null) {
                    for (k in 0 until arrOpts.length()) opts.add(arrOpts.getString(k))
                }

                list.add(
                    LetterItem(
                        id = o.optString("id", "carta_$i"),
                        title = o.optString("title", "Carta para Principessa"),
                        category = o.optString("category", "Promesa"),
                        date = o.optString("date", ""),
                        content = o.optString("content", ""),
                        isRead = o.optBoolean("isRead", false),
                        isFavorite = o.optBoolean("isFavorite", false),
                        reaction = o.optString("reaction").takeIf { it.isNotBlank() },
                        reactionTime = o.optString("reactionTime").takeIf { it.isNotBlank() },
                        isLocked = o.optBoolean("isLocked", false),
                        riddleQuestion = o.optString("riddleQuestion").takeIf { it.isNotBlank() },
                        riddleHint = o.optString("riddleHint").takeIf { it.isNotBlank() },
                        riddleOptions = opts,
                        riddleCorrectIndex = o.optInt("riddleCorrectIndex", 0),
                    )
                )
            }
        } catch (_: Exception) {
        }
        return list
    }

    private fun serializeLettersJson(letters: List<LetterItem>): String {
        val arr = JSONArray()
        for (l in letters) {
            arr.put(JSONObject().apply {
                put("id", l.id)
                put("title", l.title)
                put("category", l.category)
                put("date", l.date)
                put("content", l.content)
                put("isRead", l.isRead)
                put("isFavorite", l.isFavorite)
                l.reaction?.let { put("reaction", it) }
                l.reactionTime?.let { put("reactionTime", it) }
                put("isLocked", l.isLocked)
                l.riddleQuestion?.let { put("riddleQuestion", it) }
                l.riddleHint?.let { put("riddleHint", it) }
                if (l.riddleOptions.isNotEmpty()) {
                    put("riddleOptions", JSONArray(l.riddleOptions))
                }
                put("riddleCorrectIndex", l.riddleCorrectIndex)
            })
        }
        return arr.toString(2)
    }

    private fun serializeProgressJson(p: FrancescaProgress): String {
        return JSONObject().apply {
            put("lastSync", p.lastSync)
            put("openedWindows", JSONArray(p.openedWindows))
            put("redeemedVouchers", JSONArray(p.redeemedVouchers))
            put("reactions", JSONObject(p.reactions))
            p.currentSuspect?.let { put("currentSuspect", it) }
        }.toString(2)
    }
}
