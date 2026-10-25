package ayodong.emobilize.feature.settings

import ayodong.emobilize.core.data.FakeAppSettingsRepository
import ayodong.emobilize.core.data.FakeCategoryRepository
import ayodong.emobilize.core.data.InMemoryScheduleRepository
import ayodong.emobilize.core.data.InMemoryTaskRepository
import ayodong.emobilize.core.model.AppSettings
import ayodong.emobilize.core.model.Category
import ayodong.emobilize.core.model.WeekStart
import ayodong.emobilize.feature.settings.domain.SaveSettingsUseCase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SaveSettingsUseCaseTest {
    private val categories = FakeCategoryRepository()
    private val tasks = InMemoryTaskRepository()
    private val schedule = InMemoryScheduleRepository()
    private val settings = FakeAppSettingsRepository()
    private val save = SaveSettingsUseCase(categories, tasks, schedule, settings)

    @Test
    fun renamedCategoryReachesTasksAndBlocks() {
        val renamed = Category.Work.copy(name = "Job")
        save(AppSettings(), Category.defaults.map { if (it.id == renamed.id) renamed else it })

        assertTrue(tasks.board.value.tasks.filter { it.category?.id == renamed.id }.all { it.category == renamed })
        assertTrue(schedule.blocks.value.values.flatten().filter { it.category.id == renamed.id }.all { it.category == renamed })
    }

    @Test
    fun settingsAreStored() {
        save(AppSettings(weekStart = WeekStart.Sunday), categories.categories.value)
        assertEquals(WeekStart.Sunday, settings.settings.value.weekStart)
    }
}
