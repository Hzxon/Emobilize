package ayodong.emobilize.domain.usecase

import ayodong.emobilize.domain.model.EventItemData
import ayodong.emobilize.domain.repository.ScheduleRepository

class AddScheduleEventUseCase(
    private val repository: ScheduleRepository
) {
    suspend operator fun invoke(event: EventItemData) {
        repository.addEvent(event)
    }
}