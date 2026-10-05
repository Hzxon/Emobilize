package ayodong.emobilize.data.repository

import ayodong.emobilize.domain.model.Category
import ayodong.emobilize.domain.model.FilterKey
import ayodong.emobilize.domain.model.Priority
import ayodong.emobilize.domain.model.Task
import ayodong.emobilize.domain.model.TimeBlock
import ayodong.emobilize.domain.model.formatInputDate
import ayodong.emobilize.domain.model.isoDate
import ayodong.emobilize.domain.model.weekOf
import java.time.LocalDate

internal fun sampleTasks(today: LocalDate = LocalDate.now()): List<Task> {
    val later = isoDate(today.plusDays(2))
    return listOf(
        Task(1, "Read research paper", Category.Study, "Today", "10:30 AM", FilterKey.Today, Priority.High),
        Task(2, "Finish backend API", Category.Work, "Today", "4:00 PM", FilterKey.Today, Priority.High),
        Task(3, "Gym", Category.Health, "Tomorrow", "6:00 PM", FilterKey.Tomorrow, Priority.Medium),
        Task(
            4,
            "Design portfolio draft",
            Category.Personal,
            formatInputDate(later, today),
            "11:00 AM",
            FilterKey.Later,
            Priority.Medium,
        ),
        Task(5, "Buy groceries", Category.Personal, "Today", null, FilterKey.Today, Priority.Low),
    )
}

internal fun sampleBlocks(today: LocalDate = LocalDate.now()): Map<LocalDate, List<TimeBlock>> {
    val days = weekOf(today)
    return mapOf(
        days[0] to listOf(
            TimeBlock(101, "Read Research Paper", 9f, 10.5f, Category.Study),
            TimeBlock(102, "Team Standup", 11f, 11.5f, Category.Work),
            TimeBlock(103, "Lunch Break", 12.5f, 13.5f, Category.Personal),
            TimeBlock(104, "Finish Backend API", 14f, 16f, Category.Work),
            TimeBlock(105, "Gym", 17f, 18f, Category.Health),
        ),
        days[1] to listOf(
            TimeBlock(106, "Portfolio Design", 10f, 12f, Category.Personal),
            TimeBlock(107, "Lunch", 12.5f, 13.5f, Category.Personal),
            TimeBlock(108, "Code Review", 15f, 16.5f, Category.Work),
        ),
        days[2] to listOf(
            TimeBlock(109, "Morning Planning", 8f, 9f, Category.Study),
            TimeBlock(110, "Deep Work", 9f, 12f, Category.Work),
            TimeBlock(111, "Lunch", 12f, 13f, Category.Personal),
            TimeBlock(112, "Standup", 14f, 14.5f, Category.Work),
            TimeBlock(113, "Gym Session", 17.5f, 18.5f, Category.Health),
            TimeBlock(114, "Buy Groceries", 19f, 19.5f, Category.Personal),
        ),
        days[4] to listOf(
            TimeBlock(115, "Submit Assignment", 10f, 11f, Category.University),
            TimeBlock(116, "Lunch", 13f, 14f, Category.Personal),
        ),
        days[5] to listOf(
            TimeBlock(117, "Weekend Run", 8f, 9f, Category.Health),
        ),
    )
}
