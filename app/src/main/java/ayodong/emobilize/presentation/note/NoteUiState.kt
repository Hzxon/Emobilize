package ayodong.emobilize.presentation.note

import ayodong.emobilize.domain.model.Note

sealed interface NoteUiState {
    object Loading : NoteUiState
    data class Success(val notes: List<Note>) : NoteUiState
    data class Error(val message: String) : NoteUiState
}