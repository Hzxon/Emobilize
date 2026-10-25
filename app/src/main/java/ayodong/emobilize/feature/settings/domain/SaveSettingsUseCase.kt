package ayodong.emobilize.feature.settings.domain

import ayodong.emobilize.core.domain.AppSettingsRepository
import ayodong.emobilize.core.domain.CategoryRepository
import ayodong.emobilize.core.domain.ScheduleRepository
import ayodong.emobilize.core.domain.TaskRepository
import ayodong.emobilize.core.model.AppSettings
import ayodong.emobilize.core.model.Category

// Kept as a use case because it spans four repositories: a renamed or recoloured category
// must also update every task and time block that carries a copy of it.
class SaveSettingsUseCase(
    private val categories: CategoryRepository,
    private val tasks: TaskRepository,
    private val schedule: ScheduleRepository,
    private val settings: AppSettingsRepository,
) {
    operator fun invoke(newSettings: AppSettings, editedCategories: List<Category>) {
        editedCategories.forEach { category ->
            if (categories.categories.value.find { it.id == category.id } == category) return@forEach
            val saved = categories.update(category) ?: return@forEach
            tasks.applyCategory(saved)
            schedule.applyCategory(saved)
        }
        settings.update(newSettings)
    }
}
