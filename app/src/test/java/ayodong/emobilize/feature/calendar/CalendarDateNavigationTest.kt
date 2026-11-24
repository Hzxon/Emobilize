package ayodong.emobilize.feature.calendar

import ayodong.emobilize.core.model.CalView
import ayodong.emobilize.feature.calendar.ui.stepDate
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class CalendarDateNavigationTest {

    @Test
    fun weekModeAdvancesByOneWeek() {
        val date = LocalDate.of(2026, 11, 22)
        assertEquals(
            LocalDate.of(2026, 11, 29),
            stepDate(date, CalView.Week, 1),
        )
    }

    @Test
    fun weekModeGoesBackByOneWeek() {
        val date = LocalDate.of(2026, 11, 22)
        assertEquals(
            LocalDate.of(2026, 11, 15),
            stepDate(date, CalView.Week, -1),
        )
    }

    @Test
    fun monthModeAdvancesByOneMonth() {
        val date = LocalDate.of(2026, 11, 22)
        assertEquals(
            LocalDate.of(2026, 12, 22),
            stepDate(date, CalView.Month, 1),
        )
    }

    @Test
    fun monthModeGoesBackByOneMonth() {
        val date = LocalDate.of(2026, 11, 22)
        assertEquals(
            LocalDate.of(2026, 10, 22),
            stepDate(date, CalView.Month, -1),
        )
    }

    @Test
    fun monthModeClampsEndOfMonth() {
        val date = LocalDate.of(2026, 1, 31)
        assertEquals(
            LocalDate.of(2026, 2, 28),
            stepDate(date, CalView.Month, 1),
        )
    }
}
