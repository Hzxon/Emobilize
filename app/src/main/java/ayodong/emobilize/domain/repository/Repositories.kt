package ayodong.emobilize.domain.repository

import ayodong.emobilize.domain.model.Category
import ayodong.emobilize.domain.model.Task
import ayodong.emobilize.domain.model.TaskStatus
import ayodong.emobilize.domain.model.TimeBlock
import java.time.LocalDate

data class TaskBoard(
    val tasks: List<Task>,
    val doneIds: Set<Long>,
    val progressIds: Set<Long>,
)

interface TaskRepository {
    fun board(): TaskBoard
    fun add(task: Task)
    fun update(task: Task)
    fun delete(id: Long)
    fun setStatus(id: Long, status: TaskStatus)
    fun applyCategory(category: Category)
}

interface ScheduleRepository {
    fun blocks(): Map<LocalDate, List<TimeBlock>>
    fun place(block: TimeBlock, date: LocalDate)
    fun delete(id: Long)
    fun applyCategory(category: Category)
}

interface CategoryRepository {
    fun all(): List<Category>
    fun update(category: Category): Category?
}
