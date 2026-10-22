package ayodong.emobilize.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import ayodong.emobilize.domain.model.FabAction
import ayodong.emobilize.domain.model.NavTab
import ayodong.emobilize.domain.model.ThemeMode
import ayodong.emobilize.domain.model.WeekStart
import ayodong.emobilize.ui.calendar.CalendarScreen
import ayodong.emobilize.ui.dock.BottomDock
import ayodong.emobilize.ui.dock.RadialMenu
import ayodong.emobilize.ui.navigation.AppRoute
import ayodong.emobilize.ui.settings.SettingsScreen
import ayodong.emobilize.ui.splash.SplashScreen
import ayodong.emobilize.ui.tasks.AddTaskCard
import ayodong.emobilize.ui.tasks.CalendarFormSheet
import ayodong.emobilize.ui.tasks.TaskFormSheet
import ayodong.emobilize.ui.tasks.TasksScreen
import ayodong.emobilize.ui.tasks.TasksViewModel
import ayodong.emobilize.ui.theme.EmobilizeTheme
import ayodong.emobilize.ui.theme.LocalPalette
import ayodong.emobilize.ui.theme.NeutralDark
import ayodong.emobilize.ui.theme.NeutralLight
import ayodong.emobilize.ui.theme.menuColor
import java.time.DayOfWeek

private val overlayDialog = DialogProperties(
    usePlatformDefaultWidth = false,
    dismissOnBackPress = false,
    dismissOnClickOutside = false,
    decorFitsSystemWindows = false,
)

@Composable
fun EmobilizeApp(viewModel: TasksViewModel) {
    val navController = rememberNavController()
    val systemDark = isSystemInDarkTheme()
    val isDark = when (viewModel.appSettings.themeMode) {
        ThemeMode.System -> systemDark
        ThemeMode.Light -> false
        ThemeMode.Dark -> true
    }
    val palette = if (isDark) NeutralDark else NeutralLight

    EmobilizeTheme(palette) {
        NavHost(
            navController = navController,
            startDestination = AppRoute.Splash,
            modifier = Modifier.fillMaxSize(),
        ) {
            composable(AppRoute.Splash) {
                SplashScreen(
                    onFinished = {
                        navController.navigate(AppRoute.Tasks) {
                            popUpTo(AppRoute.Splash) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                )
            }
            composable(AppRoute.Tasks) {
                MainShell(navController, NavTab.Tasks, viewModel)
            }
            composable(AppRoute.Calendar) {
                MainShell(navController, NavTab.Calendar, viewModel)
            }
            dialog(AppRoute.Settings, dialogProperties = overlayDialog) {
                SettingsScreen(
                    appSettings = viewModel.appSettings,
                    categories = viewModel.categories,
                    onSave = viewModel::saveSettings,
                    onClose = { navController.leave() },
                )
            }
            dialog(AppRoute.AddTask, dialogProperties = overlayDialog) {
                LaunchedEffect(Unit) { viewModel.prepareAdd() }
                AddTaskCard(
                    draft = viewModel.addDraft,
                    categories = viewModel.categories,
                    onDismiss = {
                        viewModel.closeAdd()
                        navController.leave()
                    },
                    onSubmit = {
                        if (viewModel.submitAdd()) {
                            viewModel.closeAdd()
                            navController.leave()
                        }
                    },
                )
            }
            dialog(
                route = AppRoute.EditTask,
                arguments = listOf(navArgument("taskId") { type = NavType.LongType }),
                dialogProperties = overlayDialog,
            ) { entry ->
                val taskId = entry.arguments?.getLong("taskId") ?: 0L
                LaunchedEffect(taskId) {
                    if (!viewModel.loadTaskEdit(taskId)) navController.leave()
                }
                if (viewModel.editingId == taskId) {
                    val colors = LocalPalette.current
                    BackHandler {
                        viewModel.closeEdit()
                        navController.leave()
                    }
                    TaskFormSheet(
                        title = "Edit Task",
                        submitLabel = "Save Changes",
                        draft = viewModel.editDraft,
                        categories = viewModel.categories,
                        canSubmit = viewModel.canSubmit(viewModel.editDraft),
                        saveColor = colors.menuColor(FabAction.Edit),
                        onDismiss = {
                            viewModel.closeEdit()
                            navController.leave()
                        },
                        onSubmit = {
                            if (viewModel.submitEdit()) {
                                viewModel.closeEdit()
                                navController.leave()
                            }
                        },
                    )
                }
            }
            dialog(AppRoute.CalendarAdd, dialogProperties = overlayDialog) {
                CalendarFormDestination(
                    navController = navController,
                    viewModel = viewModel,
                    editing = false,
                    onPrepare = { viewModel.prepareCalendarAdd() },
                )
            }
            dialog(
                route = AppRoute.CalendarTask,
                arguments = listOf(navArgument("taskId") { type = NavType.LongType }),
                dialogProperties = overlayDialog,
            ) { entry ->
                val taskId = entry.arguments?.getLong("taskId") ?: 0L
                CalendarFormDestination(
                    navController = navController,
                    viewModel = viewModel,
                    editing = true,
                    onPrepare = { viewModel.loadCalendarTask(taskId) },
                )
            }
            dialog(
                route = AppRoute.CalendarBlock,
                arguments = listOf(
                    navArgument("blockId") { type = NavType.LongType },
                    navArgument("date") { type = NavType.StringType },
                ),
                dialogProperties = overlayDialog,
            ) { entry ->
                val blockId = entry.arguments?.getLong("blockId") ?: 0L
                val date = entry.arguments?.getString("date").orEmpty()
                CalendarFormDestination(
                    navController = navController,
                    viewModel = viewModel,
                    editing = true,
                    onPrepare = { viewModel.loadCalendarBlock(blockId, date) },
                )
            }
        }
    }
}

@Composable
private fun CalendarFormDestination(
    navController: NavHostController,
    viewModel: TasksViewModel,
    editing: Boolean,
    onPrepare: () -> Boolean,
) {
    var ready by rememberSaveable { mutableStateOf(false) }
    val dismiss = {
        viewModel.closeCalendarForm()
        navController.leave()
    }
    BackHandler(onBack = dismiss)
    LaunchedEffect(Unit) {
        ready = onPrepare()
        if (!ready) navController.leave()
    }
    if (!ready) return
    val colors = LocalPalette.current
    CalendarFormSheet(
        draft = viewModel.addDraft,
        categories = viewModel.categories,
        lockType = editing,
        canSubmit = viewModel.canSubmitCalendar(viewModel.addDraft),
        saveColor = colors.menuColor(if (editing) FabAction.Edit else FabAction.Add),
        submitLabel = if (editing) "Save Changes" else "Save",
        onDismiss = dismiss,
        onSubmit = {
            if (viewModel.submitCalendar()) dismiss()
        },
    )
}

@Composable
private fun MainShell(
    navController: NavHostController,
    tab: NavTab,
    viewModel: TasksViewModel,
) {
    var menuOpen by rememberSaveable { mutableStateOf(false) }
    val colors = LocalPalette.current
    val overlayOpen = navController.currentDestination?.route != AppRoute.Tasks &&
        navController.currentDestination?.route != AppRoute.Calendar
    BackHandler(
        enabled = !overlayOpen && (menuOpen || viewModel.pendingAction != null || viewModel.selectedBlockId != null),
    ) {
        when {
            menuOpen -> menuOpen = false
            else -> {
                viewModel.clearBlockSelection()
                viewModel.cancelPending()
            }
        }
    }
    Box(
        Modifier
            .fillMaxSize()
            .background(colors.surface),
    ) {
        Column(Modifier.fillMaxSize()) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(start = 24.dp, end = 18.dp, top = 8.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Spacer(Modifier.weight(1f))
                Box(
                    Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .clickable {
                            menuOpen = false
                            navController.navigate(AppRoute.Settings) { launchSingleTop = true }
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = "Settings",
                        tint = colors.text,
                        modifier = Modifier.size(22.dp),
                    )
                }
            }
            Box(
                Modifier
                    .weight(1f)
                    .fillMaxSize(),
            ) {
                if (tab == NavTab.Tasks) {
                    TasksScreen(
                        viewModel = viewModel,
                        onEditTask = { taskId ->
                            navController.navigate(AppRoute.editTask(taskId))
                        },
                    )
                } else {
                    CalendarScreen(
                        tasks = viewModel.tasks,
                        blocks = viewModel.blocks,
                        defaultView = viewModel.appSettings.defaultCalendarView,
                        weekStart = if (viewModel.appSettings.weekStart == WeekStart.Monday) {
                            DayOfWeek.MONDAY
                        } else {
                            DayOfWeek.SUNDAY
                        },
                        pending = viewModel.pendingAction,
                        selectedTaskId = viewModel.selectedTaskId,
                        selectedBlockId = viewModel.selectedBlockId,
                        onSelectTask = { task ->
                            when (viewModel.pendingAction) {
                                FabAction.Edit -> {
                                    viewModel.clearBlockSelection()
                                    navController.navigate(AppRoute.calendarTask(task.id))
                                }
                                FabAction.Delete -> {
                                    viewModel.clearBlockSelection()
                                    viewModel.toggleTaskSelection(task.id)
                                }
                                else -> Unit
                            }
                        },
                        onSelectBlock = { date, block ->
                            when (viewModel.pendingAction) {
                                FabAction.Edit -> {
                                    viewModel.clearBlockSelection()
                                    navController.navigate(AppRoute.calendarBlock(block.id, date.toString()))
                                }
                                FabAction.Delete -> {
                                    viewModel.clearTaskSelection()
                                    viewModel.toggleBlockSelection(block.id)
                                }
                                else -> viewModel.toggleBlockSelection(block.id)
                            }
                        },
                        onDelete = {
                            val blockId = viewModel.selectedBlockId
                            if (viewModel.selectedTaskId != null) viewModel.confirmAction()
                            if (blockId != null) viewModel.deleteSelectedBlock()
                        },
                        onCancel = {
                            viewModel.clearBlockSelection()
                            viewModel.cancelPending()
                        },
                    )
                }
            }
            BottomDock(
                tab = tab,
                onTab = { next ->
                    menuOpen = false
                    viewModel.clearBlockSelection()
                    if (next == NavTab.Calendar && viewModel.pendingAction == FabAction.Update) {
                        viewModel.cancelPending()
                    }
                    navController.openTab(if (next == NavTab.Calendar) AppRoute.Calendar else AppRoute.Tasks)
                },
            )
        }
        RadialMenu(
            tab = tab,
            menuOpen = menuOpen,
            onMenuOpenChange = { menuOpen = it },
            onAction = { action ->
                menuOpen = false
                viewModel.clearBlockSelection()
                when {
                    tab == NavTab.Calendar && action == FabAction.Add -> navController.navigate(AppRoute.CalendarAdd)
                    action == FabAction.Add -> navController.navigate(AppRoute.AddTask)
                    else -> viewModel.onFabAction(action)
                }
            },
            modifier = Modifier.zIndex(2f),
        )
    }
}

private fun NavHostController.leave() {
    popBackStack()
}

private fun NavHostController.openTab(route: String) {
    if (currentDestination?.route == route) return
    if (route == AppRoute.Tasks) {
        popBackStack(AppRoute.Tasks, inclusive = false)
        return
    }
    navigate(route) {
        popUpTo(AppRoute.Tasks) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
