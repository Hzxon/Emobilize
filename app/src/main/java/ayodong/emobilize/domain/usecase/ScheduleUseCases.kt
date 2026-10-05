package ayodong.emobilize.domain.usecase

import ayodong.emobilize.domain.model.TimeBlock
import ayodong.emobilize.domain.repository.ScheduleRepository
import java.time.LocalDate

class GetScheduleUseCase(private val repository: ScheduleRepository) {
    operator fun invoke(): Map<LocalDate, List<TimeBlock>> = repository.blocks()
}

class PlaceTimeBlockUseCase(private val repository: ScheduleRepository) {
    operator fun invoke(block: TimeBlock, date: LocalDate) = repository.place(block, date)
}

class DeleteTimeBlockUseCase(private val repository: ScheduleRepository) {
    operator fun invoke(id: Long) = repository.delete(id)
}
