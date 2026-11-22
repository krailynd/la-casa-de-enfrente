package app.opposite.domain

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.UUID

data class NotebookTodoItem(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val isDone: Boolean = false,
)

data class NotebookNote(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val content: String = "",
    val imageUri: String? = null,
    val todos: List<NotebookTodoItem> = emptyList(),
    val dateStr: String = "Noviembre 2026",
    val updatedAt: Long = System.currentTimeMillis(),
)

data class NotebookCategory(
    val id: String,
    val title: String,
    val icon: String,
    val description: String,
    val notes: List<NotebookNote> = emptyList(),
)

data class OurNotebookData(
    val categories: List<NotebookCategory> = emptyList(),
)

object OurNotebookRepository {
    private const val FILE_NAME = "our_notebook.json"
    private val _data = MutableStateFlow(OurNotebookData())
    val data: StateFlow<OurNotebookData> = _data.asStateFlow()

    fun initialize(context: Context) {
        val file = File(context.filesDir, FILE_NAME)
        if (file.exists()) {
            try {
                val json = file.readText()
                _data.value = parseJson(json)
                return
            } catch (_: Exception) {}
        }
        // Semilla inicial por defecto
        val initial = getSeedData()
        _data.value = initial
        save(context, initial)
    }

    fun save(context: Context, notebookData: OurNotebookData) {
        _data.value = notebookData
        try {
            val file = File(context.filesDir, FILE_NAME)
            file.writeText(toJson(notebookData))
        } catch (_: Exception) {}
    }

    fun toggleTodo(context: Context, categoryId: String, noteId: String, todoId: String) {
        val current = _data.value
        val updated = current.copy(
            categories = current.categories.map { cat ->
                if (cat.id == categoryId) {
                    cat.copy(
                        notes = cat.notes.map { note ->
                            if (note.id == noteId) {
                                note.copy(
                                    todos = note.todos.map { todo ->
                                        if (todo.id == todoId) todo.copy(isDone = !todo.isDone) else todo
                                    }
                                )
                            } else note
                        }
                    )
                } else cat
            }
        )
        save(context, updated)
    }

    fun addTodo(context: Context, categoryId: String, noteId: String, text: String) {
        if (text.isBlank()) return
        val current = _data.value
        val updated = current.copy(
            categories = current.categories.map { cat ->
                if (cat.id == categoryId) {
                    cat.copy(
                        notes = cat.notes.map { note ->
                            if (note.id == noteId) {
                                note.copy(todos = note.todos + NotebookTodoItem(text = text.trim()))
                            } else note
                        }
                    )
                } else cat
            }
        )
        save(context, updated)
    }

    fun addNote(context: Context, categoryId: String, title: String, content: String, imageUri: String? = null) {
        val current = _data.value
        val newNote = NotebookNote(
            title = title.ifBlank { "Nota sin título" },
            content = content,
            imageUri = imageUri,
        )
        val updated = current.copy(
            categories = current.categories.map { cat ->
                if (cat.id == categoryId) {
                    cat.copy(notes = listOf(newNote) + cat.notes)
                } else cat
            }
        )
        save(context, updated)
    }

    fun addCategory(context: Context, title: String, icon: String, desc: String) {
        val current = _data.value
        val newCat = NotebookCategory(
            id = "cat_${UUID.randomUUID()}",
            title = title.ifBlank { "Nuevo Cuaderno" },
            icon = icon.ifBlank { "📓" },
            description = desc.ifBlank { "Notas compartidas" },
        )
        save(context, current.copy(categories = current.categories + newCat))
    }

    private fun getSeedData(): OurNotebookData {
        return OurNotebookData(
            categories = listOf(
                NotebookCategory(
                    id = "metas_suenos",
                    title = "Nuestras Metas & Sueños",
                    icon = "📌",
                    description = "Cosas bonitas que construiremos juntos paso a paso.",
                    notes = listOf(
                        NotebookNote(
                            id = "metas_2027",
                            title = "Metas para nuestra nueva etapa",
                            content = "Pequeños y grandes pasos para cuidar nuestra paz, crecer y ser felices juntos.",
                            todos = listOf(
                                NotebookTodoItem(text = "Aprender a cocinar pasta italiana desde cero juntos", isDone = false),
                                NotebookTodoItem(text = "Hacer un viaje tranquilo a una cabaña en la sierra o el campo", isDone = false),
                                NotebookTodoItem(text = "Componer una canción juntos donde tú escribas una estrofa", isDone = false),
                                NotebookTodoItem(text = "Tener nuestro propio rincón acogedor lleno de plantitas y luz suave", isDone = false),
                                NotebookTodoItem(text = "Cuidar nuestra comunicación con amor en cada conversación", isDone = true),
                            ),
                            dateStr = "Noviembre 2026",
                        )
                    )
                ),
                NotebookCategory(
                    id = "peliculas_series",
                    title = "Películas & Series por Ver",
                    icon = "🎬",
                    description = "Nuestra lista de noches de cine con manta y palomitas.",
                    notes = listOf(
                        NotebookNote(
                            id = "cine_lista",
                            title = "Maratón de películas pendientes",
                            content = "Películas recomendadas y comedias para reírnos abrazados.",
                            todos = listOf(
                                NotebookTodoItem(text = "Cuestión de Tiempo (About Time)", isDone = false),
                                NotebookTodoItem(text = "El Diario de Noah (The Notebook)", isDone = false),
                                NotebookTodoItem(text = "Maratón de películas animadas navideñas", isDone = false),
                                NotebookTodoItem(text = "La La Land con luz tenue", isDone = false),
                            ),
                            dateStr = "Diciembre 2026",
                        )
                    )
                ),
                NotebookCategory(
                    id = "lugares_visitar",
                    title = "Lugares por Descubrir",
                    icon = "✈️",
                    description = "Rutas, atardeceres y caminatas en Ica y Perú.",
                    notes = listOf(
                        NotebookNote(
                            id = "rutas_ica",
                            title = "Lugares especiales en Ica",
                            content = "Lugares para caminar despacio, tomarnos fotos y respirar aire fresco.",
                            todos = listOf(
                                NotebookTodoItem(text = "Ver el atardecer en Huacachina comiendo tejas de nuez", isDone = false),
                                NotebookTodoItem(text = "Caminata por la orilla del mar en Paracas", isDone = false),
                                NotebookTodoItem(text = "Encontrar una nueva cafetería acogedora en el centro de Ica", isDone = false),
                            ),
                            dateStr = "Noviembre 2026",
                        )
                    )
                ),
                NotebookCategory(
                    id = "recetas_antojitos",
                    title = "Recetas & Antojitos",
                    icon = "🍲",
                    description = "Comidas calientitas y postres que nos alegran el día.",
                    notes = listOf(
                        NotebookNote(
                            id = "receta_pasta",
                            title = "Nuestra Pasta Especial",
                            content = "Ingredientes: Fideos fettuccine, crema de leche suave, queso parmesano, mantequilla, champiñones dorados y pimienta negra al gusto.\n\nPreparación: Hervir la pasta al dente. Saltear los champiñones con mantequilla y ajo picado fino. Verter la crema, fundir el queso y mezclar con amor. Servir calientito con dos tenedores.",
                            dateStr = "Receta de la casa",
                        )
                    )
                ),
                NotebookCategory(
                    id = "notas_amor",
                    title = "Notas & Cartitas Rápidas",
                    icon = "📝",
                    description = "Recordatorios de por qué te amo tanto todos los días.",
                    notes = listOf(
                        NotebookNote(
                            id = "nota_06nov",
                            title = "Viernes 6 de noviembre · 6:18 PM",
                            content = "Ese minuto en que volvimos a mirarnos a los ojos y decidimos darnos esta oportunidad fue el más feliz de mi año. Gracias por estar aquí, mi Principessa.",
                            dateStr = "06 Nov 2026",
                        )
                    )
                ),
            )
        )
    }

    private fun toJson(data: OurNotebookData): String {
        val root = JSONObject()
        val catArr = JSONArray()
        data.categories.forEach { cat ->
            val cObj = JSONObject()
            cObj.put("id", cat.id)
            cObj.put("title", cat.title)
            cObj.put("icon", cat.icon)
            cObj.put("description", cat.description)
            val notesArr = JSONArray()
            cat.notes.forEach { n ->
                val nObj = JSONObject()
                nObj.put("id", n.id)
                nObj.put("title", n.title)
                nObj.put("content", n.content)
                n.imageUri?.let { nObj.put("imageUri", it) }
                nObj.put("dateStr", n.dateStr)
                nObj.put("updatedAt", n.updatedAt)
                val todosArr = JSONArray()
                n.todos.forEach { t ->
                    val tObj = JSONObject()
                    tObj.put("id", t.id)
                    tObj.put("text", t.text)
                    tObj.put("isDone", t.isDone)
                    todosArr.put(tObj)
                }
                nObj.put("todos", todosArr)
                notesArr.put(nObj)
            }
            cObj.put("notes", notesArr)
            catArr.put(cObj)
        }
        root.put("categories", catArr)
        return root.toString(2)
    }

    private fun parseJson(jsonStr: String): OurNotebookData {
        val root = JSONObject(jsonStr)
        val catArr = root.optJSONArray("categories") ?: return getSeedData()
        val cats = mutableListOf<NotebookCategory>()
        for (i in 0 until catArr.length()) {
            val cObj = catArr.getJSONObject(i)
            val notesArr = cObj.optJSONArray("notes")
            val notes = mutableListOf<NotebookNote>()
            if (notesArr != null) {
                for (j in 0 until notesArr.length()) {
                    val nObj = notesArr.getJSONObject(j)
                    val todosArr = nObj.optJSONArray("todos")
                    val todos = mutableListOf<NotebookTodoItem>()
                    if (todosArr != null) {
                        for (k in 0 until todosArr.length()) {
                            val tObj = todosArr.getJSONObject(k)
                            todos.add(
                                NotebookTodoItem(
                                    id = tObj.optString("id", UUID.randomUUID().toString()),
                                    text = tObj.getString("text"),
                                    isDone = tObj.optBoolean("isDone", false)
                                )
                            )
                        }
                    }
                    notes.add(
                        NotebookNote(
                            id = nObj.optString("id", UUID.randomUUID().toString()),
                            title = nObj.optString("title", ""),
                            content = nObj.optString("content", ""),
                            imageUri = nObj.optString("imageUri").takeIf { it.isNotBlank() },
                            todos = todos,
                            dateStr = nObj.optString("dateStr", ""),
                            updatedAt = nObj.optLong("updatedAt", System.currentTimeMillis()),
                        )
                    )
                }
            }
            cats.add(
                NotebookCategory(
                    id = cObj.getString("id"),
                    title = cObj.getString("title"),
                    icon = cObj.optString("icon", "📓"),
                    description = cObj.optString("description", ""),
                    notes = notes
                )
            )
        }
        return OurNotebookData(categories = cats)
    }
}
