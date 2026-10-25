package ayodong.emobilize.feature.tasks.ui

import androidx.lifecycle.ViewModel
import ayodong.emobilize.core.domain.AppSettingsRepository
import ayodong.emobilize.core.domain.CategoryRepository
import ayodong.emobilize.core.domain.TaskRepository
import ayodong.emobilize.core.model.Category
import ayodong.emobilize.core.model.EventType
import ayodong.emobilize.core.model.ScheduleMode
import ayodong.emobilize.core.model.deadlineToInputDate
import ayodong.emobilize.core.model.displayTimeToInput
import ayodong.emobilize.core.ui.form.TaskDraft
import kotlinx.coroutines.flow.StateFlow

// Backs the Add card (taskId == null) and the Edit sheet. One instance per open form,
// scoped to its navigation entry, so the draft starts fresh every time the form opens.
class TaskFormViewModel(
    private val tasks: TaskRepository,
    categories: CategoryRepository,
    settings: AppSettingsRepository,
    private val taskId: Long?,
) : ViewModel() {
    val draft = TaskDraft()
    val categories: StateFlow<List<Category>> = categories.categories

    // False when the task to edit no longer exists; the screen closes itself.
    val found: Boolean

    init {
        found = if (taskId == null) {
            val all = categories.categories.value
            val defaults = settings.settings.value
            draft.reset(
                type = EventType.Task,
                taskCategory = defaults.defaultCategoryId?.let { id -> all.find { it.id == id } },
                calendarCategory = all.find { it.id == Category.Work.id } ?: all.first(),
                taskPriority = defaults.defaultPriority,
            )
            true
        } else {
            loadForEdit(taskId)
        }
    }

    fun canSubmitEdit(): Boolean = draft.canSubmitTask()

    fun submitAdd(): Boolean {
        if (draft.titleError() != null || draft.descriptionError() != null || draft.minutesError() != null) return false
        if (draft.includeSchedule && draft.scheduleMode == ScheduleMode.Range && !draft.rangeValid()) return false
        tasks.add(draft.toTask(System.currentTimeMillis()))
        return true
    }

    fun submitEdit(): Boolean {
        val id = taskId ?: return false
        if (!draft.canSubmitTask() || draft.descriptionError() != null) return false
        tasks.update(draft.toTask(id))
        return true
    }

    private fun loadForEdit(id: Long): Boolean {
        val task = tasks.board.value.tasks.find { it.id == id } ?: return false
        val date = deadlineToInputDate(task.deadline)
        draft.name = task.name
        draft.description = task.description
        draft.category = task.category
        draft.priority = task.priority
        draft.includeSchedule = task.range != null || task.deadline.isNotBlank()
        draft.scheduleMode = if (task.range != null) ScheduleMode.Range else ScheduleMode.Deadline
        draft.date = if (task.deadline.isBlank()) "" else date
        draft.time = displayTimeToInput(task.time)
        draft.startDate = task.range?.startDate ?: date
        draft.startTime = task.range?.startTime ?: "09:00"
        draft.endDate = task.range?.endDate ?: date
        draft.endTime = task.range?.endTime ?: displayTimeToInput(task.time).ifBlank { "17:00" }
        draft.eventType = task.eventType
        draft.eventEndTime = task.eventEndTime
        draft.reminder = task.reminder
        draft.minutes = task.estimateMinutes?.toString().orEmpty()
        return true
    }
}
