package ayodong.emobilize.core.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import ayodong.emobilize.R

@Composable
fun EmobilizeLogo(
    modifier: Modifier = Modifier,
    contentDescription: String = "Emobilize",
) {
    val dark = LocalPalette.current.id == 2
    Image(
        painter = painterResource(
            if (dark) R.drawable.ic_emobilize_logo_dark else R.drawable.ic_emobilize_logo_light,
        ),
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = ContentScale.Fit,
    )
}
