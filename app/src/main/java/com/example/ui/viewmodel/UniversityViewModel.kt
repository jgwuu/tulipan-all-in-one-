package com.example.ui.viewmodel

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.Reminder
import com.example.data.Subject
import com.example.data.Task
import com.example.data.UniversityRepository
import com.example.receiver.ReminderReceiver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class UniversityViewModel(private val repository: UniversityRepository) : ViewModel() {

    // Main DB flows mapped safely
    val subjects: StateFlow<List<Subject>> = repository.allSubjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tasks: StateFlow<List<Task>> = repository.allTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val reminders: StateFlow<List<Reminder>> = repository.allReminders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI state for Calendar Screen
    private val _selectedDate = MutableStateFlow(Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis)
    val selectedDate: StateFlow<Long> = _selectedDate.asStateFlow()

    // Friendly, inspiring, motivational message generator for university students
    val motivationalPhrases = listOf(
        "Planifique su jornada con anticipación para optimizar el rendimiento académico.",
        "La organización diaria contribuye a una mejor gestión del tiempo y reducción del estrés.",
        "Establezca prioridades claras para abordar las entregas más relevantes de la semana.",
        "El estudio constante y progresivo facilita la asimilación profunda de conocimientos.",
        "Hacer pausas activas durante las sesiones largas ayuda a mantener la concentración.",
        "Recuerde revisar los criterios de evaluación antes de realizar el envío de sus tareas.",
        "Mantener un registro ordenado de las asignaturas ayuda al cumplimiento de los objetivos.",
        "La constancia es un factor fundamental en el desarrollo profesional y personal.",
        "Dedique un espacio de trabajo libre de distracciones para mejorar la calidad del estudio.",
        "Revise de manera periódica las fechas de entrega y evite acumulaciones de última hora.",
        "La gestión oportuna de dudas con docentes previene inconvenientes en las evaluaciones.",
        "Asista puntualmente a las sesiones académicas para asegurar una trayectoria óptima.",
        "Conserve buenos hábitos de descanso para sustentar el esfuerzo intelectual diario."
    )

    private val _currentPhrase = MutableStateFlow(motivationalPhrases.first())
    val currentPhrase: StateFlow<String> = _currentPhrase.asStateFlow()

    init {
        rotatePhrase()
    }

    fun rotatePhrase() {
        _currentPhrase.value = motivationalPhrases.random()
    }

    fun selectDate(timestamp: Long) {
        _selectedDate.value = timestamp
    }

    // CRUD for Subjects
    fun addSubject(
        name: String,
        days: List<String>,
        startTime: String,
        endTime: String,
        location: String?,
        colorHex: String,
        teacherName: String?,
        groupName: String? = null
    ) {
        viewModelScope.launch {
            val daysStr = days.joinToString(",")
            val subject = Subject(
                name = name,
                daysOfWeek = daysStr,
                startTime = startTime,
                endTime = endTime,
                location = location,
                colorHex = colorHex,
                teacherName = teacherName,
                groupName = groupName
            )
            repository.insertSubject(subject)
        }
    }

    fun updateSubject(
        id: Int,
        name: String,
        days: List<String>,
        startTime: String,
        endTime: String,
        location: String?,
        colorHex: String,
        teacherName: String?,
        groupName: String? = null
    ) {
        viewModelScope.launch {
            val daysStr = days.joinToString(",")
            val subject = Subject(
                id = id,
                name = name,
                daysOfWeek = daysStr,
                startTime = startTime,
                endTime = endTime,
                location = location,
                colorHex = colorHex,
                teacherName = teacherName,
                groupName = groupName
            )
            repository.insertSubject(subject)
        }
    }

    fun deleteSubject(subject: Subject) {
        viewModelScope.launch {
            repository.deleteSubject(subject)
        }
    }

    // CRUD for Tasks
    fun addTask(
        title: String,
        description: String?,
        deadlineDate: Long,
        priority: String,
        subjectId: Int?,
        context: Context
    ) {
        viewModelScope.launch {
            val task = Task(
                title = title,
                description = description,
                deadlineDate = deadlineDate,
                priority = priority,
                subjectId = subjectId
            )
            val generatedId = repository.insertTask(task)

            // Auto-schedule an alert for Tasks 1 hour before, or on the deadline morning
            if (deadlineDate > System.currentTimeMillis()) {
                scheduleAlarm(
                    context = context,
                    id = 100000 + generatedId.toInt(),
                    timeInMillis = deadlineDate - (60 * 60 * 1000), // 1 hour buffer
                    title = "Entrega pendiente de tarea 📚",
                    message = "Recuerda entregar tu tarea: \"$title\" pronto."
                )
            }
        }
    }

    fun updateTask(
        id: Int,
        title: String,
        description: String?,
        deadlineDate: Long,
        priority: String,
        subjectId: Int?,
        isCompleted: Boolean,
        context: Context
    ) {
        viewModelScope.launch {
            val task = Task(
                id = id,
                title = title,
                description = description,
                deadlineDate = deadlineDate,
                priority = priority,
                subjectId = subjectId,
                isCompleted = isCompleted
            )
            repository.insertTask(task)

            // Auto-schedule an alert for Tasks 1 hour before, or on the deadline morning
            if (deadlineDate > System.currentTimeMillis()) {
                scheduleAlarm(
                    context = context,
                    id = 100000 + id,
                    timeInMillis = deadlineDate - (60 * 60 * 1000), // 1 hour buffer
                    title = "Entrega pendiente de tarea 📚",
                    message = "Recuerda entregar tu tarea: \"$title\" pronto."
                )
            }
        }
    }

    fun toggleTaskStatus(task: Task, isNowCompleted: Boolean) {
        viewModelScope.launch {
            repository.insertTask(task.copy(isCompleted = isNowCompleted))
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    // CRUD for Reminders and Notification Scheduling
    fun addReminder(
        title: String,
        description: String?,
        triggerTimeInMillis: Long,
        priority: String,
        repeatIntervalType: String,
        preAlertMinutes: Int,
        context: Context
    ) {
        viewModelScope.launch {
            val reminder = Reminder(
                title = title,
                description = description,
                triggerTime = triggerTimeInMillis,
                priority = priority,
                repeatIntervalType = repeatIntervalType,
                preAlertMinutes = preAlertMinutes
            )
            val generatedId = repository.insertReminder(reminder)

            // Math configuration for warning alert
            val targetAlertTime = triggerTimeInMillis - (preAlertMinutes * 60 * 1000)

            if (targetAlertTime > System.currentTimeMillis()) {
                val lovingTitle = if (priority == "Alta") "Pendiente Urgente" else "Recordatorio"
                val finalMessage = if (!description.isNullOrEmpty()) {
                    description
                } else {
                    "Recuerde realizar este compromiso programado en su agenda."
                }

                scheduleAlarm(
                    context = context,
                    id = generatedId.toInt(),
                    timeInMillis = targetAlertTime,
                    title = "$lovingTitle: $title",
                    message = finalMessage
                )
            }
        }
    }

    fun deleteReminder(reminder: Reminder, context: Context) {
        viewModelScope.launch {
            cancelAlarm(context, reminder.id)
            repository.deleteReminder(reminder)
        }
    }

    // AlarmManager utility for precise offline local alerts
    private fun scheduleAlarm(
        context: Context,
        id: Int,
        timeInMillis: Long,
        title: String,
        message: String
    ) {
        try {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, ReminderReceiver::class.java).apply {
                putExtra("id", id)
                putExtra("title", title)
                putExtra("desc", message)
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                id,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
            }
            Log.d("UniversityVM", "Scheduled Alarm ID: $id at ${SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault()).format(Date(timeInMillis))}")
        } catch (e: Exception) {
            Log.e("UniversityVM", "Error scheduling alarm", e)
        }
    }

    private fun cancelAlarm(context: Context, id: Int) {
        try {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, ReminderReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                id,
                intent,
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )
            if (pendingIntent != null) {
                alarmManager.cancel(pendingIntent)
                pendingIntent.cancel()
            }
        } catch (e: Exception) {
            Log.e("UniversityVM", "Error cancelling alarm", e)
        }
    }
}

class UniversityViewModelFactory(private val repository: UniversityRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UniversityViewModel::class.java)) {
            return UniversityViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
