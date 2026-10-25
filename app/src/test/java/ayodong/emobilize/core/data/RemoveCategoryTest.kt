package ayodong.emobilize.core.data

import ayodong.emobilize.core.model.Category
import org.junit.Assert.assertTrue
import org.junit.Test

class RemoveCategoryTest {
    @Test
    fun clearCategoryLeavesTasksWithoutALine() {
        val tasks = InMemoryTaskRepository()
        assertTrue(tasks.board.value.tasks.any { it.category?.id == Category.Work.id })
        tasks.clearCategory(Category.Work.id)
        assertTrue(tasks.board.value.tasks.none { it.category?.id == Category.Work.id })
        assertTrue(tasks.board.value.tasks.any { it.category?.id == Category.Study.id })
    }

    @Test
    fun replaceCategoryMovesBlocksToReplacement() {
        val schedule = InMemoryScheduleRepository()
        schedule.replaceCategory(Category.Work.id, Category.Health)
        val blocks = schedule.blocks.value.values.flatten()
        assertTrue(blocks.none { it.category.id == Category.Work.id })
        assertTrue(blocks.any { it.category == Category.Health })
    }
}
