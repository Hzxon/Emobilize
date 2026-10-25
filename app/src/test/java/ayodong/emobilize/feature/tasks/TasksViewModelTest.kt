package ayodong.emobilize.feature.tasks

import ayodong.emobilize.core.data.FakeAppSettingsRepository
import ayodong.emobilize.core.data.FakeCategoryRepository
import ayodong.emobilize.core.data.InMemoryTaskRepository
import ayodong.emobilize.core.model.FilterKey
import ayodong.emobilize.core.model.ScheduleMode
import ayodong.emobilize.feature.tasks.ui.TaskFormViewModel
import ayodong.emobilize.feature.tasks.ui.TasksViewModel
import ayodong.emobilize.testing.MainDispatcherRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class TasksViewModelTest {
    @get:Rule
    val mainDispatcher = MainDispatcherRule()

    private val tasks = InMemoryTaskRepository()
    private val categories = FakeCategoryRepository()
    private val settings = FakeAppSettingsRepository()

    @Test
    fun filterNarrowsVisibleTasks() {
        val viewModel = TasksViewModel(tasks, settings)
        viewModel.selectFilter(FilterKey.Tomorrow)

        val state = viewModel.uiState.value
        assertEquals(FilterKey.Tomorrow, state.filter)
        assertTrue(state.visibleTasks().all { it.filterKey == FilterKey.Tomorrow || it.range != null })
    }

    @Test
    fun listSeesTasksAddedByAForm() {
        val list = TasksViewModel(tasks, settings)
        val form = TaskFormViewModel(tasks, categories, settings, taskId = null)
        form.draft.name = "Write tests"
        form.draft.description = "Cover the ViewModels"

        assertTrue(form.submitAdd())
        assertTrue(list.uiState.value.tasks.any { it.name == "Write tests" })
    }

    @Test
    fun addNeedsADescription() {
        val form = TaskFormViewModel(tasks, categories, settings, taskId = null)
        form.draft.name = "No description"
        assertFalse(form.submitAdd())
    }

    @Test
    fun editLoadsAndSavesTheTask() {
        val form = TaskFormViewModel(tasks, categories, settings, taskId = 7L)
        assertTrue(form.found)
        assertEquals(ScheduleMode.Range, form.draft.scheduleMode)

        form.draft.name = "Renamed sprint"
        assertTrue(form.submitEdit())
        assertEquals("Renamed sprint", tasks.board.value.tasks.first { it.id == 7L }.name)
    }

    @Test
    fun editOfMissingTaskIsNotFound() {
        assertFalse(TaskFormViewModel(tasks, categories, settings, taskId = 999L).found)
    }
}
