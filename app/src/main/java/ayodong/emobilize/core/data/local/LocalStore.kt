package ayodong.emobilize.core.data.local

import android.content.Context
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.atomic.AtomicInteger

/**
 * The Room database behind the repositories. Repositories change their in-memory state at once
 * and queue the matching row write here, so screens stay synchronous. Writes run one at a time,
 * in the order they were queued, under [lock]; sync takes the same lock so the two never interleave.
 */
class LocalStore(context: Context, val scope: CoroutineScope) {
    val rows: RowDao = EmobilizeDatabase.open(context).rows()
    val lock = Mutex()

    /** Called after each queued write lands, to ask for a sync. */
    var onWrite: () -> Unit = {}

    private val pending = AtomicInteger()
    private val writes = Channel<suspend RowDao.() -> Unit>(Channel.UNLIMITED)

    /** True when no queued write is waiting, so the database matches the in-memory state. */
    val idle: Boolean get() = pending.get() == 0

    init {
        scope.launch {
            for (write in writes) {
                lock.withLock { rows.write() }
                pending.decrementAndGet()
                onWrite()
            }
        }
    }

    fun write(change: suspend RowDao.() -> Unit) {
        pending.incrementAndGet()
        writes.trySend(change)
    }

    /** Suspends until every write queued before this call has landed. */
    suspend fun flush() {
        val done = CompletableDeferred<Unit>()
        write { done.complete(Unit) }
        done.await()
    }

    /** Blocking read for the first load, before any screen draws. */
    fun <T> load(read: suspend RowDao.() -> T): T = runBlocking(Dispatchers.IO) { rows.read() }
}
