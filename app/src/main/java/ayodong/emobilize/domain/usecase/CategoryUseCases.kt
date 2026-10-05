package ayodong.emobilize.domain.usecase

import ayodong.emobilize.domain.model.Category
import ayodong.emobilize.domain.repository.CategoryRepository
import ayodong.emobilize.domain.repository.ScheduleRepository
import ayodong.emobilize.domain.repository.TaskRepository

class GetCategoriesUseCase(private val categories: CategoryRepository) {
    operator fun invoke(): List<Category> = categories.all()
}

class UpdateCategoryUseCase(
    private val categories: CategoryRepository,
    private val tasks: TaskRepository,
    private val schedule: ScheduleRepository,
) {
    operator fun invoke(category: Category): Category? {
        val saved = categories.update(category) ?: return null
        tasks.applyCategory(saved)
        schedule.applyCategory(saved)
        return saved
    }
}
