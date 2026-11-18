package ayodong.emobilize.core.sync

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.util.Log
import ayodong.emobilize.core.data.local.LocalStore
import ayodong.emobilize.core.data.local.RoomCategoryRepository
import ayodong.emobilize.core.data.local.RoomScheduleRepository
import ayodong.emobilize.core.data.local.RoomTaskRepository
import ayodong.emobilize.core.data.local.SyncedRow
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject

/**
 * Keeps Room and Supabase in step for the signed-in user. Room stays the source of truth, so the
 * app works offline; a sync runs after sign-in, on every local write, and when the network returns.
 * Each sync pulls the user's rows, keeps the newer copy of each row by updatedAt, then pushes
 * the local rows that are still newer. Without a signed-in user (guest mode) nothing leaves the phone.
 */
@OptIn(FlowPreview::class)
class SyncManager(
    context: Context,
    private val client: SupabaseClient?,
    private val store: LocalStore,
    private val categories: RoomCategoryRepository,
    private val tasks: RoomTaskRepository,
    private val schedule: RoomScheduleRepository,
) {
    private val requests = MutableSharedFlow<Unit>(extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    private val json = Json { encodeDefaults = true }

    /** Email of the signed-in user, or null in guest mode. */
    val email: String? get() = client?.auth?.currentUserOrNull()?.email

    init {
        if (client != null) {
            store.onWrite = ::requestSync
            store.scope.launch {
                // Batches a burst of edits into one sync.
                requests.debounce(1_000).collect { runCatching { sync() }.onFailure { Log.w(Tag, "Sync failed", it) } }
            }
            store.scope.launch {
                client.auth.sessionStatus.collect { if (it is SessionStatus.Authenticated) requestSync() }
            }
            context.getSystemService(ConnectivityManager::class.java).registerDefaultNetworkCallback(
                object : ConnectivityManager.NetworkCallback() {
                    override fun onAvailable(network: Network) = requestSync()
                },
            )
        }
    }

    fun requestSync() {
        requests.tryEmit(Unit)
    }

    /** Waits for the saved session to load, so the first screen can skip sign-in. */
    suspend fun isSignedIn(): Boolean {
        val auth = client?.auth ?: return false
        auth.awaitInitialization()
        return auth.currentUserOrNull() != null
    }

    /**
     * Pushes every unsynced change, then signs out and empties this phone so the next account
     * starts clean. Returns false, and stays signed in, when the changes could not be pushed.
     */
    suspend fun signOut(): Boolean {
        val auth = client?.auth ?: return true
        if (auth.currentUserOrNull() == null) return true
        store.flush()
        if (runCatching { sync() }.isFailure) return false
        runCatching { auth.signOut() }.onFailure { auth.clearSession() }
        store.lock.withLock {
            store.rows.clearAll()
            reload()
        }
        return true
    }

    private suspend fun sync() {
        val client = client ?: return
        val userId = client.auth.currentUserOrNull()?.id ?: return
        store.lock.withLock {
            val rows = store.rows
            merge(client, "categories", userId, rows.categories(), rows::saveCategories) { it.copy(dirty = false) }
            merge(client, "tasks", userId, rows.tasks(), rows::saveTasks) { it.copy(dirty = false) }
            merge(client, "blocks", userId, rows.blocks(), rows::saveBlocks) { it.copy(dirty = false) }
            // A write queued meanwhile would be hidden by a reload; its own sync reloads later.
            if (store.idle) reload()
        }
    }

    private suspend fun reload() {
        categories.reload()
        tasks.reload()
        schedule.reload()
    }

    private suspend inline fun <reified T : SyncedRow> merge(
        client: SupabaseClient,
        table: String,
        userId: String,
        local: List<T>,
        save: suspend (List<T>) -> Unit,
        clean: (T) -> T,
    ) {
        val remote = client.from(table).select().decodeList<T>()
        val mine = local.associateBy { it.id }
        val incoming = remote.filter { row -> mine[row.id].let { it == null || row.updatedAt >= it.updatedAt } }
        save(incoming.map(clean))

        val taken = incoming.mapTo(HashSet()) { it.id }
        val outgoing = local.filter { it.dirty && it.id !in taken }
        if (outgoing.isEmpty()) return
        val payload = JsonArray(
            outgoing.map { row ->
                JsonObject(json.encodeToJsonElement(row).jsonObject + ("user_id" to JsonPrimitive(userId)))
            },
        )
        client.from(table).upsert(payload) { onConflict = "user_id,id" }
        save(outgoing.map(clean))
    }

    private companion object {
        const val Tag = "SyncManager"
    }
}
