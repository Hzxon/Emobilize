package ayodong.emobilize.core.ui.dock

import org.junit.Assert.assertEquals
import org.junit.Test

class DialSlicesTest {
    private fun assertNoGaps(slices: List<DialSlice>) {
        slices.zipWithNext().forEach { (a, b) -> assertEquals(a.end, b.start, 0.001f) }
    }

    @Test
    fun tasksDialIsOneArcInActionOrder() {
        val slices = dialSlices(FabAction.entries, NavTab.Tasks)
        assertEquals(listOf(FabAction.Add, FabAction.Edit, FabAction.Update, FabAction.Delete), slices.map { it.action })
        assertNoGaps(slices)
        assertEquals(-115f, slices.first().start, 0.001f)
        assertEquals(115f, slices.last().end, 0.001f)
    }

    @Test
    fun calendarDialKeepsEditOnTop() {
        val slices = dialSlices(FabAction.entries.filter { it != FabAction.Update }, NavTab.Calendar)
        assertEquals(listOf(FabAction.Add, FabAction.Edit, FabAction.Delete), slices.map { it.action })
        assertNoGaps(slices)
        assertEquals(0f, slices[1].center, 0.001f)
    }
}
