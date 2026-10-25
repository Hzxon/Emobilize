package ayodong.emobilize.feature.reminders.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ayodong.emobilize.core.domain.AppSettingsRepository
import ayodong.emobilize.core.domain.TaskRepository
import ayodong.emobilize.core.model.Task
import ayodong.emobilize.core.model.TaskStatus
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class RemindersUiState(
    val tasks: List<Task> = emptyList(),
    val doneIds: Set<Long> = emptySet(),
    val notificationsEnabled: Boolean = true,
)

class RemindersViewModel(
    private val tasks: TaskRepository,
    settings: AppSettingsRepository,
) : ViewModel() {
    val uiState: StateFlow<RemindersUiState> = combine(tasks.board, settings.settings) { board, settings ->
        RemindersUiState(board.tasks, board.doneIds, settings.notificationsEnabled)
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        RemindersUiState(tasks.board.value.tasks, tasks.board.value.doneIds, settings.settings.value.notificationsEnabled),
    )

    fun markDone(id: Long) {
        tasks.setStatus(id, TaskStatus.Done)
    }
}
