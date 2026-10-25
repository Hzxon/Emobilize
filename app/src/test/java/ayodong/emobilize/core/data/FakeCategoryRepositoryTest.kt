package ayodong.emobilize.core.data

import ayodong.emobilize.core.model.Category
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FakeCategoryRepositoryTest {
    private val repo = FakeCategoryRepository()

    @Test
    fun addAssignsNextIdAndTrims() {
        val added = repo.add("  Thesis ", "Th", 0xFF5C2D91.toInt())
        assertNotNull(added)
        assertEquals(4L, added!!.id)
        assertEquals("Thesis", added.name)
        assertEquals(4, repo.categories.value.size)
    }

    @Test
    fun colourMayRepeatButCodeMayNot() {
        assertNotNull(repo.add("Freelance", "F", Category.Work.color))
        assertNull(repo.add("Sport", "s", 0xFF007A4D.toInt()))
        assertNull(repo.update(Category.Health.copy(code = "W")))
    }

    @Test
    fun invalidInputIsRejected() {
        assertNull(repo.add("", "X", Category.lineColors[0]))
        assertNull(repo.add("Name", "", Category.lineColors[0]))
        assertNull(repo.add("Name", "ABC", Category.lineColors[0]))
        assertNull(repo.add("Name", "A", 0xFF123456.toInt()))
        assertNull(repo.add("x".repeat(25), "A", Category.lineColors[0]))
    }

    @Test
    fun deleteRemovesOnlyThatLine() {
        assertTrue(repo.delete(Category.Work.id))
        assertFalse(repo.delete(Category.Work.id))
        assertEquals(listOf(Category.Study, Category.Health), repo.categories.value)
    }

    @Test
    fun noCapOnLineCount() {
        repeat(20) { i ->
            val code = if (i < 10) "L$i" else "M${i - 10}"
            assertNotNull(repo.add("Line $i", code, Category.lineColors[i % Category.lineColors.size]))
        }
        assertEquals(23, repo.categories.value.size)
    }
}
