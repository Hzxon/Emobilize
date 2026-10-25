package ayodong.emobilize.core.ui.form

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ayodong.emobilize.core.model.Category
import ayodong.emobilize.core.model.DateRange
import ayodong.emobilize.core.model.EventType
import ayodong.emobilize.core.model.FilterKey
import ayodong.emobilize.core.model.Priority
import ayodong.emobilize.core.model.ReminderOption
import ayodong.emobilize.core.model.ScheduleMode
import ayodong.emobilize.core.model.Task
import ayodong.emobilize.core.model.TimeBlock
import ayodong.emobilize.core.model.filterKeyFor
import ayodong.emobilize.core.model.formatInputDate
import ayodong.emobilize.core.model.formatInputTime
import ayodong.emobilize.core.model.inputToHours
import ayodong.emobilize.core.model.isoDate
import ayodong.emobilize.core.model.rangeIsValid
import java.time.LocalDate

// Editable form state shared by the Tasks forms (add, edit) and the Calendar form.
// Text fields write into it on every keystroke, so it stays Compose state instead of a UiState copy.
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

    fun canSubmitTask(): Boolean {
        if (titleError() != null || minutesError() != null) return false
        if (!includeSchedule) return true
        return if (scheduleMode == ScheduleMode.Deadline) date.isNotBlank() else rangeValid()
    }

    fun canSubmitCalendar(): Boolean {
        if (titleError() != null) return false
        return when (eventType) {
            EventType.Event -> date.isNotBlank() && eventTimeError() == null
            EventType.Task -> canSubmitTask()
            EventType.RegularSchedule -> rangeValid()
        }
    }

    // Clears the form for a new entry. A plain task starts unscheduled with the user's defaults;
    // calendar entries start scheduled for today.
    fun reset(type: EventType, taskCategory: Category?, calendarCategory: Category, taskPriority: Priority?) {
        val today = isoDate(LocalDate.now())
        val taskOnly = type == EventType.Task
        name = ""
        time = ""
        category = if (taskOnly) taskCategory else calendarCategory
        priority = if (taskOnly) taskPriority else Priority.Medium
        includeSchedule = !taskOnly
        date = if (taskOnly) "" else today
        scheduleMode = ScheduleMode.Deadline
        startDate = today
        startTime = "09:00"
        endDate = today
        endTime = "10:00"
        eventType = type
        eventEndTime = null
        reminder = ReminderOption.None
        minutes = ""
        description = ""
    }

    fun toTask(id: Long): Task {
        val isRange = includeSchedule && scheduleMode == ScheduleMode.Range && rangeValid()
        val hasDeadline = includeSchedule && scheduleMode == ScheduleMode.Deadline && date.isNotBlank()
        val scheduleDate = when {
            isRange -> endDate
            hasDeadline -> date
            else -> ""
        }
        val scheduleTime = when {
            isRange -> endTime
            hasDeadline -> time
            else -> ""
        }
        return Task(
            id = id,
            name = name.trim(),
            description = description.trim(),
            category = category,
            deadline = if (scheduleDate.isBlank()) "" else formatInputDate(scheduleDate),
            time = scheduleTime.takeIf { it.isNotBlank() }?.let(::formatInputTime),
            filterKey = if (scheduleDate.isBlank()) FilterKey.All else filterKeyFor(scheduleDate),
            priority = priority,
            range = if (isRange) DateRange(startDate, startTime, endDate, endTime) else null,
            eventType = eventType,
            eventEndTime = eventEndTime,
            reminder = reminder,
            estimateMinutes = minutes.toIntOrNull(),
        )
    }

    fun toCalendarTask(id: Long): Task {
        if (eventType == EventType.Event) {
            return Task(
                id = id,
                name = name.trim(),
                description = description.trim(),
                category = category,
                deadline = formatInputDate(date),
                time = startTime.takeIf { it.isNotBlank() }?.let(::formatInputTime),
                filterKey = filterKeyFor(date),
                priority = priority,
                eventType = EventType.Event,
                eventEndTime = endTime.takeIf { it.isNotBlank() }?.let(::formatInputTime),
                reminder = reminder,
                estimateMinutes = minutes.toIntOrNull(),
            )
        }
        return toTask(id).copy(eventType = EventType.Task, eventEndTime = null)
    }

    fun toTimeBlock(id: Long, fallbackCategory: Category): TimeBlock = TimeBlock(
        id = id,
        title = name.trim(),
        start = inputToHours(startTime),
        end = inputToHours(endTime),
        category = category ?: fallbackCategory,
        reminder = reminder,
    )
}
