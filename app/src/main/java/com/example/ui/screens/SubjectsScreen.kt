package com.example.ui.screens

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.Subject
import com.example.ui.viewmodel.UniversityViewModel
import android.app.TimePickerDialog
import androidx.compose.ui.platform.LocalContext
import java.util.Calendar

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SubjectsScreen(
    viewModel: UniversityViewModel,
    modifier: Modifier = Modifier
) {
    val subjects by viewModel.subjects.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }
    var editingSubject by remember { mutableStateOf<Subject?>(null) }

    // Form states
    var name by remember { mutableStateOf("") }
    var teacherName by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var groupName by remember { mutableStateOf("") }
    var startTime by remember { mutableStateOf("08:00 AM") }
    var endTime by remember { mutableStateOf("10:00 AM") }

    val daysOfWeekOptions = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")
    val selectedDays = remember { mutableStateListOf<String>() }

    // Swatches color listing
    val pastelSwatches = listOf(
        "#FFF59D", // Soft Yellow
        "#FFCC80", // Warm Peach
        "#F8BBD0", // Sweet Rose
        "#C8E6C9", // Fresh Mint
        "#B3E5FC", // Dreamy Blue
        "#E1BEE7", // Soft Lavender
        "#B2F7EF", // Aero Mint Blue
        "#F7D6C8", // Warm Peach/Clay
        "#F2C6DE", // Rose Orchid/Pink
        "#D8E2DC", // Sage Milk Green
        "#FFE5EC"  // Blush Cotton Candy
    )
    var selectedColorHex by remember { mutableStateOf(pastelSwatches.first()) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Upper Intro info
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
                        imageVector = Icons.Filled.School,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Materias",
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
                    onClick = { showAddDialog = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.testTag("add_subject_button")
                ) {
                    Icon(imageVector = Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Nueva")
                }
            }
        }

        // List subjects grouped by Day
        if (subjects.isEmpty()) {
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
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Filled.School,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Aún no tienes materias agregadas",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Agrega tus materias para organizar tu horario universitario y notificaciones.",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        } else {
            // Group and represent elements
            val daysOfTheWeek = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")
            daysOfTheWeek.forEach { weekday ->
                val dayClasses = subjects.filter { it.daysOfWeek.contains(weekday, ignoreCase = true) }.sortedBy { it.getStartTimeMinutes() }
                if (dayClasses.isNotEmpty()) {
                    item {
                        Text(
                            text = weekday,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                            ),
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    items(dayClasses) { subject ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    width = 1.5.dp,
                                    color = Color(android.graphics.Color.parseColor(subject.colorHex)).copy(alpha = 0.6f),
                                    shape = RoundedCornerShape(24.dp)
                                )
                                .testTag("subject_card_${subject.id}"),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(android.graphics.Color.parseColor(subject.colorHex)).copy(alpha = 0.12f)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clip(CircleShape)
                                        .background(Color(android.graphics.Color.parseColor(subject.colorHex)))
                                )

                                Spacer(modifier = Modifier.width(16.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = subject.name,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Horario: ${subject.startTime} - ${subject.endTime}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                                    )
                                    if (!subject.location.isNullOrEmpty()) {
                                        Text(
                                            text = "Aula: ${subject.location}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                        )
                                    }
                                    if (!subject.teacherName.isNullOrEmpty()) {
                                        Text(
                                            text = "Profesor: ${subject.teacherName}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                        )
                                    }
                                    if (!subject.groupName.isNullOrEmpty()) {
                                        Text(
                                            text = "Grupo: ${subject.groupName}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                        )
                                    }
                                }

                                IconButton(
                                    onClick = { editingSubject = subject },
                                    modifier = Modifier.testTag("edit_subject_${subject.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Edit,
                                        contentDescription = "Editar clase",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }

                                Spacer(modifier = Modifier.width(4.dp))

                                IconButton(
                                    onClick = { viewModel.deleteSubject(subject) },
                                    modifier = Modifier.testTag("delete_subject_${subject.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.DeleteOutline,
                                        contentDescription = "Borrar clase",
                                        tint = MaterialTheme.colorScheme.tertiary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal view for adding subject
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (name.isNotBlank() && selectedDays.isNotEmpty()) {
                            viewModel.addSubject(
                                name = name,
                                days = selectedDays.toList(),
                                startTime = startTime,
                                endTime = endTime,
                                location = location.takeIf { it.isNotBlank() },
                                colorHex = selectedColorHex,
                                teacherName = teacherName.takeIf { it.isNotBlank() },
                                groupName = groupName.takeIf { it.isNotBlank() }
                            )
                            // Clear form
                            name = ""
                            teacherName = ""
                            location = ""
                            groupName = ""
                            startTime = "08:00 AM"
                            endTime = "10:00 AM"
                            selectedDays.clear()
                            showAddDialog = false
                        }
                    },
                    modifier = Modifier.testTag("dialog_confirm_btn")
                ) {
                    Text("Guardar", fontWeight = FontWeight.Bold, color = Color.Black)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancelar", color = Color.Gray)
                }
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Filled.School, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Agregar Nueva Materia 📚")
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
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nombre de la materia *") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("subject_input_name"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = teacherName,
                        onValueChange = { teacherName = it },
                        label = { Text("Profesor (opcional)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text("Aula u Ubicación (opcional)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = groupName,
                        onValueChange = { groupName = it },
                        label = { Text("Grupo o Sección (opcional)") },
                        modifier = Modifier.fillMaxWidth().testTag("subject_input_group"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Hours selector row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        InteractiveTimeField(
                            label = "Inicio",
                            value = startTime,
                            onValueChange = { startTime = it },
                            modifier = Modifier.weight(1f)
                        )
                        InteractiveTimeField(
                            label = "Fin",
                            value = endTime,
                            onValueChange = { endTime = it },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Choose Days flow checkbox selector
                    Text(
                        text = "Días de la semana *",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        daysOfWeekOptions.forEach { dayName ->
                            val isSelected = selectedDays.contains(dayName)
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    if (isSelected) selectedDays.remove(dayName) else selectedDays.add(dayName)
                                },
                                label = { Text(dayName) },
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                    }

                    // Select aesthetic color hex code swatches
                    Text(
                        text = "Color de identificación 🎨",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        pastelSwatches.forEach { colorString ->
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(android.graphics.Color.parseColor(colorString)))
                                    .border(
                                        width = if (selectedColorHex == colorString) 2.5.dp else 0.dp,
                                        color = if (selectedColorHex == colorString) MaterialTheme.colorScheme.primary else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickable { selectedColorHex = colorString }
                            )
                        }
                    }
                }
            },
            shape = RoundedCornerShape(24.dp)
        )
    }

    // Modal view for editing subject
    editingSubject?.let { subjectToEdit ->
        var editName by remember(subjectToEdit) { mutableStateOf(subjectToEdit.name) }
        var editTeacherName by remember(subjectToEdit) { mutableStateOf(subjectToEdit.teacherName ?: "") }
        var editLocation by remember(subjectToEdit) { mutableStateOf(subjectToEdit.location ?: "") }
        var editGroupName by remember(subjectToEdit) { mutableStateOf(subjectToEdit.groupName ?: "") }
        var editStartTime by remember(subjectToEdit) { mutableStateOf(subjectToEdit.startTime) }
        var editEndTime by remember(subjectToEdit) { mutableStateOf(subjectToEdit.endTime) }
        val editSelectedDays = remember(subjectToEdit) {
            val list = mutableStateListOf<String>()
            list.addAll(subjectToEdit.daysOfWeek.split(","))
            list
        }
        var editColorHex by remember(subjectToEdit) { mutableStateOf(subjectToEdit.colorHex) }

        AlertDialog(
            onDismissRequest = { editingSubject = null },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (editName.isNotBlank() && editSelectedDays.isNotEmpty()) {
                            viewModel.updateSubject(
                                id = subjectToEdit.id,
                                name = editName,
                                days = editSelectedDays.toList(),
                                startTime = editStartTime,
                                endTime = editEndTime,
                                location = editLocation.takeIf { it.isNotBlank() },
                                colorHex = editColorHex,
                                teacherName = editTeacherName.takeIf { it.isNotBlank() },
                                groupName = editGroupName.takeIf { it.isNotBlank() }
                            )
                            editingSubject = null
                        }
                    },
                    modifier = Modifier.testTag("edit_dialog_confirm_btn")
                ) {
                    Text("Guardar", fontWeight = FontWeight.Bold, color = Color.Black)
                }
            },
            dismissButton = {
                TextButton(onClick = { editingSubject = null }) {
                    Text("Cancelar", color = Color.Gray)
                }
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Filled.Edit, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Modificar Materia 📚")
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
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Nombre de la materia *") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("edit_subject_input_name"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = editTeacherName,
                        onValueChange = { editTeacherName = it },
                        label = { Text("Profesor (opcional)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = editLocation,
                        onValueChange = { editLocation = it },
                        label = { Text("Aula u Ubicación (opcional)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = editGroupName,
                        onValueChange = { editGroupName = it },
                        label = { Text("Grupo o Sección (opcional)") },
                        modifier = Modifier.fillMaxWidth().testTag("edit_subject_input_group"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Hours selector row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        InteractiveTimeField(
                            label = "Inicio",
                            value = editStartTime,
                            onValueChange = { editStartTime = it },
                            modifier = Modifier.weight(1f)
                        )
                        InteractiveTimeField(
                            label = "Fin",
                            value = editEndTime,
                            onValueChange = { editEndTime = it },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Choose Days flow checkbox selector
                    Text(
                        text = "Días de la semana *",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        daysOfWeekOptions.forEach { dayName ->
                            val isSelected = editSelectedDays.contains(dayName)
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    if (isSelected) editSelectedDays.remove(dayName) else editSelectedDays.add(dayName)
                                },
                                label = { Text(dayName) },
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                    }

                    // Select aesthetic color hex code swatches
                    Text(
                        text = "Color de identificación 🎨",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        pastelSwatches.forEach { colorString ->
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(android.graphics.Color.parseColor(colorString)))
                                    .border(
                                        width = if (editColorHex == colorString) 2.5.dp else 0.dp,
                                        color = if (editColorHex == colorString) MaterialTheme.colorScheme.primary else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickable { editColorHex = colorString }
                            )
                        }
                    }
                }
            },
            shape = RoundedCornerShape(24.dp)
        )
    }
}

@Composable
fun InteractiveTimeField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    var currentHour = 8
    var currentMinute = 0

    if (value.isNotBlank()) {
        try {
            val isPM = value.uppercase().contains("PM")
            val isAM = value.uppercase().contains("AM")
            val cleanValue = value.replace("AM", "").replace("PM", "").replace("am", "").replace("pm", "").trim()
            val parts = cleanValue.split(":")
            if (parts.size >= 2) {
                var hr = parts[0].trim().toIntOrNull() ?: 8
                val min = parts[1].trim().toIntOrNull() ?: 0
                if (isPM && hr < 12) hr += 12
                if (isAM && hr == 12) hr = 0
                currentHour = hr
                currentMinute = min
            }
        } catch (e: Exception) {
            // keep standard defaults
        }
    }

    val timePickerDialog = remember(value) {
        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                val suffix = if (hourOfDay >= 12) "PM" else "AM"
                val hour12 = when {
                    hourOfDay == 0 -> 12
                    hourOfDay > 12 -> hourOfDay - 12
                    else -> hourOfDay
                }
                val formattedTime = String.format("%02d:%02d %s", hour12, minute, suffix)
                onValueChange(formattedTime)
            },
            currentHour,
            currentMinute,
            false // 12-hour mode with AM/PM selector!
        )
    }

    Box(
        modifier = modifier
            .clickable { timePickerDialog.show() }
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            label = { Text(label) },
            readOnly = true,
            enabled = false, // Touch intercepted by parent Box
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledBorderColor = MaterialTheme.colorScheme.outline,
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Reloj",
                    modifier = Modifier.size(18.dp)
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
    }
}
