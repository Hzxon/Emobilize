package ayodong.emobilize.core.navigation

import androidx.activity.OnBackPressedDispatcher
import androidx.activity.OnBackPressedDispatcherOwner
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleRegistry
import ayodong.emobilize.core.data.FakeAppSettingsRepository
import ayodong.emobilize.core.data.FakeCategoryRepository
import ayodong.emobilize.core.data.InMemoryScheduleRepository
import ayodong.emobilize.core.data.InMemoryTaskRepository
import ayodong.emobilize.core.model.Category
import ayodong.emobilize.core.model.EventType
import ayodong.emobilize.core.model.FilterKey
import ayodong.emobilize.core.model.Priority
import ayodong.emobilize.core.model.ReminderOption
import ayodong.emobilize.core.model.Task
import ayodong.emobilize.core.model.WeekStart
import ayodong.emobilize.core.ui.dock.BottomDock
import ayodong.emobilize.core.ui.dock.NavTab
import ayodong.emobilize.core.ui.theme.AppPalette
import ayodong.emobilize.core.ui.theme.EmobilizeTheme
import ayodong.emobilize.core.ui.theme.LocalPalette
import ayodong.emobilize.core.ui.theme.NeutralDark
import ayodong.emobilize.core.ui.theme.NeutralLight
import ayodong.emobilize.feature.calendar.ui.CalendarScreen
import ayodong.emobilize.feature.reminders.ui.ExpandableReminderCard
import ayodong.emobilize.feature.settings.domain.SaveSettingsUseCase
import ayodong.emobilize.feature.settings.ui.SettingsScreen
import ayodong.emobilize.feature.tasks.ui.TaskDetailScreen
import ayodong.emobilize.feature.tasks.ui.TasksScreen
import ayodong.emobilize.feature.tasks.ui.TasksUiState
import java.time.DayOfWeek

private const val PreviewBackgroundLight = 0xFFFFFFFF
private const val PreviewBackgroundDark = 0xFF0A0A0A

@Preview(name = "Tasks", group = "Screens", device = Devices.PIXEL_6, showBackground = true, backgroundColor = PreviewBackgroundLight)
@Composable
private fun TasksPreview() {
    ScreenPreview(NeutralLight, NavTab.Tasks)
}

@Preview(name = "Tasks dark", group = "Screens", device = Devices.PIXEL_6, showBackground = true, backgroundColor = PreviewBackgroundDark)
@Composable
private fun TasksDarkPreview() {
    ScreenPreview(NeutralDark, NavTab.Tasks)
}

@Preview(name = "Calendar", group = "Screens", device = Devices.PIXEL_6, showBackground = true, backgroundColor = PreviewBackgroundLight)
@Composable
private fun CalendarPreview() {
    ScreenPreview(NeutralLight, NavTab.Calendar)
}

@Preview(name = "Settings", group = "Screens", device = Devices.PIXEL_6, showBackground = true, backgroundColor = PreviewBackgroundLight)
@Composable
private fun SettingsPreview() {
    ScreenPreview(NeutralLight, NavTab.Tasks, showSettings = true)
}

@Preview(name = "Task detail", group = "Screens", device = Devices.PIXEL_6, showBackground = true, backgroundColor = PreviewBackgroundLight)
@Composable
private fun TaskDetailPreview() {
    val data = remember { PreviewData() }
    val board = data.tasks.board.value
    PreviewFrame(NeutralLight) {
        TaskDetailScreen(
            task = board.tasks.find { it.id == 1L },
            taskId = 1,
            isDone = 1L in board.doneIds,
            inProgress = 1L in board.progressIds,
            onBack = {},
            onEdit = {},
        )
    }
}

@Preview(name = "Notification", group = "Notifications", device = Devices.PIXEL_6, showBackground = true, backgroundColor = PreviewBackgroundLight)
@Composable
private fun NotificationPreview() {
    ScreenPreview(NeutralLight, NavTab.Tasks, bannerExpanded = false)
}

@Preview(name = "Notification expanded", group = "Notifications", device = Devices.PIXEL_6, showBackground = true, backgroundColor = PreviewBackgroundLight)
@Composable
private fun NotificationExpandedPreview() {
    ScreenPreview(NeutralLight, NavTab.Tasks, bannerExpanded = true)
}

@Preview(name = "Notification dark", group = "Notifications", device = Devices.PIXEL_6, showBackground = true, backgroundColor = PreviewBackgroundDark)
@Composable
private fun NotificationDarkPreview() {
    ScreenPreview(NeutralDark, NavTab.Tasks, bannerExpanded = true)
}

@Composable
private fun ScreenPreview(
    palette: AppPalette,
    tab: NavTab,
    showSettings: Boolean = false,
    bannerExpanded: Boolean? = null,
) {
    PreviewFrame(palette) {
        val data = remember { PreviewData() }
        val appSettings by data.settings.settings.collectAsState()
        val categories by data.categories.categories.collectAsState()
        var currentTab by remember { mutableStateOf(tab) }
        var settingsOpen by remember { mutableStateOf(showSettings) }
        val colors = LocalPalette.current
        Box(
            Modifier
                .fillMaxSize()
                .background(colors.surface),
        ) {
            Column(Modifier.fillMaxSize()) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, end = 18.dp, top = 8.dp, bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Spacer(Modifier.weight(1f))
                    Box(
                        Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .clickable { settingsOpen = true },
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
                        .fillMaxWidth(),
                ) {
                    if (currentTab == NavTab.Tasks) {
                        TasksScreen(
                            state = data.tasksState(),
                            pending = null,
                            selectedTaskId = null,
                            onSelectFilter = {},
                            onOpenTask = {},
                            onEditTask = {},
                            onSelectTask = {},
                            onCancelPending = {},
                            onConfirmDelete = {},
                            onApplyStatus = {},
                            onAddTask = {},
                            onCompleteTask = {},
                            onDeleteTask = {},
                            onUndo = {},
                            onDismissUndo = {},
                        )
                    } else {
                        CalendarScreen(
                            tasks = data.tasks.board.value.tasks,
                            blocks = data.schedule.blocks.value,
                            defaultView = appSettings.defaultCalendarView,
                            weekStart = if (appSettings.weekStart == WeekStart.Monday) {
                                DayOfWeek.MONDAY
                            } else {
                                DayOfWeek.SUNDAY
                            },
                            pending = null,
                            selectedTaskId = null,
                            selectedBlockId = null,
                            onSelectTask = {},
                            onSelectBlock = { _, _ -> },
                            onDelete = {},
                            onCancel = {},
                            onAdd = {},
                        )
                    }
                }
                BottomDock(
                    tab = currentTab,
                    onTab = { currentTab = it },
                )
            }
            if (bannerExpanded != null) {
                ExpandableReminderCard(
                    task = previewReminder,
                    onMarkDone = {},
                    onReschedule = {},
                    initiallyExpanded = bannerExpanded,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .zIndex(2f)
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                )
            }
            if (settingsOpen) {
                SettingsScreen(
                    appSettings = appSettings,
                    categories = categories,
                    onSave = { settings, categories ->
                        data.saveSettings(settings, categories)
                        settingsOpen = false
                    },
                    onClose = { settingsOpen = false },
                )
            }
        }
    }
}

@Composable
private fun PreviewFrame(
    palette: AppPalette,
    content: @Composable () -> Unit,
) {
    val backOwner = remember { PreviewBackOwner() }
    EmobilizeTheme(palette) {
        CompositionLocalProvider(LocalOnBackPressedDispatcherOwner provides backOwner) {
            Box(Modifier.fillMaxSize()) {
                content()
            }
        }
    }
}

// Builds every screen from the same in-memory and fake repositories the unit tests use.
private class PreviewData {
    val tasks = InMemoryTaskRepository()
    val schedule = InMemoryScheduleRepository()
    val categories = FakeCategoryRepository()
    val settings = FakeAppSettingsRepository()
    val saveSettings = SaveSettingsUseCase(categories, tasks, schedule, settings)

    fun tasksState(): TasksUiState = tasks.board.value.let { board ->
        TasksUiState(board.tasks, doneIds = board.doneIds, progressIds = board.progressIds)
    }
}

private class PreviewBackOwner : OnBackPressedDispatcherOwner {
    override val lifecycle: Lifecycle = LifecycleRegistry(this)
    override val onBackPressedDispatcher = OnBackPressedDispatcher()

    init {
        (lifecycle as LifecycleRegistry).currentState = Lifecycle.State.RESUMED
    }
}

private val previewReminder = Task(
    id = 42,
    name = "Team standup",
    category = Category.Work,
    deadline = "Today",
    time = "10:30 AM",
    filterKey = FilterKey.Today,
    priority = Priority.Medium,
    eventType = EventType.Event,
    reminder = ReminderOption.TenMinutesBefore,
)
