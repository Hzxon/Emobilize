package ayodong.emobilize.core.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import ayodong.emobilize.core.di.LocalAppContainer
import ayodong.emobilize.core.ui.dock.BottomDock
import ayodong.emobilize.core.ui.dock.FabAction
import ayodong.emobilize.core.ui.dock.NavTab
import ayodong.emobilize.core.ui.dock.RadialMenu
import ayodong.emobilize.core.ui.theme.LocalPalette
import ayodong.emobilize.feature.calendar.di.CalendarModule
import ayodong.emobilize.feature.calendar.ui.CalendarScreen
import ayodong.emobilize.feature.calendar.ui.CalendarViewModel
import ayodong.emobilize.feature.tasks.di.TasksModule
import ayodong.emobilize.feature.tasks.ui.TasksScreen
import ayodong.emobilize.feature.tasks.ui.TasksViewModel

// The two-tab frame: top bar, Tasks and Calendar pages, bottom dock and radial menu.
// It wires each feature screen to its ViewModel and to the shared action mode in ShellViewModel.
@Composable
internal fun MainShell(
    navController: NavHostController,
    initialTab: NavTab,
    shell: ShellViewModel,
) {
    val container = LocalAppContainer.current
    val tasksViewModel: TasksViewModel = viewModel(factory = TasksModule.tasksViewModel(container))
    val calendarViewModel: CalendarViewModel = viewModel(factory = CalendarModule.calendarViewModel(container))
    val shellState by shell.uiState.collectAsState()
    val tasksState by tasksViewModel.uiState.collectAsState()
    val calendarState by calendarViewModel.uiState.collectAsState()
    var menuOpen by rememberSaveable { mutableStateOf(false) }
    var tab by rememberSaveable { mutableStateOf(initialTab) }
    val colors = LocalPalette.current
    val backEntry by navController.currentBackStackEntryAsState()
    val route = backEntry?.destination?.route
    val overlayOpen = route != AppRoute.Tasks && route != AppRoute.Calendar
    BackHandler(
        enabled = !overlayOpen && (menuOpen || shellState.pendingAction != null || shellState.selectedBlockId != null),
    ) {
        when {
            menuOpen -> menuOpen = false
            else -> {
                shell.clearBlockSelection()
                shell.cancelPending()
            }
        }
    }
    BackHandler(
        enabled = !overlayOpen &&
            tab == NavTab.Calendar &&
            !menuOpen &&
            shellState.pendingAction == null &&
            shellState.selectedBlockId == null,
    ) {
        tab = NavTab.Tasks
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
                    .padding(start = 24.dp, end = 18.dp, top = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Spacer(Modifier.weight(1f))
                HeaderButton(
                    icon = Icons.Outlined.Notifications,
                    contentDescription = "Reminders",
                    onClick = {
                        menuOpen = false
                        navController.navigate(AppRoute.Reminders) { launchSingleTop = true }
                    },
                )
                HeaderButton(
                    icon = Icons.Outlined.Settings,
                    contentDescription = "Settings",
                    onClick = {
                        menuOpen = false
                        navController.navigate(AppRoute.Settings) { launchSingleTop = true }
                    },
                )
            }
            Box(
                Modifier
                    .weight(1f)
                    .fillMaxSize(),
            ) {
                val onTasks = tab == NavTab.Tasks
                TabPage(visible = onTasks, hiddenShift = (-16).dp) {
                    TasksScreen(
                        state = tasksState,
                        pending = shellState.pendingAction,
                        selectedTaskId = shellState.selectedTaskId,
                        onSelectFilter = tasksViewModel::selectFilter,
                        onOpenTask = { taskId ->
                            navController.navigate(AppRoute.taskDetail(taskId))
                        },
                        onEditTask = { taskId ->
                            navController.openEditor(shell, taskId)
                        },
                        onSelectTask = shell::selectTask,
                        onCancelPending = shell::cancelPending,
                        onConfirmDelete = shell::confirmAction,
                        onApplyStatus = shell::applyStatus,
                    )
                }
                TabPage(visible = !onTasks, hiddenShift = 16.dp) {
                    CalendarScreen(
                        tasks = calendarState.tasks,
                        blocks = calendarState.blocks,
                        defaultView = calendarState.defaultView,
                        weekStart = calendarState.weekStart,
                        pending = shellState.pendingAction,
                        selectedTaskId = shellState.selectedTaskId,
                        selectedBlockId = shellState.selectedBlockId,
                        onSelectTask = { task ->
                            when (shellState.pendingAction) {
                                FabAction.Edit -> {
                                    shell.clearBlockSelection()
                                    navController.openForm(shell, AppRoute.calendarTask(task.id))
                                }
                                FabAction.Delete -> {
                                    shell.clearBlockSelection()
                                    shell.toggleTaskSelection(task.id)
                                }
                                else -> Unit
                            }
                        },
                        onSelectBlock = { date, block ->
                            when (shellState.pendingAction) {
                                FabAction.Edit -> {
                                    shell.clearBlockSelection()
                                    navController.openForm(shell, AppRoute.calendarBlock(block.id, date.toString()))
                                }
                                FabAction.Delete -> {
                                    shell.clearTaskSelection()
                                    shell.toggleBlockSelection(block.id)
                                }
                                else -> shell.toggleBlockSelection(block.id)
                            }
                        },
                        onDelete = {
                            if (shellState.selectedTaskId != null) shell.confirmAction()
                            if (shellState.selectedBlockId != null) shell.deleteSelectedBlock()
                        },
                        onCancel = {
                            shell.clearBlockSelection()
                            shell.cancelPending()
                        },
                    )
                }
            }
            BottomDock(
                tab = tab,
                onTab = { next ->
                    menuOpen = false
                    shell.clearBlockSelection()
                    if (next == NavTab.Calendar && shellState.pendingAction == FabAction.Update) {
                        shell.cancelPending()
                    }
                    tab = next
                },
            )
        }
        RadialMenu(
            tab = tab,
            menuOpen = menuOpen,
            onMenuOpenChange = { menuOpen = it },
            onAction = { action ->
                menuOpen = false
                shell.clearBlockSelection()
                when {
                    tab == NavTab.Calendar && action == FabAction.Add -> navController.openForm(shell, AppRoute.CalendarAdd)
                    action == FabAction.Add -> navController.openForm(shell, AppRoute.AddTask)
                    else -> shell.onFabAction(action)
                }
            },
            modifier = Modifier.zIndex(2f),
        )
    }
}

@Composable
private fun TabPage(
    visible: Boolean,
    hiddenShift: Dp,
    content: @Composable () -> Unit,
) {
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 240, easing = FastOutSlowInEasing),
        label = "tabAlpha",
    )
    val shift by animateDpAsState(
        targetValue = if (visible) 0.dp else hiddenShift,
        animationSpec = tween(durationMillis = 240, easing = FastOutSlowInEasing),
        label = "tabShift",
    )
    Box(
        Modifier
            .fillMaxSize()
            .zIndex(if (visible) 1f else 0f)
            .then(
                if (visible) {
                    // Claims every touch inside the visible page so taps on blank space never reach the faded page below.
                    // It sits before the offset so it covers the full width while the page slides in.
                    Modifier.pointerInput(Unit) { awaitPointerEventScope { while (true) awaitPointerEvent() } }
                } else {
                    Modifier.clearAndSetSemantics {}
                },
            )
            .offset(x = shift)
            .graphicsLayer { this.alpha = alpha },
    ) {
        content()
    }
}

// Round secondary button for the top-right header actions, matching the calendar's month arrows.
@Composable
private fun HeaderButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
) {
    val colors = LocalPalette.current
    Box(
        Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(colors.scheme.secondary)
            .border(1.dp, colors.scheme.border, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = colors.text,
            modifier = Modifier.size(20.dp),
        )
    }
}
