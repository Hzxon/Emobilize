package ayodong.emobilize.data.repository

import ayodong.emobilize.domain.model.Category
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

    override fun applyCategory(category: Category) {
        stored = stored.mapValues { (_, list) ->
            list.map { block ->
                if (block.category.id == category.id) block.copy(category = category) else block
            }
        }
    }
}
