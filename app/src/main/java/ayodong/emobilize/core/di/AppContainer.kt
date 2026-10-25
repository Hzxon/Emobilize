package ayodong.emobilize.core.di

import android.content.Context
import androidx.compose.runtime.staticCompositionLocalOf
import ayodong.emobilize.core.data.InMemoryScheduleRepository
import ayodong.emobilize.core.data.InMemoryTaskRepository
import ayodong.emobilize.core.data.PreferencesAppSettingsRepository
import ayodong.emobilize.core.data.PreferencesCategoryRepository
import ayodong.emobilize.core.domain.AppSettingsRepository
import ayodong.emobilize.core.domain.CategoryRepository
import ayodong.emobilize.core.domain.ScheduleRepository
import ayodong.emobilize.core.domain.TaskRepository

// Holds only the shared repositories. Each feature builds its own ViewModels in feature/<x>/di,
// so adding a feature never edits this file. Created once in EmobilizeApplication so every
// ViewModel, before and after a configuration change, sees the same data.
class AppContainer(context: Context) {
    val appContext: Context = context.applicationContext
    val tasks: TaskRepository = InMemoryTaskRepository()
    val schedule: ScheduleRepository = InMemoryScheduleRepository()
    val categories: CategoryRepository = PreferencesCategoryRepository(appContext)
    val appSettings: AppSettingsRepository = PreferencesAppSettingsRepository(appContext)
}

val LocalAppContainer = staticCompositionLocalOf<AppContainer> { error("AppContainer is not provided") }
