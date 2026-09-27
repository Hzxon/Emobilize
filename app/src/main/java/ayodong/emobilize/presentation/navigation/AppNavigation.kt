package ayodong.emobilize.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ayodong.emobilize.presentation.components.AppBottomBar
import ayodong.emobilize.presentation.note.NoteScreen
import ayodong.emobilize.presentation.schedule.ScheduleScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            AppBottomBar(navController = navController)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.SCHEDULE,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.SCHEDULE) {
                ScheduleScreen()
            }
            composable(Routes.NOTE) {
                NoteScreen()
            }
        }
    }
}