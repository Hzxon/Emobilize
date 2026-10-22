package ayodong.emobilize.ui.tasks

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import ayodong.emobilize.domain.model.Category
import ayodong.emobilize.domain.model.AppSettings
import ayodong.emobilize.domain.model.DateRange
import ayodong.emobilize.domain.model.EventType
import ayodong.emobilize.domain.model.FabAction
import ayodong.emobilize.domain.model.FilterKey
import ayodong.emobilize.domain.model.Priority
import ayodong.emobilize.domain.model.ReminderOption
import ayodong.emobilize.domain.model.ScheduleMode
import ayodong.emobilize.domain.model.Task
import ayodong.emobilize.domain.model.TaskStatus
import ayodong.emobilize.domain.model.TimeBlock
import ayodong.emobilize.domain.model.deadlineToInputDate
import ayodong.emobilize.domain.model.displayTimeToInput
import ayodong.emobilize.domain.model.filterKeyFor
import ayodong.emobilize.domain.model.filteredTasks
import ayodong.emobilize.domain.model.formatInputDate
import ayodong.emobilize.domain.model.formatInputTime
import ayodong.emobilize.domain.model.hoursToInput
import ayodong.emobilize.domain.model.inputToHours
import ayodong.emobilize.domain.model.isoDate
import ayodong.emobilize.domain.model.rangeIsValid
import ayodong.emobilize.domain.usecase.AddTaskUseCase
import ayodong.emobilize.domain.usecase.DeleteTaskUseCase
import ayodong.emobilize.domain.usecase.DeleteTimeBlockUseCase
import ayodong.emobilize.domain.usecase.GetCategoriesUseCase
import ayodong.emobilize.domain.usecase.GetAppSettingsUseCase
import ayodong.emobilize.domain.usecase.GetScheduleUseCase
import ayodong.emobilize.domain.usecase.GetTasksUseCase
import ayodong.emobilize.domain.usecase.UpdateCategoryUseCase
import ayodong.emobilize.domain.usecase.UpdateAppSettingsUseCase
import ayodong.emobilize.domain.usecase.PlaceTimeBlockUseCase
import ayodong.emobilize.domain.usecase.SetTaskStatusUseCase
import ayodong.emobilize.domain.usecase.UpdateTaskUseCase
import java.time.LocalDate

class TaskDraft {
    var name by mutableStateOf("")
    var category by mutableStateOf<Category?>(null)
    var priority by mutableStateOf<Priority?>(null)
    var reminder by mutableStateOf(ReminderOption.None)
    var includeSchedule by mutableStateOf(false)
    var scheduleMode by mutableStateOf(ScheduleMode.Deadline)
    var date by mutableStateOf("")
    var time by mutableStateOf("")
    var startDate by mutableStateOf("")
    var startTime by mutableStateOf("09:00")
    var endDate by mutableStateOf("")
    var endTime by mutableStateOf("10:00")
    var eventType by mutableStateOf(EventType.Task)
    var eventEndTime by mutableStateOf<String?>(null)
    var minutes by mutableStateOf("")
    var description by mutableStateOf("")

    fun rangeValid(): Boolean = rangeIsValid(startDate, startTime, endDate, endTime)

    fun titleError(): String? = if (name.isBlank()) "Enter a title" else null

    fun descriptionError(): String? = if (description.isBlank()) "Enter a description" else null

    fun minutesError(): String? = when {
        minutes.isEmpty() -> null
        !minutes.all(Char::isDigit) -> "Use whole numbers only"
        minutes.toIntOrNull() !in 1..1440 -> "Enter 1 to 1440 minutes"
        else -> null
    }

    fun eventTimeError(): String? =
        if (startTime.isNotBlank() && endTime.isNotBlank() && inputToHours(endTime) <= inputToHours(startTime)) {
            "End time must be after start time"
        } else {
            null
        }
}

class TasksViewModel(
    private val getTasks: GetTasksUseCase,
    private val addTask: AddTaskUseCase,
    private val updateTask: UpdateTaskUseCase,
    private val deleteTask: DeleteTaskUseCase,
    private val setTaskStatus: SetTaskStatusUseCase,
    private val getSchedule: GetScheduleUseCase,
    private val placeTimeBlock: PlaceTimeBlockUseCase,
    private val deleteTimeBlock: DeleteTimeBlockUseCase,
    private val getCategories: GetCategoriesUseCase,
    private val saveCategory: UpdateCategoryUseCase,
    private val getAppSettings: GetAppSettingsUseCase,
    private val saveAppSettings: UpdateAppSettingsUseCase,
) : ViewModel() {
    private val initialBoard = getTasks()

    var tasks by mutableStateOf(initialBoard.tasks)
        private set
    var filter by mutableStateOf(FilterKey.All)
        private set
    var done by mutableStateOf(initialBoard.doneIds)
        private set
    var inProgress by mutableStateOf(initialBoard.progressIds)
        private set
    var blocks by mutableStateOf(getSchedule())
        private set
    var categories by mutableStateOf(getCategories())
        private set
    var appSettings by mutableStateOf(getAppSettings())
        private set
    var selectedBlockId by mutableStateOf<Long?>(null)
        private set
    var pendingAction by mutableStateOf<FabAction?>(null)
        private set
    var selectedTaskId by mutableStateOf<Long?>(null)
        private set
    var editingId by mutableStateOf<Long?>(null)
        private set
    var calendarTaskId by mutableStateOf<Long?>(null)
        private set
    var calendarBlockId by mutableStateOf<Long?>(null)
        private set
    var notificationsEnabled by mutableStateOf(true)
        private set

    val addDraft = TaskDraft()
    val editDraft = TaskDraft()

    fun visibleTasks(today: LocalDate = LocalDate.now()): List<Task> = filteredTasks(tasks, filter, today)

    fun selectFilter(next: FilterKey) {
        filter = next
    }

    fun clearBlockSelection() {
        selectedBlockId = null
    }

    fun toggleBlockSelection(id: Long) {
        selectedBlockId = if (selectedBlockId == id) null else id
    }

    fun deleteSelectedBlock() {
        val id = selectedBlockId ?: return
        deleteTimeBlock(id)
        selectedBlockId = null
        blocks = getSchedule()
        cancelPending()
    }

    private var addPrepared = false
    private var calendarAddPrepared = false

    fun prepareAdd() {
        if (addPrepared) return
        resetAddDraft(EventType.Task)
        pendingAction = null
        selectedTaskId = null
        addPrepared = true
    }

    fun closeAdd() {
        addPrepared = false
    }

    fun prepareCalendarAdd(): Boolean {
        if (calendarAddPrepared && calendarTaskId == null && calendarBlockId == null) return true
        resetAddDraft(EventType.Event)
        calendarTaskId = null
        calendarBlockId = null
        pendingAction = null
        selectedTaskId = null
        calendarAddPrepared = true
        return true
    }

    fun loadCalendarTask(taskId: Long): Boolean {
        if (calendarTaskId == taskId) return true
        val task = tasks.find { it.id == taskId } ?: return false
        val date = deadlineToInputDate(task.deadline)
        val startTime = task.range?.startTime ?: displayTimeToInput(task.time).ifBlank {
            if (task.eventType == EventType.Event) "" else "09:00"
        }
        resetAddDraft(task.eventType)
        addDraft.name = task.name
        addDraft.description = task.description
        addDraft.category = task.category
        addDraft.priority = task.priority
        addDraft.includeSchedule = true
        addDraft.scheduleMode = if (task.range != null) ScheduleMode.Range else ScheduleMode.Deadline
        addDraft.date = date
        addDraft.time = displayTimeToInput(task.time)
        addDraft.startDate = task.range?.startDate ?: date
        addDraft.startTime = startTime
        addDraft.endDate = task.range?.endDate ?: date
        addDraft.endTime = task.range?.endTime ?: displayTimeToInput(task.eventEndTime).ifBlank {
            if (task.eventType == EventType.Event) "" else startTime
        }
        addDraft.reminder = task.reminder
        addDraft.minutes = task.estimateMinutes?.toString().orEmpty()
        calendarTaskId = task.id
        calendarBlockId = null
        pendingAction = null
        selectedTaskId = null
        calendarAddPrepared = false
        return true
    }

    fun loadCalendarBlock(blockId: Long, dateIso: String): Boolean {
        if (calendarBlockId == blockId) return true
        val date = runCatching { LocalDate.parse(dateIso) }.getOrNull() ?: return false
        val block = blocks[date]?.find { it.id == blockId } ?: return false
        val iso = isoDate(date)
        resetAddDraft(EventType.RegularSchedule)
        addDraft.name = block.title
        addDraft.category = block.category
        addDraft.includeSchedule = true
        addDraft.scheduleMode = ScheduleMode.Range
        addDraft.date = iso
        addDraft.time = hoursToInput(block.start)
        addDraft.startDate = iso
        addDraft.startTime = hoursToInput(block.start)
        addDraft.endDate = iso
        addDraft.endTime = hoursToInput(block.end)
        addDraft.reminder = block.reminder
        calendarTaskId = null
        calendarBlockId = block.id
        pendingAction = null
        selectedTaskId = null
        calendarAddPrepared = false
        return true
    }

    fun closeCalendarForm() {
        calendarAddPrepared = false
        calendarTaskId = null
        calendarBlockId = null
    }

    fun loadTaskEdit(taskId: Long): Boolean {
        if (editingId == taskId) return true
        val task = tasks.find { it.id == taskId } ?: return false
        val date = deadlineToInputDate(task.deadline)
        editDraft.name = task.name
        editDraft.description = task.description
        editDraft.category = task.category
        editDraft.priority = task.priority
        editDraft.includeSchedule = task.range != null || task.deadline.isNotBlank()
        editDraft.scheduleMode = if (task.range != null) ScheduleMode.Range else ScheduleMode.Deadline
        editDraft.date = if (task.deadline.isBlank()) "" else date
        editDraft.time = displayTimeToInput(task.time)
        editDraft.startDate = task.range?.startDate ?: date
        editDraft.startTime = task.range?.startTime ?: "09:00"
        editDraft.endDate = task.range?.endDate ?: date
        editDraft.endTime = task.range?.endTime ?: displayTimeToInput(task.time).ifBlank { "17:00" }
        editDraft.eventType = task.eventType
        editDraft.eventEndTime = task.eventEndTime
        editDraft.reminder = task.reminder
        editDraft.minutes = task.estimateMinutes?.toString().orEmpty()
        editingId = task.id
        pendingAction = null
        selectedTaskId = null
        return true
    }

    fun closeEdit() {
        editingId = null
    }

    fun onFabAction(action: FabAction) {
        if (action == FabAction.Add) return
        pendingAction = action
        selectedTaskId = null
    }

    fun selectTask(id: Long) {
        if (pendingAction == null) return
        selectedTaskId = id
    }

    fun toggleTaskSelection(id: Long) {
        if (pendingAction == null) return
        selectedTaskId = if (selectedTaskId == id) null else id
    }

    fun clearTaskSelection() {
        selectedTaskId = null
    }

    fun cancelPending() {
        pendingAction = null
        selectedTaskId = null
    }

    fun confirmAction() {
        val id = selectedTaskId
        when {
            pendingAction == FabAction.Delete && id != null -> {
                deleteTask(id)
                reloadTasks()
                pendingAction = null
                selectedTaskId = null
            }
            else -> {
                pendingAction = null
                selectedTaskId = null
            }
        }
    }

    fun applyStatus(status: TaskStatus) {
        val id = selectedTaskId ?: return
        setTaskStatus(id, status)
        reloadTasks()
        pendingAction = null
        selectedTaskId = null
    }

    fun markDone(id: Long) {
        setTaskStatus(id, TaskStatus.Done)
        reloadTasks()
    }

    fun changeNotifications(enabled: Boolean) {
        notificationsEnabled = enabled
    }

    fun canSubmit(draft: TaskDraft): Boolean {
        if (draft.titleError() != null || draft.minutesError() != null) return false
        if (!draft.includeSchedule) return true
        return if (draft.scheduleMode == ScheduleMode.Deadline) {
            draft.date.isNotBlank()
        } else {
            draft.rangeValid()
        }
    }

    fun canSubmitCalendar(draft: TaskDraft): Boolean {
        if (draft.titleError() != null) return false
        return when (draft.eventType) {
            EventType.Event -> draft.date.isNotBlank() && draft.eventTimeError() == null
            EventType.Task -> canSubmit(draft)
            EventType.RegularSchedule -> draft.rangeValid()
        }
    }

    fun submitCalendar(): Boolean {
        if (!canSubmitCalendar(addDraft)) return false
        if (addDraft.eventType == EventType.RegularSchedule) {
            val date = runCatching { LocalDate.parse(addDraft.startDate) }.getOrNull() ?: return false
            placeTimeBlock(
                TimeBlock(
                    id = calendarBlockId ?: System.currentTimeMillis(),
                    title = addDraft.name.trim(),
                    start = inputToHours(addDraft.startTime),
                    end = inputToHours(addDraft.endTime),
                    category = addDraft.category ?: workCategory(),
                    reminder = addDraft.reminder,
                ),
                date,
            )
            reloadSchedule()
        } else {
            val id = calendarTaskId ?: System.currentTimeMillis()
            val task = draftToCalendarTask(addDraft, id)
            if (calendarTaskId == null) addTask(task) else updateTask(task)
            reloadTasks()
        }
        return true
    }

    fun submitAdd(): Boolean {
        if (addDraft.titleError() != null || addDraft.descriptionError() != null || addDraft.minutesError() != null) return false
        if (addDraft.includeSchedule && addDraft.scheduleMode == ScheduleMode.Range && !addDraft.rangeValid()) return false
        addTask(draftToTask(addDraft, System.currentTimeMillis()))
        reloadTasks()
        return true
    }

    fun submitEdit(): Boolean {
        val id = editingId ?: return false
        if (!canSubmit(editDraft) || editDraft.descriptionError() != null) return false
        updateTask(draftToTask(editDraft, id))
        reloadTasks()
        return true
    }

    fun updateCategory(category: Category) {
        if (saveCategory(category) == null) return
        categories = getCategories()
        addDraft.category = addDraft.category?.let { current -> categories.find { it.id == current.id } }
        editDraft.category = editDraft.category?.let { current -> categories.find { it.id == current.id } }
        reloadTasks()
        reloadSchedule()
    }

    fun saveSettings(settings: AppSettings, editedCategories: List<Category>) {
        editedCategories.forEach { category ->
            if (categories.find { it.id == category.id } != category) saveCategory(category)
        }
        categories = getCategories()
        addDraft.category = addDraft.category?.let { current -> categories.find { it.id == current.id } }
        editDraft.category = editDraft.category?.let { current -> categories.find { it.id == current.id } }
        reloadTasks()
        reloadSchedule()
        saveAppSettings(settings)
        appSettings = getAppSettings()
    }

    private fun workCategory(): Category = categories.find { it.id == Category.Work.id } ?: categories.first()

    private fun defaultTaskCategory(): Category? = appSettings.defaultCategoryId?.let { id ->
        categories.find { it.id == id }
    }


    private fun resetAddDraft(type: EventType) {
        val today = isoDate(LocalDate.now())
        val taskOnly = type == EventType.Task
        addDraft.name = ""
        addDraft.time = ""
        addDraft.category = if (taskOnly) defaultTaskCategory() else workCategory()
        addDraft.priority = if (taskOnly) appSettings.defaultPriority else Priority.Medium
        addDraft.includeSchedule = !taskOnly
        addDraft.date = if (taskOnly) "" else today
        addDraft.scheduleMode = ScheduleMode.Deadline
        addDraft.startDate = today
        addDraft.startTime = "09:00"
        addDraft.endDate = today
        addDraft.endTime = "10:00"
        addDraft.eventType = type
        addDraft.eventEndTime = null
        addDraft.reminder = ReminderOption.None
        addDraft.minutes = ""
        addDraft.description = ""
    }

    private fun reloadTasks() {
        val board = getTasks()
        tasks = board.tasks
        done = board.doneIds
        inProgress = board.progressIds
    }

    private fun reloadSchedule() {
        blocks = getSchedule()
    }

    private fun draftToTask(draft: TaskDraft, id: Long): Task {
        val isRange = draft.includeSchedule && draft.scheduleMode == ScheduleMode.Range && draft.rangeValid()
        val hasDeadline = draft.includeSchedule && draft.scheduleMode == ScheduleMode.Deadline && draft.date.isNotBlank()
        val scheduleDate = when {
            isRange -> draft.endDate
            hasDeadline -> draft.date
            else -> ""
        }
        val scheduleTime = when {
            isRange -> draft.endTime
            hasDeadline -> draft.time
            else -> ""
        }
        return Task(
            id = id,
            name = draft.name.trim(),
            description = draft.description.trim(),
            category = draft.category,
            deadline = if (scheduleDate.isBlank()) "" else formatInputDate(scheduleDate),
            time = scheduleTime.takeIf { it.isNotBlank() }?.let(::formatInputTime),
            filterKey = if (scheduleDate.isBlank()) FilterKey.All else filterKeyFor(scheduleDate),
            priority = draft.priority,
            range = if (isRange) {
                DateRange(draft.startDate, draft.startTime, draft.endDate, draft.endTime)
            } else {
                null
            },
            eventType = draft.eventType,
            eventEndTime = draft.eventEndTime,
            reminder = draft.reminder,
            estimateMinutes = draft.minutes.toIntOrNull(),
        )
    }

    private fun draftToCalendarTask(draft: TaskDraft, id: Long): Task {
        if (draft.eventType == EventType.Event) {
            return Task(
                id = id,
                name = draft.name.trim(),
                description = draft.description.trim(),
                category = draft.category,
                deadline = formatInputDate(draft.date),
                time = draft.startTime.takeIf { it.isNotBlank() }?.let(::formatInputTime),
                filterKey = filterKeyFor(draft.date),
                priority = draft.priority,
                eventType = EventType.Event,
                eventEndTime = draft.endTime.takeIf { it.isNotBlank() }?.let(::formatInputTime),
                reminder = draft.reminder,
                estimateMinutes = draft.minutes.toIntOrNull(),
            )
        }
        return draftToTask(draft, id).copy(eventType = EventType.Task, eventEndTime = null)
    }
}
