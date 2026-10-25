package ayodong.emobilize.core.data

import ayodong.emobilize.core.domain.ScheduleRepository
import ayodong.emobilize.core.model.Category
import ayodong.emobilize.core.model.TimeBlock
import ayodong.emobilize.core.model.placeSchedule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate

class InMemoryScheduleRepository(
    initial: Map<LocalDate, List<TimeBlock>> = sampleBlocks(),
) : ScheduleRepository {
    private val state = MutableStateFlow(initial)

    override val blocks: StateFlow<Map<LocalDate, List<TimeBlock>>> = state.asStateFlow()

    override fun place(block: TimeBlock, date: LocalDate) {
        state.update { placeSchedule(it, block, date) }
    }

    override fun delete(id: Long) {
        state.update { stored ->
            stored
                .mapValues { (_, list) -> list.filter { it.id != id } }
                .filterValues { it.isNotEmpty() }
        }
    }

    override fun applyCategory(category: Category) {
        state.update { stored ->
            stored.mapValues { (_, list) ->
                list.map { block ->
                    if (block.category.id == category.id) block.copy(category = category) else block
                }
            }
        }
    }

    override fun replaceCategory(id: Long, replacement: Category) {
        state.update { stored ->
            stored.mapValues { (_, list) ->
                list.map { block ->
                    if (block.category.id == id) block.copy(category = replacement) else block
                }
            }
        }
    }
}
