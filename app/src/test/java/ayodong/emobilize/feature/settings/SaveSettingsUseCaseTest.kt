package ayodong.emobilize.feature.settings

import ayodong.emobilize.core.data.FakeAppSettingsRepository
import ayodong.emobilize.core.data.FakeCategoryRepository
import ayodong.emobilize.core.data.InMemoryScheduleRepository
import ayodong.emobilize.core.data.InMemoryTaskRepository
import ayodong.emobilize.core.model.AppSettings
import ayodong.emobilize.core.model.Category
import ayodong.emobilize.core.model.WeekStart
import ayodong.emobilize.feature.settings.domain.LineDeletion
import ayodong.emobilize.feature.settings.domain.LineEdits
import ayodong.emobilize.feature.settings.domain.SaveResult
import ayodong.emobilize.feature.settings.domain.SaveSettingsUseCase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SaveSettingsUseCaseTest {
    private val categories = FakeCategoryRepository()
    private val tasks = InMemoryTaskRepository()
    private val schedule = InMemoryScheduleRepository()
    private val settings = FakeAppSettingsRepository()
    private val save = SaveSettingsUseCase(categories, tasks, schedule, settings)

    private val blocks get() = schedule.blocks.value.values.flatten()

    @Test
    fun renamedCategoryReachesTasksAndBlocks() {
        val renamed = Category.Work.copy(name = "Job")
        val result = save(AppSettings(), LineEdits(Category.defaults.map { if (it.id == renamed.id) renamed else it }))

        assertEquals(SaveResult.Saved, result)
        assertTrue(tasks.board.value.tasks.filter { it.category?.id == renamed.id }.all { it.category == renamed })
        assertTrue(blocks.filter { it.category.id == renamed.id }.all { it.category == renamed })
    }

    @Test
    fun settingsAreStored() {
        save(AppSettings(weekStart = WeekStart.Sunday), LineEdits(categories.categories.value))
        assertEquals(WeekStart.Sunday, settings.settings.value.weekStart)
    }

    @Test
    fun newLineIsAddedAndCanBeTheDefault() {
        val gym = Category(-1L, "Gym", Category.lineColors[3], "G")
        val result = save(AppSettings(defaultCategoryId = gym.id), LineEdits(Category.defaults + gym))

        assertEquals(SaveResult.Saved, result)
        val saved = categories.categories.value.single { it.code == "G" }
        assertEquals("Gym", saved.name)
        assertTrue(saved.id > 0L)
        assertEquals(saved.id, settings.settings.value.defaultCategoryId)
    }

    @Test
    fun deletedLineReleasesTasksAndMovesBlocks() {
        val work = Category.Work
        val result = save(
            AppSettings(defaultCategoryId = work.id),
            LineEdits(
                lines = listOf(Category.Study, Category.Health),
                deletions = listOf(LineDeletion(work.id, replacementId = Category.Health.id)),
            ),
        )

        assertEquals(SaveResult.Saved, result)
        assertTrue(categories.categories.value.none { it.id == work.id })
        assertTrue(tasks.board.value.tasks.none { it.category?.id == work.id })
        assertTrue(tasks.board.value.tasks.any { it.category == null })
        assertTrue(blocks.none { it.category.id == work.id })
        assertTrue(blocks.any { it.title == "Team Standup" && it.category == Category.Health })
        assertNull(settings.settings.value.defaultCategoryId)
    }

    @Test
    fun duplicateCodeIsRejectedWithoutWriting() {
        val clash = Category.Health.copy(code = "w")
        val result = save(
            AppSettings(weekStart = WeekStart.Sunday),
            LineEdits(listOf(Category.Study, Category.Work, clash)),
        )

        assertEquals(SaveResult.DuplicateCode("w"), result)
        assertEquals(Category.defaults, categories.categories.value)
        assertEquals(WeekStart.Monday, settings.settings.value.weekStart)
    }

    @Test
    fun swappedCodesAreSaved() {
        val study = Category.Study.copy(code = "W")
        val work = Category.Work.copy(code = "S")
        val result = save(AppSettings(), LineEdits(listOf(study, work, Category.Health)))

        assertEquals(SaveResult.Saved, result)
        assertEquals("W", categories.categories.value.first { it.id == study.id }.code)
        assertEquals("S", categories.categories.value.first { it.id == work.id }.code)
    }

    @Test
    fun removingEveryLineIsRefused() {
        val result = save(AppSettings(), LineEdits(emptyList(), Category.defaults.map { LineDeletion(it.id, null) }))

        assertEquals(SaveResult.LastLine, result)
        assertEquals(Category.defaults, categories.categories.value)
    }
}
