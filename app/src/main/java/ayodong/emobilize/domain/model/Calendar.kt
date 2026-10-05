package ayodong.emobilize.domain.model

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import kotlin.math.roundToInt

enum class CalView { Month, Week, Day }

data class TimeBlock(
    val id: Long,
    val title: String,
    val start: Float,
    val end: Float,
    val category: Category,
)

fun weekOf(date: LocalDate, firstDay: DayOfWeek = DayOfWeek.MONDAY): List<LocalDate> {
    val daysBack = (date.dayOfWeek.value - firstDay.value + 7) % 7
    val start = date.minusDays(daysBack.toLong())
    return (0..6).map { start.plusDays(it.toLong()) }
}

fun monthCells(month: YearMonth, firstDay: DayOfWeek = DayOfWeek.SUNDAY): List<LocalDate?> {
    val first = month.atDay(1)
    val leading = (first.dayOfWeek.value - firstDay.value + 7) % 7
    val days = (1..month.lengthOfMonth()).map { month.atDay(it) }
    return List(leading) { null } + days
}

fun taskDate(task: Task, today: LocalDate = LocalDate.now()): LocalDate? {
    if (task.deadline.isBlank()) return null
    return runCatching { LocalDate.parse(deadlineToInputDate(task.deadline, today)) }.getOrNull()
}

fun taskOccursOn(task: Task, date: LocalDate, today: LocalDate = LocalDate.now()): Boolean {
    val range = task.range
    if (range != null) {
        val start = runCatching { LocalDate.parse(range.startDate) }.getOrNull() ?: return false
        val end = runCatching { LocalDate.parse(range.endDate) }.getOrNull() ?: return false
        return !date.isBefore(start) && !date.isAfter(end)
    }
    return taskDate(task, today) == date
}

fun hoursToInput(value: Float): String {
    val hours = value.toInt().coerceIn(0, 23)
    val minutes = ((value - hours) * 60f).roundToInt().coerceIn(0, 59)
    return "%02d:%02d".format(hours, minutes)
}

fun inputToHours(value: String): Float {
    val parts = value.split(":")
    val hours = parts.getOrNull(0)?.toIntOrNull() ?: 0
    val minutes = parts.getOrNull(1)?.toIntOrNull() ?: 0
    return hours + minutes / 60f
}

fun formatHourLabel(hour: Int): String = when (hour) {
    0, 24 -> "12 AM"
    12 -> "12 PM"
    in 13..23 -> "${hour - 12} PM"
    else -> "$hour AM"
}

fun formatBlockTime(value: Float): String {
    val hours = value.toInt()
    val minutes = ((value - hours) * 60f).roundToInt()
    val suffix = if (hours >= 12) "PM" else "AM"
    val display = when {
        hours == 0 -> 12
        hours > 12 -> hours - 12
        else -> hours
    }
    return if (minutes == 0) "$display $suffix" else "$display:${minutes.toString().padStart(2, '0')} $suffix"
}

fun placeSchedule(
    blocks: Map<LocalDate, List<TimeBlock>>,
    block: TimeBlock,
    date: LocalDate,
): Map<LocalDate, List<TimeBlock>> {
    val exists = blocks.values.any { list -> list.any { it.id == block.id } }
    return if (exists) {
        replaceBlock(blocks, block.id, block.title, block.category, block.start, block.end, date)
    } else {
        blocks + (date to (blocks[date].orEmpty() + block))
    }
}

fun replaceBlock(
    blocks: Map<LocalDate, List<TimeBlock>>,
    id: Long,
    title: String,
    category: Category,
    start: Float,
    end: Float,
    target: LocalDate,
): Map<LocalDate, List<TimeBlock>> {
    var updated: TimeBlock? = null
    val stripped = blocks.mapValues { (_, list) ->
        list.filter { block ->
            if (block.id == id) {
                updated = block.copy(title = title, category = category, start = start, end = end)
                false
            } else {
                true
            }
        }
    }.filterValues { it.isNotEmpty() }
    val block = updated ?: return blocks
    return stripped + (target to (stripped[target].orEmpty() + block))
}

