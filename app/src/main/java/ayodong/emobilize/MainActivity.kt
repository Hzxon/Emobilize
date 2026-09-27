package ayodong.emobilize

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import ayodong.emobilize.presentation.navigation.AppNavigation
import ayodong.emobilize.presentation.ui.theme.EmobilizeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EmobilizeTheme {
                AppNavigation()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Preview() {
    EmobilizeTheme {
        AppNavigation()
    }
}