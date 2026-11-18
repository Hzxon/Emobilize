package ayodong.emobilize.core.data.local

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

// One row type per table, shared by Room and the Supabase tables of the same name (see
// supabase/schema.sql). A delete keeps the row with deleted = true so it can reach other devices.
// dirty marks a row changed on this phone and not yet pushed; it is never sent to the server.

interface SyncedRow {
    val id: Long
    val updatedAt: Long
    val dirty: Boolean
}

@Serializable
@Entity(tableName = "tasks")
data class TaskRow(
    @PrimaryKey override val id: Long,
    val name: String,
    @SerialName("category_id") val categoryId: Long?,
    val date: String?,
    val time: String?,
    val priority: String?,
    @SerialName("range_start_date") val rangeStartDate: String?,
    @SerialName("range_start_time") val rangeStartTime: String?,
    @SerialName("range_end_date") val rangeEndDate: String?,
    @SerialName("range_end_time") val rangeEndTime: String?,
    @SerialName("event_type") val eventType: String,
    @SerialName("event_end_time") val eventEndTime: String?,
    val reminder: String,
    val description: String,
    @SerialName("estimate_minutes") val estimateMinutes: Int?,
    val status: String,
    @SerialName("updated_at") override val updatedAt: Long,
    val deleted: Boolean = false,
    @Transient override val dirty: Boolean = true,
) : SyncedRow

@Serializable
@Entity(tableName = "blocks")
data class BlockRow(
    @PrimaryKey override val id: Long,
    val date: String,
    val title: String,
    @SerialName("start_hour") val start: Float,
    @SerialName("end_hour") val end: Float,
    @SerialName("category_id") val categoryId: Long,
    val reminder: String,
    @SerialName("updated_at") override val updatedAt: Long,
    val deleted: Boolean = false,
    @Transient override val dirty: Boolean = true,
) : SyncedRow

@Serializable
@Entity(tableName = "categories")
data class CategoryRow(
    @PrimaryKey override val id: Long,
    val name: String,
    val color: Int,
    val code: String,
    @SerialName("updated_at") override val updatedAt: Long,
    val deleted: Boolean = false,
    @Transient override val dirty: Boolean = true,
) : SyncedRow

@Dao
interface RowDao {
    @Query("SELECT * FROM tasks")
    suspend fun tasks(): List<TaskRow>

    @Query("SELECT * FROM blocks")
    suspend fun blocks(): List<BlockRow>

    @Query("SELECT * FROM categories")
    suspend fun categories(): List<CategoryRow>

    @Upsert
    suspend fun saveTasks(rows: List<TaskRow>)

    @Upsert
    suspend fun saveBlocks(rows: List<BlockRow>)

    @Upsert
    suspend fun saveCategories(rows: List<CategoryRow>)

    @Query("UPDATE tasks SET deleted = 1, dirty = 1, updatedAt = :at WHERE id = :id")
    suspend fun deleteTask(id: Long, at: Long)

    @Query("UPDATE blocks SET deleted = 1, dirty = 1, updatedAt = :at WHERE id = :id")
    suspend fun deleteBlock(id: Long, at: Long)

    @Query("UPDATE categories SET deleted = 1, dirty = 1, updatedAt = :at WHERE id = :id")
    suspend fun deleteCategory(id: Long, at: Long)

    @Query("DELETE FROM tasks")
    suspend fun clearTasks()

    @Query("DELETE FROM blocks")
    suspend fun clearBlocks()

    @Query("DELETE FROM categories")
    suspend fun clearCategories()

    @Transaction
    suspend fun clearAll() {
        clearTasks()
        clearBlocks()
        clearCategories()
    }
}

@Database(entities = [TaskRow::class, BlockRow::class, CategoryRow::class], version = 1)
abstract class EmobilizeDatabase : RoomDatabase() {
    abstract fun rows(): RowDao

    companion object {
        fun open(context: Context): EmobilizeDatabase =
            Room.databaseBuilder(context, EmobilizeDatabase::class.java, "emobilize.db").build()
    }
}
