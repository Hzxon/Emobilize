package ayodong.emobilize.core.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import ayodong.emobilize.core.di.AppContainer
import ayodong.emobilize.core.di.LocalAppContainer
import ayodong.emobilize.core.model.ThemeMode
import ayodong.emobilize.core.ui.dock.FabAction
import ayodong.emobilize.core.ui.dock.NavTab
import ayodong.emobilize.core.ui.theme.EmobilizeTheme
import ayodong.emobilize.core.ui.theme.LocalPalette
import ayodong.emobilize.core.ui.theme.NeutralDark
import ayodong.emobilize.core.ui.theme.NeutralLight
import ayodong.emobilize.core.ui.theme.menuColor
import ayodong.emobilize.feature.auth.di.AuthModule
import ayodong.emobilize.feature.auth.ui.AuthScreen
import ayodong.emobilize.feature.auth.ui.AuthViewModel
import ayodong.emobilize.feature.calendar.di.CalendarModule
import ayodong.emobilize.feature.calendar.ui.CalendarFormSheet
import ayodong.emobilize.feature.calendar.ui.CalendarFormTarget
import ayodong.emobilize.feature.calendar.ui.CalendarFormViewModel
import ayodong.emobilize.feature.reminders.di.RemindersModule
import ayodong.emobilize.feature.reminders.ui.ReminderScreen
import ayodong.emobilize.feature.reminders.ui.RemindersViewModel
import ayodong.emobilize.feature.settings.di.SettingsModule
import ayodong.emobilize.feature.settings.ui.SettingsScreen
import ayodong.emobilize.feature.settings.ui.SettingsViewModel
import ayodong.emobilize.feature.splash.ui.SplashScreen
import ayodong.emobilize.feature.tasks.di.TasksModule
import ayodong.emobilize.feature.tasks.ui.AddTaskCard
import ayodong.emobilize.feature.tasks.ui.TaskFormSheet
import ayodong.emobilize.feature.tasks.ui.TaskFormViewModel

// Hotspot file: every route is registered here once by the core owner. A feature fills its own
// screen and ViewModel; it does not edit this graph.

private val overlayDialog = DialogProperties(
    usePlatformDefaultWidth = false,
    dismissOnBackPress = false,
    dismissOnClickOutside = false,
    decorFitsSystemWindows = false,
)

@Composable
fun AppNavHost(shell: ShellViewModel) {
    val container = LocalAppContainer.current
    val navController = rememberNavController()
    val shellState by shell.uiState.collectAsState()
    val systemDark = isSystemInDarkTheme()
    val isDark = when (shellState.themeMode) {
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
                SplashScreen(isDark = isDark) {
                    navController.navigate(AppRoute.Auth) {
                        popUpTo(AppRoute.Splash) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
            composable(AppRoute.Auth) {
                val viewModel: AuthViewModel = viewModel(factory = AuthModule.authViewModel(container))
                val state by viewModel.uiState.collectAsState()
                LaunchedEffect(state.signedIn) {
                    if (state.signedIn) navController.openTasks()
                }
                AuthScreen(
                    state = state,
                    onNameChange = viewModel::updateName,
                    onEmailChange = viewModel::updateEmail,
                    onPasswordChange = viewModel::updatePassword,
                    onConfirmChange = viewModel::updateConfirm,
                    onSubmit = viewModel::submit,
                    onShowLogin = viewModel::showLogin,
                    onShowRegister = viewModel::showRegister,
                    onSkip = { navController.openTasks() },
                )
            }
            composable(AppRoute.Tasks) {
                MainShell(navController, NavTab.Tasks, shell)
            }
            composable(AppRoute.Calendar) {
                MainShell(navController, NavTab.Calendar, shell)
            }
            dialog(AppRoute.Reminders, dialogProperties = overlayDialog) {
                val viewModel: RemindersViewModel = viewModel(factory = RemindersModule.remindersViewModel(container))
                val state by viewModel.uiState.collectAsState()
                ReminderScreen(
                    tasks = state.tasks,
                    doneIds = state.doneIds,
                    notificationsEnabled = state.notificationsEnabled,
                    onBack = { navController.leave() },
                    onMarkDone = viewModel::markDone,
                    onReschedule = { taskId ->
                        navController.leave()
                        navController.openEditor(shell, taskId)
                    },
                )
            }
            dialog(AppRoute.Settings, dialogProperties = overlayDialog) {
                val viewModel: SettingsViewModel = viewModel(factory = SettingsModule.settingsViewModel(container))
                val state by viewModel.uiState.collectAsState()
                SettingsScreen(
                    appSettings = state.appSettings,
                    categories = state.categories,
                    onSave = viewModel::save,
                    onClose = { navController.leave() },
                )
            }
            dialog(AppRoute.AddTask, dialogProperties = overlayDialog) {
                val viewModel: TaskFormViewModel = viewModel(factory = TasksModule.taskFormViewModel(container, taskId = null))
                val categories by viewModel.categories.collectAsState()
                val haptics = LocalHapticFeedback.current
                AddTaskCard(
                    draft = viewModel.draft,
                    categories = categories,
                    onDismiss = { navController.leave() },
                    onSubmit = {
                        if (viewModel.submitAdd()) {
                            haptics.performHapticFeedback(HapticFeedbackType.Confirm)
                            navController.leave()
                        } else {
                            haptics.performHapticFeedback(HapticFeedbackType.Reject)
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
                val viewModel: TaskFormViewModel = viewModel(factory = TasksModule.taskFormViewModel(container, taskId))
                if (!viewModel.found) {
                    LaunchedEffect(Unit) { navController.leave() }
                } else {
                    val categories by viewModel.categories.collectAsState()
                    val colors = LocalPalette.current
                    val haptics = LocalHapticFeedback.current
                    BackHandler { navController.leave() }
                    TaskFormSheet(
                        title = "Edit Task",
                        submitLabel = "Save Changes",
                        draft = viewModel.draft,
                        categories = categories,
                        canSubmit = viewModel.canSubmitEdit(),
                        saveColor = colors.menuColor(FabAction.Edit),
                        onDismiss = { navController.leave() },
                        onSubmit = {
                            if (viewModel.submitEdit()) {
                                haptics.performHapticFeedback(HapticFeedbackType.Confirm)
                                navController.leave()
                            } else {
                                haptics.performHapticFeedback(HapticFeedbackType.Reject)
                            }
                        },
                    )
                }
            }
            dialog(AppRoute.CalendarAdd, dialogProperties = overlayDialog) {
                CalendarFormDestination(navController, container, CalendarFormTarget.New)
            }
            dialog(
                route = AppRoute.CalendarTask,
                arguments = listOf(navArgument("taskId") { type = NavType.LongType }),
                dialogProperties = overlayDialog,
            ) { entry ->
                val taskId = entry.arguments?.getLong("taskId") ?: 0L
                CalendarFormDestination(navController, container, CalendarFormTarget.EditTask(taskId))
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
                CalendarFormDestination(navController, container, CalendarFormTarget.EditBlock(blockId, date))
            }
        }
    }
}

@Composable
private fun CalendarFormDestination(
    navController: NavHostController,
    container: AppContainer,
    target: CalendarFormTarget,
) {
    val viewModel: CalendarFormViewModel = viewModel(factory = CalendarModule.calendarFormViewModel(container, target))
    val dismiss = { navController.leave() }
    BackHandler(onBack = dismiss)
    if (!viewModel.found) {
        LaunchedEffect(Unit) { dismiss() }
        return
    }
    val categories by viewModel.categories.collectAsState()
    val colors = LocalPalette.current
    val haptics = LocalHapticFeedback.current
    CalendarFormSheet(
        draft = viewModel.draft,
        categories = categories,
        lockType = viewModel.editing,
        canSubmit = viewModel.canSubmit(),
        saveColor = colors.menuColor(if (viewModel.editing) FabAction.Edit else FabAction.Add),
        submitLabel = if (viewModel.editing) "Save Changes" else "Save",
        onDismiss = dismiss,
        onSubmit = {
            if (viewModel.submit()) {
                haptics.performHapticFeedback(HapticFeedbackType.Confirm)
                dismiss()
            } else {
                haptics.performHapticFeedback(HapticFeedbackType.Reject)
            }
        },
    )
}

internal fun NavHostController.leave() {
    popBackStack()
}

// Opening any form ends the radial menu's action mode, as the form takes over the screen.
internal fun NavHostController.openForm(shell: ShellViewModel, route: String) {
    shell.cancelPending()
    navigate(route)
}

internal fun NavHostController.openEditor(shell: ShellViewModel, taskId: Long) {
    openForm(shell, shell.editorRoute(taskId))
}

private fun NavHostController.openTasks() {
    navigate(AppRoute.Tasks) {
        popUpTo(AppRoute.Auth) { inclusive = true }
        launchSingleTop = true
    }
}
