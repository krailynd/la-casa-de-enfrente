package app.opposite.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.opposite.domain.NotebookCategory
import app.opposite.domain.NotebookNote
import app.opposite.domain.OurNotebookRepository
import app.opposite.ui.theme.C
import app.opposite.ui.theme.Sh
import app.opposite.ui.theme.T

/**
 * "Nuestro Cuaderno": Notion-style shared couple space with
 * categories, to-do lists, love notes, and photo memories.
 */
@Composable
fun OurNotebookScreen(acts: Acts) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        OurNotebookRepository.initialize(context)
    }

    val notebookData by OurNotebookRepository.data.collectAsState()
    val categories = notebookData.categories
    var selectedCatId by remember(categories) {
        mutableStateOf(categories.firstOrNull()?.id ?: "")
    }

    val currentCat = categories.firstOrNull { it.id == selectedCatId } ?: categories.firstOrNull()

    var showAddNoteDialog by remember { mutableStateOf(false) }
    var showAddTodoDialog by remember { mutableStateOf<NotebookNote?>(null) }
    var showAddCategoryDialog by remember { mutableStateOf(false) }

    Column(
        Modifier
            .fillMaxSize()
            .background(C.bg)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Top Header
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
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
                Text("Nuestro Cuaderno 📖✨", style = T.Title.copy(color = C.warm, fontSize = 20.sp))
                Text("Espacio compartido de Francesca & Elvis", style = T.Muted.copy(color = C.paperMuted, fontSize = 12.sp))
            }
            // Add Category button
            Box(
                Modifier
                    .clip(Sh.Sm)
                    .background(Color(0xFF38121E))
                    .border(1.dp, C.red, Sh.Sm)
                    .clickable { showAddCategoryDialog = true }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text("+ Libreta", style = T.Eyebrow.copy(color = C.warm, fontSize = 11.sp))
            }
        }

        // Horizontal Category Tabs
        LazyRow(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { cat ->
                val isSelected = cat.id == currentCat?.id
                Box(
                    Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) C.red else Color(0xFF220D16))
                        .border(1.dp, if (isSelected) C.warm else Color(0x33F7EFE2), RoundedCornerShape(20.dp))
                        .clickable { selectedCatId = cat.id }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        "${cat.icon} ${cat.title}",
                        style = T.Name.copy(
                            color = if (isSelected) C.warm else C.ink,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    )
                }
            }
        }

        // Category Description Banner
        currentCat?.let { cat ->
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clip(Sh.Sm)
                    .background(Color(0xFF190910))
                    .border(1.dp, Color(0x22F7EFE2), Sh.Sm)
                    .padding(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(cat.icon, fontSize = 24.sp, modifier = Modifier.padding(end = 10.dp))
                    Column(Modifier.weight(1f)) {
                        Text(cat.title, style = T.Name.copy(color = C.warm, fontSize = 14.sp, fontWeight = FontWeight.Bold))
                        Text(cat.description, style = T.Body.copy(color = C.paperMuted, fontSize = 11.sp))
                    }
                    if (cat.id.startsWith("cat_")) {
                        Text(
                            "🗑",
                            modifier = Modifier
                                .padding(end = 10.dp)
                                .clickable {
                                    OurNotebookRepository.deleteCategory(context, cat.id)
                                    selectedCatId = categories.firstOrNull { it.id != cat.id }?.id ?: ""
                                    acts.toast("Libreta eliminada")
                                },
                            fontSize = 16.sp
                        )
                    }
                    WarmButton("+ Nota") { showAddNoteDialog = true }
                }
            }

            // Notes and Todo lists
            LazyColumn(
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                if (cat.notes.isEmpty()) {
                    item {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("📝", fontSize = 40.sp)
                                Spacer(Modifier.height(8.dp))
                                Text("Aún no hay notas en esta libreta", style = T.Muted.copy(color = C.paperMuted))
                                Spacer(Modifier.height(8.dp))
                                GhostButton("Crear la primera nota") { showAddNoteDialog = true }
                            }
                        }
                    }
                }

                items(cat.notes) { note ->
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .clip(Sh.Md)
                            .background(C.card)
                            .border(1.dp, C.line, Sh.Md)
                            .padding(14.dp)
                    ) {
                        Column {
                            // Note Header
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    note.title,
                                    style = T.Name.copy(color = C.warm, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    note.dateStr,
                                    style = T.Eyebrow.copy(color = C.dim, fontSize = 10.sp)
                                )
                            }

                            if (note.content.isNotBlank()) {
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    note.content,
                                    style = T.Body.copy(color = C.ink, fontSize = 13.sp, lineHeight = 19.sp)
                                )
                            }

                            // To-Do Checklist Items
                            if (note.todos.isNotEmpty()) {
                                Spacer(Modifier.height(10.dp))
                                Box(
                                    Modifier
                                        .fillMaxWidth()
                                        .clip(Sh.Sm)
                                        .background(Color(0xFF180A11))
                                        .padding(8.dp)
                                ) {
                                    Column {
                                        note.todos.forEachIndexed { idx, todo ->
                                            Row(
                                                Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 1.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Checkbox(
                                                    checked = todo.isDone,
                                                    onCheckedChange = {
                                                        OurNotebookRepository.toggleTodo(
                                                            context,
                                                            cat.id,
                                                            note.id,
                                                            todo.id
                                                        )
                                                    },
                                                    colors = CheckboxDefaults.colors(
                                                        checkedColor = C.red,
                                                        uncheckedColor = C.paperMuted,
                                                        checkmarkColor = C.warm
                                                    )
                                                )
                                                Text(
                                                    todo.text,
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .clickable {
                                                            OurNotebookRepository.toggleTodo(
                                                                context,
                                                                cat.id,
                                                                note.id,
                                                                todo.id
                                                            )
                                                        },
                                                    style = T.Body.copy(
                                                        color = if (todo.isDone) C.dim else C.ink,
                                                        fontSize = 13.sp,
                                                        textDecoration = if (todo.isDone) TextDecoration.LineThrough else null
                                                    )
                                                )

                                                // Reordenar arriba
                                                if (idx > 0) {
                                                    Text(
                                                        "▲",
                                                        modifier = Modifier
                                                            .padding(horizontal = 4.dp)
                                                            .clickable {
                                                                OurNotebookRepository.moveTodoUp(context, cat.id, note.id, idx)
                                                            },
                                                        fontSize = 11.sp,
                                                        color = Color(0xFFFFD700)
                                                    )
                                                }

                                                // Reordenar abajo
                                                if (idx < note.todos.size - 1) {
                                                    Text(
                                                        "▼",
                                                        modifier = Modifier
                                                            .padding(horizontal = 4.dp)
                                                            .clickable {
                                                                OurNotebookRepository.moveTodoDown(context, cat.id, note.id, idx)
                                                            },
                                                        fontSize = 11.sp,
                                                        color = Color(0xFFFFD700)
                                                    )
                                                }

                                                // Eliminar tarea individual
                                                Text(
                                                    "✕",
                                                    modifier = Modifier
                                                        .padding(start = 6.dp, end = 4.dp)
                                                        .clickable {
                                                            OurNotebookRepository.deleteTodo(context, cat.id, note.id, todo.id)
                                                            acts.toast("Tarea eliminada")
                                                        },
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFFE85D75)
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(Modifier.height(10.dp))

                            // Action buttons: Vaciar lista, Eliminar nota, Agregar tarea
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (note.todos.isNotEmpty()) {
                                        Text(
                                            "🧹 Vaciar lista",
                                            modifier = Modifier
                                                .clickable {
                                                    OurNotebookRepository.clearAllTodos(context, cat.id, note.id)
                                                    acts.toast("Lista vaciada a 0 ✨")
                                                }
                                                .padding(end = 12.dp),
                                            style = T.Eyebrow.copy(color = Color(0xFFFF85A1), fontSize = 11.sp)
                                        )
                                    }

                                    Text(
                                        "🗑 Eliminar",
                                        modifier = Modifier.clickable {
                                            OurNotebookRepository.deleteNote(context, cat.id, note.id)
                                            acts.toast("Nota eliminada")
                                        },
                                        style = T.Eyebrow.copy(color = Color(0xFFB5838D), fontSize = 11.sp)
                                    )
                                }

                                GhostButton("+ Agregar tarea") {
                                    showAddTodoDialog = note
                                }
                            }
                        }
                    }
                }

                item { Spacer(Modifier.height(30.dp)) }
            }
        }
    }

    val paperTextFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color(0xFF1E0A12),
        unfocusedTextColor = Color(0xFF1E0A12),
        cursorColor = Color(0xFFB8142C),
        focusedBorderColor = Color(0xFFB8142C),
        unfocusedBorderColor = Color(0xFF6B2B38),
        focusedLabelColor = Color(0xFFB8142C),
        unfocusedLabelColor = Color(0xFF6B2B38),
        focusedPlaceholderColor = Color(0xFF8B5B68),
        unfocusedPlaceholderColor = Color(0xFF8B5B68),
    )

    // Dialog: Add Note
    if (showAddNoteDialog && currentCat != null) {
        var noteTitle by remember { mutableStateOf("") }
        var noteContent by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddNoteDialog = false },
            title = { Text("Nueva Nota en ${currentCat.title}", style = T.Title.copy(color = Color(0xFF1E0A12), fontSize = 18.sp, fontWeight = FontWeight.Bold)) },
            text = {
                Column {
                    OutlinedTextField(
                        value = noteTitle,
                        onValueChange = { noteTitle = it },
                        label = { Text("Título de la nota") },
                        placeholder = { Text("Ej: Nuestra próxima salida...") },
                        textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF1E0A12), fontSize = 14.sp, fontWeight = FontWeight.Medium),
                        modifier = Modifier.fillMaxWidth(),
                        colors = paperTextFieldColors
                    )
                    Spacer(Modifier.height(10.dp))
                    OutlinedTextField(
                        value = noteContent,
                        onValueChange = { noteContent = it },
                        label = { Text("Contenido de la nota") },
                        placeholder = { Text("Escribe algo lindo para nosotros...") },
                        textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF1E0A12), fontSize = 14.sp, fontWeight = FontWeight.Normal),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp),
                        colors = paperTextFieldColors
                    )
                }
            },
            confirmButton = {
                WarmButton("Guardar") {
                    if (noteTitle.isNotBlank() || noteContent.isNotBlank()) {
                        OurNotebookRepository.addNote(context, currentCat.id, noteTitle, noteContent)
                        acts.toast("¡Nota guardada en Nuestro Cuaderno! ✨")
                    }
                    showAddNoteDialog = false
                }
            },
            dismissButton = {
                GhostButton("Cancelar", dark = false) { showAddNoteDialog = false }
            },
            containerColor = Color(0xFFFFF7EF)
        )
    }

    // Dialog: Add To-Do Item
    showAddTodoDialog?.let { targetNote ->
        var todoText by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddTodoDialog = null },
            title = { Text("Agregar tarea a '${targetNote.title}'", style = T.Title.copy(color = Color(0xFF1E0A12), fontSize = 16.sp, fontWeight = FontWeight.Bold)) },
            text = {
                OutlinedTextField(
                    value = todoText,
                    onValueChange = { todoText = it },
                    label = { Text("Tarea o meta") },
                    placeholder = { Text("Ej: Ver el atardecer en Huacachina...") },
                    textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF1E0A12), fontSize = 14.sp, fontWeight = FontWeight.Medium),
                    modifier = Modifier.fillMaxWidth(),
                    colors = paperTextFieldColors
                )
            },
            confirmButton = {
                WarmButton("Agregar") {
                    if (todoText.isNotBlank() && currentCat != null) {
                        OurNotebookRepository.addTodo(context, currentCat.id, targetNote.id, todoText)
                        acts.toast("¡Tarea agregada! 📋")
                    }
                    showAddTodoDialog = null
                }
            },
            dismissButton = {
                GhostButton("Cancelar", dark = false) { showAddTodoDialog = null }
            },
            containerColor = Color(0xFFFFF7EF)
        )
    }

    // Dialog: Add Category
    if (showAddCategoryDialog) {
        var catTitle by remember { mutableStateOf("") }
        var catIcon by remember { mutableStateOf("📓") }
        var catDesc by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddCategoryDialog = false },
            title = { Text("Crear Nueva Libreta", style = T.Title.copy(color = Color(0xFF1E0A12), fontSize = 18.sp, fontWeight = FontWeight.Bold)) },
            text = {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = catIcon,
                            onValueChange = { catIcon = it.take(2) },
                            label = { Text("Emoji") },
                            textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF1E0A12), fontSize = 18.sp),
                            modifier = Modifier.width(76.dp),
                            colors = paperTextFieldColors
                        )
                        Spacer(Modifier.width(8.dp))
                        OutlinedTextField(
                            value = catTitle,
                            onValueChange = { catTitle = it },
                            label = { Text("Título de la libreta") },
                            placeholder = { Text("Ej: Mis Recuerdos") },
                            textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF1E0A12), fontSize = 14.sp, fontWeight = FontWeight.Medium),
                            modifier = Modifier.weight(1f),
                            colors = paperTextFieldColors
                        )
                    }
                    Spacer(Modifier.height(10.dp))
                    OutlinedTextField(
                        value = catDesc,
                        onValueChange = { catDesc = it },
                        label = { Text("Descripción corta") },
                        placeholder = { Text("¿De qué se trata esta libreta?") },
                        textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFF1E0A12), fontSize = 14.sp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = paperTextFieldColors
                    )
                }
            },
            confirmButton = {
                WarmButton("Crear") {
                    if (catTitle.isNotBlank()) {
                        OurNotebookRepository.addCategory(context, catTitle, catIcon, catDesc)
                        acts.toast("¡Nueva libreta creada! 📓")
                    }
                    showAddCategoryDialog = false
                }
            },
            dismissButton = {
                GhostButton("Cancelar", dark = false) { showAddCategoryDialog = false }
            },
            containerColor = Color(0xFFFFF7EF)
        )
    }
}
