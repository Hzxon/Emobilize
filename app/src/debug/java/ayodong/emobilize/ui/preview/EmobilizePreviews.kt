package ayodong.emobilize.ui.preview

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
import ayodong.emobilize.data.repository.InMemoryScheduleRepository
import ayodong.emobilize.data.repository.InMemoryTaskRepository
import ayodong.emobilize.domain.model.AppSettings
import ayodong.emobilize.domain.model.Category
import ayodong.emobilize.domain.model.EventType
import ayodong.emobilize.domain.model.FilterKey
import ayodong.emobilize.domain.model.NavTab
import ayodong.emobilize.domain.model.Priority
import ayodong.emobilize.domain.model.ReminderOption
import ayodong.emobilize.domain.model.Task
import ayodong.emobilize.domain.model.WeekStart
import ayodong.emobilize.domain.repository.AppSettingsRepository
import ayodong.emobilize.domain.repository.CategoryRepository
import ayodong.emobilize.domain.usecase.AddTaskUseCase
import ayodong.emobilize.domain.usecase.DeleteTaskUseCase
import ayodong.emobilize.domain.usecase.DeleteTimeBlockUseCase
import ayodong.emobilize.domain.usecase.GetAppSettingsUseCase
import ayodong.emobilize.domain.usecase.GetCategoriesUseCase
import ayodong.emobilize.domain.usecase.GetScheduleUseCase
import ayodong.emobilize.domain.usecase.GetTasksUseCase
import ayodong.emobilize.domain.usecase.PlaceTimeBlockUseCase
import ayodong.emobilize.domain.usecase.SetTaskStatusUseCase
import ayodong.emobilize.domain.usecase.UpdateAppSettingsUseCase
import ayodong.emobilize.domain.usecase.UpdateCategoryUseCase
import ayodong.emobilize.domain.usecase.UpdateTaskUseCase
import ayodong.emobilize.ui.calendar.CalendarScreen
import ayodong.emobilize.ui.dock.BottomDock
import ayodong.emobilize.ui.notifications.ExpandableReminderCard
import ayodong.emobilize.ui.settings.SettingsScreen
import ayodong.emobilize.ui.splash.SplashScreen
import ayodong.emobilize.ui.tasks.TasksScreen
import ayodong.emobilize.ui.tasks.TasksViewModel
import ayodong.emobilize.ui.theme.AppPalette
import ayodong.emobilize.ui.theme.EmobilizeTheme
import ayodong.emobilize.ui.theme.LocalPalette
import ayodong.emobilize.ui.theme.NeutralDark
import ayodong.emobilize.ui.theme.NeutralLight
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

@Preview(name = "Splash", group = "Screens", device = Devices.PIXEL_6, showBackground = true, backgroundColor = PreviewBackgroundLight)
@Composable
private fun SplashPreview() {
    PreviewFrame(NeutralLight) {
        SplashScreen(onFinished = {})
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
        val viewModel = remember { previewViewModel() }
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
                        TasksScreen(viewModel, onEditTask = {})
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
                            pending = null,
                            selectedTaskId = null,
                            selectedBlockId = null,
                            onSelectTask = {},
                            onSelectBlock = { _, _ -> },
                            onDelete = {},
                            onCancel = {},
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
                    appSettings = viewModel.appSettings,
                    categories = viewModel.categories,
                    onSave = { settings, categories ->
                        viewModel.saveSettings(settings, categories)
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

private fun previewViewModel(): TasksViewModel {
    val tasks = InMemoryTaskRepository()
    val schedule = InMemoryScheduleRepository()
    val categories = PreviewCategoryRepository()
    val settings = PreviewSettingsRepository()
    return TasksViewModel(
        getTasks = GetTasksUseCase(tasks),
        addTask = AddTaskUseCase(tasks),
        updateTask = UpdateTaskUseCase(tasks),
        deleteTask = DeleteTaskUseCase(tasks),
        setTaskStatus = SetTaskStatusUseCase(tasks),
        getSchedule = GetScheduleUseCase(schedule),
        placeTimeBlock = PlaceTimeBlockUseCase(schedule),
        deleteTimeBlock = DeleteTimeBlockUseCase(schedule),
        getCategories = GetCategoriesUseCase(categories),
        saveCategory = UpdateCategoryUseCase(categories, tasks, schedule),
        getAppSettings = GetAppSettingsUseCase(settings),
        saveAppSettings = UpdateAppSettingsUseCase(settings),
    )
}

private class PreviewCategoryRepository : CategoryRepository {
    private val items = Category.defaults.toMutableList()

    override fun all(): List<Category> = items.toList()

    override fun update(category: Category): Category? {
        val index = items.indexOfFirst { it.id == category.id }
        if (index < 0) return null
        items[index] = category
        return category
    }
}

private class PreviewSettingsRepository : AppSettingsRepository {
    private var current = AppSettings()

    override fun get(): AppSettings = current

    override fun update(settings: AppSettings) {
        current = settings
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
