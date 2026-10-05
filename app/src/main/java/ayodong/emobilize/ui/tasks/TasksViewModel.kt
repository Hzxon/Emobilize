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

    fun rangeValid(): Boolean = rangeIsValid(startDate, startTime, endDate, endTime)
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
    var showSettings by mutableStateOf(false)
        private set
    var selectedBlockId by mutableStateOf<Long?>(null)
        private set
    var pendingAction by mutableStateOf<FabAction?>(null)
        private set
    var selectedTaskId by mutableStateOf<Long?>(null)
        private set
    var showAdd by mutableStateOf(false)
        private set
    var showCalendar by mutableStateOf(false)
        private set
    var showEdit by mutableStateOf(false)
        private set
    var editingId by mutableStateOf<Long?>(null)
        private set
    var calendarTaskId by mutableStateOf<Long?>(null)
        private set
    var calendarBlockId by mutableStateOf<Long?>(null)
        private set

    val calendarEditing: Boolean
        get() = calendarTaskId != null || calendarBlockId != null

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

    fun showBlockEditor(block: TimeBlock, date: LocalDate) {
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
        calendarTaskId = null
        calendarBlockId = block.id
        pendingAction = null
        selectedTaskId = null
        showCalendar = true
    }

    fun beginCalendarAdd() {
        resetAddDraft(EventType.Event)
        calendarTaskId = null
        calendarBlockId = null
        pendingAction = null
        selectedTaskId = null
        showCalendar = true
    }

    fun beginCalendarEdit(task: Task) {
        val date = deadlineToInputDate(task.deadline)
        val startTime = task.range?.startTime ?: displayTimeToInput(task.time).ifBlank { "09:00" }
        resetAddDraft(task.eventType)
        addDraft.name = task.name
        addDraft.category = task.category
        addDraft.priority = task.priority
        addDraft.includeSchedule = true
        addDraft.scheduleMode = if (task.range != null) ScheduleMode.Range else ScheduleMode.Deadline
        addDraft.date = date
        addDraft.time = displayTimeToInput(task.time)
        addDraft.startDate = task.range?.startDate ?: date
        addDraft.startTime = startTime
        addDraft.endDate = task.range?.endDate ?: date
        addDraft.endTime = task.range?.endTime ?: displayTimeToInput(task.eventEndTime).ifBlank { startTime }
        calendarTaskId = task.id
        calendarBlockId = null
        pendingAction = null
        selectedTaskId = null
        showCalendar = true
    }

    fun onFabAction(action: FabAction) {
        if (action == FabAction.Add) {
            resetAddDraft(EventType.Task)
            showAdd = true
        } else {
            pendingAction = action
            selectedTaskId = null
        }
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
            pendingAction == FabAction.Edit && id != null -> {
                val task = tasks.find { it.id == id }
                if (task != null) {
                    val date = deadlineToInputDate(task.deadline)
                    editDraft.name = task.name
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
                    editingId = task.id
                    showEdit = true
                }
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

    fun canSubmit(draft: TaskDraft): Boolean {
        if (draft.name.isBlank()) return false
        if (!draft.includeSchedule) return true
        return if (draft.scheduleMode == ScheduleMode.Deadline) {
            draft.date.isNotBlank()
        } else {
            draft.rangeValid()
        }
    }

    fun canSubmitCalendar(draft: TaskDraft): Boolean {
        if (draft.name.isBlank()) return false
        return when (draft.eventType) {
            EventType.Event -> draft.date.isNotBlank()
            EventType.Task -> canSubmit(draft)
            EventType.RegularSchedule -> draft.rangeValid()
        }
    }

    fun submitCalendar() {
        if (!canSubmitCalendar(addDraft)) return
        if (addDraft.eventType == EventType.RegularSchedule) {
            val date = runCatching { LocalDate.parse(addDraft.startDate) }.getOrNull() ?: return
            placeTimeBlock(
                TimeBlock(
                    id = calendarBlockId ?: System.currentTimeMillis(),
                    title = addDraft.name.trim(),
                    start = inputToHours(addDraft.startTime),
                    end = inputToHours(addDraft.endTime),
                    category = addDraft.category ?: workCategory(),
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
        dismissCalendar()
    }

    fun submitAdd() {
        if (addDraft.name.isBlank()) return
        if (addDraft.includeSchedule && addDraft.scheduleMode == ScheduleMode.Range && !addDraft.rangeValid()) return
        addTask(draftToTask(addDraft, System.currentTimeMillis()))
        reloadTasks()
        showAdd = false
    }

    fun submitEdit() {
        val id = editingId ?: return
        if (!canSubmit(editDraft)) return
        updateTask(draftToTask(editDraft, id))
        reloadTasks()
        showEdit = false
        editingId = null
    }

    fun openSettings() {
        showSettings = true
    }

    fun dismissSettings() {
        showSettings = false
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

    fun dismissAdd() {
        showAdd = false
    }

    private fun workCategory(): Category = categories.find { it.id == Category.Work.id } ?: categories.first()

    private fun defaultTaskCategory(): Category? = appSettings.defaultCategoryId?.let { id ->
        categories.find { it.id == id }
    }

    fun dismissCalendar() {
        showCalendar = false
        calendarTaskId = null
        calendarBlockId = null
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
    }

    fun dismissEdit() {
        showEdit = false
        editingId = null
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
        )
    }

    private fun draftToCalendarTask(draft: TaskDraft, id: Long): Task {
        if (draft.eventType == EventType.Event) {
            return Task(
                id = id,
                name = draft.name.trim(),
                category = draft.category,
                deadline = formatInputDate(draft.date),
                time = draft.startTime.takeIf { it.isNotBlank() }?.let(::formatInputTime),
                filterKey = filterKeyFor(draft.date),
                priority = draft.priority,
                eventType = EventType.Event,
                eventEndTime = draft.endTime.takeIf { it.isNotBlank() }?.let(::formatInputTime),
            )
        }
        return draftToTask(draft, id).copy(eventType = EventType.Task, eventEndTime = null)
    }
}
