package ayodong.emobilize.feature.settings.domain

import ayodong.emobilize.core.domain.AppSettingsRepository
import ayodong.emobilize.core.domain.CategoryRepository
import ayodong.emobilize.core.domain.ScheduleRepository
import ayodong.emobilize.core.domain.TaskRepository
import ayodong.emobilize.core.model.AppSettings
import ayodong.emobilize.core.model.Category

/** A line removed on the settings screen. [replacementId] takes its time blocks; null when no block uses it. */
data class LineDeletion(val id: Long, val replacementId: Long?)

/**
 * Everything the settings screen wants saved about lines: the list as the user left it, where a
 * line with an id of zero or less is new, plus the lines removed since the screen opened.
 */
data class LineEdits(
    val lines: List<Category>,
    val deletions: List<LineDeletion> = emptyList(),
) {
    companion object {
        fun isNew(line: Category): Boolean = line.id <= 0L
    }
}

sealed interface SaveResult {
    data object Saved : SaveResult

    /** Nothing was written: two lines share [code]. */
    data class DuplicateCode(val code: String) : SaveResult

    /** Nothing was written: a line failed [Category.normalized]. */
    data object InvalidLine : SaveResult

    /** Nothing was written: the save would leave no line at all. */
    data object LastLine : SaveResult
}

// Kept as a use case because it spans four repositories: a renamed or recoloured line must also
// update every task and time block that carries a copy of it, a deleted line must release its
// tasks and move its blocks, and a new line's id is only known after the repository assigns it.
class SaveSettingsUseCase(
    private val categories: CategoryRepository,
    private val tasks: TaskRepository,
    private val schedule: ScheduleRepository,
    private val settings: AppSettingsRepository,
) {
    operator fun invoke(newSettings: AppSettings, edits: LineEdits): SaveResult {
        val lines = edits.lines.map { it.normalized() ?: return SaveResult.InvalidLine }
        if (lines.isEmpty()) return SaveResult.LastLine
        lines.groupBy { it.code.uppercase() }.values.firstOrNull { it.size > 1 }?.let { clash ->
            return SaveResult.DuplicateCode(clash.last().code)
        }

        // Stored ids by the id the screen used, so new lines and the default-category setting line up.
        val savedIds = HashMap<Long, Long>()
        var pending = lines
        pending = applyLines(pending, savedIds)
        edits.deletions.forEach { deletion ->
            val replacement = deletion.replacementId?.let(savedIds::get)?.let { id ->
                categories.categories.value.find { it.id == id }
            } ?: categories.categories.value.first { it.id != deletion.id }
            tasks.clearCategory(deletion.id)
            if (schedule.blocks.value.values.any { list -> list.any { it.category.id == deletion.id } }) {
                schedule.replaceCategory(deletion.id, replacement)
            }
            categories.delete(deletion.id)
        }
        // A code freed by a deletion may have been waiting on it; the two swapped lines of a cycle
        // wait on each other, so park one of them on a free code and try again.
        pending = applyLines(pending, savedIds)
        repeat(lines.size) {
            val parked = pending.firstOrNull { !LineEdits.isNew(it) } ?: return@repeat
            val stored = categories.categories.value.first { it.id == parked.id }
            categories.update(stored.copy(code = freeCode(lines)))
            pending = applyLines(pending, savedIds)
        }

        val defaultId = newSettings.defaultCategoryId?.let(savedIds::get)
        settings.update(newSettings.copy(defaultCategoryId = defaultId))
        return SaveResult.Saved
    }

    /** Writes every line the repository accepts and returns the ones it refused (a code still taken). */
    private fun applyLines(lines: List<Category>, savedIds: MutableMap<Long, Long>): List<Category> =
        lines.filterNot { line ->
            val stored = categories.categories.value.find { it.id == line.id }
            val saved = when {
                LineEdits.isNew(line) -> categories.add(line.name, line.code, line.color)
                stored == line -> line
                else -> categories.update(line)?.also { saved ->
                    tasks.applyCategory(saved)
                    schedule.applyCategory(saved)
                }
            } ?: return@filterNot false
            savedIds[line.id] = saved.id
            true
        }

    private fun freeCode(wanted: List<Category>): String {
        val taken = (wanted + categories.categories.value).map { it.code.uppercase() }.toSet()
        val alphabet = ('0'..'9') + ('A'..'Z')
        for (a in alphabet) for (b in alphabet) {
            val code = "$a$b"
            if (code !in taken) return code
        }
        return "??"
    }
}
