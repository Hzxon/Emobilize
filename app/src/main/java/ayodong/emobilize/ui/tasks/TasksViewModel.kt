package ayodong.emobilize.ui.tasks

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import ayodong.emobilize.model.Category
import ayodong.emobilize.model.DateRange
import ayodong.emobilize.model.EventType
import ayodong.emobilize.model.FabAction
import ayodong.emobilize.model.TimeBlock
import ayodong.emobilize.model.hoursToInput
import ayodong.emobilize.model.inputToHours
import ayodong.emobilize.model.FilterKey
import ayodong.emobilize.model.Priority
import ayodong.emobilize.model.ScheduleMode
import ayodong.emobilize.model.Task
import ayodong.emobilize.model.TaskStatus
import ayodong.emobilize.model.deadlineToInputDate
import ayodong.emobilize.model.displayTimeToInput
import ayodong.emobilize.model.filterKeyFor
import ayodong.emobilize.model.filteredTasks
import ayodong.emobilize.model.formatInputDate
import ayodong.emobilize.model.formatInputTime
import ayodong.emobilize.model.isoDate
import ayodong.emobilize.model.rangeIsValid
import ayodong.emobilize.model.sampleTasks
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

data class SavedSchedule(
    val block: TimeBlock,
    val date: LocalDate,
)

class TasksViewModel : ViewModel() {
    var tasks by mutableStateOf(sampleTasks())
        private set
    var filter by mutableStateOf(FilterKey.All)
        private set
    var done by mutableStateOf(setOf(1L))
        private set
    var inProgress by mutableStateOf(setOf<Long>())
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
                tasks = tasks.filter { it.id != id }
                done = done - id
                inProgress = inProgress - id
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
        if (status == TaskStatus.Done) {
            done = done + id
            inProgress = inProgress - id
        } else {
            inProgress = inProgress + id
            done = done - id
        }
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

    fun submitCalendar(): SavedSchedule? {
        if (!canSubmitCalendar(addDraft)) return null
        val schedule = if (addDraft.eventType == EventType.RegularSchedule) {
            val date = runCatching { LocalDate.parse(addDraft.startDate) }.getOrNull() ?: return null
            SavedSchedule(
                block = TimeBlock(
                    id = calendarBlockId ?: System.currentTimeMillis(),
                    title = addDraft.name.trim(),
                    start = inputToHours(addDraft.startTime),
                    end = inputToHours(addDraft.endTime),
                    category = addDraft.category ?: Category.Work,
                ),
                date = date,
            )
        } else {
            val id = calendarTaskId ?: System.currentTimeMillis()
            val task = draftToCalendarTask(addDraft, id)
            tasks = if (calendarTaskId == null) tasks + task else tasks.map { if (it.id == id) task else it }
            null
        }
        dismissCalendar()
        return schedule
    }

    fun submitAdd() {
        if (addDraft.name.isBlank()) return
        if (addDraft.includeSchedule && addDraft.scheduleMode == ScheduleMode.Range && !addDraft.rangeValid()) return
        tasks = tasks + draftToTask(addDraft, System.currentTimeMillis())
        showAdd = false
    }

    fun submitEdit() {
        val id = editingId ?: return
        if (!canSubmit(editDraft)) return
        val updated = draftToTask(editDraft, id)
        tasks = tasks.map { if (it.id == id) updated else it }
        showEdit = false
        editingId = null
    }

    fun dismissAdd() {
        showAdd = false
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
        addDraft.category = if (taskOnly) null else Category.Work
        addDraft.priority = if (taskOnly) null else Priority.Medium
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
