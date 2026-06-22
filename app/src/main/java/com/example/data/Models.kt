package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subjects")
data class Subject(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val daysOfWeek: String, // Comma-separated list of days (e.g., "Lunes,Miércoles")
    val startTime: String, // "HH:mm" (e.g., "08:30")
    val endTime: String,   // "HH:mm" (e.g., "10:00")
    val location: String? = null,
    val colorHex: String = "#FFF59D", // Hex code string of the theme color
    val teacherName: String? = null,
    val groupName: String? = null // Room / Class section group (e.g., "Grupo 1", "H5")
) {
    fun getStartTimeMinutes(): Int {
        if (startTime.isBlank()) return 0
        return try {
            var hour = 0
            var minute = 0
            val upper = startTime.uppercase()
            val isPm = upper.contains("PM")
            val isAm = upper.contains("AM")
            val clean = upper
                .replace("AM", "")
                .replace("PM", "")
                .trim()
            val parts = clean.split(":")
            if (parts.size >= 2) {
                hour = parts[0].trim().toIntOrNull() ?: 0
                val minPart = parts[1].trim().split(" ")[0].trim()
                minute = minPart.toIntOrNull() ?: 0
            } else if (parts.isNotEmpty()) {
                hour = parts[0].trim().toIntOrNull() ?: 0
            }
            if (isPm && hour < 12) hour += 12
            if (isAm && hour == 12) hour = 0
            hour * 60 + minute
        } catch (e: Exception) {
            0
        }
    }
}

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String? = null,
    val deadlineDate: Long, // Timestamps of target date
    val isCompleted: Boolean = false,
    val priority: String = "Normal", // "Urgente", "Normal", "Secundaria"
    val subjectId: Int? = null // Links to Subject.id
)

@Entity(tableName = "reminders")
data class Reminder(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String? = null,
    val triggerTime: Long, // Timestamp of when it alerts
    val priority: String = "Media", // "Baja", "Media", "Alta"
    val repeatIntervalType: String = "None", // "None", "Daily", "Weekly"
    val preAlertMinutes: Int = 0 // 0 = on event, 5, 15, 30 etc.
)
