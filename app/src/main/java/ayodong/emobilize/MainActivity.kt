package ayodong.emobilize

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import ayodong.emobilize.di.AppContainer
import ayodong.emobilize.di.TasksViewModelFactory
import ayodong.emobilize.ui.EmobilizeApp

class MainActivity : ComponentActivity() {
    private val container = AppContainer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EmobilizeApp(viewModel(factory = TasksViewModelFactory(container)))
        }
    }
}
