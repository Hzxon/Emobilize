package ayodong.emobilize.feature.settings.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ayodong.emobilize.core.domain.AppSettingsRepository
import ayodong.emobilize.core.domain.CategoryRepository
import ayodong.emobilize.core.model.AppSettings
import ayodong.emobilize.core.model.Category
import ayodong.emobilize.feature.settings.domain.SaveSettingsUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class SettingsUiState(
    val appSettings: AppSettings = AppSettings(),
    val categories: List<Category> = emptyList(),
)

class SettingsViewModel(
    categories: CategoryRepository,
    settings: AppSettingsRepository,
    private val saveSettings: SaveSettingsUseCase,
) : ViewModel() {
    val uiState: StateFlow<SettingsUiState> = combine(settings.settings, categories.categories, ::SettingsUiState)
        .stateIn(viewModelScope, SharingStarted.Eagerly, SettingsUiState(settings.settings.value, categories.categories.value))

    fun save(settings: AppSettings, editedCategories: List<Category>) {
        saveSettings(settings, editedCategories)
    }
}
