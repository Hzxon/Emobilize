package ayodong.emobilize.core.data

import ayodong.emobilize.core.domain.TaskBoard
import ayodong.emobilize.core.domain.TaskRepository
import ayodong.emobilize.core.model.Category
import ayodong.emobilize.core.model.Task
import ayodong.emobilize.core.model.TaskStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class InMemoryTaskRepository(
    initial: TaskBoard = TaskBoard(sampleTasks(), doneIds = setOf(1L), progressIds = setOf(4L, 7L)),
) : TaskRepository {
    private val state = MutableStateFlow(initial)

    override val board: StateFlow<TaskBoard> = state.asStateFlow()

    override fun add(task: Task) {
        state.update { it.copy(tasks = it.tasks + task) }
    }

    override fun update(task: Task) {
        state.update { board -> board.copy(tasks = board.tasks.map { if (it.id == task.id) task else it }) }
    }

    override fun delete(id: Long) {
        state.update { board ->
            TaskBoard(
                tasks = board.tasks.filter { it.id != id },
                doneIds = board.doneIds - id,
                progressIds = board.progressIds - id,
            )
        }
    }

    override fun setStatus(id: Long, status: TaskStatus) {
        state.update { board ->
            when (status) {
                TaskStatus.Done -> board.copy(doneIds = board.doneIds + id, progressIds = board.progressIds - id)
                TaskStatus.Progress -> board.copy(progressIds = board.progressIds + id, doneIds = board.doneIds - id)
                TaskStatus.Todo -> board.copy(doneIds = board.doneIds - id, progressIds = board.progressIds - id)
            }
        }
    }

    override fun applyCategory(category: Category) {
        state.update { board ->
            board.copy(
                tasks = board.tasks.map { task ->
                    if (task.category?.id == category.id) task.copy(category = category) else task
                },
            )
        }
    }
}
