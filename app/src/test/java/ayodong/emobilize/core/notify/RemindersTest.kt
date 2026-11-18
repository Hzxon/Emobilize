package ayodong.emobilize.core.notify

import ayodong.emobilize.core.domain.TaskBoard
import ayodong.emobilize.core.model.Category
import ayodong.emobilize.core.model.DateRange
import ayodong.emobilize.core.model.EventType
import ayodong.emobilize.core.model.ReminderOption
import ayodong.emobilize.core.model.Task
import ayodong.emobilize.core.model.TimeBlock
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class RemindersTest {
    private val today = LocalDate.of(2026, 11, 18)
    private val now = today.atTime(8, 0)

    private fun board(vararg tasks: Task, done: Set<Long> = emptySet()) = TaskBoard(tasks.toList(), done, emptySet())

    @Test
    fun timedTaskFiresLeadBeforeDeadline() {
        val task = Task(1, "Essay", null, today, "4:30 PM", reminder = ReminderOption.TenMinutesBefore)
        val reminder = upcomingReminders(board(task), emptyMap(), now).single()
        assertEquals(today.atTime(16, 20), reminder.at)
        assertEquals("Due 4:30 PM", reminder.text)
    }

    @Test
    fun dateOnlyTaskCountsAsDueAtNine() {
        val task = Task(2, "Pay rent", null, today.plusDays(1), reminder = ReminderOption.OneDayBefore)
        assertEquals(today.atTime(9, 0), upcomingReminders(board(task), emptyMap(), now).single().at)
    }

    @Test
    fun rangeIsDueWhenItEnds() {
        val task = Task(
            3, "Sprint", null, today.plusDays(1),
            range = DateRange(today.toString(), "13:00", today.plusDays(1).toString(), "15:00"),
            reminder = ReminderOption.OneHourBefore,
        )
        assertEquals(today.plusDays(1).atTime(14, 0), upcomingReminders(board(task), emptyMap(), now).single().at)
    }

    @Test
    fun eventSaysStarts() {
        val task = Task(4, "Dentist", null, today, "2 PM", eventType = EventType.Event, reminder = ReminderOption.AtDeadline)
        assertEquals("Starts 2 PM", upcomingReminders(board(task), emptyMap(), now).single().text)
    }

    @Test
    fun timeBlockFiresBeforeItsStart() {
        val block = TimeBlock(5, "Lecture", 10.5f, 12f, Category.Study, ReminderOption.TenMinutesBefore)
        val reminder = upcomingReminders(board(), mapOf(today to listOf(block)), now).single()
        assertEquals(today.atTime(10, 20), reminder.at)
    }

    @Test
    fun skipsDonePastAndUnsetReminders() {
        val done = Task(6, "Done", null, today, "6 PM", reminder = ReminderOption.AtDeadline)
        val past = Task(7, "Past", null, today, "7 AM", reminder = ReminderOption.AtDeadline)
        val none = Task(8, "None", null, today, "6 PM")
        val undated = Task(9, "Undated", null, null, reminder = ReminderOption.AtDeadline)
        assertTrue(upcomingReminders(board(done, past, none, undated, done = setOf(6L)), emptyMap(), now).isEmpty())
    }
}
