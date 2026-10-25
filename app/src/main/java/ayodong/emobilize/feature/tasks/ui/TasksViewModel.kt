package ayodong.emobilize.feature.tasks.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ayodong.emobilize.core.domain.AppSettingsRepository
import ayodong.emobilize.core.domain.TaskRepository
import ayodong.emobilize.core.model.FilterKey
import ayodong.emobilize.core.model.Task
import ayodong.emobilize.core.model.TaskStatus
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
    val undo: UndoEntry? = null,
) {
    fun visibleTasks(today: LocalDate = LocalDate.now()): List<Task> = filteredTasks(tasks, filter, today)

    fun task(id: Long): Task? = tasks.find { it.id == id }
}

/** A swipe action the user can take back from the snackbar. */
data class UndoEntry(
    val id: Long,
    val message: String,
    val task: Task? = null,
    val previousStatus: TaskStatus = TaskStatus.Todo,
)

class TasksViewModel(
    private val tasks: TaskRepository,
    settings: AppSettingsRepository,
) : ViewModel() {
    private val filter = MutableStateFlow(FilterKey.All)
    private val undo = MutableStateFlow<UndoEntry?>(null)

    val uiState: StateFlow<TasksUiState> = combine(tasks.board, filter, settings.settings, undo) { board, filter, settings, undo ->
        TasksUiState(board.tasks, filter, board.doneIds, board.progressIds, settings.showCompletedTasks, undo)
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

    /** Swipe right: mark done, remembering the status it had so Undo can put it back. */
    fun completeTask(id: Long) {
        val board = tasks.board.value
        if (id in board.doneIds) return
        val previous = if (id in board.progressIds) TaskStatus.Progress else TaskStatus.Todo
        tasks.setStatus(id, TaskStatus.Done)
        undo.value = UndoEntry(id, "Task done", previousStatus = previous)
    }

    /** Swipe left: delete, keeping a copy so Undo can add it back. */
    fun deleteTask(id: Long) {
        val board = tasks.board.value
        val task = board.tasks.find { it.id == id } ?: return
        val previous = when (id) {
            in board.doneIds -> TaskStatus.Done
            in board.progressIds -> TaskStatus.Progress
            else -> TaskStatus.Todo
        }
        tasks.delete(id)
        undo.value = UndoEntry(id, "Task deleted", task = task, previousStatus = previous)
    }

    fun undo() {
        val entry = undo.value ?: return
        undo.value = null
        entry.task?.let { tasks.add(it) }
        tasks.setStatus(entry.id, entry.previousStatus)
    }

    fun dismissUndo() {
        undo.value = null
    }
}
