package ayodong.emobilize.core.data

import ayodong.emobilize.core.domain.AppSettingsRepository
import ayodong.emobilize.core.domain.CategoryRepository
import ayodong.emobilize.core.model.AppSettings
import ayodong.emobilize.core.model.Category
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// In-memory stand-ins for the SharedPreferences repositories. Previews and unit tests use them,
// and a feature can build its screen against them before the real data source exists.
// Tasks and the schedule need no fake: their real repositories are already in-memory.

class FakeCategoryRepository(
    initial: List<Category> = Category.defaults,
) : CategoryRepository {
    private val state = MutableStateFlow(initial)

    override val categories: StateFlow<List<Category>> = state.asStateFlow()

    override fun update(category: Category): Category? {
        if (state.value.none { it.id == category.id }) return null
        state.update { list -> list.map { if (it.id == category.id) category else it } }
        return category
    }
}

class FakeAppSettingsRepository(
    initial: AppSettings = AppSettings(),
) : AppSettingsRepository {
    private val state = MutableStateFlow(initial)

    override val settings: StateFlow<AppSettings> = state.asStateFlow()

    override fun update(settings: AppSettings) {
        state.value = settings
    }
}
