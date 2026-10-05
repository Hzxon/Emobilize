package ayodong.emobilize.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ayodong.emobilize.data.repository.InMemoryScheduleRepository
import ayodong.emobilize.data.repository.InMemoryTaskRepository
import ayodong.emobilize.domain.usecase.AddTaskUseCase
import ayodong.emobilize.domain.usecase.DeleteTaskUseCase
import ayodong.emobilize.domain.usecase.DeleteTimeBlockUseCase
import ayodong.emobilize.domain.usecase.GetScheduleUseCase
import ayodong.emobilize.domain.usecase.GetTasksUseCase
import ayodong.emobilize.domain.usecase.PlaceTimeBlockUseCase
import ayodong.emobilize.domain.usecase.SetTaskStatusUseCase
import ayodong.emobilize.domain.usecase.UpdateTaskUseCase
import ayodong.emobilize.ui.tasks.TasksViewModel

class AppContainer {
    private val tasks = InMemoryTaskRepository()
    private val schedule = InMemoryScheduleRepository()

    val getTasks = GetTasksUseCase(tasks)
    val addTask = AddTaskUseCase(tasks)
    val updateTask = UpdateTaskUseCase(tasks)
    val deleteTask = DeleteTaskUseCase(tasks)
    val setTaskStatus = SetTaskStatusUseCase(tasks)
    val getSchedule = GetScheduleUseCase(schedule)
    val placeTimeBlock = PlaceTimeBlockUseCase(schedule)
    val deleteTimeBlock = DeleteTimeBlockUseCase(schedule)
}

class TasksViewModelFactory(
    private val container: AppContainer,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TasksViewModel(
            getTasks = container.getTasks,
            addTask = container.addTask,
            updateTask = container.updateTask,
            deleteTask = container.deleteTask,
            setTaskStatus = container.setTaskStatus,
            getSchedule = container.getSchedule,
            placeTimeBlock = container.placeTimeBlock,
            deleteTimeBlock = container.deleteTimeBlock,
        ) as T
    }
}
