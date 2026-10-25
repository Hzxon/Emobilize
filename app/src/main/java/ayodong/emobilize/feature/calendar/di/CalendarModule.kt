package ayodong.emobilize.feature.calendar.di

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import ayodong.emobilize.core.di.AppContainer
import ayodong.emobilize.feature.calendar.ui.CalendarFormTarget
import ayodong.emobilize.feature.calendar.ui.CalendarFormViewModel
import ayodong.emobilize.feature.calendar.ui.CalendarViewModel

object CalendarModule {
    fun calendarViewModel(container: AppContainer): ViewModelProvider.Factory = viewModelFactory {
        initializer { CalendarViewModel(container.tasks, container.schedule, container.appSettings) }
    }

    fun calendarFormViewModel(container: AppContainer, target: CalendarFormTarget): ViewModelProvider.Factory = viewModelFactory {
        initializer {
            CalendarFormViewModel(container.tasks, container.schedule, container.categories, container.appSettings, target)
        }
    }
}
