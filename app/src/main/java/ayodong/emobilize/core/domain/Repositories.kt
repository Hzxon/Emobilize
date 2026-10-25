package ayodong.emobilize.core.domain

import ayodong.emobilize.core.model.AppSettings
import ayodong.emobilize.core.model.Category
import ayodong.emobilize.core.model.Task
import ayodong.emobilize.core.model.TaskStatus
import ayodong.emobilize.core.model.TimeBlock
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate

// Shared contracts. Tasks, the schedule, categories and settings are read by several features,
// so their repositories live in core and expose StateFlow: a write on one screen shows on every other.
// Feature-only repositories (for example AuthRepository) live in feature/<x>/domain instead.

data class TaskBoard(
    val tasks: List<Task>,
    val doneIds: Set<Long>,
    val progressIds: Set<Long>,
)

interface TaskRepository {
    val board: StateFlow<TaskBoard>
    fun add(task: Task)
    fun update(task: Task)
    fun delete(id: Long)
    fun setStatus(id: Long, status: TaskStatus)
    fun applyCategory(category: Category)

    /** Tasks on the deleted line keep running without a category. */
    fun clearCategory(id: Long)
}

interface ScheduleRepository {
    val blocks: StateFlow<Map<LocalDate, List<TimeBlock>>>
    fun place(block: TimeBlock, date: LocalDate)
    fun delete(id: Long)
    fun applyCategory(category: Category)

    /** A time block always has a line, so blocks on a deleted line move to [replacement]. */
    fun replaceCategory(id: Long, replacement: Category)
}

interface CategoryRepository {
    val categories: StateFlow<List<Category>>

    /** Null when the name or code is invalid, or the code is already taken (colours may repeat). */
    fun add(name: String, code: String, color: Int): Category?
    fun update(category: Category): Category?
    fun delete(id: Long): Boolean
}

interface AppSettingsRepository {
    val settings: StateFlow<AppSettings>
    fun update(settings: AppSettings)
}
