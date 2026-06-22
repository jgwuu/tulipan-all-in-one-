package com.example.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf

// Languages: 0 = Español, 1 = English, 2 = Português
val LocalLanguage = staticCompositionLocalOf { 0 }

object L10n {
    private val translations = mapOf(
        // General & Bottom Bar
        "tab_inicio" to listOf("Inicio", "Home", "Início"),
        "tab_calendario" to listOf("Calendario", "Calendar", "Calendário"),
        "tab_materias" to listOf("Materias", "Subjects", "Matérias"),
        "tab_tareas" to listOf("Tareas", "Tasks", "Tarefas"),
        "tab_alarmas" to listOf("Alarmas", "Alarms", "Alarmes"),
        
        // Header and general items
        "today" to listOf("Hoy", "Today", "Hoje"),
        "save" to listOf("Guardar", "Save", "Salvar"),
        "delete" to listOf("Eliminar", "Delete", "Excluir"),
        "cancel" to listOf("Cancelar", "Cancel", "Cancelar"),
        "edit" to listOf("Editar", "Edit", "Editar"),
        "close" to listOf("Cerrar", "Close", "Fechar"),
        "empty" to listOf("Vacío", "Empty", "Vazio"),
        
        // Dashboard
        "today_classes" to listOf("Clases de hoy", "Today's classes", "Aulas de hoje"),
        "no_classes_today" to listOf("Aún no tienes clases registradas para hoy.", "No classes registered for today.", "Você ainda não tem aulas registradas hoje."),
        "next_alarm" to listOf("Próxima alarma", "Next alarm", "Próximo alarme"),
        "in_time" to listOf("En", "In", "Em"),
        "no_active_alarms" to listOf("Sin alarmas activas", "No active alarms", "Sem alarmes ativos"),
        "today_tasks" to listOf("Tareas de hoy", "Today's tasks", "Tarefas de hoje"),
        "urgent" to listOf("Urgentes", "Urgent", "Urgentes"),
        "pending" to listOf("Pendientes", "Pending", "Pendentes"),
        "view_all" to listOf("Ver todas", "View all", "Ver tudo"),
        "subjects" to listOf("Asignaturas", "Course subjects", "Disciplinas"),
        "view_subjects" to listOf("Ver materias", "View subjects", "Ver matérias"),
        "academic_progress" to listOf("Rendimiento académico", "Academic performance", "Desempenho acadêmico"),
        "flower_constancy" to listOf("Flor de la Constancia", "Flower of Constancy", "Flor da Constância"),
        
        // Theme / Configuration Dialog
        "theme_customizer" to listOf("Personalice la paleta de colores y el modo de la agenda clásica Tulipán.", "Customize the color palette and active mode of Tulipán.", "Personalize a paleta de cores e o modo da agenda clássica Tulipán."),
        "display_mode" to listOf("Modo de Pantalla", "Display Mode", "Modo de Tela"),
        "auto" to listOf("Auto", "Auto", "Auto"),
        "light" to listOf("Claro", "Light", "Claro"),
        "dark" to listOf("Oscuro", "Dark", "Escuro"),
        "language" to listOf("Idioma", "Language", "Idioma"),
        "select_language" to listOf("Seleccione el idioma de la aplicación", "Select application language", "Selecione o idioma do aplicativo"),
        
        // Subjects screen
        "add_subject" to listOf("Agregar Materia", "Add Subject", "Adicionar Matéria"),
        "edit_subject" to listOf("Editar Materia", "Edit Subject", "Editar Matéria"),
        "subject_name" to listOf("Nombre de la Materia", "Subject Name", "Nome da Matéria"),
        "classroom" to listOf("Aula", "Classroom", "Sala"),
        "professor" to listOf("Profesor", "Professor", "Professor"),
        "start_time" to listOf("Hora Inicio", "Start Time", "Hora de Início"),
        "end_time" to listOf("Hora Fin", "End Time", "Hora de Término"),
        "study_group" to listOf("Grupo de estudio (Opcional)", "Study group (Optional)", "Grupo de estudo (Opcional)"),
        "selected_days" to listOf("Días de clase", "Class days", "Dias de aula"),
        "no_subjects" to listOf("No tienes materias registradas aún. Presiona + para agregar.", "No subjects registered yet. Press + to add.", "Nenhuma matéria registrada ainda. Pressione + para adicionar."),
        
        // Tasks screen
        "add_task" to listOf("Agregar Tarea", "Add Task", "Adicionar Tarefa"),
        "edit_task" to listOf("Editar Tarea", "Edit Task", "Editar Tarefa"),
        "task_name" to listOf("Título de la tarea", "Task Title", "Título da tarefa"),
        "select_subject" to listOf("Seleccionar Materia", "Select Subject", "Selecionar Matéria"),
        "priority" to listOf("Prioridad", "Priority", "Prioridade"),
        "deadline" to listOf("Fecha límite", "Deadline Date", "Prazo limite"),
        "completed" to listOf("Completada", "Completed", "Concluída"),
        "pending_tasks" to listOf("Pendientes", "Pending", "Pendentes"),
        "completed_tasks" to listOf("Completadas", "Completed", "Concluídas"),
        "no_tasks" to listOf("No tienes tareas registradas. Presiona + para agregar.", "No tasks registered yet. Press + to add.", "Nenhuma tarefa registrada. Pressione + para adicionar."),
        
        // Reminders / Alerts screen
        "add_alarm" to listOf("Programar Alarma", "Add Alarm", "Agendar Alarme"),
        "alarm_title" to listOf("Asunto de la alarma", "Alarm Title", "Assunto do alarme"),
        "reminder_time" to listOf("Fecha y Hora del evento", "Event Date & Time", "Data & Hora do evento"),
        "pre_alert" to listOf("Antelación (Minutos antes)", "Pre-alert time", "Antecedência"),
        "pre_alert_option" to listOf("minutos antes", "minutes before", "minutos antes"),
        "no_alarms" to listOf("No tienes alarmas configuradas. Presiona + para crear.", "No alarms configured yet. Press + to create.", "Nenhum alarme configurado. Pressione + para criar."),
        "expired" to listOf("Disparada / Expirada", "Triggered / Expired", "Disparado / Expirado"),
        "active" to listOf("Activa", "Active", "Ativo")
    )

    fun getString(key: String, languageCode: Int): String {
        val list = translations[key] ?: return key
        return list.getOrElse(languageCode) { list[0] }
    }
}
