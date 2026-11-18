package ayodong.emobilize.core.di

import android.content.Context
import androidx.compose.runtime.staticCompositionLocalOf
import ayodong.emobilize.BuildConfig
import ayodong.emobilize.core.data.CategoryCodec
import ayodong.emobilize.core.data.PreferencesAppSettingsRepository
import ayodong.emobilize.core.data.local.LocalStore
import ayodong.emobilize.core.data.local.RoomCategoryRepository
import ayodong.emobilize.core.data.local.RoomScheduleRepository
import ayodong.emobilize.core.data.local.RoomTaskRepository
import ayodong.emobilize.core.domain.AppSettingsRepository
import ayodong.emobilize.core.domain.CategoryRepository
import ayodong.emobilize.core.domain.ScheduleRepository
import ayodong.emobilize.core.domain.TaskRepository
import ayodong.emobilize.core.sync.SyncManager
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.serializer.KotlinXSerializer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.serialization.json.Json

// Holds only the shared repositories. Each feature builds its own ViewModels in feature/<x>/di,
// so adding a feature never edits this file. Created once in EmobilizeApplication so every
// ViewModel, before and after a configuration change, sees the same data.
class AppContainer(context: Context) {
    val appContext: Context = context.applicationContext

    /** Null when local.properties has no Supabase URL and key; the app then runs offline only. */
    val supabase: SupabaseClient? = BuildConfig.SUPABASE_URL.takeIf { it.isNotBlank() }?.let { url ->
        createSupabaseClient(url, BuildConfig.SUPABASE_KEY) {
            defaultSerializer = KotlinXSerializer(Json { ignoreUnknownKeys = true })
            install(Auth)
            install(Postgrest)
        }
    }

    private val store = LocalStore(appContext, CoroutineScope(SupervisorJob() + Dispatchers.IO))

    // Lines saved by versions before Room are read once, to seed the first database.
    private val roomCategories = RoomCategoryRepository(
        store,
        CategoryCodec.decode(
            appContext.getSharedPreferences("emobilize_categories", Context.MODE_PRIVATE)
                .getString("categories", null).orEmpty(),
        ),
    )
    private val roomTasks = RoomTaskRepository(store, roomCategories)
    private val roomSchedule = RoomScheduleRepository(store, roomCategories)

    val categories: CategoryRepository = roomCategories
    val tasks: TaskRepository = roomTasks
    val schedule: ScheduleRepository = roomSchedule
    val appSettings: AppSettingsRepository = PreferencesAppSettingsRepository(appContext)
    val sync = SyncManager(appContext, supabase, store, roomCategories, roomTasks, roomSchedule)
}

val LocalAppContainer = staticCompositionLocalOf<AppContainer> { error("AppContainer is not provided") }
