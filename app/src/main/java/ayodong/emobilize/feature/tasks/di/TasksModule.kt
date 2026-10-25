package ayodong.emobilize.feature.tasks.di

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import ayodong.emobilize.core.di.AppContainer
import ayodong.emobilize.feature.tasks.ui.TaskFormViewModel
import ayodong.emobilize.feature.tasks.ui.TasksViewModel

object TasksModule {
    fun tasksViewModel(container: AppContainer): ViewModelProvider.Factory = viewModelFactory {
        initializer { TasksViewModel(container.tasks, container.appSettings) }
    }

    // taskId == null opens the Add card; a task id opens the Edit sheet.
    fun taskFormViewModel(container: AppContainer, taskId: Long?): ViewModelProvider.Factory = viewModelFactory {
        initializer { TaskFormViewModel(container.tasks, container.categories, container.appSettings, taskId) }
    }
}
