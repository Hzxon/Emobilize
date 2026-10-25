package ayodong.emobilize.core.ui.form

import ayodong.emobilize.core.model.deadlineToInputDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDate

class TaskDraftValidationTest {
    private fun minutes(value: String) = TaskDraft().apply { minutes = value }.minutesError()

    private fun eventTimes(start: String, end: String) = TaskDraft().apply {
        startTime = start
        endTime = end
    }.eventTimeError()

    @Test
    fun estimateIsOptional() {
        assertNull(minutes(""))
    }

    @Test
    fun estimateAcceptsWholeMinutesInRange() {
        assertNull(minutes("1"))
        assertNull(minutes("45"))
        assertNull(minutes("1440"))
    }

    @Test
    fun estimateRejectsNonDigits() {
        listOf("12a", "1.5", "-3", " 5", "abc").forEach { value ->
            assertEquals(value, "Use whole numbers only", minutes(value))
        }
    }

    @Test
    fun estimateRejectsOutOfRange() {
        listOf("0", "1441", "2000", "99999999999").forEach { value ->
            assertEquals(value, "Enter 1 to 1440 minutes", minutes(value))
        }
    }

    @Test
    fun eventEndMustBeAfterStart() {
        assertNull(eventTimes("09:00", "10:00"))
        assertEquals("End time must be after start time", eventTimes("10:00", "09:30"))
        assertEquals("End time must be after start time", eventTimes("14:00", "14:00"))
    }

    @Test
    fun eventTimesAreOptional() {
        assertNull(eventTimes("", "09:00"))
        assertNull(eventTimes("09:00", ""))
    }

    @Test
    fun eventTimesCompareByValueNotText() {
        // Pickers format with the device locale, so a start time can arrive in Arabic-Indic digits.
        assertNull(eventTimes("٠٨:٠٠", "10:00"))
    }

    @Test
    fun deadlineWithMissingLeapDayFallsBackToToday() {
        val today = LocalDate.of(2026, 10, 22)
        assertEquals("2026-10-22", deadlineToInputDate("Feb 29", today))
        assertEquals("2026-10-25", deadlineToInputDate("Oct 25", today))
    }
}
