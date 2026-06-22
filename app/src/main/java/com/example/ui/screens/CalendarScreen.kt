package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.data.Reminder
import com.example.data.Subject
import com.example.data.Task
import com.example.ui.viewmodel.UniversityViewModel
import com.example.ui.theme.LocalThemeIsDark
import com.example.ui.theme.TulipYellowPrimary
import com.example.ui.theme.L10n
import com.example.ui.theme.LocalLanguage
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.basicMarquee
import java.text.SimpleDateFormat
import java.util.*
import android.app.TimePickerDialog
import androidx.compose.ui.platform.LocalContext

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class, androidx.compose.foundation.layout.ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    viewModel: UniversityViewModel,
    modifier: Modifier = Modifier
) {
    val lang = LocalLanguage.current
    val locale = when(lang) {
        1 -> Locale("en", "US")
        2 -> Locale("pt", "BR")
        else -> Locale("es", "ES")
    }

    val weekdayMap = mapOf(
        "Lunes" to listOf("Lunes", "Monday", "Segunda-feira"),
        "Martes" to listOf("Martes", "Tuesday", "Terça-feira"),
        "Miércoles" to listOf("Miércoles", "Wednesday", "Quarta-feira"),
        "Jueves" to listOf("Jueves", "Thursday", "Quinta-feira"),
        "Viernes" to listOf("Viernes", "Friday", "Sexta-feira"),
        "Sábado" to listOf("Sábado", "Saturday", "Sábado"),
        "Domingo" to listOf("Domingo", "Sunday", "Domingo")
    )
    fun getDayLabel(day: String): String = weekdayMap[day]?.get(lang) ?: day

    val subjects by viewModel.subjects.collectAsStateWithLifecycle()
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    val reminders by viewModel.reminders.collectAsStateWithLifecycle()
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()

    val context = androidx.compose.ui.platform.LocalContext.current
    var editingSubject by remember { mutableStateOf<Subject?>(null) }
    var editingTask by remember { mutableStateOf<Task?>(null) }

    var currentMonthCalendar by remember { mutableStateOf(Calendar.getInstance()) }

    val sdfMonthYear = SimpleDateFormat("MMMM yyyy", locale)
    val displayedMonthName = sdfMonthYear.format(currentMonthCalendar.time)
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

    // Calendar generation helpers
    val year = currentMonthCalendar.get(Calendar.YEAR)
    val month = currentMonthCalendar.get(Calendar.MONTH) // 0-11

    // Determine first day of week. Set calendar to first day of month.
    val calculationCalendar = Calendar.getInstance().apply {
        set(Calendar.YEAR, year)
        set(Calendar.MONTH, month)
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    // day of week for 1st day of month (Sunday = 1, Monday = 2...)
    val firstDayOfWeek = calculationCalendar.get(Calendar.DAY_OF_WEEK)
    // Convert to European layout (Monday=0, Tuesday=1 ... Sunday=6)
    val startOffset = if (firstDayOfWeek == Calendar.SUNDAY) 6 else firstDayOfWeek - 2

    val maxDays = calculationCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)

    // Build the grid list items - representation of Days in month
    val daysList = remember(year, month) {
        val list = mutableListOf<Calendar?>()
        // Padding days
        for (i in 0 until startOffset) {
            list.add(null)
        }
        // Month days
        for (day in 1..maxDays) {
            val dayCal = Calendar.getInstance().apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month)
                set(Calendar.DAY_OF_MONTH, day)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            list.add(dayCal)
        }
        list
    }

    // Selected Day events computation helper
    val selectedDayCalendar = Calendar.getInstance().apply {
        timeInMillis = selectedDate
    }

    val selectedYear = selectedDayCalendar.get(Calendar.YEAR)
    val selectedMonth = selectedDayCalendar.get(Calendar.MONTH)
    val selectedDayOfMonth = selectedDayCalendar.get(Calendar.DAY_OF_MONTH)

    fun isSameDay(time1: Long, cal2: Calendar): Boolean {
        val cal1 = Calendar.getInstance().apply { timeInMillis = time1 }
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)
    }

    // selected day name in Spanish for database queries
    val databaseDayOfWeekName = selectedDayCalendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale("es", "ES")) ?: "Lunes"
    val displayDayOfWeekName = selectedDayCalendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, locale) ?: ""
    val formattedSelectedDateHeader = when(lang) {
        1 -> SimpleDateFormat("MMMM dd, yyyy", locale).format(selectedDayCalendar.time)
        2 -> SimpleDateFormat("dd 'de' MMMM 'de' yyyy", locale).format(selectedDayCalendar.time)
        else -> SimpleDateFormat("dd 'de' MMMM, yyyy", locale).format(selectedDayCalendar.time)
    }

    // Filter events for the exact selected date
    val dayTasks = tasks.filter { isSameDay(it.deadlineDate, selectedDayCalendar) }
    val dayReminders = reminders.filter { isSameDay(it.triggerTime, selectedDayCalendar) }

    // Subject frequency lookup for selected weekday name
    val daySubjects = subjects.filter {
        it.daysOfWeek.contains(databaseDayOfWeekName, ignoreCase = true)
    }.sortedBy { it.getStartTimeMinutes() }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App Title
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.CalendarMonth,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = L10n.getString("tab_calendario", lang),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                )
            }
        }

        // Custom Calendar Controller Header
        item {
            val isDark = LocalThemeIsDark.current
            val bentoBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = bentoBorderColor,
                        shape = RoundedCornerShape(28.dp)
                    ),
                colors = CardDefaults.cardColors(containerColor = if (isDark) MaterialTheme.colorScheme.surface else Color.White),
                shape = RoundedCornerShape(28.dp),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    // Month controller bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(
                            onClick = {
                                val prev = (currentMonthCalendar.clone() as Calendar).apply {
                                    add(Calendar.MONTH, -1)
                                }
                                currentMonthCalendar = prev
                            },
                            modifier = Modifier.testTag("prev_month_btn")
                        ) {
                            Icon(imageVector = Icons.Filled.ChevronLeft, contentDescription = "Mes anterior", tint = MaterialTheme.colorScheme.primary)
                        }

                        Text(
                            text = displayedMonthName,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.testTag("month_label")
                        )

                        IconButton(
                            onClick = {
                                val next = (currentMonthCalendar.clone() as Calendar).apply {
                                    add(Calendar.MONTH, 1)
                                }
                                currentMonthCalendar = next
                            },
                            modifier = Modifier.testTag("next_month_btn")
                        ) {
                            Icon(imageVector = Icons.Filled.ChevronRight, contentDescription = "Siguiente mes", tint = MaterialTheme.colorScheme.primary)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Days labels (Lunes - Domingo)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        val localDaysShort = when(lang) {
                            1 -> listOf("Mo", "Tu", "We", "Th", "Fr", "Sa", "Su")
                            2 -> listOf("Se", "Te", "Qu", "Qi", "Se", "Sa", "Do")
                            else -> listOf("Lu", "Ma", "Mi", "Ju", "Vi", "Sá", "Do")
                        }
                        localDaysShort.forEach { dayLabel ->
                            Text(
                                text = dayLabel,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Grid Days display using simple column rows
                    val rowsCount = (daysList.size + 6) / 7
                    for (rowIdx in 0 until rowsCount) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            for (colIdx in 0 until 7) {
                                val itemIdx = rowIdx * 7 + colIdx
                                if (itemIdx < daysList.size) {
                                    val dateCal = daysList[itemIdx]
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .heightIn(min = 106.dp)
                                            .padding(1.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(
                                                when {
                                                    dateCal == null -> Color.Transparent
                                                    dateCal.get(Calendar.YEAR) == selectedYear &&
                                                            dateCal.get(Calendar.MONTH) == selectedMonth &&
                                                            dateCal.get(Calendar.DAY_OF_MONTH) == selectedDayOfMonth ->
                                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.85f)
                                                    else -> Color.Transparent
                                                }
                                            )
                                            .clickable(enabled = dateCal != null) {
                                                dateCal?.let {
                                                    viewModel.selectDate(it.timeInMillis)
                                                }
                                            }
                                            .padding(horizontal = 2.dp, vertical = 2.dp),
                                        contentAlignment = Alignment.TopCenter
                                    ) {
                                        if (dateCal != null) {
                                            val dayNum = dateCal.get(Calendar.DAY_OF_MONTH).toString()
                                            val isSelected = dateCal.get(Calendar.YEAR) == selectedYear &&
                                                    dateCal.get(Calendar.MONTH) == selectedMonth &&
                                                    dateCal.get(Calendar.DAY_OF_MONTH) == selectedDayOfMonth
 
                                            val dayName = dateCal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale("es", "ES")) ?: ""
                                            val dayClasses = subjects.filter { it.daysOfWeek.contains(dayName, ignoreCase = true) }.sortedBy { it.getStartTimeMinutes() }
 
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.spacedBy(4.dp),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Text(
                                                    text = dayNum,
                                                    style = MaterialTheme.typography.bodyMedium.copy(
                                                        fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
                                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                                        fontSize = 12.sp
                                                    ),
                                                    modifier = Modifier.padding(top = 2.dp)
                                                )
 
                                                // List little colored strips for subjects showing both Name and Location/Salon
                                                dayClasses.take(2).forEach { subject ->
                                                    Column(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .clip(RoundedCornerShape(6.dp))
                                                            .background(Color(android.graphics.Color.parseColor(subject.colorHex)).copy(alpha = if (isSelected) 1f else 0.95f))
                                                            .padding(horizontal = 4.dp, vertical = 3.dp),
                                                        horizontalAlignment = Alignment.CenterHorizontally,
                                                        verticalArrangement = Arrangement.Center
                                                    ) {
                                                        Text(
                                                            text = subject.name,
                                                            fontSize = 9.sp,
                                                            fontWeight = FontWeight.ExtraBold,
                                                            color = Color.Black,
                                                            maxLines = 1,
                                                            textAlign = TextAlign.Center,
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .basicMarquee()
                                                        )
                                                        if (!subject.location.isNullOrBlank()) {
                                                            Text(
                                                                text = subject.location,
                                                                fontSize = 8.sp,
                                                                fontWeight = FontWeight.Bold,
                                                                color = Color.Black.copy(alpha = 0.85f),
                                                                maxLines = 1,
                                                                textAlign = TextAlign.Center,
                                                                modifier = Modifier
                                                                    .fillMaxWidth()
                                                                    .basicMarquee()
                                                            )
                                                        }
                                                    }
                                                }
 
                                                // Task/Reminder indicator dots under classes
                                                val hasTasks = tasks.any { !it.isCompleted && isSameDay(it.deadlineDate, dateCal) }
                                                val hasRems = reminders.any { isSameDay(it.triggerTime, dateCal) }
                                                if (hasTasks || hasRems) {
                                                    Row(
                                                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        if (hasTasks) {
                                                            Box(modifier = Modifier.size(3.1.dp).clip(CircleShape).background(if (isSelected) Color.White else Color(0xFFF9A825)))
                                                        }
                                                        if (hasRems) {
                                                            Box(modifier = Modifier.size(3.1.dp).clip(CircleShape).background(if (isSelected) Color.White else MaterialTheme.colorScheme.tertiary))
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }
        }

        // Selected Date Agenda Detail Box Header
        item {
            Text(
                text = "${if (lang == 1) "Agenda for" else if (lang == 2) "Agenda de" else "Agenda del"} $displayDayOfWeekName, $formattedSelectedDateHeader 🌸",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(vertical = 4.dp).testTag("calendar_selected_header")
            )
        }

        // Empty state of lists
        if (daySubjects.isEmpty() && dayTasks.isEmpty() && dayReminders.isEmpty()) {
            item {
                val isDark = LocalThemeIsDark.current
                val bentoBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = bentoBorderColor,
                            shape = RoundedCornerShape(24.dp)
                        ),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = if (isDark) MaterialTheme.colorScheme.surface else Color(0xFFFEF9C3).copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (lang == 1) "No scheduled items" else if (lang == 2) "Sem compromissos agendados" else "Sin pendientes programados",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // Subjects Section
        if (daySubjects.isNotEmpty()) {
            item {
                Text(
                    text = if (lang == 1) "Classes and Subjects" else if (lang == 2) "Aulas e Matérias" else "Clases y Materias",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                )
            }
            items(daySubjects) { subject ->
                val classColor = Color(android.graphics.Color.parseColor(subject.colorHex))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Colored accent bar at the left side of the card
                        Box(
                            modifier = Modifier
                                .width(8.dp)
                                .height(100.dp) // Substantial beautiful accent height block
                                .clip(RoundedCornerShape(topStart = 20.dp, bottomStart = 20.dp))
                                .background(classColor)
                        )

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = subject.name,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 18.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Time Badge
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AccessTime,
                                        contentDescription = "Horario",
                                        tint = classColor,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${subject.startTime} - ${subject.endTime}",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                                        )
                                    )
                                }

                                // Salon / Aula Badge with gorgeous legibility and adaptive outline border
                                if (!subject.location.isNullOrBlank()) {
                                    val isDark = LocalThemeIsDark.current
                                    val badgeContentColor = if (isDark) Color.White else Color(0xFF422006)
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(classColor.copy(alpha = if (isDark) 0.2f else 0.25f))
                                            .border(1.dp, classColor.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Place,
                                                contentDescription = "Salón",
                                                tint = badgeContentColor,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = subject.location,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = badgeContentColor
                                            )
                                        }
                                    }
                                }
                            }

                            // Teacher name if present
                            if (!subject.teacherName.isNullOrEmpty()) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(top = 2.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = "Docente",
                                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Prof. ${subject.teacherName}",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                            fontWeight = FontWeight.Medium
                                        )
                                    )
                                }
                            }

                            // Group name if present
                            if (!subject.groupName.isNullOrEmpty()) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(top = 2.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AccountBox,
                                        contentDescription = "Grupo",
                                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Grupo: ${subject.groupName}",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                            fontWeight = FontWeight.Medium
                                        )
                                    )
                                }
                            }
                        }

                        // Edit and Delete buttons on the right-end side of the class card
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(end = 12.dp)
                        ) {
                            IconButton(
                                onClick = { editingSubject = subject },
                                modifier = Modifier.size(36.dp).testTag("cal_edit_subject_${subject.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = if (lang == 1) "Edit subject" else if (lang == 2) "Editar matéria" else "Editar clase",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            IconButton(
                                onClick = { viewModel.deleteSubject(subject) },
                                modifier = Modifier.size(36.dp).testTag("cal_delete_subject_${subject.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DeleteOutline,
                                    contentDescription = if (lang == 1) "Delete subject" else if (lang == 2) "Excluir matéria" else "Eliminar clase",
                                    tint = MaterialTheme.colorScheme.tertiary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Day Tasks Section
        if (dayTasks.isNotEmpty()) {
            item {
                Text(
                    text = if (lang == 1) "Tasks to submit 📚" else if (lang == 2) "Tarefas para entregar 📚" else "Tareas por entregar 📚",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                )
            }
            items(dayTasks) { task ->
                val associatedSubject = subjects.find { it.id == task.subjectId }
                val isDark = LocalThemeIsDark.current
                val bentoBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = bentoBorderColor,
                            shape = RoundedCornerShape(24.dp)
                        ),
                    colors = CardDefaults.cardColors(containerColor = if (isDark) MaterialTheme.colorScheme.surface else Color.White),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (task.isCompleted) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked,
                                    contentDescription = null,
                                    tint = if (task.isCompleted) Color(0xFF81C784) else Color.Gray,
                                    modifier = Modifier.clickable {
                                        viewModel.toggleTaskStatus(task, !task.isCompleted)
                                    }
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = task.title,
                                    textDecoration = if (task.isCompleted) androidx.compose.ui.text.style.TextDecoration.LineThrough else null,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }

                            // Priority Label Tag
                            val badgeColor = when (task.priority) {
                                "Urgente" -> Color(0xFFFF8A80)
                                "Normal" -> Color(0xFFFFD54F)
                                else -> Color(0xFFB3E5FC)
                            }
                            val translatedPriority = when (task.priority) {
                                "Urgente" -> if (lang == 1) "Urgent" else "Urgente"
                                "Normal" -> "Normal"
                                else -> if (lang == 1) "Low" else if (lang == 2) "Baixa" else "Baja"
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(badgeColor.copy(alpha = 0.2f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = translatedPriority,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (task.priority == "Urgente") Color.Red else Color.DarkGray
                                )
                            }
                        }

                        if (!task.description.isNullOrEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = task.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                            )
                        }

                        // Bottom row containing associatedSubject AND task action buttons
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (associatedSubject != null) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(android.graphics.Color.parseColor(associatedSubject.colorHex)).copy(alpha = 0.25f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = associatedSubject.name,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            } else {
                                Spacer(modifier = Modifier.width(1.dp))
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { editingTask = task },
                                    modifier = Modifier.size(36.dp).testTag("cal_edit_task_${task.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = if (lang == 1) "Edit task" else if (lang == 2) "Editar tarefa" else "Editar tarea",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                IconButton(
                                    onClick = { viewModel.deleteTask(task) },
                                    modifier = Modifier.size(36.dp).testTag("cal_delete_task_${task.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DeleteOutline,
                                        contentDescription = if (lang == 1) "Delete task" else if (lang == 2) "Excluir tarefa" else "Eliminar tarea",
                                        tint = MaterialTheme.colorScheme.tertiary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Day Reminders Section
        if (dayReminders.isNotEmpty()) {
            item {
                Text(
                    text = "Compromisos y recordatorios",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                )
            }
            items(dayReminders) { reminder ->
                val timeStr = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(reminder.triggerTime))
                val isDark = LocalThemeIsDark.current
                val bentoBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = bentoBorderColor,
                            shape = RoundedCornerShape(24.dp)
                        ),
                    colors = CardDefaults.cardColors(containerColor = if (isDark) MaterialTheme.colorScheme.surface else Color.White),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.NotificationsActive,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.tertiary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = reminder.title,
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                            Text(
                                text = timeStr,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                        }
                        if (!reminder.description.isNullOrEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = reminder.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
        }
    }

    // Modal dialog to edit subject
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

        val daysOfWeekOptions = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")
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
                    modifier = Modifier.testTag("cal_edit_subject_dialog_confirm")
                ) {
                    Text(L10n.getString("save", lang), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
                }
            },
            dismissButton = {
                TextButton(onClick = { editingSubject = null }) {
                    Text(L10n.getString("cancel", lang), color = Color.Gray)
                }
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Filled.Edit, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(L10n.getString("edit_subject", lang) + " 📚")
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
                        label = { Text(L10n.getString("subject_name", lang) + " *") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("cal_edit_subject_input_name"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = editTeacherName,
                        onValueChange = { editTeacherName = it },
                        label = { Text(L10n.getString("professor", lang) + (if (lang == 1) " (optional)" else " (opcional)")) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = editLocation,
                        onValueChange = { editLocation = it },
                        label = { Text(L10n.getString("classroom", lang) + (if (lang == 1) " (optional)" else " (opcional)")) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = editGroupName,
                        onValueChange = { editGroupName = it },
                        label = { Text(L10n.getString("study_group", lang)) },
                        modifier = Modifier.fillMaxWidth().testTag("cal_edit_subject_input_group"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Hours selector row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        InteractiveTimeField(
                            label = L10n.getString("start_time", lang),
                            value = editStartTime,
                            onValueChange = { editStartTime = it },
                            modifier = Modifier.weight(1f)
                        )
                        InteractiveTimeField(
                            label = L10n.getString("end_time", lang),
                            value = editEndTime,
                            onValueChange = { editEndTime = it },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Choose Days flow checkbox selector
                    Text(
                        text = L10n.getString("selected_days", lang) + " *",
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
                                label = { Text(getDayLabel(dayName)) },
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                    }

                    // Select aesthetic color hex code swatches
                    Text(
                        text = if (lang == 1) "Identification Color 🎨" else if (lang == 2) "Cor de identificação 🎨" else "Color de identificación 🎨",
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
                    modifier = Modifier.testTag("cal_edit_task_confirm")
                ) {
                    Text(L10n.getString("save", lang), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
                }
            },
            dismissButton = {
                TextButton(onClick = { editingTask = null }) {
                    Text(L10n.getString("cancel", lang), color = Color.Gray)
                }
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Filled.Edit, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (lang == 1) "Edit Task 📚" else if (lang == 2) "Editar Tarefa 📚" else "Modificar Tarea 📚")
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
                        label = { Text(if (lang == 1) "Task Title *" else if (lang == 2) "Título da tarefa *" else "Título de la tarea *") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("cal_edit_task_input_title"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = editDesc,
                        onValueChange = { editDesc = it },
                        label = { Text(if (lang == 1) "Instructions / Notes (optional)" else if (lang == 2) "Instruções / Notas (opcional)" else "Instrucciones / Notas (opcional)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        maxLines = 3
                    )

                    // Priority levels RadioButtons
                    Text(
                        text = if (lang == 1) "Submission Priority" else if (lang == 2) "Prioridade de entrega" else "Prioridad de entrega",
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

                            val translatedPrioLabel = when (prio) {
                                "Urgente" -> if (lang == 1) "Urgent" else "Urgente"
                                "Normal" -> "Normal"
                                else -> if (lang == 1) "Low" else if (lang == 2) "Secundária" else "Secundaria"
                            }

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
                                    text = translatedPrioLabel,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) prioTextColor else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }

                    // Links to subject optionally
                    Text(
                        text = if (lang == 1) "Link to a Subject (optional)" else if (lang == 2) "Vincular a uma Matéria (opcional)" else "Vincular a una Materia (opcional)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )

                    if (subjects.isEmpty()) {
                        Text(
                            text = if (lang == 1) "You don't have subjects created yet to link." else if (lang == 2) "Você ainda não tem matérias criadas para vincular." else "No tienes materias creadas aún para vincular.",
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
                                label = { Text(if (lang == 1) "None" else if (lang == 2) "Nenhuma" else "Ninguna") }
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

                    // Date Selection selection
                    Text(
                        text = if (lang == 1) "Due Date" else if (lang == 2) "Data de entrega" else "Fecha de Entrega",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )

                    val activeFormattedDeadline = SimpleDateFormat("EEEE dd/MM/yyyy", locale).format(Date(editDeadlineMillis))
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
                }
            },
            shape = RoundedCornerShape(24.dp)
        )
    }
}
