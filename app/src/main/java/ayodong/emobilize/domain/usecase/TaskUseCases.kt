package ayodong.emobilize.domain.usecase

import ayodong.emobilize.domain.model.Task
import ayodong.emobilize.domain.model.TaskStatus
import ayodong.emobilize.domain.repository.TaskBoard
import ayodong.emobilize.domain.repository.TaskRepository

class GetTasksUseCase(private val repository: TaskRepository) {
    operator fun invoke(): TaskBoard = repository.board()
}

class AddTaskUseCase(private val repository: TaskRepository) {
    operator fun invoke(task: Task) = repository.add(task)
}

class UpdateTaskUseCase(private val repository: TaskRepository) {
    operator fun invoke(task: Task) = repository.update(task)
}

class DeleteTaskUseCase(private val repository: TaskRepository) {
    operator fun invoke(id: Long) = repository.delete(id)
}

class SetTaskStatusUseCase(private val repository: TaskRepository) {
    operator fun invoke(id: Long, status: TaskStatus) = repository.setStatus(id, status)
}
