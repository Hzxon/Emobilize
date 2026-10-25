package ayodong.emobilize.core.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CategoryTest {
    @Test
    fun codeForTakesInitialThenTwoLetters() {
        assertEquals("T", Category.codeFor("Thesis"))
        assertEquals("Th", Category.codeFor("Thesis", setOf("t")))
        assertEquals("F", Category.codeFor("  freelance work"))
        assertEquals("?", Category.codeFor("   "))
    }

    @Test
    fun normalizedTrimsAndValidates() {
        assertEquals(Category(5, "Home", Category.lineColors[4], "Ho"), Category(5, " Home ", Category.lineColors[4], "Ho ").normalized())
        assertNull(Category(5, "Home", Category.lineColors[4], "H o").normalized())
        assertNull(Category(5, "Home", 0xFF000001.toInt(), "H").normalized())
    }

    @Test
    fun everyLineColourIsOpaqueAndDistinct() {
        assertEquals(Category.lineColors.size, Category.lineColors.toSet().size)
        Category.lineColors.forEach { assertEquals(0xFF, (it ushr 24) and 0xFF) }
    }
}
