# Agenda Universitaria 📚🎓

Una potente y elegante aplicación de organización académica para Android diseñada para ayudar a las y los estudiantes universitarios a gestionar sus materias, clases, tareas y recordatorios de manera centralizada, eficiente y profesional.

## 🌟 Características Principales

*   **Panel de Control Centralizado (Dashboard):** Visualiza de un vistazo las asignaturas del día, próximas alertas activas y recibe frases motivacionales de estudio que fomentan la constancia y disciplina académica.
*   **Gestión de Materias:** Registra tus asignaturas detallando nombre, aula, profesor, grupo de estudio, horarios (inicio/fin) y días de la semana correspondientes.
*   **Calendario Académico Integrado:** Un calendario interactivo donde puedes ver de forma gráfica tu horario semanal, filtrar pendientes programados por día específico y ver códigos de colores asociados a cada materia.
*   **Seguimiento de Tareas:** Organiza tus deberes y proyectos. Asócialos a una materia específica, define su nivel de prioridad (Alta/Normal) y realiza un seguimiento visual del estado (Pendientes vs. Completadas).
*   **Módulo de Recordatorios y Alarmas:** Configura recordatorios para tus entregas con opciones de pre-alerta (de 5 a 60 minutos antes) que emiten notificaciones nativas en el sistema para evitar entregas tardías.
*   **Visualización de Progreso (Flor de la Constancia):** Un gráfico dinámico e interactivo en forma de flor que crece y florece a medida que completas tus tareas diarias, proporcionando retroalimentación visual sobre tu nivel de cumplimiento ético y académico.
*   **Selector de Temas Personalizado:** Ajusta el aspecto visual de la aplicación con una paleta de 6 combinaciones de colores únicas (*Amarillo Oro*, *Gris Neutro*, *Azul Océano*, *Verde Bosque*, *Rosa Pastel* y *Púrpura Imperial*), totalmente compatible con el **Tema Claro** y **Tema Oscuro** del sistema y con persistencia en el dispositivo.

## 🛠️ Stack Tecnológico y Arquitectura

*   **Lenguaje:** [Kotlin](https://kotlinlang.org/) — 100% de la lógica de programación.
*   **Interfaz de Usuario:** [Jetpack Compose](https://developer.android.com/compose) — UI declarativa, moderna y totalmente adaptativa a diferentes resoluciones.
*   **Base de Datos Local:** [Room (SQLite)](https://developer.android.com/training/data-storage/room) — Persistencia robusta y estructurada de asignaturas, tareas y alarmas de forma local y sin conexión a internet.
*   **Inyección y Gestión de Estado:** MVVM (Model-View-ViewModel) estructurado y reactivo con `ViewModel` y `StateFlow`.
*   **Sincronización:** Empleo de Kotlin Coroutines y Flow para operaciones asíncronas fluidas en segundo plano.
*   **Notificaciones:** Administrador de alarmas persistentes del sistema (`AlarmManager` y `BroadcastReceiver`) para asegurar el disparo de pre-alertas con alta precisión de manera local.

## 📁 Estructura del Proyecto

*   `/app/src/main/java/com/example/data/`: Declaración del esquema de la base de datos (Room Entity, DAOs, Modelos y Repositorio de datos).
*   `/app/src/main/java/com/example/ui/screens/`: Pantallas de la aplicación (Dashboard, Calendario, Materias, Tareas, Recordatorios y Estadísticas de Progreso).
*   `/app/src/main/java/com/example/ui/theme/`: Definición de la tipografía, formas y el constructor del esquema de colores dinámico.
*   `/app/src/main/java/com/example/receiver/`: Receptores del sistema para la gestión y despliegue de las alertas en segundo plano.

---
*Desarrollado con altos estándares de diseño Material Design 3, asegurando la mejor legibilidad, accesibilidad visual y adaptabilidad del sistema.*
