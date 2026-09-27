package ayodong.emobilize.domain.usecase

import ayodong.emobilize.domain.model.Note
import ayodong.emobilize.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow

class GetNoteUseCase(
    private val repository: NoteRepository
) {
    operator fun invoke(): Flow<List<Note>> {
        return repository.getNotes()
    }
}