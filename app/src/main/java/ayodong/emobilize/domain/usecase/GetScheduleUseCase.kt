package ayodong.emobilize.domain.usecase

import ayodong.emobilize.domain.model.EventItemData
import ayodong.emobilize.domain.repository.ScheduleRepository
import kotlinx.coroutines.flow.Flow

class GetScheduleUseCase(
    private val repository: ScheduleRepository
) {
    operator fun invoke(): Flow<List<EventItemData>> {
        return repository.getScheduleEvents()
    }
}