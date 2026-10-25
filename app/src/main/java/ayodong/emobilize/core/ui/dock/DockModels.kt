package ayodong.emobilize.core.ui.dock

enum class NavTab { Tasks, Calendar }

enum class FabAction(val label: String, val angle: Float) {
    Add("Add", -87f),
    Update("Update", 0f),
    Edit("Edit", 58f),
    Delete("Delete", 87f),
}

fun displayAngle(action: FabAction, tab: NavTab): Float = when {
    action == FabAction.Edit -> -30f
    tab == NavTab.Tasks && action == FabAction.Update -> 30f
    else -> action.angle
}
