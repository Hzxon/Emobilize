package ayodong.emobilize.core.navigation

import ayodong.emobilize.core.data.FakeAppSettingsRepository
import ayodong.emobilize.core.data.InMemoryScheduleRepository
import ayodong.emobilize.core.data.InMemoryTaskRepository
import ayodong.emobilize.core.model.AppSettings
import ayodong.emobilize.core.model.TaskStatus
import ayodong.emobilize.core.model.ThemeMode
import ayodong.emobilize.core.ui.dock.FabAction
import ayodong.emobilize.testing.MainDispatcherRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ShellViewModelTest {
    @get:Rule
    val mainDispatcher = MainDispatcherRule()

    private val tasks = InMemoryTaskRepository()
    private val schedule = InMemoryScheduleRepository()
    private val settings = FakeAppSettingsRepository()
    private val shell by lazy { ShellViewModel(tasks, schedule, settings) }

    @Test
    fun selectingNeedsAnActionMode() {
        shell.selectTask(2L)
        assertNull(shell.uiState.value.selectedTaskId)
    }

    @Test
    fun deleteModeRemovesTheSelectedTask() {
        shell.onFabAction(FabAction.Delete)
        shell.selectTask(2L)
        shell.confirmAction()

        assertTrue(tasks.board.value.tasks.none { it.id == 2L })
        assertNull(shell.uiState.value.pendingAction)
        assertNull(shell.uiState.value.selectedTaskId)
    }

    @Test
    fun updateModeSetsStatus() {
        shell.onFabAction(FabAction.Update)
        shell.selectTask(3L)
        shell.applyStatus(TaskStatus.Done)

        assertTrue(3L in tasks.board.value.doneIds)
        assertNull(shell.uiState.value.pendingAction)
    }

    @Test
    fun deletingABlockClearsSelection() {
        val blockId = schedule.blocks.value.values.first().first().id
        shell.toggleBlockSelection(blockId)
        shell.deleteSelectedBlock()

        assertFalse(schedule.blocks.value.values.flatten().any { it.id == blockId })
        assertNull(shell.uiState.value.selectedBlockId)
    }

    @Test
    fun eventsOpenTheCalendarEditor() {
        assertEquals(AppRoute.calendarTask(8L), shell.editorRoute(8L))
        assertEquals(AppRoute.editTask(2L), shell.editorRoute(2L))
    }

    @Test
    fun themeFollowsSettings() {
        settings.update(AppSettings(themeMode = ThemeMode.Dark))
        assertEquals(ThemeMode.Dark, shell.uiState.value.themeMode)
    }
}
