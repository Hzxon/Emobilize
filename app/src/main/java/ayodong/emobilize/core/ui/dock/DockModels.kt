package ayodong.emobilize.core.ui.dock

enum class NavTab { Tasks, Calendar }

enum class FabAction(val label: String, val angle: Float) {
    Add("Add", -87f),
    Update("Update", 0f),
    Edit("Edit", 58f),
    Delete("Delete", 87f),
}

fun displayAngle(action: FabAction, tab: NavTab): Float = when {
    // With Update hidden on Calendar, Edit takes the top so the three dial slices stay balanced.
    action == FabAction.Edit -> if (tab == NavTab.Calendar) 0f else -30f
    tab == NavTab.Tasks && action == FabAction.Update -> 30f
    else -> action.angle
}

/** One slice of the dial: its action and the angles where it starts and ends (0 is straight up). */
data class DialSlice(val action: FabAction, val start: Float, val end: Float) {
    val center: Float get() = (start + end) / 2f
}

/**
 * Slices share edges at the midpoint between neighbouring actions, so the dial is one arc
 * with no gaps. The outer two reach [edge] degrees past their action.
 */
fun dialSlices(actions: List<FabAction>, tab: NavTab, edge: Float = 28f): List<DialSlice> {
    val sorted = actions.sortedBy { displayAngle(it, tab) }
    return sorted.mapIndexed { index, action ->
        val angle = displayAngle(action, tab)
        val start = sorted.getOrNull(index - 1)?.let { (displayAngle(it, tab) + angle) / 2f } ?: (angle - edge)
        val end = sorted.getOrNull(index + 1)?.let { (displayAngle(it, tab) + angle) / 2f } ?: (angle + edge)
        DialSlice(action, start, end)
    }
}
