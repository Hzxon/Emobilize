package ayodong.emobilize.feature.calendar.ui

import androidx.lifecycle.ViewModel
import ayodong.emobilize.core.domain.AppSettingsRepository
import ayodong.emobilize.core.domain.CategoryRepository
import ayodong.emobilize.core.domain.ScheduleRepository
import ayodong.emobilize.core.domain.TaskRepository
import ayodong.emobilize.core.model.Category
import ayodong.emobilize.core.model.EventType
import ayodong.emobilize.core.model.ScheduleMode
import ayodong.emobilize.core.model.deadlineToInputDate
import ayodong.emobilize.core.model.displayTimeToInput
import ayodong.emobilize.core.model.hoursToInput
import ayodong.emobilize.core.model.isoDate
import ayodong.emobilize.core.ui.form.TaskDraft
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate

sealed interface CalendarFormTarget {
    data object New : CalendarFormTarget
    data class EditTask(val taskId: Long) : CalendarFormTarget
    data class EditBlock(val blockId: Long, val date: String) : CalendarFormTarget
}

// Backs the Calendar add/edit form. One instance per open form, scoped to its navigation entry.
class CalendarFormViewModel(
    private val tasks: TaskRepository,
    private val schedule: ScheduleRepository,
    private val categoryRepository: CategoryRepository,
    settings: AppSettingsRepository,
    private val target: CalendarFormTarget,
) : ViewModel() {
    val draft = TaskDraft()
    val categories: StateFlow<List<Category>> = categoryRepository.categories
    val editing: Boolean = target != CalendarFormTarget.New

    // False when the task or block to edit no longer exists; the screen closes itself.
    val found: Boolean

    init {
        val defaults = settings.settings.value
        found = when (target) {
            CalendarFormTarget.New -> {
                draft.reset(
                    type = EventType.Event,
                    taskCategory = defaults.defaultCategoryId?.let { id -> categories.value.find { it.id == id } },
                    calendarCategory = workCategory(),
                    taskPriority = defaults.defaultPriority,
                )
                true
            }
            is CalendarFormTarget.EditTask -> loadTask(target.taskId)
            is CalendarFormTarget.EditBlock -> loadBlock(target.blockId, target.date)
        }
    }

    fun canSubmit(): Boolean = draft.canSubmitCalendar()

    fun submit(): Boolean {
        if (!draft.canSubmitCalendar()) return false
        if (draft.eventType == EventType.RegularSchedule) {
            val date = runCatching { LocalDate.parse(draft.startDate) }.getOrNull() ?: return false
            val blockId = (target as? CalendarFormTarget.EditBlock)?.blockId ?: System.currentTimeMillis()
            schedule.place(draft.toTimeBlock(blockId, workCategory()), date)
        } else {
            val taskId = (target as? CalendarFormTarget.EditTask)?.taskId
            val task = draft.toCalendarTask(taskId ?: System.currentTimeMillis())
            if (taskId == null) tasks.add(task) else tasks.update(task)
        }
        return true
    }

    private fun workCategory(): Category = categories.value.let { all -> all.find { it.id == Category.Work.id } ?: all.first() }

    private fun loadTask(taskId: Long): Boolean {
        val task = tasks.board.value.tasks.find { it.id == taskId } ?: return false
        val date = deadlineToInputDate(task.deadline)
        val startTime = task.range?.startTime ?: displayTimeToInput(task.time).ifBlank {
            if (task.eventType == EventType.Event) "" else "09:00"
        }
        draft.reset(task.eventType, taskCategory = null, calendarCategory = workCategory(), taskPriority = null)
        draft.name = task.name
        draft.description = task.description
        draft.category = task.category
        draft.priority = task.priority
        draft.includeSchedule = true
        draft.scheduleMode = if (task.range != null) ScheduleMode.Range else ScheduleMode.Deadline
        draft.date = date
        draft.time = displayTimeToInput(task.time)
        draft.startDate = task.range?.startDate ?: date
        draft.startTime = startTime
        draft.endDate = task.range?.endDate ?: date
        draft.endTime = task.range?.endTime ?: displayTimeToInput(task.eventEndTime).ifBlank {
            if (task.eventType == EventType.Event) "" else startTime
        }
        draft.reminder = task.reminder
        draft.minutes = task.estimateMinutes?.toString().orEmpty()
        return true
    }

    private fun loadBlock(blockId: Long, dateIso: String): Boolean {
        val date = runCatching { LocalDate.parse(dateIso) }.getOrNull() ?: return false
        val block = schedule.blocks.value[date]?.find { it.id == blockId } ?: return false
        val iso = isoDate(date)
        draft.reset(EventType.RegularSchedule, taskCategory = null, calendarCategory = workCategory(), taskPriority = null)
        draft.name = block.title
        draft.category = block.category
        draft.includeSchedule = true
        draft.scheduleMode = ScheduleMode.Range
        draft.date = iso
        draft.time = hoursToInput(block.start)
        draft.startDate = iso
        draft.startTime = hoursToInput(block.start)
        draft.endDate = iso
        draft.endTime = hoursToInput(block.end)
        draft.reminder = block.reminder
        return true
    }
}
