package ayodong.emobilize.data.repository

import android.content.Context
import ayodong.emobilize.domain.model.Category
import ayodong.emobilize.domain.repository.CategoryRepository

class PreferencesCategoryRepository(context: Context) : CategoryRepository {
    private val prefs = context.applicationContext.getSharedPreferences(PrefsName, Context.MODE_PRIVATE)
    private val items = load().toMutableList()

    override fun all(): List<Category> = items.toList()

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
