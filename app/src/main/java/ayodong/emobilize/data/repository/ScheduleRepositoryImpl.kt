package ayodong.emobilize.data.repository

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import ayodong.emobilize.domain.model.CardType
import ayodong.emobilize.domain.model.EventItemData
import ayodong.emobilize.domain.repository.ScheduleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ScheduleRepositoryImpl : ScheduleRepository {
    private val _eventsFlow = MutableStateFlow(getDummyEventData())

    override fun getScheduleEvents(): Flow<List<EventItemData>> {
        return _eventsFlow.asStateFlow()
    }

    override suspend fun addEvent(event: EventItemData) {
        _eventsFlow.update { currentList ->
            currentList + event
        }
    }
    private fun getDummyEventData(): List<EventItemData> {
        return listOf(
            EventItemData(
                id = "1",
                time = "08.00",
                icon = Icons.Default.PlayArrow,
                isActive = true,
                cardType = CardType.Lecture(
                    lecturer = "Pak Wawo",
                    timeRange = "08.00 - 09.40",
                    title = "Pemrograman Bergerak Terapan",
                    location = "R. Lab Komputer 304 (Gedung D)",
                )
            ),
            EventItemData(
                id = "2",
                time = "08.00",
                icon = Icons.Default.PlayArrow,
                isActive = true,
                cardType = CardType.Lecture(
                    lecturer = "Pak Wawo",
                    timeRange = "08.00 - 09.40",
                    title = "Pemrograman Bergerak Terapan",
                    location = "R. Lab Komputer 304 (Gedung D)",
                )
            ),
            EventItemData(
                id = "3",
                time = "08.00",
                icon = Icons.Default.PlayArrow,
                isActive = true,
                cardType = CardType.Lecture(
                    lecturer = "Pak Wawo",
                    timeRange = "08.00 - 09.40",
                    title = "Pemrograman Bergerak Terapan",
                    location = "R. Lab Komputer 304 (Gedung D)",
                )
            ),
            EventItemData(
                id = "4",
                time = "08.00",
                icon = Icons.Default.PlayArrow,
                isActive = true,
                cardType = CardType.Lecture(
                    lecturer = "Pak Wawo",
                    timeRange = "08.00 - 09.40",
                    title = "Pemrograman Bergerak Terapan",
                    location = "R. Lab Komputer 304 (Gedung D)",
                )
            ),
            EventItemData(
                id = "5",
                time = "08.00",
                icon = Icons.Default.PlayArrow,
                isActive = true,
                cardType = CardType.Lecture(
                    lecturer = "Pak Wawo",
                    timeRange = "08.00 - 09.40",
                    title = "Pemrograman Bergerak Terapan",
                    location = "R. Lab Komputer 304 (Gedung D)",
                )
            ),
            EventItemData(
                id = "6",
                time = "08.00",
                icon = Icons.Default.PlayArrow,
                isActive = true,
                cardType = CardType.Lecture(
                    lecturer = "Pak Wawo",
                    timeRange = "08.00 - 09.40",
                    title = "Pemrograman Bergerak Terapan",
                    location = "R. Lab Komputer 304 (Gedung D)",
                )
            ),
            EventItemData(
                id = "7",
                time = "08.00",
                icon = Icons.Default.PlayArrow,
                isActive = true,
                cardType = CardType.Lab(
                    lecturer = "Ko Wian",
                    timeRange = "08.00 - 09.40",
                    title = "Pemrograman Bergerak Terapan",
                    location = "R. Lab Komputer 304 (Gedung D)",
                )
            )

        )
    }
}