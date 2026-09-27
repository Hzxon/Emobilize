package ayodong.emobilize.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dataset
import androidx.compose.material.icons.filled.Edit
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Schedule : BottomNavItem(
        route = Routes.SCHEDULE,
        title = "Schedule",
        icon = Icons.Default.Dataset
    )

    object Note : BottomNavItem(
        route = Routes.NOTE,
        title = "Note",
        icon = Icons.Default.Edit
    )
}