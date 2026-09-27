package ayodong.emobilize.data.repository


import ayodong.emobilize.domain.model.Note
import ayodong.emobilize.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class NoteRepositoryImpl : NoteRepository {
    override fun getNotes(): Flow<List<Note>> {
        return flowOf(
            listOf(
                Note(id = "1", title = "234dasdfasdf", content = "234423"),
                Note(id = "2", title = "2342343", content = "234243"),
                Note(id = "3", title = "e2r4242", content = "asdfasdfa"),
                Note(id = "4", title = "asdfasdf", content = "asdfasdfasdf")
            )
        )
    }
}