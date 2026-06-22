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
    val currentPhrase by viewModel.currentPhrase.collectAsStateWithLifecycle()

    val pendingTasksNum = tasks.count { !it.isCompleted }
    val urgentTasksNum = tasks.count { !it.isCompleted && it.priority == "Urgente" }

    val todayCalendar = Calendar.getInstance()
    val dayNameEnglish = todayCalendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.US) ?: ""

    // Map English day in database to local day names
    val dayMapping = mapOf(
        "Monday" to "Lunes",
        "Tuesday" to "Martes",
        "Wednesday" to "Miércoles",
        "Thursday" to "Jueves",
        "Friday" to "Viernes",
        "Saturday" to "Sábado",
        "Sunday" to "Domingo"
    )
    val todaySpanishDay = dayMapping[dayNameEnglish] ?: "Lunes"

    // Today's classes filtering
    val todaySubjects = subjects.filter { it.daysOfWeek.contains(todaySpanishDay, ignoreCase = true) }.sortedBy { it.getStartTimeMinutes() }

    val isDark = isSystemInDarkTheme()
    val bentoBorderColor = if (isDark) Color(0xFF3B2F11) else Color(0xFFFEF08A)
    val bentoBgGradient = if (isDark) {
        Brush.linearGradient(colors = listOf(Color(0xFF32280F), Color(0xFF1C1A16)))
    } else {
        Brush.linearGradient(colors = listOf(Color(0xFFFEF9C3), Color(0xFFFEF08A)))
    }

    val todayDateFormatted = SimpleDateFormat("EEEE, d 'de' MMMM", Locale("es", "ES")).format(Date())
    val formattedDate = todayDateFormatted.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale("es", "ES")) else it.toString() }

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
                        text = "Agenda Académica",
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) MaterialTheme.colorScheme.primary else Color(0xFF854D0E)
                        )
                    )
                    Text(
                        text = "Hoy es $formattedDate",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = if (isDark) MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f) else Color(0xFFA16207),
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
                            containerColor = if (isDark) Color(0xFF3E2D1A) else Color(0xFFFEF08A)
                        ),
                        modifier = Modifier
                            .size(44.dp)
                            .testTag("theme_selector_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Palette,
                            contentDescription = "Cambiar Tema",
                            tint = if (isDark) Color(0xFFFEF08A) else Color(0xFF854D0E),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(if (isDark) Color(0xFF3E2D1A) else Color(0xFFFEF08A))
                            .border(BorderStroke(1.5.dp, Color.White), CircleShape)
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CuteTulipCanvas(
                            modifier = Modifier.fillMaxSize(),
                            flowerColor = if (isDark) Color(0xFFFEF08A) else Color(0xFF854D0E)
                        )
                    }
                }
            }
        }

        // Animated Love-Note Box styled beautifully like a bento motivational quote
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = bentoBorderColor,
                        shape = RoundedCornerShape(28.dp)
                    )
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(28.dp),
                        ambientColor = Color(0xFFEAB308),
                        spotColor = Color(0xFFEAB308)
                    )
                    .testTag("love_note_card"),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isDark) MaterialTheme.colorScheme.surface else Color(0xFFFFFFFF).copy(alpha = 0.75f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(
                            text = "“",
                            fontSize = 44.sp,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color(0xFFFEF08A) else Color(0xFFEAB308),
                            modifier = Modifier.height(32.dp).padding(end = 8.dp)
                        )
                        
                        Text(
                            text = currentPhrase,
                            textAlign = TextAlign.Start,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                lineHeight = 22.sp,
                                fontWeight = FontWeight.Medium,
                                fontStyle = FontStyle.Italic,
                                color = if (isDark) MaterialTheme.colorScheme.onSurface else Color(0xFF713F12)
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { viewModel.rotatePhrase() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isDark) MaterialTheme.colorScheme.primary else Color(0xFF854D0E)
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.testTag("rotate_phrase_button")
                    ) {
                        Icon(imageVector = Icons.Filled.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp), tint = if (isDark) Color(0xFF1C1A16) else Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Siguiente frase", color = if (isDark) Color(0xFF1C1A16) else Color.White, style = MaterialTheme.typography.labelLarge)
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
                                .background(if (isDark) Color(0xFF3E2D1A) else Color(0xFFFEF9C3)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Assignment,
                                contentDescription = null,
                                tint = if (isDark) Color(0xFFFEF08A) else Color(0xFF713F12),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "$pendingTasksNum Pendientes",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Tareas por entregar",
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
                                .background(if (isDark) Color(0xFF3E2D1A) else Color(0xFFFEF9C3)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.NotificationsActive,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "${reminders.size} Activos",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Alarmas listas",
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
                text = "Materias de hoy ($todaySpanishDay)",
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
                            text = "No hay materias programadas para hoy",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Aproveche este tiempo para repasar los temas o adelantar tareas pendientes.",
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
                                Text(
                                    text = "Horario: ${subject.startTime} - ${subject.endTime}",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                                )
                                if (!subject.location.isNullOrEmpty()) {
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = "Aula: ${subject.location}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                                    )
                                }
                            }
                            if (!subject.teacherName.isNullOrEmpty()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Profesor: ${subject.teacherName}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                            if (!subject.groupName.isNullOrEmpty()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Grupo: ${subject.groupName}",
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
                    text = "Próximas alarmas activas",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                upcomingReminders.forEach { reminder ->
                    val dateFormatted = SimpleDateFormat("EEEE d 'de' MMMM, HH:mm", Locale("es", "ES")).format(Date(reminder.triggerTime))
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
                                    .background(if (isDark) Color(0xFF3E2D1A) else Color(0xFFFEF9C3)),
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
