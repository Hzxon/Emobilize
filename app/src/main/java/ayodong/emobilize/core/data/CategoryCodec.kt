package ayodong.emobilize.core.data

import ayodong.emobilize.core.model.Category

/**
 * Text form of the category list in SharedPreferences. One record per category, fields in order:
 * id, name, colour (hex ARGB), code. Records written before line codes existed have three fields;
 * they get a generated code and their colour is mapped through [Category.legacyLineColors].
 * Kept apart from the repository so the format can be tested without a Context.
 */
object CategoryCodec {
    const val Record = "\u001E"
    const val Field = "\u001F"

    fun encode(categories: List<Category>): String = categories.joinToString(Record) {
        listOf(it.id.toString(), it.name, it.color.toUInt().toString(16), it.code).joinToString(Field)
    }

    /** Parsed categories, or [Category.defaults] when nothing usable is stored. */
    fun decode(raw: String): List<Category> {
        if (raw.isEmpty()) return Category.defaults
        val taken = mutableSetOf<String>()
        val parsed = raw.split(Record).mapNotNull { line ->
            val parts = line.split(Field)
            if (parts.size != 3 && parts.size != 4) return@mapNotNull null
            val id = parts[0].toLongOrNull() ?: return@mapNotNull null
            val stored = parts[2].toUIntOrNull(16)?.toInt() ?: return@mapNotNull null
            val color = Category.legacyLineColors[stored] ?: stored
            val code = parts.getOrNull(3) ?: Category.codeFor(parts[1], taken)
            val category = Category(id, parts[1], color, code).normalized() ?: return@mapNotNull null
            if (!taken.add(category.code.uppercase())) return@mapNotNull null
            category
        }
        return parsed.distinctBy { it.id }.ifEmpty { Category.defaults }
    }
}
