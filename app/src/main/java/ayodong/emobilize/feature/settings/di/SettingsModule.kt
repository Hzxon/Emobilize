package ayodong.emobilize.feature.settings.di

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import ayodong.emobilize.core.di.AppContainer
import ayodong.emobilize.feature.settings.domain.SaveSettingsUseCase
import ayodong.emobilize.feature.settings.ui.SettingsViewModel

object SettingsModule {
    fun settingsViewModel(container: AppContainer): ViewModelProvider.Factory = viewModelFactory {
        initializer {
            SettingsViewModel(
                categories = container.categories,
                settings = container.appSettings,
                saveSettings = SaveSettingsUseCase(container.categories, container.tasks, container.schedule, container.appSettings),
            )
        }
    }
}
