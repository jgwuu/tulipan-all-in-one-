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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.Reminder
import com.example.ui.viewmodel.UniversityViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemindersScreen(
    viewModel: UniversityViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val reminders by viewModel.reminders.collectAsStateWithLifecycle()

    var showAddDialog by remember { mutableStateOf(false) }

    // Form states
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("Media") } // "Baja", "Media", "Alta"
    var repeatType by remember { mutableStateOf("None") } // "None", "Daily", "Weekly"
    var preAlertMinutes by remember { mutableStateOf(0) } // 0, 5, 15, 30

    // Manual custom offset trigger state (Defaults to tomorrow at 9 AM)
    var selectedCalendar by remember {
        mutableStateOf(Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 1)
            set(Calendar.HOUR_OF_DAY, 9)
            set(Calendar.MINUTE, 0)
        })
    }

    var manualDateInput by remember {
        mutableStateOf(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(selectedCalendar.time))
    }
    var manualTimeInput by remember {
        mutableStateOf(SimpleDateFormat("HH:mm", Locale.getDefault()).format(selectedCalendar.time))
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Upper action bar intro 
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
                        imageVector = Icons.Filled.NotificationsActive,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Recordatorios",
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
                    modifier = Modifier.testTag("add_reminder_btn")
                ) {
                    Icon(imageVector = Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Nuevo")
                }
            }
        }

        // Checklist alarms structure
        if (reminders.isEmpty()) {
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
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Filled.NotificationsActive,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "No hay alertas registradas",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Programa recordatorios y alarmas de estudio para recibir notificaciones del sistema.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(reminders, key = { it.id }) { reminder ->
                val dateStr = SimpleDateFormat("EEEE dd 'de' MMMM - HH:mm", Locale("es", "ES")).format(Date(reminder.triggerTime))
                val isExpired = reminder.triggerTime < System.currentTimeMillis()

                val isDark = androidx.compose.foundation.isSystemInDarkTheme()
                val bentoBorderColor = if (isDark) Color(0xFF3B2F11) else Color(0xFFFEF08A)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = bentoBorderColor,
                            shape = RoundedCornerShape(24.dp)
                        )
                        .testTag("reminder_item_${reminder.id}"),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isExpired) {
                            if (isDark) MaterialTheme.colorScheme.surface.copy(alpha = 0.4f) else Color(0xFFFEF9C3).copy(alpha = 0.15f)
                        } else {
                            if (isDark) MaterialTheme.colorScheme.surface else Color.White
                        }
                    ),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (isExpired) Icons.Filled.NotificationsNone else Icons.Filled.NotificationsActive,
                                    contentDescription = null,
                                    tint = if (isExpired) Color.Gray else MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = reminder.title,
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                    color = if (isExpired) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Alerta: $dateStr",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                            if (reminder.preAlertMinutes > 0) {
                                Text(
                                    text = "Pre-Alerta: ${reminder.preAlertMinutes} minutos antes",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            if (reminder.repeatIntervalType != "None") {
                                Box(
                                    modifier = Modifier
                                        .padding(top = 4.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(MaterialTheme.colorScheme.primaryContainer)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "Repetición: " + when (reminder.repeatIntervalType) {
                                            "Daily" -> "Diaria"
                                            "Weekly" -> "Semanal"
                                            else -> "Simple"
                                        },
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            if (!reminder.description.isNullOrEmpty()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = reminder.description,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        }

                        IconButton(
                            onClick = { viewModel.deleteReminder(reminder, context) },
                            modifier = Modifier.testTag("delete_reminder_${reminder.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.DeleteOutline,
                                contentDescription = "Eliminar recordatorio",
                                tint = MaterialTheme.colorScheme.tertiary
                            )
                        }
                    }
                }
            }
        }
    }

    // Modal view to add reminders
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (title.isNotBlank()) {
                            try {
                                val dateParts = manualDateInput.split("/")
                                val timeParts = manualTimeInput.split(":")

                                val targetCal = Calendar.getInstance().apply {
                                    set(Calendar.DAY_OF_MONTH, dateParts[0].toInt())
                                    set(Calendar.MONTH, dateParts[1].toInt() - 1)
                                    set(Calendar.YEAR, dateParts[2].toInt())
                                    set(Calendar.HOUR_OF_DAY, timeParts[0].toInt())
                                    set(Calendar.MINUTE, timeParts[1].toInt())
                                    set(Calendar.SECOND, 0)
                                }

                                viewModel.addReminder(
                                    title = title,
                                    description = description.takeIf { it.isNotBlank() },
                                    triggerTimeInMillis = targetCal.timeInMillis,
                                    priority = priority,
                                    repeatIntervalType = repeatType,
                                    preAlertMinutes = preAlertMinutes,
                                    context = context
                                )

                                // Clear forms
                                title = ""
                                description = ""
                                priority = "Media"
                                repeatType = "None"
                                preAlertMinutes = 0
                                showAddDialog = false
                            } catch (e: Exception) {
                                // Ignore format exceptions gracefully
                            }
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
                    Icon(imageVector = Icons.Filled.NotificationsActive, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Agregar Recordatorio")
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
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Título del recordatorio *") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("reminder_input_title"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Detalle u Notas (opcional)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Priority options
                    Text(
                        text = "Prioridad de alerta",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        listOf("Baja", "Media", "Alta").forEach { prio ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable { priority = prio }
                            ) {
                                RadioButton(selected = priority == prio, onClick = { priority = prio })
                                Text(prio, fontSize = 13.sp)
                            }
                        }
                    }

                    // Precise Date and Time inputs picker selections
                    Text(
                        text = "Programación de fecha y hora *",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )

                    val dPicker = android.app.DatePickerDialog(
                        context,
                        { _, year, month, dayOfMonth ->
                            selectedCalendar.set(Calendar.YEAR, year)
                            selectedCalendar.set(Calendar.MONTH, month)
                            selectedCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                            manualDateInput = String.format("%02d/%02d/%d", dayOfMonth, month + 1, year)
                        },
                        selectedCalendar.get(Calendar.YEAR),
                        selectedCalendar.get(Calendar.MONTH),
                        selectedCalendar.get(Calendar.DAY_OF_MONTH)
                    )

                    val tPicker = android.app.TimePickerDialog(
                        context,
                        { _, hourOfDay, minute ->
                            selectedCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                            selectedCalendar.set(Calendar.MINUTE, minute)
                            manualTimeInput = String.format("%02d:%02d", hourOfDay, minute)
                        },
                        selectedCalendar.get(Calendar.HOUR_OF_DAY),
                        selectedCalendar.get(Calendar.MINUTE),
                        true
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { dPicker.show() }
                        ) {
                            OutlinedTextField(
                                value = manualDateInput,
                                onValueChange = {},
                                label = { Text("Fecha") },
                                readOnly = true,
                                enabled = false,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = TextFieldDefaults.colors(
                                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                    disabledLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                    disabledIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                                    disabledContainerColor = Color.Transparent
                                )
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { tPicker.show() }
                        ) {
                            OutlinedTextField(
                                value = manualTimeInput,
                                onValueChange = {},
                                label = { Text("Hora") },
                                readOnly = true,
                                enabled = false,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = TextFieldDefaults.colors(
                                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                    disabledLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                    disabledIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                                    disabledContainerColor = Color.Transparent
                                )
                            )
                        }
                    }

                    // Preset alerts options prior to the event (Pre-Alert minutes)
                    Text(
                        text = "Anticipación de Alerta (Pre-aviso)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val offsets = listOf(0 to "A tiempo", 5 to "5m antes", 15 to "15m antes", 30 to "30m antes")
                        offsets.forEach { (minutes, label) ->
                            Row(
                                modifier = Modifier.clickable { preAlertMinutes = minutes },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(selected = preAlertMinutes == minutes, onClick = { preAlertMinutes = minutes })
                                Text(label, fontSize = 11.sp, maxLines = 1)
                            }
                        }
                    }

                    // Alarms repeat intervals
                    Text(
                        text = "Intervalo de repetición",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val repeats = listOf("None" to "Solo una vez", "Daily" to "Diario", "Weekly" to "Semanal")
                        repeats.forEach { (type, label) ->
                            Row(
                                modifier = Modifier.clickable { repeatType = type },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(selected = repeatType == type, onClick = { repeatType = type })
                                Text(label, fontSize = 11.sp)
                            }
                        }
                    }
                }
            },
            shape = RoundedCornerShape(24.dp)
        )
    }
}
