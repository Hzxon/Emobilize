package ayodong.emobilize.core.notify

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.net.toUri
import ayodong.emobilize.core.domain.AppSettingsRepository
import ayodong.emobilize.core.domain.ScheduleRepository
import ayodong.emobilize.core.domain.TaskRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * Keeps one system alarm per upcoming reminder. Whenever tasks, blocks or the Settings switch
 * change, alarms for reminders that are gone are cancelled and the rest are set again.
 * Alarms survive the app being closed; a reboot clears them, so [ReminderReceiver] calls
 * [reschedule] on boot.
 */
class ReminderScheduler(
    private val context: Context,
    private val tasks: TaskRepository,
    private val schedule: ScheduleRepository,
    private val settings: AppSettingsRepository,
    scope: CoroutineScope,
) {
    private val alarms = context.getSystemService(AlarmManager::class.java)
    private var scheduled = emptyList<Reminder>()

    init {
        scope.launch {
            combine(tasks.board, schedule.blocks, settings.settings) { _, _, _ -> due() }
                .distinctUntilChanged()
                .collect(::apply)
        }
    }

    /** Sets every alarm again from the current data, for when the system has cleared them (reboot). */
    fun reschedule() = apply(due(), force = true)

    private fun due(): List<Reminder> =
        if (settings.settings.value.notificationsEnabled) {
            upcomingReminders(tasks.board.value, schedule.blocks.value, LocalDateTime.now())
        } else {
            emptyList()
        }

    // Cancels reminders that are gone and sets only the new or changed ones.
    @Synchronized
    private fun apply(next: List<Reminder>, force: Boolean = false) {
        val ids = next.mapTo(HashSet()) { it.id }
        scheduled.filter { it.id !in ids }.forEach { alarms.cancel(pendingIntent(it)) }
        val set = if (force) next else next - scheduled.toSet()
        set.forEach(::set)
        scheduled = next
    }

    private fun set(reminder: Reminder) {
        val at = reminder.at.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val intent = pendingIntent(reminder)
        // USE_EXACT_ALARM grants this on Android 13+; on Android 12 the user can deny it, and Android may then deliver late.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S || alarms.canScheduleExactAlarms()) {
            alarms.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, at, intent)
        } else {
            alarms.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, at, intent)
        }
    }

    // The data URI makes each reminder's intent distinct, so cancel() finds the right alarm.
    private fun pendingIntent(reminder: Reminder): PendingIntent {
        val intent = Intent(context, ReminderReceiver::class.java)
            .setData("emobilize://reminder/${reminder.id}".toUri())
            .putExtra(ExtraId, reminder.id)
            .putExtra(ExtraTitle, reminder.title)
            .putExtra(ExtraText, reminder.text)
        return PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
    }

    internal companion object {
        const val ExtraId = "id"
        const val ExtraTitle = "title"
        const val ExtraText = "text"
        const val Channel = "reminders"
    }
}
