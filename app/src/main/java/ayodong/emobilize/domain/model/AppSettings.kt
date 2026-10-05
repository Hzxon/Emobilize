package ayodong.emobilize.domain.model

enum class ThemeMode { System, Light, Dark }
enum class WeekStart { Monday, Sunday }

data class AppSettings(
    val themeMode: ThemeMode = ThemeMode.Light,
    val defaultCalendarView: CalView = CalView.Week,
    val weekStart: WeekStart = WeekStart.Monday,
    val defaultCategoryId: Long? = null,
    val defaultPriority: Priority? = null,
    val showCompletedTasks: Boolean = true,
)
