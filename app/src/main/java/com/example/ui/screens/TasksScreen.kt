package com.example.ui.screens

import android.content.Context
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.Subject
import com.example.data.Task
import com.example.ui.viewmodel.UniversityViewModel
import androidx.compose.foundation.isSystemInDarkTheme
import com.example.ui.theme.LocalThemeIsDark
import com.example.ui.theme.TulipYellowPrimary
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(
    viewModel: UniversityViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val subjects by viewModel.subjects.collectAsStateWithLifecycle()
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()

    var showAddTaskDialog by remember { mutableStateOf(false) }
    var editingTask by remember { mutableStateOf<Task?>(null) }

    // Filter type states
    var selectedFilter by remember { mutableStateOf("Pendientes") } // "Pendientes", "Completadas", "Urgentes", "Todas"

    // Form states
    var taskTitle by remember { mutableStateOf("") }
    var taskDesc by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("Normal") } // "Urgente", "Normal", "Secundaria"
    var selectedSubjectId by remember { mutableStateOf<Int?>(null) }
    var selectedDeadlineMillis by remember { mutableStateOf(System.currentTimeMillis()) }

    val filterOptions = listOf("Pendientes", "Completadas", "Urgentes", "Todas")

    // Dynamic list filtering
    val filteredTasks = tasks.filter { task ->
        when (selectedFilter) {
            "Pendientes" -> !task.isCompleted
            "Completadas" -> task.isCompleted
            "Urgentes" -> !task.isCompleted && task.priority == "Urgente"
            else -> true
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App bar intro
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Assignment,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Mis Pendientes 📚",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Button(
                    onClick = { showAddTaskDialog = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.testTag("add_task_btn")
                ) {
                    Icon(imageVector = Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Nueva")
                }
            }
        }

        // Horizontal filter chips
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filterOptions.forEach { filter ->
                    val isSelected = selectedFilter == filter
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedFilter = filter },
                        label = { Text(filter) },
                        modifier = Modifier.testTag("filter_chip_$filter"),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            labelColor = MaterialTheme.colorScheme.onSurface
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
        }

        // Checklist body
        if (filteredTasks.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "No se encontraron tareas",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "No hay tareas registradas que coincidan con el filtro seleccionado.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(filteredTasks, key = { it.id }) { task ->
                val associatedSubject = subjects.find { it.id == task.subjectId }
                val dateStr = SimpleDateFormat("dd 'de' MMMM, yyyy", Locale("es", "ES")).format(Date(task.deadlineDate))

                val isDark = LocalThemeIsDark.current
                val bentoBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = bentoBorderColor,
                            shape = RoundedCornerShape(24.dp)
                        )
                        .testTag("task_item_${task.id}"),
                    colors = CardDefaults.cardColors(
                        containerColor = if (task.isCompleted) {
                            if (isDark) MaterialTheme.colorScheme.surface.copy(alpha = 0.4f) else Color(0xFFFEF9C3).copy(alpha = 0.15f)
                        } else {
                            if (isDark) MaterialTheme.colorScheme.surface else Color.White
                        }
                    ),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (task.isCompleted) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked,
                                    contentDescription = "Cambiar estado",
                                    tint = if (task.isCompleted) Color(0xFF81C784) else Color.Gray,
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clickable {
                                            viewModel.toggleTaskStatus(task, !task.isCompleted)
                                        }
                                        .testTag("task_toggle_${task.id}")
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(
                                        text = task.title,
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            textDecoration = if (task.isCompleted) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
                                        ),
                                        color = if (task.isCompleted) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "📅 Entrega: $dateStr",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // Priority flag with beautiful high-contrast Material 3 colors
                                val isDark = LocalThemeIsDark.current
                                val badgeBg = when (task.priority) {
                                    "Urgente" -> MaterialTheme.colorScheme.errorContainer
                                    "Normal" -> MaterialTheme.colorScheme.primaryContainer
                                    else -> MaterialTheme.colorScheme.secondaryContainer
                                }
                                val badgeText = when (task.priority) {
                                    "Urgente" -> MaterialTheme.colorScheme.onErrorContainer
                                    "Normal" -> MaterialTheme.colorScheme.onPrimaryContainer
                                    else -> MaterialTheme.colorScheme.onSecondaryContainer
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(badgeBg)
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = task.priority,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = badgeText
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                IconButton(
                                    onClick = { editingTask = task },
                                    modifier = Modifier.testTag("task_edit_${task.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Edit,
                                        contentDescription = "Editar tarea",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(4.dp))

                                IconButton(
                                    onClick = { viewModel.deleteTask(task) },
                                    modifier = Modifier.testTag("task_delete_${task.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.DeleteOutline,
                                        contentDescription = "Eliminar tarea",
                                        tint = MaterialTheme.colorScheme.tertiary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }

                        // Task descriptions
                        if (!task.description.isNullOrEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = task.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                                modifier = Modifier.padding(start = 36.dp)
                            )
                        }

                        // Association metadata
                        if (associatedSubject != null) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(modifier = Modifier.padding(start = 36.dp)) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(
                                            Color(android.graphics.Color.parseColor(associatedSubject.colorHex)).copy(
                                                alpha = 0.2f
                                            )
                                        )
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = associatedSubject.name,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(android.graphics.Color.parseColor(associatedSubject.colorHex))
                                    )
                                }
                            }
                        } else {
                            // Fast shortcut to link a subject
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(modifier = Modifier.padding(start = 36.dp)) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .border(
                                            width = 1.dp,
                                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                        .clickable { editingTask = task }
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Adjuntar materia",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal dialog to add task
    if (showAddTaskDialog) {
        AlertDialog(
            onDismissRequest = { showAddTaskDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (taskTitle.isNotBlank()) {
                            viewModel.addTask(
                                title = taskTitle,
                                description = taskDesc.takeIf { it.isNotBlank() },
                                deadlineDate = selectedDeadlineMillis,
                                priority = priority,
                                subjectId = selectedSubjectId,
                                context = context
                            )
                            // Clear form
                            taskTitle = ""
                            taskDesc = ""
                            priority = "Normal"
                            selectedSubjectId = null
                            selectedDeadlineMillis = System.currentTimeMillis()
                            showAddTaskDialog = false
                        }
                    },
                    modifier = Modifier.testTag("dialog_confirm_btn")
                ) {
                    Text("Guardar", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddTaskDialog = false }) {
                    Text("Cancelar", color = Color.Gray)
                }
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Filled.Assignment, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Nueva Tarea / Pendiente 📚")
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 380.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = taskTitle,
                        onValueChange = { taskTitle = it },
                        label = { Text("Título de la tarea *") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("task_input_title"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = taskDesc,
                        onValueChange = { taskDesc = it },
                        label = { Text("Instrucciones / Notas (opcional)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        maxLines = 3
                    )

                    // Priority levels RadioButtons
                    Text(
                        text = "Prioridad de entrega",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )

                    val prioritiesList = listOf("Secundaria", "Normal", "Urgente")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        prioritiesList.forEach { prio ->
                            val isSelected = priority == prio
                            val prioColor = when (prio) {
                                "Urgente" -> MaterialTheme.colorScheme.errorContainer
                                "Normal" -> MaterialTheme.colorScheme.primaryContainer
                                else -> MaterialTheme.colorScheme.secondaryContainer
                            }
                            val prioTextColor = when (prio) {
                                "Urgente" -> MaterialTheme.colorScheme.onErrorContainer
                                "Normal" -> MaterialTheme.colorScheme.onPrimaryContainer
                                else -> MaterialTheme.colorScheme.onSecondaryContainer
                            }
                            val borderAccent = if (isSelected) TulipYellowPrimary else Color.Transparent

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(prioColor.copy(alpha = if (isSelected) 1f else 0.4f))
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) borderAccent else MaterialTheme.colorScheme.outlineVariant,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable { priority = prio }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = prio,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) prioTextColor else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }

                    // Links to subject optionally
                    Text(
                        text = "Vincular a una Materia (opcional)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )

                    if (subjects.isEmpty()) {
                        Text(
                            text = "No tienes materias creadas aún para vincular.",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    } else {
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            // "None" option
                            FilterChip(
                                selected = selectedSubjectId == null,
                                onClick = { selectedSubjectId = null },
                                label = { Text("Ninguna") }
                            )

                            subjects.forEach { subject ->
                                val isSelected = selectedSubjectId == subject.id
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { selectedSubjectId = subject.id },
                                    label = { Text(subject.name) },
                                    leadingIcon = if (isSelected) {
                                        { Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                                    } else null,
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Color(android.graphics.Color.parseColor(subject.colorHex)),
                                        selectedLabelColor = Color.Black,
                                        containerColor = Color(android.graphics.Color.parseColor(subject.colorHex)).copy(alpha = 0.15f),
                                        labelColor = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                            }
                        }
                    }

                    // Due Date calculation preset helper
                    Text(
                        text = "Fecha de Entrega",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )

                    val activeFormattedDeadline = SimpleDateFormat("EEEE dd/MM/yyyy", Locale("es", "ES")).format(Date(selectedDeadlineMillis))
                    val calendar = Calendar.getInstance().apply { timeInMillis = selectedDeadlineMillis }
                    val datePickerDialog = android.app.DatePickerDialog(
                        context,
                        { _, year, month, dayOfMonth ->
                            val newCal = Calendar.getInstance().apply {
                                set(Calendar.YEAR, year)
                                set(Calendar.MONTH, month)
                                set(Calendar.DAY_OF_MONTH, dayOfMonth)
                                set(Calendar.HOUR_OF_DAY, 23)
                                set(Calendar.MINUTE, 59)
                            }
                            selectedDeadlineMillis = newCal.timeInMillis
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
                            .clickable { datePickerDialog.show() }
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "📅 $activeFormattedDeadline (Toca para cambiar)",
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Quick buttons for presets!
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        val presets = listOf(
                            "Hoy" to 0,
                            "Mañana" to 1,
                            "En 3 días" to 3,
                            "En 1 semana" to 7
                        )
                        presets.forEach { (label, daysOffset) ->
                            Button(
                                onClick = {
                                    val cal = Calendar.getInstance().apply {
                                        add(Calendar.DAY_OF_YEAR, daysOffset)
                                        set(Calendar.HOUR_OF_DAY, 23)
                                        set(Calendar.MINUTE, 59)
                                    }
                                    selectedDeadlineMillis = cal.timeInMillis
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onSecondary
                                ),
                                contentPadding = PaddingValues(horizontal = 8.dp),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            },
            shape = RoundedCornerShape(24.dp)
        )
    }

    // Modal dialog to edit task
    editingTask?.let { taskToEdit ->
        var editTitle by remember(taskToEdit) { mutableStateOf(taskToEdit.title) }
        var editDesc by remember(taskToEdit) { mutableStateOf(taskToEdit.description ?: "") }
        var editPriority by remember(taskToEdit) { mutableStateOf(taskToEdit.priority) }
        var editSubjectId by remember(taskToEdit) { mutableStateOf(taskToEdit.subjectId) }
        var editDeadlineMillis by remember(taskToEdit) { mutableStateOf(taskToEdit.deadlineDate) }

        AlertDialog(
            onDismissRequest = { editingTask = null },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (editTitle.isNotBlank()) {
                            viewModel.updateTask(
                                id = taskToEdit.id,
                                title = editTitle,
                                description = editDesc.takeIf { it.isNotBlank() },
                                deadlineDate = editDeadlineMillis,
                                priority = editPriority,
                                subjectId = editSubjectId,
                                isCompleted = taskToEdit.isCompleted,
                                context = context
                            )
                            editingTask = null
                        }
                    },
                    modifier = Modifier.testTag("edit_dialog_confirm_btn")
                ) {
                    Text("Guardar", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
                }
            },
            dismissButton = {
                TextButton(onClick = { editingTask = null }) {
                    Text("Cancelar", color = Color.Gray)
                }
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Filled.Edit, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Modificar Tarea 📚")
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 380.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = editTitle,
                        onValueChange = { editTitle = it },
                        label = { Text("Título de la tarea *") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("edit_task_input_title"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = editDesc,
                        onValueChange = { editDesc = it },
                        label = { Text("Instrucciones / Notas (opcional)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        maxLines = 3
                    )

                    // Priority levels RadioButtons
                    Text(
                        text = "Prioridad de entrega",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )

                    val prioritiesList = listOf("Secundaria", "Normal", "Urgente")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        prioritiesList.forEach { prio ->
                            val isSelected = editPriority == prio
                            val prioColor = when (prio) {
                                "Urgente" -> MaterialTheme.colorScheme.errorContainer
                                "Normal" -> MaterialTheme.colorScheme.primaryContainer
                                else -> MaterialTheme.colorScheme.secondaryContainer
                            }
                            val prioTextColor = when (prio) {
                                "Urgente" -> MaterialTheme.colorScheme.onErrorContainer
                                "Normal" -> MaterialTheme.colorScheme.onPrimaryContainer
                                else -> MaterialTheme.colorScheme.onSecondaryContainer
                            }
                            val borderAccent = if (isSelected) TulipYellowPrimary else Color.Transparent

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(prioColor.copy(alpha = if (isSelected) 1f else 0.4f))
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) borderAccent else MaterialTheme.colorScheme.outlineVariant,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable { editPriority = prio }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = prio,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) prioTextColor else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }

                    // Links to subject optionally
                    Text(
                        text = "Vincular a una Materia (opcional)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )

                    if (subjects.isEmpty()) {
                        Text(
                            text = "No tienes materias creadas aún para vincular.",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    } else {
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            // "None" option
                            FilterChip(
                                selected = editSubjectId == null,
                                onClick = { editSubjectId = null },
                                label = { Text("Ninguna") }
                            )

                            subjects.forEach { subject ->
                                val isSelected = editSubjectId == subject.id
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { editSubjectId = subject.id },
                                    label = { Text(subject.name) },
                                    leadingIcon = if (isSelected) {
                                        { Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                                    } else null,
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Color(android.graphics.Color.parseColor(subject.colorHex)),
                                        selectedLabelColor = Color.Black,
                                        containerColor = Color(android.graphics.Color.parseColor(subject.colorHex)).copy(alpha = 0.15f),
                                        labelColor = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                            }
                        }
                    }

                    // Due Date calculation preset helper
                    Text(
                        text = "Fecha de Entrega",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )

                    val activeFormattedDeadline = SimpleDateFormat("EEEE dd/MM/yyyy", Locale("es", "ES")).format(Date(editDeadlineMillis))
                    val calendar = Calendar.getInstance().apply { timeInMillis = editDeadlineMillis }
                    val datePickerDialog = android.app.DatePickerDialog(
                        context,
                        { _, year, month, dayOfMonth ->
                            val newCal = Calendar.getInstance().apply {
                                set(Calendar.YEAR, year)
                                set(Calendar.MONTH, month)
                                set(Calendar.DAY_OF_MONTH, dayOfMonth)
                                set(Calendar.HOUR_OF_DAY, 23)
                                set(Calendar.MINUTE, 59)
                            }
                            editDeadlineMillis = newCal.timeInMillis
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
                            .clickable { datePickerDialog.show() }
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "📅 $activeFormattedDeadline (Toca para cambiar)",
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Quick buttons for presets!
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        val presets = listOf(
                            "Hoy" to 0,
                            "Mañana" to 1,
                            "En 3 días" to 3,
                            "En 1 semana" to 7
                        )
                        presets.forEach { (label, daysOffset) ->
                            Button(
                                onClick = {
                                    val cal = Calendar.getInstance().apply {
                                        add(Calendar.DAY_OF_YEAR, daysOffset)
                                        set(Calendar.HOUR_OF_DAY, 23)
                                        set(Calendar.MINUTE, 59)
                                    }
                                    editDeadlineMillis = cal.timeInMillis
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onSecondary
                                ),
                                contentPadding = PaddingValues(horizontal = 8.dp),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            },
            shape = RoundedCornerShape(24.dp)
        )
    }
}
