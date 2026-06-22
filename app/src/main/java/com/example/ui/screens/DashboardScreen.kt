package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.LocalThemeIsDark
import com.example.ui.theme.L10n
import com.example.ui.theme.LocalLanguage
import com.example.data.Subject
import com.example.data.Task
import com.example.data.Reminder
import com.example.ui.viewmodel.UniversityViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CuteTulipCanvas(modifier: Modifier = Modifier, flowerColor: Color = Color(0xFFF9A825)) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height

        // Stem drawing
        val stemColor = Color(0xFF81C784)
        drawLine(
            color = stemColor,
            start = Offset(width / 2, height * 0.45f),
            end = Offset(width / 2, height * 0.95f),
            strokeWidth = 6f
        )

        // Leaf path
        val leafColor = Color(0xFF9CCC65)
        val leafPath = Path().apply {
            moveTo(width / 2, height * 0.7f)
            quadraticTo(width * 0.25f, height * 0.6f, width * 0.15f, height * 0.45f)
            quadraticTo(width * 0.35f, height * 0.72f, width / 2, height * 0.8f)
        }
        drawPath(leafPath, leafColor)

        // Cute yellow tulip bulb petals
        val middlePetal = Path().apply {
            moveTo(width / 2, height * 0.5f)
            cubicTo(width * 0.25f, height * 0.4f, width * 0.28f, height * 0.12f, width / 2, height * 0.05f)
            cubicTo(width * 0.72f, height * 0.12f, width * 0.75f, height * 0.40f, width / 2, height * 0.5f)
        }
        drawPath(middlePetal, flowerColor)

        // Left petal overlay
        val leftPetal = Path().apply {
            moveTo(width * 0.45f, height * 0.48f)
            cubicTo(width * 0.2f, height * 0.35f, width * 0.22f, height * 0.15f, width * 0.42f, height * 0.1f)
            cubicTo(width * 0.5f, height * 0.22f, width * 0.52f, height * 0.38f, width * 0.45f, height * 0.48f)
        }
        drawPath(leftPetal, flowerColor.copy(red = flowerColor.red * 0.92f, green = flowerColor.green * 0.92f))

        // Right petal overlay
        val rightPetal = Path().apply {
            moveTo(width * 0.55f, height * 0.48f)
            cubicTo(width * 0.8f, height * 0.35f, width * 0.78f, height * 0.15f, width * 0.58f, height * 0.1f)
            cubicTo(width * 0.5f, height * 0.22f, width * 0.48f, height * 0.38f, width * 0.55f, height * 0.48f)
        }
        drawPath(rightPetal, flowerColor.copy(red = flowerColor.red * 0.88f, green = flowerColor.green * 0.88f))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: UniversityViewModel,
    onNavigateToCalendar: () -> Unit,
    onNavigateToTasks: () -> Unit,
    onOpenThemeSelector: () -> Unit,
    modifier: Modifier = Modifier
) {
    val subjects by viewModel.subjects.collectAsStateWithLifecycle()
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    val reminders by viewModel.reminders.collectAsStateWithLifecycle()

    val lang = com.example.ui.theme.LocalLanguage.current

    val pendingTasksNum = tasks.count { !it.isCompleted }
    val urgentTasksNum = tasks.count { !it.isCompleted && it.priority == "Urgente" }

    val todayCalendar = Calendar.getInstance()
    val dayNameEnglish = todayCalendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.US) ?: ""

    // Map English day in database to local day names for database queries (using Spanish as DB key or localized match)
    val dayMappingES = mapOf(
        "Monday" to "Lunes", "Tuesday" to "Martes", "Wednesday" to "Miércoles", "Thursday" to "Jueves",
        "Friday" to "Viernes", "Saturday" to "Sábado", "Sunday" to "Domingo"
    )
    val dayMappingEN = mapOf(
        "Monday" to "Monday", "Tuesday" to "Tuesday", "Wednesday" to "Wednesday", "Thursday" to "Thursday",
        "Friday" to "Friday", "Saturday" to "Saturday", "Sunday" to "Sunday"
    )
    val dayMappingPT = mapOf(
        "Monday" to "Segunda-feira", "Tuesday" to "Terça-feira", "Wednesday" to "Quarta-feira", "Thursday" to "Quinta-feira",
        "Friday" to "Sexta-feira", "Saturday" to "Sábado", "Sunday" to "Domingo"
    )

    val todaySpanishDay = dayMappingES[dayNameEnglish] ?: "Lunes"
    val todayLocalizedDay = when(lang) {
        1 -> dayMappingEN[dayNameEnglish] ?: "Monday"
        2 -> dayMappingPT[dayNameEnglish] ?: "Segunda-feira"
        else -> dayMappingES[dayNameEnglish] ?: "Lunes"
    }

    // Today's classes filtering (subjects keep Spanish representation in standard fields, but matches todaySpanishDay)
    val todaySubjects = subjects.filter { it.daysOfWeek.contains(todaySpanishDay, ignoreCase = true) }.sortedBy { it.getStartTimeMinutes() }

    val isDark = LocalThemeIsDark.current
    val bentoBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
    val bentoBgGradient = if (isDark) {
        Brush.linearGradient(colors = listOf(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f), MaterialTheme.colorScheme.surface))
    } else {
        Brush.linearGradient(colors = listOf(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f), MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)))
    }

    val dateLocale = when(lang) {
        1 -> Locale.US
        2 -> Locale("pt", "PT")
        else -> Locale("es", "ES")
    }
    
    val todayDateFormatted = if (lang == 1) {
        SimpleDateFormat("EEEE, MMMM d", dateLocale).format(Date())
    } else if (lang == 2) {
        SimpleDateFormat("EEEE, d 'de' MMMM", dateLocale).format(Date())
    } else {
        SimpleDateFormat("EEEE, d 'de' MMMM", dateLocale).format(Date())
    }

    val formattedDate = todayDateFormatted.replaceFirstChar { if (it.isLowerCase()) it.titlecase(dateLocale) else it.toString() }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Aesthetic Top Header Area
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Tulipán",
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Text(
                        text = if (lang == 1) "Today is $formattedDate" else if (lang == 2) "Hoje é $formattedDate" else "Hoy es $formattedDate",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                            fontWeight = FontWeight.Medium
                        )
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = onOpenThemeSelector,
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        ),
                        modifier = Modifier
                            .size(44.dp)
                            .testTag("theme_selector_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Palette,
                            contentDescription = L10n.getString("language", lang),
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                            .border(BorderStroke(1.5.dp, Color.White), CircleShape)
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CuteTulipCanvas(
                            modifier = Modifier.fillMaxSize(),
                            flowerColor = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        // Daily Quick Stats Summary Card - Bento Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToTasks() }
                        .border(
                            width = 1.dp,
                            color = bentoBorderColor,
                            shape = RoundedCornerShape(28.dp)
                        )
                        .testTag("tasks_summary_card"),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDark) MaterialTheme.colorScheme.surface else Color.White
                    ),
                    shape = RoundedCornerShape(28.dp),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = if (isDark) 0.3f else 0.7f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Assignment,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "$pendingTasksNum " + L10n.getString("pending", lang),
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (lang == 1) "Incomplete tasks" else if (lang == 2) "Tarefas a entregar" else "Tareas por entregar",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }

                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToCalendar() }
                        .border(
                            width = 1.dp,
                            color = bentoBorderColor,
                            shape = RoundedCornerShape(28.dp)
                        )
                        .testTag("reminders_summary_card"),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDark) MaterialTheme.colorScheme.surface else Color.White
                    ),
                    shape = RoundedCornerShape(28.dp),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = if (isDark) 0.3f else 0.7f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.NotificationsActive,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "${reminders.size} " + L10n.getString("active", lang),
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (lang == 1) "Alarms configured" else if (lang == 2) "Alarmes agendados" else "Alarmas listas",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }

        // Today's classes visual timeline overview
        item {
            Text(
                text = (if (lang == 1) "Today's classes" else if (lang == 2) "Aulas de hoje" else "Materias de hoy") + " ($todayLocalizedDay)",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        if (todaySubjects.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = bentoBorderColor,
                            shape = RoundedCornerShape(28.dp)
                        ),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = if (isDark) MaterialTheme.colorScheme.surface.copy(alpha = 0.6f) else Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Filled.School,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (lang == 1) "No classes scheduled for today" else if (lang == 2) "Não há aulas agendadas para hoje" else "No hay materias programadas para hoy",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (lang == 1) "Use this time to review subjects or catch up on pending tasks." else if (lang == 2) "Aproveite este tempo para estudar ou colocar tarefas em dia." else "Aproveche este tiempo para repasar los temas o adelantar tareas.",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        } else {
            items(todaySubjects) { subject ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.5.dp,
                            color = Color(android.graphics.Color.parseColor(subject.colorHex)).copy(alpha = 0.6f),
                            shape = RoundedCornerShape(28.dp)
                        ),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(android.graphics.Color.parseColor(subject.colorHex)).copy(alpha = 0.12f)
                    ),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(Color(android.graphics.Color.parseColor(subject.colorHex)))
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = subject.name,
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                val schLabel = if (lang == 1) "Schedule" else if (lang == 2) "Horário" else "Horario"
                                Text(
                                    text = "$schLabel: ${subject.startTime} - ${subject.endTime}",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                                )
                                if (!subject.location.isNullOrEmpty()) {
                                    Spacer(modifier = Modifier.width(12.dp))
                                    val roomLabel = if (lang == 1) "Room" else if (lang == 2) "Sala" else "Aula"
                                    Text(
                                        text = "$roomLabel: ${subject.location}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                                    )
                                }
                            }
                            if (!subject.teacherName.isNullOrEmpty()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                val profLabel = if (lang == 1) "Professor" else if (lang == 2) "Professor" else "Profesor"
                                Text(
                                    text = "$profLabel: ${subject.teacherName}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                            if (!subject.groupName.isNullOrEmpty()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                val grpLabel = if (lang == 1) "Group" else if (lang == 2) "Grupo" else "Grupo"
                                Text(
                                    text = "$grpLabel: ${subject.groupName}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Today's Upcoming Event Reminder
        item {
            val upcomingReminders = reminders.filter { it.triggerTime > System.currentTimeMillis() }.take(2)
            if (upcomingReminders.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (lang == 1) "Upcoming active alarms" else if (lang == 2) "Próximos alarmes ativos" else "Próximas alarmas activas",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                upcomingReminders.forEach { reminder ->
                    val dateFormattedFormatter = if (lang == 1) SimpleDateFormat("EEEE, MMMM d, HH:mm", dateLocale) else SimpleDateFormat("EEEE d 'de' MMMM, HH:mm", dateLocale)
                    val dateFormatted = dateFormattedFormatter.format(Date(reminder.triggerTime))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .border(
                                width = 1.dp,
                                color = bentoBorderColor,
                                shape = RoundedCornerShape(24.dp)
                            ),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isDark) MaterialTheme.colorScheme.surface else Color.White
                        ),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = if (isDark) 0.3f else 0.7f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.NotificationsActive,
                                    contentDescription = null,
                                    tint = if (reminder.priority == "Alta") Color.Red else MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = reminder.title,
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = dateFormatted,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
