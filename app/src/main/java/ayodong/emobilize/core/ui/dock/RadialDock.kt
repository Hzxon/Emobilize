package ayodong.emobilize.core.ui.dock

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import ayodong.emobilize.core.ui.theme.LocalPalette

// The floating key: a solid square. At rest it shows the screen a tap goes to; open, it shows close.
@Composable
internal fun FabFace(progress: Float, open: Boolean, tab: NavTab) {
    val palette = LocalPalette.current
    Box(
        Modifier
            .fillMaxSize()
            .padding(8.dp)
            .background(palette.primary)
            .border(2.dp, palette.onFilled),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            when {
                open -> Icons.Filled.Close
                tab == NavTab.Tasks -> Icons.Filled.DateRange
                else -> Icons.AutoMirrored.Filled.List
            },
            contentDescription = null,
            tint = palette.onFilled,
            modifier = Modifier
                .size(26.dp)
                .rotate(progress * 90f),
        )
    }
}
