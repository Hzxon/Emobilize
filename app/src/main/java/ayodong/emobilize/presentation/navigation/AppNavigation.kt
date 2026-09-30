package ayodong.emobilize.presentation.navigation


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.StickyNote2
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ayodong.emobilize.presentation.components.RadialMenu
import ayodong.emobilize.presentation.components.RadialMenuItem
import ayodong.emobilize.presentation.note.NoteScreen
import ayodong.emobilize.presentation.schedule.ScheduleScreen

private val radialItems = listOf(
    RadialMenuItem(Routes.SCHEDULE, "Jadwal", Icons.Outlined.CalendarMonth, Color(0xFFA0B098)),
    RadialMenuItem(Routes.NOTE, "Catatan",
        Icons.AutoMirrored.Outlined.StickyNote2, Color(0xFFD0D860)),
)

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    var menuOpen by rememberSaveable { mutableStateOf(false) }

    Scaffold { innerPadding ->
        Box(Modifier.fillMaxSize()) {
            NavHost(
                navController = navController,
                startDestination = Routes.SCHEDULE,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Routes.SCHEDULE) { ScheduleScreen() }
                composable(Routes.NOTE) { NoteScreen() }
            }

            RadialMenu(
                items = radialItems,
                expanded = menuOpen,
                selectedRoute = currentRoute,
                onExpandedChange = { menuOpen = it },
                onItemClick = { item ->
                    menuOpen = false
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
            )
        }
    }
}