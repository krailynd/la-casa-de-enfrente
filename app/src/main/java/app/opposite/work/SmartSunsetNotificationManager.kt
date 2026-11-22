package app.opposite.work

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import app.opposite.MainActivity
import app.opposite.R
import java.time.LocalDate

/**
 * Smart Sunset Notifications for Francesca.
 * Rotates sweet, complicit, and mysterious messages using her pet names
 * (Principessa, Luna, Frambuesa, Mora, sol, rata).
 */
object SmartSunsetNotificationManager {
    const val CHANNEL_ID = "adviento_francesca_sunset"
    const val NOTIF_ID = 2703

    fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Atardecer en Nuestra Casa"
            val desc = "Avisos mágicos cuando el sol cae y se enciende tu ventana del día"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = desc
                enableLights(true)
                lightColor = 0xFFC81D31.toInt()
                enableVibration(true)
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    /**
     * Pick a smart message for the given day and situation.
     */
    fun getNotificationMessage(day: Int, date: LocalDate, unlitCount: Int = 0): Pair<String, String> {
        val title = when (day) {
            1 -> "¡Comenzó nuestro Adviento, Mio Principessa! ✨"
            24 -> "¡Nochebuena! El misterio se revela hoy 🎄✨"
            25 -> "Feliz aniversario de 9 meses, mi amor ❤️"
            else -> when (day % 5) {
                0 -> "Alerta de misterio para mi Mora favorita 🕵️‍♀️"
                1 -> "Buenas noches, Luna 🌙"
                2 -> "Hora mágica, sol ✨"
                3 -> "Pst, Frambuesa… se escucha algo en el edificio 💌"
                else -> "El sol ya se escondió, Mio Principessa 🌅"
            }
        }

        val text = when {
            day == 1 -> "Al otro lado del patio se encendió la primera ventana. Hay una historia y un juego esperando por ti."
            day == 24 -> "La última ventana brilla con luz dorada. Por fin podrás descubrir quién es el admirador secreto."
            day == 25 -> "Nuestro Nuevo Comienzo: Hay un cofre especial de segunda oportunidad esperándote hoy, Principessa."
            unlitCount > 1 -> "Oye rata, el sol ya cayó y tienes ventanas esperando por ti con velitas de reserva 👀✨"
            day % 4 == 0 -> "La ventana de hoy brilla en rojo y oro. Entra a descubrir la sorpresa y la nueva pista."
            day % 4 == 1 -> "Nuestra casa tiene luz nueva. Entra cuando quieras y reclama tu recuerdo de hoy."
            day % 4 == 2 -> "Una nueva habitación cobra vida. Hay un detalle escondido esperando por tus manitas."
            else -> "Acaba de caer el atardecer en Ica. La ventana de hoy ya te espera encendida, mi niña."
        }

        return title to text
    }

    fun showNotification(context: Context, day: Int, date: LocalDate, unlitCount: Int = 0) {
        createChannel(context)
        val (title, body) = getNotificationMessage(day, date, unlitCount)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("target_day", day)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            NOTIF_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val notif = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setColor(0xFFC81D31.toInt())
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIF_ID, notif)
    }
}
