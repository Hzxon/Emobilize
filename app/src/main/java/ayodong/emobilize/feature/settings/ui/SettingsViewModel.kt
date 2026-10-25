package ayodong.emobilize.feature.settings.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ayodong.emobilize.core.domain.AppSettingsRepository
import ayodong.emobilize.core.domain.CategoryRepository
import ayodong.emobilize.core.domain.ScheduleRepository
import ayodong.emobilize.core.domain.TaskRepository
import ayodong.emobilize.core.model.AppSettings
import ayodong.emobilize.core.model.Category
import ayodong.emobilize.feature.settings.domain.LineEdits
import ayodong.emobilize.feature.settings.domain.SaveResult
import ayodong.emobilize.feature.settings.domain.SaveSettingsUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

/** How many stops ride a line: tasks that carry it and time blocks drawn in it. */
data class LineUsage(val tasks: Int = 0, val blocks: Int = 0) {
    val stops: Int get() = tasks + blocks
}

data class SettingsUiState(
    val appSettings: AppSettings = AppSettings(),
    val categories: List<Category> = emptyList(),
    val usage: Map<Long, LineUsage> = emptyMap(),
)

class SettingsViewModel(
    categories: CategoryRepository,
    settings: AppSettingsRepository,
    tasks: TaskRepository,
    schedule: ScheduleRepository,
    private val saveSettings: SaveSettingsUseCase,
) : ViewModel() {
    val uiState: StateFlow<SettingsUiState> = combine(
        settings.settings,
        categories.categories,
        tasks.board,
        schedule.blocks,
    ) { appSettings, lines, board, blocks ->
        val taskCounts = board.tasks.mapNotNull { it.category?.id }.groupingBy { it }.eachCount()
        val blockCounts = blocks.values.flatten().map { it.category.id }.groupingBy { it }.eachCount()
        SettingsUiState(
            appSettings = appSettings,
            categories = lines,
            usage = lines.associate { line ->
                line.id to LineUsage(taskCounts[line.id] ?: 0, blockCounts[line.id] ?: 0)
            },
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        SettingsUiState(settings.settings.value, categories.categories.value),
    )

    fun save(settings: AppSettings, edits: LineEdits): SaveResult = saveSettings(settings, edits)
}
