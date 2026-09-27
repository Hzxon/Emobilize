package ayodong.emobilize.presentation.schedule

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ayodong.emobilize.domain.model.EventItemData
import ayodong.emobilize.domain.usecase.GetScheduleUseCase
import ayodong.emobilize.domain.usecase.AddScheduleEventUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val getScheduleUseCase: GetScheduleUseCase,
    private val addScheduleEventUseCase: AddScheduleEventUseCase
) : ViewModel() {

    private val _eventItems = mutableStateListOf<EventItemData>()
    val eventItems: List<EventItemData> = _eventItems

    init {
        viewModelScope.launch {
            getScheduleUseCase().collect { data ->
                _eventItems.clear()
                _eventItems.addAll(data)
            }
        }
    }

    fun addEvent(newEvent: EventItemData) {
        viewModelScope.launch {
            addScheduleEventUseCase(newEvent)
        }
    }
}