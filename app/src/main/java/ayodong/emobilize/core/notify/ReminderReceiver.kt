package ayodong.emobilize.core.notify

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import ayodong.emobilize.EmobilizeApplication
import ayodong.emobilize.MainActivity
import ayodong.emobilize.R

/** Posts a due reminder, and on boot sets every alarm again. */
class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            (context.applicationContext as EmobilizeApplication).container.reminders.reschedule()
            return
        }
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(
            NotificationChannel(ReminderScheduler.Channel, "Reminders", NotificationManager.IMPORTANCE_HIGH),
        )
        // False when the user denied the notification permission or blocked the channel.
        if (!manager.areNotificationsEnabled()) return
        val open = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP),
            PendingIntent.FLAG_IMMUTABLE,
        )
        val notification = NotificationCompat.Builder(context, ReminderScheduler.Channel)
            .setSmallIcon(R.drawable.ic_stat_reminder)
            .setContentTitle(intent.getStringExtra(ReminderScheduler.ExtraTitle))
            .setContentText(intent.getStringExtra(ReminderScheduler.ExtraText))
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setContentIntent(open)
            .setAutoCancel(true)
            .build()
        manager.notify(intent.getLongExtra(ReminderScheduler.ExtraId, 0).hashCode(), notification)
    }
}
