package ayodong.emobilize.model

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToInt

enum class Category { Study, Work, Health, Personal, University }

enum class FilterKey { All, Today, Tomorrow, Later }

enum class NavTab { Tasks, Calendar }

enum class ScheduleMode { Deadline, Range }

enum class Priority { Low, Medium, High }

enum class TaskStatus { Todo, Progress, Done }

enum class FabAction(val label: String, val angle: Float) {
    Add("Add", -87f),
    Update("Update", 0f),
    Edit("Edit", 58f),
    Delete("Delete", 87f),
}

data class DateRange(
    val startDate: String,
    val startTime: String,
    val endDate: String,
    val endTime: String,
)

data class Task(
    val id: Long,
    val name: String,
    val category: Category,
    val deadline: String,
    val time: String? = null,
    val filterKey: FilterKey,
    val priority: Priority = Priority.Medium,
    val range: DateRange? = null,
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
    return isoDate(LocalDate.of(today.year, month, day))
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

fun sampleTasks(today: LocalDate = LocalDate.now()): List<Task> {
    val later = isoDate(today.plusDays(2))
    return listOf(
        Task(1, "Read research paper", Category.Study, "Today", "10:30 AM", FilterKey.Today, Priority.High),
        Task(2, "Finish backend API", Category.Work, "Today", "4:00 PM", FilterKey.Today, Priority.High),
        Task(3, "Gym", Category.Health, "Tomorrow", "6:00 PM", FilterKey.Tomorrow, Priority.Medium),
        Task(
            4,
            "Design portfolio draft",
            Category.Personal,
            formatInputDate(later, today),
            "11:00 AM",
            FilterKey.Later,
            Priority.Medium,
        ),
        Task(5, "Buy groceries", Category.Personal, "Today", null, FilterKey.Today, Priority.Low),
    )
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

fun displayAngle(action: FabAction, tab: NavTab): Float = when {
    action == FabAction.Edit -> -30f
    tab == NavTab.Tasks && action == FabAction.Update -> 30f
    else -> action.angle
}
