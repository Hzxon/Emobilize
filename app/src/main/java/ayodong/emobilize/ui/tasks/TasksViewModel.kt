package ayodong.emobilize.ui.tasks

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import ayodong.emobilize.model.Category
import ayodong.emobilize.model.DateRange
import ayodong.emobilize.model.FabAction
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
    var category by mutableStateOf(Category.Work)
    var priority by mutableStateOf(Priority.Medium)
    var scheduleMode by mutableStateOf(ScheduleMode.Deadline)
    var date by mutableStateOf("")
    var time by mutableStateOf("")
    var startDate by mutableStateOf("")
    var startTime by mutableStateOf("09:00")
    var endDate by mutableStateOf("")
    var endTime by mutableStateOf("10:00")

    fun rangeValid(): Boolean = rangeIsValid(startDate, startTime, endDate, endTime)
}

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
    var showEdit by mutableStateOf(false)
        private set
    var editingId by mutableStateOf<Long?>(null)
        private set

    val addDraft = TaskDraft()
    val editDraft = TaskDraft()

    fun visibleTasks(today: LocalDate = LocalDate.now()): List<Task> = filteredTasks(tasks, filter, today)

    fun selectFilter(next: FilterKey) {
        filter = next
    }

    fun onFabAction(action: FabAction) {
        if (action == FabAction.Add) {
            val today = isoDate(LocalDate.now())
            addDraft.name = ""
            addDraft.time = ""
            addDraft.category = Category.Work
            addDraft.priority = Priority.Medium
            addDraft.date = today
            addDraft.scheduleMode = ScheduleMode.Deadline
            addDraft.startDate = today
            addDraft.startTime = "09:00"
            addDraft.endDate = today
            addDraft.endTime = "10:00"
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
                    editDraft.scheduleMode = if (task.range != null) ScheduleMode.Range else ScheduleMode.Deadline
                    editDraft.date = date
                    editDraft.time = displayTimeToInput(task.time)
                    editDraft.startDate = task.range?.startDate ?: date
                    editDraft.startTime = task.range?.startTime ?: "09:00"
                    editDraft.endDate = task.range?.endDate ?: date
                    editDraft.endTime = task.range?.endTime ?: displayTimeToInput(task.time).ifBlank { "17:00" }
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
        return if (draft.scheduleMode == ScheduleMode.Deadline) {
            draft.date.isNotBlank()
        } else {
            draft.rangeValid()
        }
    }

    fun submitAdd() {
        if (!canSubmit(addDraft)) return
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

    fun dismissEdit() {
        showEdit = false
        editingId = null
    }

    private fun draftToTask(draft: TaskDraft, id: Long): Task {
        val isRange = draft.scheduleMode == ScheduleMode.Range
        val scheduleDate = if (isRange) draft.endDate else draft.date
        val scheduleTime = if (isRange) draft.endTime else draft.time
        return Task(
            id = id,
            name = draft.name.trim(),
            category = draft.category,
            deadline = formatInputDate(scheduleDate),
            time = scheduleTime.takeIf { it.isNotBlank() }?.let(::formatInputTime),
            filterKey = filterKeyFor(scheduleDate),
            priority = draft.priority,
            range = if (isRange) {
                DateRange(draft.startDate, draft.startTime, draft.endDate, draft.endTime)
            } else {
                null
            },
        )
    }
}
