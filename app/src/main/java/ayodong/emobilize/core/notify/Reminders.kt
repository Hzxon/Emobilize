package ayodong.emobilize.core.notify

import ayodong.emobilize.core.domain.TaskBoard
import ayodong.emobilize.core.model.EventType
import ayodong.emobilize.core.model.ReminderOption
import ayodong.emobilize.core.model.Task
import ayodong.emobilize.core.model.TimeBlock
import ayodong.emobilize.core.model.formatBlockTime
import ayodong.emobilize.core.model.hoursToInput
import ayodong.emobilize.core.model.inputToHours
import ayodong.emobilize.core.model.parseDisplayTime
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

/** One notification to post at [at]. [id] is the task or block id, so a reschedule replaces it. */
data class Reminder(val id: Long, val title: String, val text: String, val at: LocalDateTime)

// A task with a date but no time is treated as due at 9:00 that morning.
private val DefaultTime: LocalTime = LocalTime.of(9, 0)

/** Reminders still to come: open tasks and time blocks whose reminder time is after [now]. */
fun upcomingReminders(
    board: TaskBoard,
    blocks: Map<LocalDate, List<TimeBlock>>,
    now: LocalDateTime,
): List<Reminder> {
    val fromTasks = board.tasks
        .filter { it.id !in board.doneIds }
        .mapNotNull { task ->
            val verb = if (task.eventType == EventType.Event) "Starts" else "Due"
            dueAt(task)?.let { reminder(task.id, task.name, verb, it, task.reminder) }
        }
    val fromBlocks = blocks.flatMap { (date, list) ->
        list.mapNotNull { block ->
            reminder(block.id, block.title, "Starts", date.atTime(time(block.start)), block.reminder)
        }
    }
    return (fromTasks + fromBlocks).filter { it.at.isAfter(now) }
}

/** When the task is due: a range ends, an event starts, a deadline falls. Null without a date. */
private fun dueAt(task: Task): LocalDateTime? {
    val range = task.range
    if (range != null) {
        val end = runCatching { LocalDate.parse(range.endDate) }.getOrNull() ?: return null
        return end.atTime(time(inputToHours(range.endTime)))
    }
    val date = task.date ?: return null
    return date.atTime(task.time?.let { time(parseDisplayTime(it)) } ?: DefaultTime)
}

private fun time(hours: Float): LocalTime = LocalTime.parse(hoursToInput(hours))

private fun reminder(id: Long, title: String, verb: String, due: LocalDateTime, option: ReminderOption): Reminder? {
    val lead = option.lead ?: return null
    return Reminder(id, title, "$verb ${formatBlockTime(due.hour + due.minute / 60f)}", due.minus(lead))
}
