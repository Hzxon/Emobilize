package ayodong.emobilize.feature.tasks.domain

import ayodong.emobilize.core.model.FilterKey
import ayodong.emobilize.core.model.Task
import ayodong.emobilize.core.model.deadlineToInputDate
import ayodong.emobilize.core.model.isoDate
import java.time.LocalDate

/**
 * The three tabs of the departure board. Each task lands in exactly one:
 * on the clock today, dated after today, or not dated at all.
 * A time range counts by its start, so a range already running stays in Today.
 */
enum class BoardFilter { Today, Upcoming, NoDate }

/** ISO date the task departs on, or null when it has no date. */
fun departureDate(task: Task, today: LocalDate = LocalDate.now()): String? {
    task.range?.let { return it.startDate }
    if (task.deadline.isBlank()) return null
    return deadlineToInputDate(task.deadline, today)
}

fun boardTasks(
    tasks: List<Task>,
    filter: BoardFilter,
    today: LocalDate = LocalDate.now(),
): List<Task> {
    val todayIso = isoDate(today)
    return tasks.filter { task ->
        val range = task.range
        when (filter) {
            BoardFilter.NoDate -> range == null && task.deadline.isBlank()
            BoardFilter.Today -> if (range != null) {
                range.startDate <= todayIso && range.endDate >= todayIso
            } else {
                task.filterKey == FilterKey.Today
            }
            BoardFilter.Upcoming -> if (range != null) {
                range.startDate > todayIso
            } else {
                task.filterKey == FilterKey.Tomorrow || task.filterKey == FilterKey.Later
            }
        }
    }
}
