package ayodong.emobilize.data.repository

import ayodong.emobilize.domain.model.TimeBlock
import ayodong.emobilize.domain.model.placeSchedule
import ayodong.emobilize.domain.repository.ScheduleRepository
import java.time.LocalDate

class InMemoryScheduleRepository : ScheduleRepository {
    private var stored = sampleBlocks()

    override fun blocks(): Map<LocalDate, List<TimeBlock>> = stored

    override fun place(block: TimeBlock, date: LocalDate) {
        stored = placeSchedule(stored, block, date)
    }

    override fun delete(id: Long) {
        stored = stored
            .mapValues { (_, list) -> list.filter { it.id != id } }
            .filterValues { it.isNotEmpty() }
    }
}
