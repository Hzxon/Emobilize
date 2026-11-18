package ayodong.emobilize.core.data.local

import ayodong.emobilize.core.data.FakeCategoryRepository
import ayodong.emobilize.core.data.InMemoryScheduleRepository
import ayodong.emobilize.core.data.InMemoryTaskRepository
import ayodong.emobilize.core.domain.CategoryRepository
import ayodong.emobilize.core.domain.TaskBoard
import ayodong.emobilize.core.model.Category
import ayodong.emobilize.core.model.DateRange
import ayodong.emobilize.core.model.EventType
import ayodong.emobilize.core.model.Priority
import ayodong.emobilize.core.model.ReminderOption
import ayodong.emobilize.core.model.Task
import ayodong.emobilize.core.model.TaskStatus
import ayodong.emobilize.core.model.TimeBlock
import java.time.LocalDate

// Room-backed repositories. Each reuses the in-memory repository's rules for its state and
// queues the changed rows on the LocalStore. reload() runs under the store lock after a sync.

private fun now() = System.currentTimeMillis()

class RoomCategoryRepository(
    private val store: LocalStore,
    firstRunLines: List<Category>,
) : FakeCategoryRepository(store.load { lines(firstRunLines) }) {

    // Lines made on two phones must not share an id once both sync.
    override fun newId(): Long = now()

    override fun add(name: String, code: String, color: Int): Category? = super.add(name, code, color)?.also(::save)

    override fun update(category: Category): Category? = super.update(category)?.also(::save)

    override fun delete(id: Long): Boolean = super.delete(id).also { deleted ->
        if (deleted) store.write { deleteCategory(id, now()) }
    }

    suspend fun reload() = replace(store.rows.lines(Category.defaults))

    private fun save(category: Category) {
        val row = category.toRow(now())
        store.write { saveCategories(listOf(row)) }
    }
}

class RoomTaskRepository(
    private val store: LocalStore,
    private val categories: CategoryRepository,
) : InMemoryTaskRepository(store.load { board(tasks(), categories.categories.value) }) {

    override fun add(task: Task) {
        super.add(task)
        save(task.id)
    }

    override fun update(task: Task) {
        super.update(task)
        save(task.id)
    }

    override fun delete(id: Long) {
        super.delete(id)
        store.write { deleteTask(id, now()) }
    }

    override fun setStatus(id: Long, status: TaskStatus) {
        super.setStatus(id, status)
        save(id)
    }

    override fun clearCategory(id: Long) {
        val moved = board.value.tasks.filter { it.category?.id == id }.map { it.id }
        super.clearCategory(id)
        moved.forEach(::save)
    }

    suspend fun reload() = replace(board(store.rows.tasks(), categories.categories.value))

    private fun save(id: Long) {
        val current = board.value
        val task = current.tasks.find { it.id == id } ?: return
        val row = task.toRow(current.statusOf(id), now())
        store.write { saveTasks(listOf(row)) }
    }
}

class RoomScheduleRepository(
    private val store: LocalStore,
    private val categories: CategoryRepository,
) : InMemoryScheduleRepository(store.load { schedule(blocks(), categories.categories.value) }) {

    override fun place(block: TimeBlock, date: LocalDate) {
        super.place(block, date)
        save(block.id)
    }

    override fun delete(id: Long) {
        super.delete(id)
        store.write { deleteBlock(id, now()) }
    }

    override fun replaceCategory(id: Long, replacement: Category) {
        val moved = blocks.value.values.flatten().filter { it.category.id == id }.map { it.id }
        super.replaceCategory(id, replacement)
        moved.forEach(::save)
    }

    suspend fun reload() = replace(schedule(store.rows.blocks(), categories.categories.value))

    private fun save(id: Long) {
        val (date, block) = blocks.value.firstNotNullOfOrNull { (date, list) ->
            list.find { it.id == id }?.let { date to it }
        } ?: return
        val row = block.toRow(date, now())
        store.write { saveBlocks(listOf(row)) }
    }
}

// Rows written by the first run get updatedAt 0, so any copy already on the server wins over them.
private suspend fun RowDao.lines(seed: List<Category>): List<Category> {
    if (categories().isEmpty()) saveCategories(seed.map { it.toRow(updatedAt = 0) })
    return categories().filterNot { it.deleted }.sortedBy { it.id }.map { Category(it.id, it.name, it.color, it.code) }
}

private fun board(rows: List<TaskRow>, lines: List<Category>): TaskBoard {
    val live = rows.filterNot { it.deleted }
    return TaskBoard(
        tasks = live.map { it.toTask(lines) },
        doneIds = live.filter { it.status == TaskStatus.Done.name }.map { it.id }.toSet(),
        progressIds = live.filter { it.status == TaskStatus.Progress.name }.map { it.id }.toSet(),
    )
}

// A block always has a line; one whose line was deleted on another phone falls back to the first.
private fun schedule(rows: List<BlockRow>, lines: List<Category>): Map<LocalDate, List<TimeBlock>> =
    rows.filterNot { it.deleted }.groupBy({ LocalDate.parse(it.date) }) { row ->
        TimeBlock(
            id = row.id,
            title = row.title,
            start = row.start,
            end = row.end,
            category = lines.find { it.id == row.categoryId } ?: lines.first(),
            reminder = ReminderOption.valueOf(row.reminder),
        )
    }

private fun Category.toRow(updatedAt: Long) = CategoryRow(id, name, color, code, updatedAt)

private fun TimeBlock.toRow(date: LocalDate, updatedAt: Long) =
    BlockRow(id, date.toString(), title, start, end, category.id, reminder.name, updatedAt)

private fun Task.toRow(status: TaskStatus, updatedAt: Long) = TaskRow(
    id = id,
    name = name,
    categoryId = category?.id,
    date = date?.toString(),
    time = time,
    priority = priority?.name,
    rangeStartDate = range?.startDate,
    rangeStartTime = range?.startTime,
    rangeEndDate = range?.endDate,
    rangeEndTime = range?.endTime,
    eventType = eventType.name,
    eventEndTime = eventEndTime,
    reminder = reminder.name,
    description = description,
    estimateMinutes = estimateMinutes,
    status = status.name,
    updatedAt = updatedAt,
)

private fun TaskRow.toTask(lines: List<Category>) = Task(
    id = id,
    name = name,
    category = lines.find { it.id == categoryId },
    date = date?.let(LocalDate::parse),
    time = time,
    priority = priority?.let(Priority::valueOf),
    range = if (rangeStartDate != null && rangeStartTime != null && rangeEndDate != null && rangeEndTime != null) {
        DateRange(rangeStartDate, rangeStartTime, rangeEndDate, rangeEndTime)
    } else {
        null
    },
    eventType = EventType.valueOf(eventType),
    eventEndTime = eventEndTime,
    reminder = ReminderOption.valueOf(reminder),
    description = description,
    estimateMinutes = estimateMinutes,
)
