package ayodong.emobilize.feature.calendar.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ayodong.emobilize.core.domain.AppSettingsRepository
import ayodong.emobilize.core.domain.ScheduleRepository
import ayodong.emobilize.core.domain.TaskRepository
import ayodong.emobilize.core.model.AppSettings
import ayodong.emobilize.core.model.CalView
import ayodong.emobilize.core.model.Task
import ayodong.emobilize.core.model.TimeBlock
import ayodong.emobilize.core.model.WeekStart
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.DayOfWeek
import java.time.LocalDate

data class CalendarUiState(
    val tasks: List<Task> = emptyList(),
    val blocks: Map<LocalDate, List<TimeBlock>> = emptyMap(),
    val defaultView: CalView = CalView.Week,
    val weekStart: DayOfWeek = DayOfWeek.MONDAY,
)

class CalendarViewModel(
    tasks: TaskRepository,
    schedule: ScheduleRepository,
    settings: AppSettingsRepository,
) : ViewModel() {
    val uiState: StateFlow<CalendarUiState> = combine(tasks.board, schedule.blocks, settings.settings) { board, blocks, settings ->
        state(board.tasks, blocks, settings)
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        state(tasks.board.value.tasks, schedule.blocks.value, settings.settings.value),
    )

    private fun state(tasks: List<Task>, blocks: Map<LocalDate, List<TimeBlock>>, settings: AppSettings) = CalendarUiState(
        tasks = tasks,
        blocks = blocks,
        defaultView = settings.defaultCalendarView,
        weekStart = if (settings.weekStart == WeekStart.Monday) DayOfWeek.MONDAY else DayOfWeek.SUNDAY,
    )
}
