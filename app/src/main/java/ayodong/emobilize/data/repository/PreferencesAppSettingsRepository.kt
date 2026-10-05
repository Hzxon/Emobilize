package ayodong.emobilize.data.repository

import android.content.Context
import ayodong.emobilize.domain.model.AppSettings
import ayodong.emobilize.domain.model.CalView
import ayodong.emobilize.domain.model.Priority
import ayodong.emobilize.domain.model.ThemeMode
import ayodong.emobilize.domain.model.WeekStart
import ayodong.emobilize.domain.repository.AppSettingsRepository

class PreferencesAppSettingsRepository(context: Context) : AppSettingsRepository {
    private val prefs = context.applicationContext.getSharedPreferences(PrefsName, Context.MODE_PRIVATE)

    override fun get(): AppSettings = AppSettings(
        themeMode = enumValue(prefs.getString(ThemeKey, null), ThemeMode.Light),
        defaultCalendarView = enumValue(prefs.getString(CalendarViewKey, null), CalView.Week),
        weekStart = enumValue(prefs.getString(WeekStartKey, null), WeekStart.Monday),
        defaultCategoryId = if (prefs.contains(CategoryKey)) prefs.getLong(CategoryKey, 0L).takeIf { it > 0L } else null,
        defaultPriority = prefs.getString(PriorityKey, "")?.takeIf { it.isNotEmpty() }?.let { raw ->
            runCatching { Priority.valueOf(raw) }.getOrNull()
        },
        showCompletedTasks = prefs.getBoolean(ShowCompletedKey, true),
    )

    override fun update(settings: AppSettings) {
        prefs.edit()
            .putString(ThemeKey, settings.themeMode.name)
            .putString(CalendarViewKey, settings.defaultCalendarView.name)
            .putString(WeekStartKey, settings.weekStart.name)
            .putLong(CategoryKey, settings.defaultCategoryId ?: 0L)
            .putString(PriorityKey, settings.defaultPriority?.name.orEmpty())
            .putBoolean(ShowCompletedKey, settings.showCompletedTasks)
            .apply()
    }

    private inline fun <reified T : Enum<T>> enumValue(raw: String?, fallback: T): T =
        raw?.let { runCatching { enumValueOf<T>(it) }.getOrNull() } ?: fallback

    private companion object {
        const val PrefsName = "emobilize_app_settings"
        const val ThemeKey = "theme_mode"
        const val CalendarViewKey = "default_calendar_view"
        const val WeekStartKey = "week_start"
        const val CategoryKey = "default_category_id"
        const val PriorityKey = "default_priority"
        const val ShowCompletedKey = "show_completed_tasks"
    }
}
