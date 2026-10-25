package ayodong.emobilize.feature.tasks

import ayodong.emobilize.core.data.sampleTasks
import ayodong.emobilize.core.model.DateRange
import ayodong.emobilize.core.model.FilterKey
import ayodong.emobilize.core.model.Task
import ayodong.emobilize.feature.tasks.domain.BoardFilter
import ayodong.emobilize.feature.tasks.domain.boardTasks
import ayodong.emobilize.feature.tasks.domain.departureDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class BoardFilterTest {
    private val today = LocalDate.of(2026, 10, 26)
    private val tasks = sampleTasks(today)

    private fun ids(filter: BoardFilter) = boardTasks(tasks, filter, today).map { it.id }.toSet()

    @Test
    fun everyTaskLandsInExactlyOneTab() {
        val tabs = BoardFilter.entries.map(::ids)
        assertEquals(tasks.map { it.id }.toSet(), tabs.flatten().toSet())
        assertEquals(tasks.size, tabs.sumOf { it.size })
    }

    @Test
    fun rangeRunningTodayStaysInToday() {
        // Task 7 runs from today 13:00 to tomorrow 15:00.
        assertTrue(7L in ids(BoardFilter.Today))
        assertTrue(7L !in ids(BoardFilter.Upcoming))
    }

    @Test
    fun rangeStartingLaterIsUpcoming() {
        val start = today.plusDays(2).toString()
        val trip = Task(
            id = 50,
            name = "Field trip",
            category = null,
            deadline = "",
            filterKey = FilterKey.Later,
            range = DateRange(start, "09:00", start, "17:00"),
        )
        assertEquals(listOf(trip), boardTasks(listOf(trip), BoardFilter.Upcoming, today))
        assertEquals(start, departureDate(trip, today))
    }

    @Test
    fun undatedTasksOnlyShowInNoDate() {
        assertEquals(setOf(9L), ids(BoardFilter.NoDate))
        assertNull(departureDate(tasks.first { it.id == 9L }, today))
    }
}
