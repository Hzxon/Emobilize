package ayodong.emobilize.core.data

import android.content.Context
import ayodong.emobilize.core.domain.CategoryRepository
import ayodong.emobilize.core.model.Category
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PreferencesCategoryRepository(context: Context) : CategoryRepository {
    private val prefs = context.applicationContext.getSharedPreferences(PrefsName, Context.MODE_PRIVATE)
    private val items = load().toMutableList()

    private val state = MutableStateFlow(items.toList())

    override val categories: StateFlow<List<Category>> = state.asStateFlow()

    override fun update(category: Category): Category? {
        val index = items.indexOfFirst { it.id == category.id }
        if (index < 0) return null
        val name = category.name.trim()
        if (name.isEmpty() || name.length > 24) return null
        if (name.any { it == RecordChar || it == FieldChar }) return null
        if (category.color !in Category.colorChoices) return null
        val saved = category.copy(name = name)
        items[index] = saved
        save()
        state.value = items.toList()
        return saved
    }

    private fun load(): List<Category> {
        val raw = prefs.getString(CategoriesKey, null).orEmpty()
        if (raw.isEmpty()) return Category.defaults
        val parsed = raw.split(Record).mapNotNull { line ->
            val parts = line.split(Field)
            if (parts.size != 3) return@mapNotNull null
            val id = parts[0].toLongOrNull() ?: return@mapNotNull null
            val color = parts[2].toUIntOrNull(16)?.toInt() ?: return@mapNotNull null
            if (color !in Category.colorChoices) return@mapNotNull null
            Category(id = id, name = parts[1], color = color)
        }
        val known = Category.defaults.map { it.id }.toSet()
        return if (parsed.size == Category.Limit && parsed.map { it.id }.toSet() == known) parsed else Category.defaults
    }

    private fun save() {
        val raw = items.joinToString(Record) {
            listOf(it.id.toString(), it.name, it.color.toUInt().toString(16)).joinToString(Field)
        }
        prefs.edit().putString(CategoriesKey, raw).apply()
    }

    private companion object {
        const val PrefsName = "emobilize_categories"
        const val CategoriesKey = "categories"
        const val Record = "\u001E"
        const val Field = "\u001F"
        const val RecordChar = '\u001E'
        const val FieldChar = '\u001F'
    }
}
