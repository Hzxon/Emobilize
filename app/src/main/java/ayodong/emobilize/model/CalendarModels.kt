package ayodong.emobilize.model

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

fun weekOf(date: LocalDate): List<LocalDate> {
    val monday = date.with(DayOfWeek.MONDAY)
    return (0..6).map { monday.plusDays(it.toLong()) }
}

fun monthCells(month: YearMonth): List<LocalDate?> {
    val first = month.atDay(1)
    val leading = first.dayOfWeek.value % 7
    val days = (1..month.lengthOfMonth()).map { month.atDay(it) }
    return List(leading) { null } + days
}

fun taskDate(task: Task, today: LocalDate = LocalDate.now()): LocalDate? {
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

fun sampleBlocks(today: LocalDate = LocalDate.now()): Map<LocalDate, List<TimeBlock>> {
    val days = weekOf(today)
    return mapOf(
        days[0] to listOf(
            TimeBlock(101, "Read Research Paper", 9f, 10.5f, Category.Study),
            TimeBlock(102, "Team Standup", 11f, 11.5f, Category.Work),
            TimeBlock(103, "Lunch Break", 12.5f, 13.5f, Category.Personal),
            TimeBlock(104, "Finish Backend API", 14f, 16f, Category.Work),
            TimeBlock(105, "Gym", 17f, 18f, Category.Health),
        ),
        days[1] to listOf(
            TimeBlock(106, "Portfolio Design", 10f, 12f, Category.Personal),
            TimeBlock(107, "Lunch", 12.5f, 13.5f, Category.Personal),
            TimeBlock(108, "Code Review", 15f, 16.5f, Category.Work),
        ),
        days[2] to listOf(
            TimeBlock(109, "Morning Planning", 8f, 9f, Category.Study),
            TimeBlock(110, "Deep Work", 9f, 12f, Category.Work),
            TimeBlock(111, "Lunch", 12f, 13f, Category.Personal),
            TimeBlock(112, "Standup", 14f, 14.5f, Category.Work),
            TimeBlock(113, "Gym Session", 17.5f, 18.5f, Category.Health),
            TimeBlock(114, "Buy Groceries", 19f, 19.5f, Category.Personal),
        ),
        days[4] to listOf(
            TimeBlock(115, "Submit Assignment", 10f, 11f, Category.University),
            TimeBlock(116, "Lunch", 13f, 14f, Category.Personal),
        ),
        days[5] to listOf(
            TimeBlock(117, "Weekend Run", 8f, 9f, Category.Health),
        ),
    )
}
