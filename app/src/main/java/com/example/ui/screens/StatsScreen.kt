package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.LocalThemeIsDark
import com.example.ui.theme.LocalLanguage
import com.example.ui.theme.L10n
import com.example.ui.viewmodel.UniversityViewModel

@Composable
fun BloomingtonFlowerCanvas(
    modifier: Modifier = Modifier,
    completionRatio: Float // value from 0.0f to 1.0f
) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height

        // Stem
        val stemColor = Color(0xFF81C784)
        drawLine(
            color = stemColor,
            start = Offset(width / 2, height * 0.5f),
            end = Offset(width / 2, height * 0.95f),
            strokeWidth = 8f
        )

        // Leaf
        val leafPath = Path().apply {
            moveTo(width / 2, height * 0.75f)
            quadraticTo(width * 0.18f, height * 0.65f, width * 0.12f, height * 0.52f)
            quadraticTo(width * 0.35f, height * 0.78f, width / 2, height * 0.85f)
        }
        drawPath(leafPath, stemColor)

        // Flower center & petals scale based on task completion
        val flowerScale = 0.25f + (completionRatio * 0.75f) // flower is at least a small bud of 0.25
        val flowerCenterY = height * 0.46f

        val petalColor = Color(0xFFFBC02D) // Golden Yellow
        val centerColor = Color(0xFF422201) // Deep Sunflower center

        // Draw Petals if blooming
        if (flowerScale > 0.3f) {
            val petalRadius = width * 0.2f * flowerScale
            val petalCount = 8
            for (i in 0 until petalCount) {
                val angle = (2 * Math.PI / petalCount) * i
                val px = width / 2 + (Math.cos(angle) * petalRadius * 0.8f).toFloat()
                val py = flowerCenterY + (Math.sin(angle) * petalRadius * 0.8f).toFloat()

                drawCircle(
                    color = petalColor,
                    radius = petalRadius * 0.65f,
                    center = Offset(px, py)
                )
            }
        } else {
            // Unopened cute green/yellow simple bud
            drawCircle(
                color = Color(0xFFC8E6C9),
                radius = 20f,
                center = Offset(width / 2, flowerCenterY)
            )
        }

        // Draw central core
        drawCircle(
            color = centerColor,
            radius = (width * 0.1f * flowerScale).coerceAtLeast(10f),
            center = Offset(width / 2, flowerCenterY)
        )
    }
}

@Composable
fun StatsScreen(
    viewModel: UniversityViewModel,
    modifier: Modifier = Modifier
) {
    val lang = LocalLanguage.current
    val subjects by viewModel.subjects.collectAsStateWithLifecycle()
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    val reminders by viewModel.reminders.collectAsStateWithLifecycle()

    val totalSubjects = subjects.size
    val totalTasks = tasks.size
    val completedTasksNum = tasks.count { it.isCompleted }
    val pendingTasksNum = totalTasks - completedTasksNum

    val completionRatio = if (totalTasks > 0) completedTasksNum.toFloat() / totalTasks else 0.0f
    val displayPercent = (completionRatio * 100).toInt()

    val isDark = LocalThemeIsDark.current
    val bentoBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App intro header
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = null,
                    tint = if (isDark) MaterialTheme.colorScheme.primary else Color(0xFF6B7280),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (lang == 1) "Progress Overview" else if (lang == 2) "Visão Geral do Progresso" else "Resumen de Progreso",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) MaterialTheme.colorScheme.onBackground else Color(0xFF1F2937)
                    )
                )
            }
        }

        // Blooming flower Canvas Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = bentoBorderColor,
                        shape = RoundedCornerShape(28.dp)
                    )
                    .testTag("flower_blooming_card"),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = if (isDark) MaterialTheme.colorScheme.surface else Color.White),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (lang == 1) "Flower of Consistency" else if (lang == 2) "Flor da Constância" else "Flor de la Constancia",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (lang == 1) "Visualization of task completion" else if (lang == 2) "Visualização do cumprimento de tarefas" else "Visualización del cumplimiento de tareas",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    BloomingtonFlowerCanvas(
                        modifier = Modifier
                            .size(160.dp)
                            .background(Color.Transparent),
                        completionRatio = completionRatio
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "$displayPercent% " + (if (lang == 1) "of tasks completed" else if (lang == 2) "das tarefas concluídas" else "de tareas completadas"),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = if (isDark) MaterialTheme.colorScheme.primary else Color(0xFF1F2937)
                    )

                    val flowerMsg = when {
                        totalTasks == 0 -> if (lang == 1) "Register tasks to start visualizing your progress." else if (lang == 2) "Registre tarefas para começar a visualizar seu progresso." else "Registre tareas para comenzar a visualizar su progreso."
                        completionRatio == 1.0f -> if (lang == 1) "Full compliance. All registered tasks have been completed." else if (lang == 2) "Cumprimento total. Todas as tarefas registradas foram concluídas." else "Cumplimiento completo. Todas las tareas registradas han sido finalizadas."
                        completionRatio >= 0.7f -> if (lang == 1) "High progress. Most tasks are complete." else if (lang == 2) "Progresso alto. A maioria das tarefas estão concluídas." else "Progreso alto. La mayoría de las tareas están completas."
                        completionRatio >= 0.4f -> if (lang == 1) "Medium progress. Regular advancement is recorded." else if (lang == 2) "Progresso médio. Um avanço regular é registrado." else "Progreso medio. Se registra un avance regular."
                        else -> if (lang == 1) "Initial progress. Continue completing pending tasks." else if (lang == 2) "Progresso inicial. Continue concluindo as tarefas pendentes." else "Progreso inicial. Continúe completando las tareas pendientes."
                    }

                    Text(
                        text = flowerMsg,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        modifier = Modifier.padding(top = 6.dp, start = 8.dp, end = 8.dp)
                    )
                }
            }
        }

        // Summary Stats Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .border(
                            width = 1.dp,
                            color = bentoBorderColor,
                            shape = RoundedCornerShape(24.dp)
                        )
                        .testTag("stat_card_subjects"),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = if (isDark) MaterialTheme.colorScheme.surface else Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(imageVector = Icons.Filled.School, contentDescription = null, tint = if (isDark) MaterialTheme.colorScheme.primary else Color(0xFF854D0E))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "$totalSubjects", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                        Text(text = if (lang == 1) "Subjects" else if (lang == 2) "Matérias" else "Materias", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                }

                Card(
                    modifier = Modifier
                        .weight(1f)
                        .border(
                            width = 1.dp,
                            color = bentoBorderColor,
                            shape = RoundedCornerShape(24.dp)
                        )
                        .testTag("stat_card_tasks_pending"),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = if (isDark) MaterialTheme.colorScheme.surface else Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(imageVector = Icons.Filled.Assignment, contentDescription = null, tint = Color(0xFFF9A825))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "$pendingTasksNum", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                        Text(text = if (lang == 1) "Pending" else if (lang == 2) "Pendentes" else "Pendientes", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .border(
                            width = 1.dp,
                            color = bentoBorderColor,
                            shape = RoundedCornerShape(24.dp)
                        )
                        .testTag("stat_card_tasks_done"),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = if (isDark) MaterialTheme.colorScheme.surface else Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(imageVector = Icons.Filled.CheckCircle, contentDescription = null, tint = Color(0xFF81C784))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "$completedTasksNum", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                        Text(text = if (lang == 1) "Completed" else if (lang == 2) "Concluídas" else "Completadas", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                }

                Card(
                    modifier = Modifier
                        .weight(1f)
                        .border(
                            width = 1.dp,
                            color = bentoBorderColor,
                            shape = RoundedCornerShape(24.dp)
                        )
                        .testTag("stat_card_reminders"),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = if (isDark) MaterialTheme.colorScheme.surface else Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(imageVector = Icons.Filled.NotificationsActive, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "${reminders.size}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                        Text(text = if (lang == 1) "Alarms" else if (lang == 2) "Alarmes" else "Alarmas", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                }
            }
        }

        // Persistence message
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = bentoBorderColor.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(24.dp)
                    ),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = if (isDark) MaterialTheme.colorScheme.surface else Color(0xFFF3F4F6))
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.TrendingUp,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = if (lang == 1) "Consistency in daily tasks contributes significantly to achieving academic goals." else if (lang == 2) "A consistência nas tarefas diárias contribui significativamente para o cumprimento dos objetivos acadêmicos." else "La constancia en las tareas diarias contribuye de manera significativa al cumplimiento de los objetivos académicos.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
