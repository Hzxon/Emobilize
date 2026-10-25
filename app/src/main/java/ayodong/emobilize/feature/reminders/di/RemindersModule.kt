package ayodong.emobilize.feature.reminders.di

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import ayodong.emobilize.core.di.AppContainer
import ayodong.emobilize.feature.reminders.ui.RemindersViewModel

object RemindersModule {
    fun remindersViewModel(container: AppContainer): ViewModelProvider.Factory = viewModelFactory {
        initializer { RemindersViewModel(container.tasks, container.appSettings) }
    }
}
