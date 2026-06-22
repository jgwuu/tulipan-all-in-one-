package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.AppDatabase
import com.example.data.UniversityRepository
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.UniversityViewModel
import com.example.ui.viewmodel.UniversityViewModelFactory

class MainActivity : ComponentActivity() {

    // Simple robust request launcher for Android 13+ notifications permission
    private val requestNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        // Handle permission choice here, or silently continue. Alarms trigger standardly
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Ask for local notifications permission on startup if Android 13+ to guarantee popups
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // SQLite Alter Table to add the new nullable groupName column safely preserving existing data
                db.execSQL("ALTER TABLE subjects ADD COLUMN groupName TEXT")
            }
        }

        // Standard Room initialization
        val database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "tulipan_university_reminders_db"
        )
        .addMigrations(MIGRATION_1_2)
        .fallbackToDestructiveMigration()
        .build()

        val repository = UniversityRepository(
            database.subjectDao(),
            database.taskDao(),
            database.reminderDao()
        )

        val factory = UniversityViewModelFactory(repository)
        val viewModel = ViewModelProvider(this, factory)[UniversityViewModel::class.java]

        setContent {
            val sharedPrefs = remember { getSharedPreferences("tulipan_settings", android.content.Context.MODE_PRIVATE) }
            var themeId by remember { mutableStateOf(sharedPrefs.getInt("selected_theme", 0)) }
            var showThemeChooser by remember { mutableStateOf(!sharedPrefs.contains("selected_theme")) }

            MyApplicationTheme(themeId = themeId) {
                if (showThemeChooser) {
                    com.example.ui.components.ThemeSelectionDialog(
                        currentThemeId = themeId,
                        onThemeSelected = { selected ->
                            themeId = selected
                            sharedPrefs.edit().putInt("selected_theme", selected).apply()
                            showThemeChooser = false
                        },
                        onDismissRequest = {
                            if (sharedPrefs.contains("selected_theme")) {
                                showThemeChooser = false
                            }
                        }
                    )
                }

                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route ?: "inicio"

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        // Navigation bar
                        NavigationBar(
                            modifier = Modifier.testTag("bottom_nav_bar"),
                            containerColor = MaterialTheme.colorScheme.surface
                        ) {
                            NavigationBarItem(
                                icon = { Icon(imageVector = Icons.Filled.Home, contentDescription = "Inicio") },
                                label = { Text("Inicio") },
                                selected = currentRoute == "inicio",
                                onClick = { navController.navigate("inicio") { popUpTo("inicio") { saveState = true }; launchSingleTop = true } },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.onSecondary,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    indicatorColor = MaterialTheme.colorScheme.secondaryContainer
                                ),
                                modifier = Modifier.testTag("nav_item_inicio")
                            )

                            NavigationBarItem(
                                icon = { Icon(imageVector = Icons.Filled.DateRange, contentDescription = "Calendario") },
                                label = { Text("Calendario") },
                                selected = currentRoute == "calendario",
                                onClick = { navController.navigate("calendario") { popUpTo("inicio") { saveState = true }; launchSingleTop = true } },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.onSecondary,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    indicatorColor = MaterialTheme.colorScheme.secondaryContainer
                                ),
                                modifier = Modifier.testTag("nav_item_calendario")
                            )

                            NavigationBarItem(
                                icon = { Icon(imageVector = Icons.Filled.School, contentDescription = "Materias") },
                                label = { Text("Materias") },
                                selected = currentRoute == "materias",
                                onClick = { navController.navigate("materias") { popUpTo("inicio") { saveState = true }; launchSingleTop = true } },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.onSecondary,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    indicatorColor = MaterialTheme.colorScheme.secondaryContainer
                                ),
                                modifier = Modifier.testTag("nav_item_materias")
                            )

                            NavigationBarItem(
                                icon = { Icon(imageVector = Icons.Filled.Assignment, contentDescription = "Tareas") },
                                label = { Text("Tareas") },
                                selected = currentRoute == "tareas",
                                onClick = { navController.navigate("tareas") { popUpTo("inicio") { saveState = true }; launchSingleTop = true } },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.onSecondary,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    indicatorColor = MaterialTheme.colorScheme.secondaryContainer
                                ),
                                modifier = Modifier.testTag("nav_item_tareas")
                            )

                            NavigationBarItem(
                                icon = { Icon(imageVector = Icons.Filled.NotificationsActive, contentDescription = "Recordatorios") },
                                label = { Text("Alarmas") },
                                selected = currentRoute == "recordatorios",
                                onClick = { navController.navigate("recordatorios") { popUpTo("inicio") { saveState = true }; launchSingleTop = true } },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.onSecondary,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    indicatorColor = MaterialTheme.colorScheme.secondaryContainer
                                ),
                                modifier = Modifier.testTag("nav_item_recordatorios")
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background)
                            .padding(innerPadding)
                    ) {
                        NavHost(
                            navController = navController,
                            startDestination = "inicio",
                            modifier = Modifier.fillMaxSize()
                        ) {
                            composable("inicio") {
                                DashboardScreen(
                                    viewModel = viewModel,
                                    onNavigateToCalendar = { navController.navigate("calendario") },
                                    onNavigateToTasks = { navController.navigate("tareas") },
                                    onOpenThemeSelector = { showThemeChooser = true }
                                )
                                // FAB floating on Dashboard to see statistics and tracking
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp),
                                    contentAlignment = androidx.compose.ui.Alignment.BottomEnd
                                ) {
                                    FloatingActionButton(
                                        onClick = { navController.navigate("stats") },
                                        containerColor = MaterialTheme.colorScheme.tertiary,
                                        contentColor = Color.White,
                                        modifier = Modifier
                                            .padding(bottom = 12.dp)
                                            .testTag("fab_stats")
                                    ) {
                                        Icon(imageVector = Icons.Filled.BarChart, contentDescription = "Ver Estadísticas")
                                    }
                                }
                            }

                            composable("calendario") {
                                CalendarScreen(viewModel = viewModel)
                            }

                            composable("materias") {
                                SubjectsScreen(viewModel = viewModel)
                            }

                            composable("tareas") {
                                TasksScreen(viewModel = viewModel)
                            }

                            composable("recordatorios") {
                                RemindersScreen(viewModel = viewModel)
                            }

                            composable("stats") {
                                StatsScreen(viewModel = viewModel)
                            }
                        }
                    }
                }
            }
        }
    }
}
