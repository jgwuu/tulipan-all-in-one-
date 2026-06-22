# Academic Agenda

A modern and efficient Android application designed to help university students manage courses, schedules, assignments, and academic commitments from a single, organized platform.

## Key Features

### Academic Dashboard

A centralized workspace that provides a concise overview of academic activities, including scheduled courses, upcoming deadlines, active reminders, and productivity-focused insights. The dashboard is designed to support effective planning, organization, and academic consistency throughout the semester.

### Course Management

Create and manage courses by storing essential information such as course name, classroom, instructor, study group, class schedule, and weekly meeting days.

### Integrated Academic Calendar

An interactive calendar that offers a visual representation of weekly schedules, planned activities, and upcoming academic events. Students can easily review daily commitments and identify courses through color-coded organization.

### Assignment Tracking

Organize assignments, projects, and academic tasks in a structured environment. Tasks can be linked to specific courses, assigned priority levels, and monitored through clear completion status indicators.

### Reminders and Notifications

Schedule reminders for assignments, exams, and academic activities with configurable advance notifications. Native Android notifications help ensure important deadlines are not overlooked.

### Progress Visualization

A visual progress system that reflects task completion and academic consistency over time, providing students with meaningful feedback on their productivity and study habits.

### Personalized Themes

Customize the application's appearance using multiple professionally designed color schemes. Full support for both Light and Dark modes ensures a consistent and accessible user experience across different environments.

## Technology Stack and Architecture

* **Programming Language:** Kotlin
* **User Interface:** Jetpack Compose
* **Local Database:** Room (SQLite)
* **Architecture Pattern:** MVVM (Model-View-ViewModel)
* **State Management:** ViewModel and StateFlow
* **Asynchronous Operations:** Kotlin Coroutines and Flow
* **Notifications:** AlarmManager and BroadcastReceiver

## Project Structure

* `/app/src/main/java/com/example/data/` — Database entities, DAOs, repositories, and data models.
* `/app/src/main/java/com/example/ui/screens/` — Application screens including Dashboard, Calendar, Courses, Assignments, Reminders, and Progress views.
* `/app/src/main/java/com/example/ui/theme/` — Theme configuration, typography, shapes, and color system.
* `/app/src/main/java/com/example/receiver/` — System receivers responsible for background reminder and notification handling.

---

Developed following Material Design 3 guidelines, prioritizing usability, accessibility, maintainability, and a consistent user experience.
