package ayodong.emobilize.core.data

import android.content.Context
import ayodong.emobilize.core.domain.CategoryRepository
import ayodong.emobilize.core.model.Category
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PreferencesCategoryRepository(context: Context) : CategoryRepository {
    private val prefs = context.applicationContext.getSharedPreferences(PrefsName, Context.MODE_PRIVATE)
    private val items = CategoryCodec.decode(prefs.getString(CategoriesKey, null).orEmpty()).toMutableList()

    private val state = MutableStateFlow(items.toList())

    override val categories: StateFlow<List<Category>> = state.asStateFlow()

    override fun add(name: String, code: String, color: Int): Category? {
        val id = (items.maxOfOrNull { it.id } ?: 0L) + 1
        val saved = Category(id, name, color, code).normalized() ?: return null
        if (codeTaken(saved)) return null
        items += saved
        commit()
        return saved
    }

    override fun update(category: Category): Category? {
        val index = items.indexOfFirst { it.id == category.id }
        if (index < 0) return null
        val saved = category.normalized() ?: return null
        if (codeTaken(saved)) return null
        items[index] = saved
        commit()
        return saved
    }

    override fun delete(id: Long): Boolean {
        if (!items.removeAll { it.id == id }) return false
        commit()
        return true
    }

    private fun codeTaken(category: Category): Boolean =
        items.any { it.id != category.id && it.code.equals(category.code, ignoreCase = true) }

    private fun commit() {
        prefs.edit().putString(CategoriesKey, CategoryCodec.encode(items)).apply()
        state.value = items.toList()
    }

    private companion object {
        const val PrefsName = "emobilize_categories"
        const val CategoriesKey = "categories"
    }
}
