package ayodong.emobilize.feature.tasks.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ayodong.emobilize.core.domain.AppSettingsRepository
import ayodong.emobilize.core.domain.TaskRepository
import ayodong.emobilize.core.model.FilterKey
import ayodong.emobilize.core.model.Task
import ayodong.emobilize.core.model.filteredTasks
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate

data class TasksUiState(
    val tasks: List<Task> = emptyList(),
    val filter: FilterKey = FilterKey.All,
    val doneIds: Set<Long> = emptySet(),
    val progressIds: Set<Long> = emptySet(),
    val showCompletedTasks: Boolean = true,
) {
    fun visibleTasks(today: LocalDate = LocalDate.now()): List<Task> = filteredTasks(tasks, filter, today)

    fun task(id: Long): Task? = tasks.find { it.id == id }
}

class TasksViewModel(
    tasks: TaskRepository,
    settings: AppSettingsRepository,
) : ViewModel() {
    private val filter = MutableStateFlow(FilterKey.All)

    val uiState: StateFlow<TasksUiState> = combine(tasks.board, filter, settings.settings) { board, filter, settings ->
        TasksUiState(board.tasks, filter, board.doneIds, board.progressIds, settings.showCompletedTasks)
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        tasks.board.value.let { board ->
            TasksUiState(board.tasks, filter.value, board.doneIds, board.progressIds, settings.settings.value.showCompletedTasks)
        },
    )

    fun selectFilter(next: FilterKey) {
        filter.value = next
    }
}
