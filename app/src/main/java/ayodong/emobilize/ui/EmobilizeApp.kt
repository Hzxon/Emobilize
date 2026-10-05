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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import ayodong.emobilize.domain.model.FabAction
import ayodong.emobilize.domain.model.NavTab
import ayodong.emobilize.domain.model.ThemeMode
import ayodong.emobilize.domain.model.WeekStart
import java.time.DayOfWeek
import ayodong.emobilize.ui.calendar.CalendarScreen
import ayodong.emobilize.ui.dock.BottomDock
import ayodong.emobilize.ui.dock.RadialMenu
import ayodong.emobilize.ui.tasks.AddTaskCard
import ayodong.emobilize.ui.tasks.CalendarFormSheet
import ayodong.emobilize.ui.tasks.TaskFormSheet
import ayodong.emobilize.ui.settings.SettingsScreen
import ayodong.emobilize.ui.splash.SplashScreen
import ayodong.emobilize.ui.tasks.TasksScreen
import ayodong.emobilize.ui.tasks.TasksViewModel
import ayodong.emobilize.ui.theme.EmobilizeTheme
import ayodong.emobilize.ui.theme.LocalPalette
import ayodong.emobilize.ui.theme.NeutralDark
import ayodong.emobilize.ui.theme.NeutralLight
import ayodong.emobilize.ui.theme.menuColor

@Composable
fun EmobilizeApp(viewModel: TasksViewModel) {
    var tabName by rememberSaveable { mutableStateOf(NavTab.Tasks.name) }
    var menuOpen by rememberSaveable { mutableStateOf(false) }
    var splashDone by rememberSaveable { mutableStateOf(false) }
    val tab = if (tabName == NavTab.Calendar.name) NavTab.Calendar else NavTab.Tasks
    val systemDark = isSystemInDarkTheme()
    val isDark = when (viewModel.appSettings.themeMode) {
        ThemeMode.System -> systemDark
        ThemeMode.Light -> false
        ThemeMode.Dark -> true
    }
    val palette = if (isDark) NeutralDark else NeutralLight

    EmobilizeTheme(palette) {
        val colors = LocalPalette.current
        if (!splashDone) {
            SplashScreen(onFinished = { splashDone = true })
            return@EmobilizeTheme
        }
        BackHandler(enabled = viewModel.showSettings || viewModel.showAdd || viewModel.showCalendar || viewModel.showEdit || menuOpen || viewModel.pendingAction != null || viewModel.selectedBlockId != null) {
            when {
                viewModel.showSettings -> viewModel.dismissSettings()
                viewModel.showAdd -> viewModel.dismissAdd()
                viewModel.showCalendar -> viewModel.dismissCalendar()
                viewModel.showEdit -> viewModel.dismissEdit()
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
                                viewModel.openSettings()
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
                        TasksScreen(viewModel)
                    } else {
                        CalendarScreen(
                            tasks = viewModel.tasks,
                            blocks = viewModel.blocks,
                            defaultView = viewModel.appSettings.defaultCalendarView,
                            weekStart = if (viewModel.appSettings.weekStart == WeekStart.Monday) DayOfWeek.MONDAY else DayOfWeek.SUNDAY,
                            pending = viewModel.pendingAction,
                            selectedTaskId = viewModel.selectedTaskId,
                            selectedBlockId = viewModel.selectedBlockId,
                            onSelectTask = { task ->
                                when (viewModel.pendingAction) {
                                    FabAction.Edit -> {
                                        viewModel.clearBlockSelection()
                                        viewModel.beginCalendarEdit(task)
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
                                        viewModel.showBlockEditor(block, date)
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
                        tabName = next.name
                        menuOpen = false
                        viewModel.clearBlockSelection()
                        if (next == NavTab.Calendar && viewModel.pendingAction == FabAction.Update) {
                            viewModel.cancelPending()
                        }
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
                    if (tab == NavTab.Calendar && action == FabAction.Add) {
                        viewModel.beginCalendarAdd()
                    } else {
                        viewModel.onFabAction(action)
                    }
                },
                modifier = Modifier.zIndex(2f),
            )
            if (viewModel.showAdd) {
                Box(Modifier.zIndex(4f)) {
                    AddTaskCard(
                        draft = viewModel.addDraft,
                        categories = viewModel.categories,
                        onDismiss = viewModel::dismissAdd,
                        onSubmit = viewModel::submitAdd,
                    )
                }
            }
            if (viewModel.showCalendar) {
                val accent = colors.menuColor(if (viewModel.calendarEditing) FabAction.Edit else FabAction.Add)
                Box(Modifier.zIndex(4f)) {
                    CalendarFormSheet(
                        draft = viewModel.addDraft,
                        categories = viewModel.categories,
                        lockType = viewModel.calendarEditing,
                        canSubmit = viewModel.canSubmitCalendar(viewModel.addDraft),
                        saveColor = accent,
                        submitLabel = if (viewModel.calendarEditing) "Save Changes" else "Save",
                        onDismiss = viewModel::dismissCalendar,
                        onSubmit = viewModel::submitCalendar,
                    )
                }
            }
            if (viewModel.showSettings) {
                Box(Modifier.zIndex(4f)) {
                    SettingsScreen(
                        appSettings = viewModel.appSettings,
                        categories = viewModel.categories,
                        onSave = viewModel::saveSettings,
                        onClose = viewModel::dismissSettings,
                    )
                }
            }
            if (viewModel.showEdit) {
                Box(Modifier.zIndex(4f)) {
                    TaskFormSheet(
                        title = "Edit Task",
                        submitLabel = "Save Changes",
                        draft = viewModel.editDraft,
                        categories = viewModel.categories,
                        canSubmit = viewModel.canSubmit(viewModel.editDraft),
                        saveColor = colors.menuColor(FabAction.Edit),
                        onDismiss = viewModel::dismissEdit,
                        onSubmit = viewModel::submitEdit,
                    )
                }
            }
        }
    }
}
