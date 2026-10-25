package ayodong.emobilize.core.data

import ayodong.emobilize.core.model.Category
import ayodong.emobilize.core.model.DateRange
import ayodong.emobilize.core.model.EventType
import ayodong.emobilize.core.model.FilterKey
import ayodong.emobilize.core.model.Priority
import ayodong.emobilize.core.model.Task
import ayodong.emobilize.core.model.TimeBlock
import ayodong.emobilize.core.model.formatInputDate
import ayodong.emobilize.core.model.isoDate
import ayodong.emobilize.core.model.weekOf
import java.time.LocalDate

internal fun sampleTasks(today: LocalDate = LocalDate.now()): List<Task> {
    val later = isoDate(today.plusDays(2))
    val tomorrow = isoDate(today.plusDays(1))
    val dentist = isoDate(today.plusDays(3))
    return listOf(
        Task(1, "Read research paper", Category.Study, "Today", "10:30 AM", FilterKey.Today, Priority.High, description = "Read the method section and write three notes.", estimateMinutes = 90),
        Task(2, "Finish backend API", Category.Work, "Today", "4:00 PM", FilterKey.Today, Priority.High, description = "Ship the task list endpoint and check the error cases.", estimateMinutes = 120),
        Task(3, "Gym", Category.Health, "Tomorrow", "6:00 PM", FilterKey.Tomorrow, Priority.Medium, description = "Strength session, then a short stretch.", estimateMinutes = 60),
        Task(
            4,
            "Design portfolio draft",
            Category.Work,
            formatInputDate(later, today),
            "11:00 AM",
            FilterKey.Later,
            Priority.Medium,
            description = "Lay out the first three case studies.",
            estimateMinutes = 180,
        ),
        Task(5, "Buy groceries", Category.Health, "Today", null, FilterKey.Today, Priority.Low, description = "Rice, eggs, and fruit for the week."),
        Task(6, "Reply to client email", Category.Work, "Today", "8:00 AM", FilterKey.Today, Priority.Medium, description = "Confirm the revised delivery date and attach the invoice.", estimateMinutes = 15),
        Task(
            7,
            "Group project sprint",
            Category.Study,
            formatInputDate(tomorrow, today),
            "3 PM",
            FilterKey.Tomorrow,
            Priority.High,
            range = DateRange(isoDate(today), "13:00", tomorrow, "15:00"),
            description = "Build the prototype screens with the team and merge before the review.",
            estimateMinutes = 240,
        ),
        Task(
            8,
            "Dentist appointment",
            Category.Health,
            formatInputDate(dentist, today),
            "2 PM",
            FilterKey.Later,
            Priority.Medium,
            eventType = EventType.Event,
            eventEndTime = "3 PM",
            description = "Routine check-up and cleaning at the campus clinic.",
        ),
        Task(9, "Plan weekend trip", null, "", null, FilterKey.All, description = "Pick a place, check the train times, and share the plan."),
    )
}

internal fun sampleBlocks(today: LocalDate = LocalDate.now()): Map<LocalDate, List<TimeBlock>> {
    val days = weekOf(today)
    return mapOf(
        days[0] to listOf(
            TimeBlock(101, "Read Research Paper", 9f, 10.5f, Category.Study),
            TimeBlock(102, "Team Standup", 11f, 11.5f, Category.Work),
            TimeBlock(103, "Lunch Break", 12.5f, 13.5f, Category.Health),
            TimeBlock(104, "Finish Backend API", 14f, 16f, Category.Work),
            TimeBlock(105, "Gym", 17f, 18f, Category.Health),
        ),
        days[1] to listOf(
            TimeBlock(106, "Portfolio Design", 10f, 12f, Category.Work),
            TimeBlock(107, "Lunch", 12.5f, 13.5f, Category.Health),
            TimeBlock(108, "Code Review", 15f, 16.5f, Category.Work),
        ),
        days[2] to listOf(
            TimeBlock(109, "Morning Planning", 8f, 9f, Category.Study),
            TimeBlock(110, "Deep Work", 9f, 12f, Category.Work),
            TimeBlock(111, "Lunch", 12f, 13f, Category.Health),
            TimeBlock(112, "Standup", 14f, 14.5f, Category.Work),
            TimeBlock(113, "Gym Session", 17.5f, 18.5f, Category.Health),
            TimeBlock(114, "Buy Groceries", 19f, 19.5f, Category.Health),
        ),
        days[4] to listOf(
            TimeBlock(115, "Submit Assignment", 10f, 11f, Category.Study),
            TimeBlock(116, "Lunch", 13f, 14f, Category.Health),
        ),
        days[5] to listOf(
            TimeBlock(117, "Weekend Run", 8f, 9f, Category.Health),
        ),
    )
}
