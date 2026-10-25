package ayodong.emobilize.core.data

import ayodong.emobilize.core.model.Category
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CategoryCodecTest {
    private val r = CategoryCodec.Record
    private val f = CategoryCodec.Field

    @Test
    fun emptyStringGivesDefaults() {
        assertEquals(Category.defaults, CategoryCodec.decode(""))
    }

    @Test
    fun roundTripKeepsEveryField() {
        val lines = listOf(
            Category(1, "Study", 0xFFD6001C.toInt(), "S"),
            Category(7, "Thesis", 0xFF5C2D91.toInt(), "Th"),
            Category(9, "Freelance", 0xFF0057B8.toInt(), "F"),
        )
        assertEquals(lines, CategoryCodec.decode(CategoryCodec.encode(lines)))
    }

    @Test
    fun legacyThreeFieldRecordsMigrateColourAndGetCode() {
        val raw = listOf(
            "1${f}Study${f}ffe76e50",
            "2${f}Work${f}fff4a462",
            "3${f}Health${f}ff2a9d90",
        ).joinToString(r)
        val decoded = CategoryCodec.decode(raw)
        assertEquals(listOf("S", "W", "H"), decoded.map { it.code })
        assertEquals(Category.defaults.map { it.color }, decoded.map { it.color })
    }

    @Test
    fun legacyRecordsWithSameInitialGetTwoLetterCode() {
        val raw = listOf("1${f}Study${f}ffe76e50", "2${f}Sport${f}ff2a9d90").joinToString(r)
        assertEquals(listOf("S", "Sp"), CategoryCodec.decode(raw).map { it.code })
    }

    @Test
    fun unknownColourAndDuplicateCodeAreDropped() {
        val raw = listOf(
            "1${f}Study${f}ffd6001c${f}S",
            "2${f}Weird${f}ff123456${f}W",
            "3${f}Sport${f}ff007a4d${f}s",
        ).joinToString(r)
        val decoded = CategoryCodec.decode(raw)
        assertEquals(listOf(1L), decoded.map { it.id })
    }

    @Test
    fun allBadRecordsFallBackToDefaults() {
        assertEquals(Category.defaults, CategoryCodec.decode("garbage"))
    }

    @Test
    fun moreThanThreeLinesSurvive() {
        val lines = (1..12).map { Category(it.toLong(), "Line $it", Category.lineColors[it - 1], "L$it".take(2) + "") }
            .mapIndexed { i, c -> c.copy(code = listOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L")[i]) }
        assertEquals(12, CategoryCodec.decode(CategoryCodec.encode(lines)).size)
        assertTrue(CategoryCodec.decode(CategoryCodec.encode(lines)).all { it.color in Category.lineColors })
    }
}
