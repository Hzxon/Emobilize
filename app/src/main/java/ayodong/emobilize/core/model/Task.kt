package ayodong.emobilize.core.model

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * A transit line on the board. [color] is one of [lineColors]; [code] is the 1–2 character
 * line code drawn inside the station ring, so two lines that share a colour stay apart.
 */
data class Category(
    val id: Long,
    val name: String,
    val color: Int,
    val code: String = codeFor(name),
) {
    companion object {
        const val NameLimit = 24
        const val CodeLimit = 2

        val Study = Category(1L, "Study", 0xFFD6001C.toInt(), "S")
        val Work = Category(2L, "Work", 0xFF0057B8.toInt(), "W")
        val Health = Category(3L, "Health", 0xFF007A4D.toInt(), "H")
        val defaults = listOf(Study, Work, Health)

        /** Line colours, in the order the colour picker shows them. All legible under white text. */
        val lineColors = listOf(
            0xFFD6001C.toInt(), // red
            0xFF0057B8.toInt(), // blue
            0xFF007A4D.toInt(), // green
            0xFF5C2D91.toInt(), // purple
            0xFFB35900.toInt(), // orange
            0xFFE6007E.toInt(), // pink
            0xFF00858F.toInt(), // teal
            0xFF7A4B1E.toInt(), // brown
            0xFF1B2A6B.toInt(), // navy
            0xFF6E9A00.toInt(), // lime
            0xFFA0007A.toInt(), // magenta
            0xFF5A5A5A.toInt(), // grey
        )

        /** Colours stored by earlier versions, mapped to the line colour they were drawn with. */
        val legacyLineColors = mapOf(
            0xFFE76E50.toInt() to 0xFFD6001C.toInt(),
            0xFF2A9D90.toInt() to 0xFF007A4D.toInt(),
            0xFF274754.toInt() to 0xFF5C2D91.toInt(),
            0xFFE8C468.toInt() to 0xFFB35900.toInt(),
            0xFFF4A462.toInt() to 0xFF0057B8.toInt(),
        )

        fun isValidName(name: String): Boolean =
            name.isNotEmpty() && name.length <= NameLimit && name.none { it.isISOControl() }

        fun isValidCode(code: String): Boolean =
            code.isNotEmpty() && code.length <= CodeLimit && code.none { it.isWhitespace() || it.isISOControl() }

        /** First letter of the name; the first two when [taken] already has the single letter. */
        fun codeFor(name: String, taken: Set<String> = emptySet()): String {
            val letters = name.trim().filter { it.isLetterOrDigit() }
            if (letters.isEmpty()) return "?"
            val one = letters.take(1).uppercase()
            if (one !in taken.map { it.uppercase() }) return one
            return letters.take(2).replaceFirstChar { it.uppercaseChar() }
        }
    }

    /** Trimmed copy when name, code and colour pass validation, else null. */
    fun normalized(): Category? {
        val name = name.trim()
        val code = code.trim()
        if (!isValidName(name) || !isValidCode(code) || color !in lineColors) return null
        return copy(name = name, code = code)
    }
}

enum class FilterKey { All, Today, Tomorrow, Later }

enum class ScheduleMode { Deadline, Range }

enum class EventType(val label: String) {
    Event("Event"),
    Task("Task"),
    RegularSchedule("Regular Schedule"),
}

enum class ReminderOption(val label: String) {
    None("None"),
    AtDeadline("At deadline"),
    TenMinutesBefore("10 minutes before"),
    OneHourBefore("1 hour before"),
    OneDayBefore("1 day before"),
}

enum class Priority { Low, Medium, High }

enum class TaskStatus { Todo, Progress, Done }

data class DateRange(
    val startDate: String,
    val startTime: String,
    val endDate: String,
    val endTime: String,
)

data class Task(
    val id: Long,
    val name: String,
    val category: Category?,
    val deadline: String,
    val time: String? = null,
    val filterKey: FilterKey,
    val priority: Priority? = null,
    val range: DateRange? = null,
    val eventType: EventType = EventType.Task,
    val eventEndTime: String? = null,
    val reminder: ReminderOption = ReminderOption.None,
    val description: String = "",
    val estimateMinutes: Int? = null,
)

private val monthDay: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM d", Locale.US)
private val monthNames = listOf(
    "January", "February", "March", "April", "May", "June",
    "July", "August", "September", "October", "November", "December",
)

fun isoDate(date: LocalDate): String = date.toString()

fun formatInputDate(iso: String, today: LocalDate = LocalDate.now()): String {
    val date = LocalDate.parse(iso)
    return when (date) {
        today -> "Today"
        today.plusDays(1) -> "Tomorrow"
        else -> date.format(monthDay)
    }
}

fun deadlineToInputDate(deadline: String, today: LocalDate = LocalDate.now()): String {
    if (deadline == "Today") return isoDate(today)
    if (deadline == "Tomorrow") return isoDate(today.plusDays(1))
    val match = Regex("""([A-Za-z]{3}) (\d+)""").find(deadline) ?: return isoDate(today)
    val month = monthNames.indexOfFirst { it.startsWith(match.groupValues[1]) } + 1
    if (month <= 0) return isoDate(today)
    val day = match.groupValues[2].toInt()
    val date = runCatching { LocalDate.of(today.year, month, day) }.getOrNull() ?: return isoDate(today)
    return isoDate(date)
}

fun parseDisplayTime(time: String): Float {
    val parts = time.split(" ")
    val hm = parts[0].split(":")
    val h = hm[0].toInt()
    val m = hm.getOrNull(1)?.toIntOrNull() ?: 0
    val suffix = parts.getOrNull(1)
    val hours = when {
        suffix == "PM" && h != 12 -> h + 12
        suffix == "AM" && h == 12 -> 0
        else -> h
    }
    return hours + m / 60f
}

fun formatInputTime(time: String): String {
    val pieces = time.split(":")
    val hh = pieces[0].toInt()
    val mm = pieces.getOrNull(1)?.toIntOrNull() ?: 0
    val suffix = if (hh >= 12) "PM" else "AM"
    val displayHour = when {
        hh == 0 -> 12
        hh > 12 -> hh - 12
        else -> hh
    }
    return if (mm == 0) "$displayHour $suffix" else "$displayHour:${mm.toString().padStart(2, '0')} $suffix"
}

fun displayTimeToInput(time: String?): String {
    if (time.isNullOrBlank()) return ""
    val value = parseDisplayTime(time)
    val hours = value.toInt()
    val minutes = ((value - hours) * 60f).roundToInt()
    return "%02d:%02d".format(hours, minutes)
}

fun filterKeyFor(date: String, today: LocalDate = LocalDate.now()): FilterKey = when (date) {
    isoDate(today) -> FilterKey.Today
    isoDate(today.plusDays(1)) -> FilterKey.Tomorrow
    else -> FilterKey.Later
}

fun rangeIsValid(startDate: String, startTime: String, endDate: String, endTime: String): Boolean {
    if (startDate.isBlank() || startTime.isBlank() || endDate.isBlank() || endTime.isBlank()) return false
    return endDate > startDate || (endDate == startDate && endTime > startTime)
}

fun deadlineRank(task: Task, today: LocalDate = LocalDate.now()): Int {
    if (task.range == null && task.deadline.isBlank()) return Int.MAX_VALUE
    val range = task.range
    if (range != null) {
        val end = runCatching { LocalDate.parse(range.endDate) }.getOrNull() ?: return Int.MAX_VALUE
        val dayDistance = abs(ChronoUnit.DAYS.between(today, end)).toInt()
        val endMinutes = if (range.endTime.length >= 5) {
            range.endTime.substring(0, 2).toInt() * 60 + range.endTime.substring(3, 5).toInt()
        } else {
            24 * 60
        }
        return dayDistance * 24 * 60 + endMinutes
    }
    val deadlineDate = runCatching { LocalDate.parse(deadlineToInputDate(task.deadline, today)) }.getOrNull()
        ?: return Int.MAX_VALUE
    val minutes = task.time?.let { (parseDisplayTime(it) * 60f).roundToInt() } ?: (24 * 60)
    val dayDistance = abs(ChronoUnit.DAYS.between(today, deadlineDate)).toInt()
    return dayDistance * 24 * 60 + minutes
}

fun isOverdue(task: Task, nowHours: Float): Boolean {
    if (task.filterKey != FilterKey.Today || task.time.isNullOrBlank()) return false
    return parseDisplayTime(task.time) < nowHours
}

fun filteredTasks(
    tasks: List<Task>,
    filter: FilterKey,
    today: LocalDate = LocalDate.now(),
): List<Task> {
    if (filter == FilterKey.All) return tasks
    val todayIso = isoDate(today)
    val tomorrowIso = isoDate(today.plusDays(1))
    return tasks.filter { task ->
        val range = task.range
        if (range == null) {
            task.filterKey == filter
        } else when (filter) {
            FilterKey.Today -> range.startDate <= todayIso && range.endDate >= todayIso
            FilterKey.Tomorrow -> range.startDate <= tomorrowIso && range.endDate >= tomorrowIso
            FilterKey.Later -> range.endDate > tomorrowIso
            FilterKey.All -> true
        }
    }
}
