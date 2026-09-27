package ayodong.emobilize.domain.model

import androidx.compose.ui.graphics.vector.ImageVector

sealed interface CardType {
    data class Lecture(
        val lecturer: String,
        val timeRange: String,
        val title: String,
        val location: String
    ) : CardType

    data class Lab(
        val lecturer: String,
        val timeRange: String,
        val title: String,
        val location: String
    ) : CardType
}

data class EventItemData(
    val id: String,
    val time: String,
    val icon: ImageVector,
    val isActive: Boolean,
    val cardType: CardType
)