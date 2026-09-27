package ayodong.emobilize.domain.repository

import ayodong.emobilize.domain.model.EventItemData
import kotlinx.coroutines.flow.Flow

interface ScheduleRepository {
    fun getScheduleEvents(): Flow<List<EventItemData>>
    suspend fun addEvent(event: EventItemData)
}