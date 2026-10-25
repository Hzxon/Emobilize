package ayodong.emobilize.core.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import ayodong.emobilize.core.di.AppContainer
import ayodong.emobilize.core.domain.AppSettingsRepository
import ayodong.emobilize.core.domain.ScheduleRepository
import ayodong.emobilize.core.domain.TaskRepository
import ayodong.emobilize.core.model.EventType
import ayodong.emobilize.core.model.TaskStatus
import ayodong.emobilize.core.model.ThemeMode
import ayodong.emobilize.core.ui.dock.FabAction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

data class ShellUiState(
    val themeMode: ThemeMode = ThemeMode.Light,
    val pendingAction: FabAction? = null,
    val selectedTaskId: Long? = null,
    val selectedBlockId: Long? = null,
)

// App-wide state that no single feature owns: the theme and the radial menu's action mode.
// An Edit, Update or Delete started on one tab stays active on the other, so it lives here.
class ShellViewModel(
    private val tasks: TaskRepository,
    private val schedule: ScheduleRepository,
    settings: AppSettingsRepository,
) : ViewModel() {
    private val mode = MutableStateFlow(ShellUiState())

    val uiState: StateFlow<ShellUiState> = combine(settings.settings, mode) { settings, mode ->
        mode.copy(themeMode = settings.themeMode)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, ShellUiState(themeMode = settings.settings.value.themeMode))

    fun onFabAction(action: FabAction) {
        if (action == FabAction.Add) return
        mode.update { it.copy(pendingAction = action, selectedTaskId = null) }
    }

    fun selectTask(id: Long) {
        mode.update { if (it.pendingAction == null) it else it.copy(selectedTaskId = id) }
    }

    fun toggleTaskSelection(id: Long) {
        mode.update {
            if (it.pendingAction == null) it else it.copy(selectedTaskId = if (it.selectedTaskId == id) null else id)
        }
    }

    fun clearTaskSelection() {
        mode.update { it.copy(selectedTaskId = null) }
    }

    fun toggleBlockSelection(id: Long) {
        mode.update { it.copy(selectedBlockId = if (it.selectedBlockId == id) null else id) }
    }

    fun clearBlockSelection() {
        mode.update { it.copy(selectedBlockId = null) }
    }

    fun cancelPending() {
        mode.update { it.copy(pendingAction = null, selectedTaskId = null) }
    }

    fun confirmAction() {
        val current = mode.value
        val id = current.selectedTaskId
        if (current.pendingAction == FabAction.Delete && id != null) tasks.delete(id)
        cancelPending()
    }

    fun applyStatus(status: TaskStatus) {
        val id = mode.value.selectedTaskId ?: return
        tasks.setStatus(id, status)
        cancelPending()
    }

    fun deleteSelectedBlock() {
        val id = mode.value.selectedBlockId ?: return
        schedule.delete(id)
        mode.update { it.copy(selectedBlockId = null) }
        cancelPending()
    }

    // Events carry an end time that only the calendar form shows and validates, so they are edited there.
    fun editorRoute(taskId: Long): String {
        val isEvent = tasks.board.value.tasks.find { it.id == taskId }?.eventType == EventType.Event
        return if (isEvent) AppRoute.calendarTask(taskId) else AppRoute.editTask(taskId)
    }

    companion object {
        fun factory(container: AppContainer): ViewModelProvider.Factory = viewModelFactory {
            initializer { ShellViewModel(container.tasks, container.schedule, container.appSettings) }
        }
    }
}
