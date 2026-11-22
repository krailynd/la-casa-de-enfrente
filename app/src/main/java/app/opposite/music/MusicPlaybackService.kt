package app.opposite.music

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import app.opposite.MainActivity

class MusicPlaybackService : Service() {

    companion object {
        const val CHANNEL_ID = "opposite_music_player_channel"
        const val NOTIFICATION_ID = 2026

        const val ACTION_PLAY = "app.opposite.music.ACTION_PLAY"
        const val ACTION_PAUSE = "app.opposite.music.ACTION_PAUSE"
        const val ACTION_TOGGLE = "app.opposite.music.ACTION_TOGGLE"
        const val ACTION_PREV = "app.opposite.music.ACTION_PREV"
        const val ACTION_NEXT = "app.opposite.music.ACTION_NEXT"
        const val ACTION_STOP = "app.opposite.music.ACTION_STOP"
        const val ACTION_UPDATE = "app.opposite.music.ACTION_UPDATE"

        fun startOrUpdate(context: Context, action: String = ACTION_UPDATE) {
            val intent = Intent(context, MusicPlaybackService::class.java).apply {
                this.action = action
            }
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(intent)
                } else {
                    context.startService(intent)
                }
            } catch (_: Exception) {
            }
        }

        fun stop(context: Context) {
            val intent = Intent(context, MusicPlaybackService::class.java).apply {
                this.action = ACTION_STOP
            }
            try {
                context.startService(intent)
            } catch (_: Exception) {
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_TOGGLE -> MusicController.togglePlayPause(this)
            ACTION_PLAY -> MusicController.resume(this)
            ACTION_PAUSE -> MusicController.pause(this)
            ACTION_NEXT -> MusicController.next(this)
            ACTION_PREV -> MusicController.prev(this)
            ACTION_STOP -> {
                MusicController.stopModulePlayer(this)
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
                return START_NOT_STICKY
            }
            ACTION_UPDATE -> updateNotification()
        }

        val song = MusicController.currentSong
        if (song != null) {
            updateNotification()
        } else {
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        }

        return START_NOT_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Música para Francesca"
            val descriptionText = "Controles de reproducción del refugio musical"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                setShowBadge(false)
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    fun updateNotification() {
        val song = MusicController.currentSong ?: return
        val isPlaying = MusicController.isPlaying

        val openAppIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val openAppPendingIntent = PendingIntent.getActivity(
            this,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        fun actionPI(action: String, requestCode: Int): PendingIntent {
            val intent = Intent(this, MusicPlaybackService::class.java).apply { this.action = action }
            return PendingIntent.getService(this, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }

        val prevPI = actionPI(ACTION_PREV, 1)
        val togglePI = actionPI(ACTION_TOGGLE, 2)
        val nextPI = actionPI(ACTION_NEXT, 3)
        val stopPI = actionPI(ACTION_STOP, 4)

        val playPauseIcon = if (isPlaying) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play
        val playPauseText = if (isPlaying) "Pausa" else "Reproducir"

        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("🎶 ${song.title}")
            .setContentText("${song.artist} · ${song.playlistName}")
            .setSubText("Refugio de Principessa")
            .setSmallIcon(if (isPlaying) android.R.drawable.ic_media_play else android.R.drawable.ic_media_pause)
            .setContentIntent(openAppPendingIntent)
            .addAction(android.R.drawable.ic_media_previous, "Anterior", prevPI)
            .addAction(playPauseIcon, playPauseText, togglePI)
            .addAction(android.R.drawable.ic_media_next, "Siguiente", nextPI)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Cerrar", stopPI)
            .setOngoing(isPlaying)
            .setOnlyAlertOnce(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK)
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }
}
