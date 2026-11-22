package app.opposite.work

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import app.opposite.MainActivity
import app.opposite.R
import app.opposite.domain.House
import app.opposite.domain.Season
import app.opposite.store
import app.opposite.sunset
import java.time.Instant
import java.time.ZoneId

/**
 * One quiet reminder a day, a few minutes after sunset, while tonight's window is still dark:
 * «В доме напротив зажглось окно». Scheduled inexactly; the phone may deliver it a little late.
 */
class Dusk : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val st = context.store.now
        val s = st.season
        val zone = ZoneId.systemDefault()
        val today = Instant.now().atZone(zone).toLocalDate()
        if (st.notify && s != null && !s.over) {
            val d = s.day(today)
            if (d in 1..Season.DAYS && !s.lit.containsKey(d)) post(context, d)
        }
        schedule(context)
    }

    private fun post(context: Context, d: Int) {
        if (Build.VERSION.SDK_INT >= 33 && ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) return
        val nm = context.getSystemService(NotificationManager::class.java)
        val ru = context.resources.configuration.locales[0].language == "ru"
        nm.createNotificationChannel(NotificationChannel(CHANNEL, if (ru) "Окно на закате" else "Ventana al atardecer", NotificationManager.IMPORTANCE_DEFAULT))
        val r = House.resident(d)
        val open = PendingIntent.getActivity(context, 0, Intent(context, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), PendingIntent.FLAG_IMMUTABLE)
        val n = NotificationCompat.Builder(context, CHANNEL)
            .setSmallIcon(R.drawable.ic_notify)
            .setContentTitle(if (ru) "В доме напротив зажглось окно" else "Una ventana se encendió enfrente")
            .setContentText(if (ru) "Окно $d. Огонёк: ${r.task.ru}" else "Ventana $d. Chispa de hoy: ${r.task.en}")
            .setStyle(NotificationCompat.BigTextStyle().bigText(if (ru) "Окно $d. Огонёк: ${r.task.ru}" else "Ventana $d. Chispa de hoy: ${r.task.en}"))
            .setContentIntent(open)
            .setAutoCancel(true)
            .build()
        nm.notify(1, n)
    }

    companion object {
        private const val CHANNEL = "dusk"

        /** Sets the next reminder: today's sunset + 10 minutes if still ahead, else tomorrow's. */
        fun schedule(context: Context) {
            val st = context.store.now
            val am = context.getSystemService(AlarmManager::class.java) ?: return
            val pi = PendingIntent.getBroadcast(context, 7, Intent(context, Dusk::class.java), PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
            am.cancel(pi)
            if (!st.notify || st.season == null || st.season.over) return
            val zone = ZoneId.systemDefault()
            val now = System.currentTimeMillis()
            val today = Instant.ofEpochMilli(now).atZone(zone).toLocalDate()
            val sun = st.sunset(zone)
            var at = sun(today) + 10 * 60_000L
            if (at <= now) at = sun(today.plusDays(1)) + 10 * 60_000L
            am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, at, pi)
        }
    }
}

class Boot : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) Dusk.schedule(context)
    }
}
