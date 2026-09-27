package ayodong.emobilize.di


import ayodong.emobilize.data.repository.NoteRepositoryImpl
import ayodong.emobilize.data.repository.ScheduleRepositoryImpl
import ayodong.emobilize.domain.repository.NoteRepository
import ayodong.emobilize.domain.repository.ScheduleRepository
import ayodong.emobilize.domain.usecase.AddScheduleEventUseCase
import ayodong.emobilize.domain.usecase.GetNoteUseCase
import ayodong.emobilize.domain.usecase.GetScheduleUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideNoteRepository(): NoteRepository {
        return NoteRepositoryImpl()
    }

    @Provides
    @Singleton
    fun provideGetNoteUseCase(
        repository: NoteRepository
    ): GetNoteUseCase {
        return GetNoteUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideScheduleRepository(): ScheduleRepository {
        return ScheduleRepositoryImpl()
    }

    @Provides
    @Singleton
    fun provideGetScheduleUseCase(
        repository: ScheduleRepository
    ): GetScheduleUseCase {
        return GetScheduleUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideAddScheduleEventUseCase(
        repository: ScheduleRepository
    ): AddScheduleEventUseCase {
        return AddScheduleEventUseCase(repository)
    }
}