package ayodong.emobilize.data.repository

import ayodong.emobilize.domain.model.Task
import ayodong.emobilize.domain.model.TaskStatus
import ayodong.emobilize.domain.repository.TaskBoard
import ayodong.emobilize.domain.repository.TaskRepository

class InMemoryTaskRepository : TaskRepository {
    private var tasks = sampleTasks()
    private var doneIds = setOf(1L)
    private var progressIds = setOf<Long>()

    override fun board(): TaskBoard = TaskBoard(tasks, doneIds, progressIds)

    override fun add(task: Task) {
        tasks = tasks + task
    }

    override fun update(task: Task) {
        tasks = tasks.map { if (it.id == task.id) task else it }
    }

    override fun delete(id: Long) {
        tasks = tasks.filter { it.id != id }
        doneIds = doneIds - id
        progressIds = progressIds - id
    }

    override fun setStatus(id: Long, status: TaskStatus) {
        if (status == TaskStatus.Done) {
            doneIds = doneIds + id
            progressIds = progressIds - id
        } else {
            progressIds = progressIds + id
            doneIds = doneIds - id
        }
    }
}
