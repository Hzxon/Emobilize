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

    override fun add(name: String, code: String, color: Int): Category? {
        val id = (state.value.maxOfOrNull { it.id } ?: 0L) + 1
        val saved = Category(id, name, color, code).normalized() ?: return null
        if (codeTaken(saved)) return null
        state.update { it + saved }
        return saved
    }

    override fun update(category: Category): Category? {
        if (state.value.none { it.id == category.id }) return null
        val saved = category.normalized() ?: return null
        if (codeTaken(saved)) return null
        state.update { list -> list.map { if (it.id == saved.id) saved else it } }
        return saved
    }

    override fun delete(id: Long): Boolean {
        if (state.value.none { it.id == id }) return false
        state.update { list -> list.filter { it.id != id } }
        return true
    }

    private fun codeTaken(category: Category): Boolean =
        state.value.any { it.id != category.id && it.code.equals(category.code, ignoreCase = true) }
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
