package ayodong.emobilize.presentation.note


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ayodong.emobilize.domain.usecase.GetNoteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val getNoteUseCase: GetNoteUseCase
) : ViewModel() {

    var uiState by mutableStateOf<NoteUiState>(NoteUiState.Loading)
        private set

    init {
        viewModelScope.launch {

            uiState = NoteUiState.Loading

            try {
                getNoteUseCase().collect { data ->
                    uiState = NoteUiState.Success(data)
                }
            } catch (e: Exception) {
                uiState = NoteUiState.Error(e.message ?: "Error")
            }
        }
    }
}