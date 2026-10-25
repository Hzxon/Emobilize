package ayodong.emobilize.core.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import ayodong.emobilize.core.di.LocalAppContainer
import ayodong.emobilize.core.ui.dock.FabAction
import ayodong.emobilize.core.ui.dock.NavTab
import ayodong.emobilize.core.ui.dock.RadialMenu
import ayodong.emobilize.core.ui.theme.AppMetrics
import ayodong.emobilize.core.ui.theme.LocalPalette
import ayodong.emobilize.core.ui.theme.ShadcnRadius
import ayodong.emobilize.feature.calendar.di.CalendarModule
import ayodong.emobilize.feature.calendar.ui.CalendarScreen
import ayodong.emobilize.feature.calendar.ui.CalendarViewModel
import ayodong.emobilize.feature.tasks.di.TasksModule
import ayodong.emobilize.feature.tasks.ui.TaskDetailScreen
import ayodong.emobilize.feature.tasks.ui.TasksScreen
import ayodong.emobilize.feature.tasks.ui.TasksViewModel

// The two-tab frame: top bar, Tasks and Calendar pages, bottom dock, radial menu and the task detail sheet.
// It wires each feature screen to its ViewModel and to the shared action mode in ShellViewModel.
@OptIn(ExperimentalSharedTransitionApi::class)
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
    // The open detail sheet. It is drawn inside this layout, not as a dialog route, so the card can
    // morph into it with a shared transition. shownDetailId keeps the id while the sheet fades out.
    var detailId by rememberSaveable { mutableStateOf<Long?>(null) }
    var shownDetailId by remember { mutableStateOf<Long?>(null) }
    detailId?.let { shownDetailId = it }
    val colors = LocalPalette.current
    val backEntry by navController.currentBackStackEntryAsState()
    val route = backEntry?.destination?.route
    val overlayOpen = route != AppRoute.Tasks && route != AppRoute.Calendar
    BackHandler(enabled = !overlayOpen && detailId != null) { detailId = null }
    BackHandler(
        enabled = !overlayOpen && detailId == null && (menuOpen || shellState.pendingAction != null || shellState.selectedBlockId != null),
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
            detailId == null &&
            tab == NavTab.Calendar &&
            !menuOpen &&
            shellState.pendingAction == null &&
            shellState.selectedBlockId == null,
    ) {
        tab = NavTab.Tasks
    }
    SharedTransitionLayout(
        Modifier
            .fillMaxSize()
            .background(colors.surface),
    ) {
        val cardShape = RoundedCornerShape(AppMetrics.cardRadius)
        // Each tab draws its own header (clock or route title) with these two buttons on its right.
        val headerActions: @Composable () -> Unit = {
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
        Column(Modifier.fillMaxSize().statusBarsPadding()) {
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
                        onOpenTask = { taskId -> detailId = taskId },
                        onEditTask = { taskId ->
                            navController.openEditor(shell, taskId)
                        },
                        onSelectTask = shell::selectTask,
                        onCancelPending = shell::cancelPending,
                        onConfirmDelete = shell::confirmAction,
                        onApplyStatus = shell::applyStatus,
                        onAddTask = { navController.openForm(shell, AppRoute.AddTask) },
                        onCompleteTask = tasksViewModel::completeTask,
                        onDeleteTask = tasksViewModel::deleteTask,
                        onUndo = tasksViewModel::undo,
                        onDismissUndo = tasksViewModel::dismissUndo,
                        headerActions = headerActions,
                        cardContainer = { taskId, content ->
                            SharedCard(
                                key = "task-$taskId",
                                hidden = taskId == detailId,
                                shape = cardShape,
                                content = content,
                            )
                        },
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
                        onAdd = { navController.openForm(shell, AppRoute.CalendarAdd) },
                        headerActions = headerActions,
                    )
                }
            }
            // No tab bar: this strip only keeps content clear of the floating key, which switches screens.
            Spacer(
                Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .height(AppMetrics.navHeight),
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
            onSwitchTab = {
                val next = if (tab == NavTab.Tasks) NavTab.Calendar else NavTab.Tasks
                shell.clearBlockSelection()
                if (next == NavTab.Calendar && shellState.pendingAction == FabAction.Update) {
                    shell.cancelPending()
                }
                tab = next
            },
            modifier = Modifier.zIndex(2f),
        )
        AnimatedVisibility(
            visible = detailId != null,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.zIndex(3f),
        ) {
            val taskId = detailId ?: shownDetailId ?: 0L
            TaskDetailScreen(
                task = tasksState.task(taskId),
                taskId = taskId,
                isDone = taskId in tasksState.doneIds,
                inProgress = taskId in tasksState.progressIds,
                onBack = { detailId = null },
                onEdit = { navController.openEditor(shell, taskId) },
                panelModifier = Modifier.sharedBounds(
                    rememberSharedContentState(key = "task-$taskId"),
                    animatedVisibilityScope = this,
                    clipInOverlayDuringTransition = OverlayClip(RoundedCornerShape(ShadcnRadius.xl)),
                ),
            )
        }
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

// Square outlined button for the top-right header actions.
@Composable
private fun HeaderButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
) {
    val colors = LocalPalette.current
    Box(
        Modifier
            .size(44.dp)
            .background(colors.card)
            .border(2.dp, colors.text)
            .clickable(onClick = onClick, role = Role.Button),
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

// The open card hides in the list while its bounds morph into the detail sheet. A separate
// function so the call resolves to the plain AnimatedVisibility, not the ColumnScope one.
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedTransitionScope.SharedCard(
    key: String,
    hidden: Boolean,
    shape: RoundedCornerShape,
    content: @Composable () -> Unit,
) {
    AnimatedVisibility(
        visible = !hidden,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        Box(
            Modifier.sharedBounds(
                rememberSharedContentState(key = key),
                animatedVisibilityScope = this,
                clipInOverlayDuringTransition = OverlayClip(shape),
            ),
        ) {
            content()
        }
    }
}
