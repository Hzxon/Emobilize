package ayodong.emobilize.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import ayodong.emobilize.model.FabAction
import ayodong.emobilize.model.NavTab
import ayodong.emobilize.model.placeSchedule
import ayodong.emobilize.model.sampleBlocks
import ayodong.emobilize.ui.calendar.CalendarScreen
import ayodong.emobilize.ui.dock.BottomDock
import ayodong.emobilize.ui.dock.RadialMenu
import ayodong.emobilize.ui.dock.ThemeOrb
import ayodong.emobilize.ui.tasks.CalendarFormSheet
import ayodong.emobilize.ui.tasks.TaskFormSheet
import ayodong.emobilize.ui.tasks.TasksScreen
import ayodong.emobilize.ui.tasks.TasksViewModel
import ayodong.emobilize.ui.theme.EmobilizeTheme
import ayodong.emobilize.ui.theme.LocalPalette
import ayodong.emobilize.ui.theme.NeutralDark
import ayodong.emobilize.ui.theme.NeutralLight
import ayodong.emobilize.ui.theme.menuColor

@Composable
fun EmobilizeApp(viewModel: TasksViewModel = viewModel()) {
    var themeId by rememberSaveable { mutableIntStateOf(1) }
    var tabName by rememberSaveable { mutableStateOf(NavTab.Tasks.name) }
    var menuOpen by rememberSaveable { mutableStateOf(false) }
    var blocks by remember { mutableStateOf(sampleBlocks()) }
    var selectedBlockId by remember { mutableStateOf<Long?>(null) }
    val tab = if (tabName == NavTab.Calendar.name) NavTab.Calendar else NavTab.Tasks
    val palette = if (themeId == 1) NeutralLight else NeutralDark

    EmobilizeTheme(palette) {
        val colors = LocalPalette.current
        BackHandler(enabled = viewModel.showAdd || viewModel.showCalendar || viewModel.showEdit || menuOpen || viewModel.pendingAction != null || selectedBlockId != null) {
            when {
                viewModel.showAdd -> viewModel.dismissAdd()
                viewModel.showCalendar -> viewModel.dismissCalendar()
                viewModel.showEdit -> viewModel.dismissEdit()
                menuOpen -> menuOpen = false
                else -> {
                    selectedBlockId = null
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
                Box(
                    Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .statusBarsPadding(),
                ) {
                    if (tab == NavTab.Tasks) {
                        TasksScreen(viewModel)
                    } else {
                        CalendarScreen(
                            tasks = viewModel.tasks,
                            blocks = blocks,
                            pending = viewModel.pendingAction,
                            selectedTaskId = viewModel.selectedTaskId,
                            selectedBlockId = selectedBlockId,
                            onSelectTask = { task ->
                                when (viewModel.pendingAction) {
                                    FabAction.Edit -> {
                                        selectedBlockId = null
                                        viewModel.beginCalendarEdit(task)
                                    }
                                    FabAction.Delete -> {
                                        selectedBlockId = null
                                        viewModel.toggleTaskSelection(task.id)
                                    }
                                    else -> Unit
                                }
                            },
                            onSelectBlock = { date, block ->
                                when (viewModel.pendingAction) {
                                    FabAction.Edit -> {
                                        selectedBlockId = null
                                        viewModel.showBlockEditor(block, date)
                                    }
                                    FabAction.Delete -> {
                                        viewModel.clearTaskSelection()
                                        selectedBlockId = if (selectedBlockId == block.id) null else block.id
                                    }
                                    else -> selectedBlockId = if (selectedBlockId == block.id) null else block.id
                                }
                            },
                            onDelete = {
                                val blockId = selectedBlockId
                                if (viewModel.selectedTaskId != null) viewModel.confirmAction()
                                if (blockId != null) {
                                    blocks = blocks
                                        .mapValues { (_, list) -> list.filter { it.id != blockId } }
                                        .filterValues { it.isNotEmpty() }
                                    selectedBlockId = null
                                    viewModel.cancelPending()
                                }
                            },
                            onCancel = {
                                selectedBlockId = null
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
                        selectedBlockId = null
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
                    selectedBlockId = null
                    if (tab == NavTab.Calendar && action == FabAction.Add) {
                        viewModel.beginCalendarAdd()
                    } else {
                        viewModel.onFabAction(action)
                    }
                },
                modifier = Modifier.zIndex(2f),
            )
            ThemeOrb(
                themeId = themeId,
                onClick = { themeId = if (themeId == 1) 2 else 1 },
                modifier = Modifier
                    .zIndex(3f)
                    .align(Alignment.TopEnd)
                    .statusBarsPadding()
                    .padding(top = 6.dp, end = 18.dp),
            )
            if (viewModel.showAdd) {
                val addColor = colors.menuColor(FabAction.Add)
                Box(Modifier.zIndex(4f)) {
                    TaskFormSheet(
                        title = "New Task",
                        submitLabel = "Add Task",
                        draft = viewModel.addDraft,
                        canSubmit = viewModel.canSubmit(viewModel.addDraft),
                        saveColor = addColor,
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
                        lockType = viewModel.calendarEditing,
                        canSubmit = viewModel.canSubmitCalendar(viewModel.addDraft),
                        saveColor = accent,
                        submitLabel = if (viewModel.calendarEditing) "Save Changes" else "Save",
                        onDismiss = viewModel::dismissCalendar,
                        onSubmit = {
                            val schedule = viewModel.submitCalendar()
                            if (schedule != null) {
                                blocks = placeSchedule(blocks, schedule.block, schedule.date)
                            }
                        },
                    )
                }
            }
            if (viewModel.showEdit) {
                Box(Modifier.zIndex(4f)) {
                    TaskFormSheet(
                        title = "Edit Task",
                        submitLabel = "Save Changes",
                        draft = viewModel.editDraft,
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
