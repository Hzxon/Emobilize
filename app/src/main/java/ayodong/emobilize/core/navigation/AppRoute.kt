package ayodong.emobilize.core.navigation

object AppRoute {
    const val Splash = "splash"
    const val Auth = "auth"
    const val Tasks = "tasks"
    const val Calendar = "calendar"
    const val Settings = "settings"
    const val Reminders = "reminders"
    const val AddTask = "add"
    const val EditTask = "task/{taskId}"
    const val CalendarAdd = "calendar/add"
    const val CalendarTask = "calendar/task/{taskId}"
    const val CalendarBlock = "calendar/block/{blockId}/{date}"

    fun editTask(taskId: Long) = "task/$taskId"

    fun calendarTask(taskId: Long) = "calendar/task/$taskId"

    fun calendarBlock(blockId: Long, date: String) = "calendar/block/$blockId/$date"
}
