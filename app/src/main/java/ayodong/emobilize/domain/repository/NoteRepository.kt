package ayodong.emobilize.domain.repository


import ayodong.emobilize.domain.model.Note
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun getNotes(): Flow<List<Note>>
}