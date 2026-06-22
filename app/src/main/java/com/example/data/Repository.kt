package com.example.data

import kotlinx.coroutines.flow.Flow

class UniversityRepository(
    private val subjectDao: SubjectDao,
    private val taskDao: TaskDao,
    private val reminderDao: ReminderDao
) {
    // Flows mapping state changes instantly
    val allSubjects: Flow<List<Subject>> = subjectDao.getAllSubjects()
    val allTasks: Flow<List<Task>> = taskDao.getAllTasks()
    val allReminders: Flow<List<Reminder>> = reminderDao.getAllReminders()

    suspend fun getSubjectById(id: Int): Subject? = subjectDao.getSubjectById(id)
    suspend fun insertSubject(subject: Subject): Long = subjectDao.insertSubject(subject)
    suspend fun deleteSubject(subject: Subject) = subjectDao.deleteSubject(subject)

    suspend fun insertTask(task: Task): Long = taskDao.insertTask(task)
    suspend fun updateTaskStatus(taskId: Int, completed: Boolean) = taskDao.updateTaskStatus(taskId, completed)
    suspend fun deleteTask(task: Task) = taskDao.deleteTask(task)

    suspend fun getReminderById(id: Int): Reminder? = reminderDao.getReminderById(id)
    suspend fun insertReminder(reminder: Reminder): Long = reminderDao.insertReminder(reminder)
    suspend fun deleteReminder(reminder: Reminder) = reminderDao.deleteReminder(reminder)
}
