package ayodong.emobilize.di

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ayodong.emobilize.data.repository.InMemoryScheduleRepository
import ayodong.emobilize.data.repository.InMemoryTaskRepository
import ayodong.emobilize.data.repository.PreferencesAuthRepository
import ayodong.emobilize.data.repository.PreferencesCategoryRepository
import ayodong.emobilize.data.repository.PreferencesAppSettingsRepository
import ayodong.emobilize.domain.usecase.GetAppSettingsUseCase
import ayodong.emobilize.domain.usecase.UpdateAppSettingsUseCase
import ayodong.emobilize.domain.usecase.AddTaskUseCase
import ayodong.emobilize.domain.usecase.DeleteTaskUseCase
import ayodong.emobilize.domain.usecase.DeleteTimeBlockUseCase
import ayodong.emobilize.domain.usecase.GetCategoriesUseCase
import ayodong.emobilize.domain.usecase.GetScheduleUseCase
import ayodong.emobilize.domain.usecase.GetTasksUseCase
import ayodong.emobilize.domain.usecase.UpdateCategoryUseCase
import ayodong.emobilize.domain.usecase.LoginUseCase
import ayodong.emobilize.domain.usecase.PlaceTimeBlockUseCase
import ayodong.emobilize.domain.usecase.RegisterAccountUseCase
import ayodong.emobilize.domain.usecase.SetTaskStatusUseCase
import ayodong.emobilize.domain.usecase.UpdateTaskUseCase
import ayodong.emobilize.ui.auth.AuthViewModel
import ayodong.emobilize.ui.tasks.TasksViewModel

class AppContainer(context: Context) {
    private val tasks = InMemoryTaskRepository()
    private val schedule = InMemoryScheduleRepository()
    private val auth = PreferencesAuthRepository(context)
    private val categories = PreferencesCategoryRepository(context)
    private val appSettings = PreferencesAppSettingsRepository(context)

    val getTasks = GetTasksUseCase(tasks)
    val addTask = AddTaskUseCase(tasks)
    val updateTask = UpdateTaskUseCase(tasks)
    val deleteTask = DeleteTaskUseCase(tasks)
    val setTaskStatus = SetTaskStatusUseCase(tasks)
    val getSchedule = GetScheduleUseCase(schedule)
    val placeTimeBlock = PlaceTimeBlockUseCase(schedule)
    val deleteTimeBlock = DeleteTimeBlockUseCase(schedule)
    val login = LoginUseCase(auth)
    val register = RegisterAccountUseCase(auth)
    val getCategories = GetCategoriesUseCase(categories)
    val updateCategory = UpdateCategoryUseCase(categories, tasks, schedule)
    val getAppSettings = GetAppSettingsUseCase(appSettings)
    val updateAppSettings = UpdateAppSettingsUseCase(appSettings)
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
            getCategories = container.getCategories,
            saveCategory = container.updateCategory,
            getAppSettings = container.getAppSettings,
            saveAppSettings = container.updateAppSettings,
        ) as T
    }
}

class AuthViewModelFactory(
    private val container: AppContainer,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AuthViewModel(
            login = container.login,
            register = container.register,
        ) as T
    }
}
