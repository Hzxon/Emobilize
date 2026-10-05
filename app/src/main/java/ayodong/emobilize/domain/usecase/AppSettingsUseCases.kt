package ayodong.emobilize.domain.usecase

import ayodong.emobilize.domain.model.AppSettings
import ayodong.emobilize.domain.repository.AppSettingsRepository

class GetAppSettingsUseCase(private val repository: AppSettingsRepository) {
    operator fun invoke(): AppSettings = repository.get()
}

class UpdateAppSettingsUseCase(private val repository: AppSettingsRepository) {
    operator fun invoke(settings: AppSettings) = repository.update(settings)
}
