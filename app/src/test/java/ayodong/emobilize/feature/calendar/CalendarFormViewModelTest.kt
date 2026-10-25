package ayodong.emobilize.feature.calendar

import ayodong.emobilize.core.data.FakeAppSettingsRepository
import ayodong.emobilize.core.data.FakeCategoryRepository
import ayodong.emobilize.core.data.InMemoryScheduleRepository
import ayodong.emobilize.core.data.InMemoryTaskRepository
import ayodong.emobilize.core.model.EventType
import ayodong.emobilize.feature.calendar.ui.CalendarFormTarget
import ayodong.emobilize.feature.calendar.ui.CalendarFormViewModel
import ayodong.emobilize.testing.MainDispatcherRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class CalendarFormViewModelTest {
    @get:Rule
    val mainDispatcher = MainDispatcherRule()

    private val tasks = InMemoryTaskRepository()
    private val schedule = InMemoryScheduleRepository()

    private fun form(target: CalendarFormTarget) =
        CalendarFormViewModel(tasks, schedule, FakeCategoryRepository(), FakeAppSettingsRepository(), target)

    @Test
    fun newEntryStartsAsAnEvent() {
        val viewModel = form(CalendarFormTarget.New)
        assertEquals(EventType.Event, viewModel.draft.eventType)
        assertFalse(viewModel.editing)
    }

    @Test
    fun editingABlockWritesItBack() {
        val (date, blocks) = schedule.blocks.value.entries.first()
        val block = blocks.first()
        val viewModel = form(CalendarFormTarget.EditBlock(block.id, date.toString()))
        assertTrue(viewModel.found)

        viewModel.draft.name = "Moved block"
        assertTrue(viewModel.submit())
        assertEquals("Moved block", schedule.blocks.value.values.flatten().first { it.id == block.id }.title)
    }

    @Test
    fun badBlockDateIsNotFound() {
        assertFalse(form(CalendarFormTarget.EditBlock(101L, "not-a-date")).found)
    }
}
