package app.opposite.sync

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import app.opposite.MainActivity
import app.opposite.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

data class AppUpdateInfo(
    val versionCode: Int,
    val versionName: String,
    val title: String,
    val message: String,
    val releaseNotes: String,
    val downloadUrl: String,
    val releaseDate: String = "",
    val isMandatory: Boolean = false,
)

object AppUpdateChecker {
    private const val CHANNEL_ID = "adviento_app_updates"
    private const val NOTIF_ID = 9981

    private val _updateAvailable = MutableStateFlow<AppUpdateInfo?>(null)
    val updateAvailable: StateFlow<AppUpdateInfo?> = _updateAvailable.asStateFlow()

    private val _isChecking = MutableStateFlow(false)
    val isChecking: StateFlow<Boolean> = _isChecking.asStateFlow()

    private val _dismissedVersion = MutableStateFlow(0)
    val dismissedVersion: StateFlow<Int> = _dismissedVersion.asStateFlow()

    private val _isDownloading = MutableStateFlow(false)
    val isDownloading: StateFlow<Boolean> = _isDownloading.asStateFlow()

    private val _downloadProgress = MutableStateFlow(0)
    val downloadProgress: StateFlow<Int> = _downloadProgress.asStateFlow()

    private val _downloadError = MutableStateFlow<String?>(null)
    val downloadError: StateFlow<String?> = _downloadError.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.IO)

    fun initialize(context: Context) {
        createChannel(context)
        scope.launch {
            // Cargar versión desestimada de SharedPreferences
            val prefs = context.getSharedPreferences("app_update_prefs", Context.MODE_PRIVATE)
            _dismissedVersion.value = prefs.getInt("dismissed_version", 0)
            checkForUpdates(context, notifySystem = true)
        }
    }

    private fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Actualizaciones de La Casa de Enfrente"
            val desc = "Avisos de nuevas versiones y detalles de Elvis para Francesca"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = desc
                enableLights(true)
                lightColor = 0xFFC81D31.toInt() // Rojo carmesí
                enableVibration(true)
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    fun getCurrentVersionCode(context: Context): Int {
        return try {
            val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                pInfo.longVersionCode.toInt()
            } else {
                @Suppress("DEPRECATION")
                pInfo.versionCode
            }
        } catch (_: Exception) {
            1
        }
    }

    fun getCurrentVersionName(context: Context): String {
        return try {
            val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            pInfo.versionName ?: "1.0.0"
        } catch (_: Exception) {
            "1.0.0"
        }
    }

    fun dismissCurrentUpdate(context: Context, versionCode: Int) {
        _dismissedVersion.value = versionCode
        val prefs = context.getSharedPreferences("app_update_prefs", Context.MODE_PRIVATE)
        prefs.edit().putInt("dismissed_version", versionCode).apply()
    }

    fun checkForUpdates(
        context: Context,
        notifySystem: Boolean = false,
        onResult: ((hasUpdate: Boolean, message: String) -> Unit)? = null
    ) {
        scope.launch {
            _isChecking.value = true
            try {
                val urls = listOf(
                    "https://raw.githubusercontent.com/krailynd/nuestro-refugio/main/data/version.json",
                    "https://raw.githubusercontent.com/krailynd/la-casa-de-enfrente/main/version.json"
                )

                var jsonStr: String? = null
                for (u in urls) {
                    try {
                        val conn = URL(u).openConnection() as HttpURLConnection
                        conn.connectTimeout = 5000
                        conn.readTimeout = 5000
                        if (conn.responseCode == 200) {
                            jsonStr = conn.inputStream.bufferedReader().use { it.readText() }
                            break
                        }
                    } catch (_: Exception) {
                    }
                }

                val currentCode = getCurrentVersionCode(context)
                val currentName = getCurrentVersionName(context)

                if (jsonStr != null) {
                    val obj = JSONObject(jsonStr)
                    val remoteVersionCode = obj.optInt("versionCode", 0)
                    val remoteVersionName = obj.optString("versionName", "1.0.0")
                    val title = obj.optString("title", "¡Nueva actualización disponible, Principessa! 🏠✨")
                    val message = obj.optString(
                        "message",
                        "Tu Rata Aspirable ha preparado nuevos detalles, cartas y canciones para ti."
                    )
                    val releaseNotes = obj.optString(
                        "releaseNotes",
                        "• Mejoras de estabilidad y detalles de pareja."
                    )
                    val downloadUrl = obj.optString(
                        "downloadUrl",
                        "https://github.com/krailynd/la-casa-de-enfrente/releases/latest"
                    )
                    val releaseDate = obj.optString("releaseDate", "")
                    val isMandatory = obj.optBoolean("forceUpdate", false)

                    if (remoteVersionCode > currentCode) {
                        val updateInfo = AppUpdateInfo(
                            versionCode = remoteVersionCode,
                            versionName = remoteVersionName,
                            title = title,
                            message = message,
                            releaseNotes = releaseNotes,
                            downloadUrl = downloadUrl,
                            releaseDate = releaseDate,
                            isMandatory = isMandatory
                        )
                        _updateAvailable.value = updateInfo

                        if (notifySystem && (_dismissedVersion.value < remoteVersionCode || isMandatory)) {
                            postSystemNotification(context, updateInfo)
                        }

                        withContext(Dispatchers.Main) {
                            onResult?.invoke(
                                true,
                                "¡Hay una nueva versión v$remoteVersionName ($remoteVersionCode) disponible!"
                            )
                        }
                    } else {
                        _updateAvailable.value = null
                        withContext(Dispatchers.Main) {
                            onResult?.invoke(
                                false,
                                "La app está en la versión más reciente (v$currentName · $currentCode)."
                            )
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        onResult?.invoke(
                            false,
                            "No se pudo consultar el servidor de actualizaciones en GitHub. Revisa tu conexión."
                        )
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onResult?.invoke(false, "Error al comprobar actualizaciones: ${e.message}")
                }
            } finally {
                _isChecking.value = false
            }
        }
    }

    private fun postSystemNotification(context: Context, info: AppUpdateInfo) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return

        // Intent al presionar la notificación: abrir URL de descarga o la app
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(info.downloadUrl)).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            1001,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("La Casa de Enfrente ✨")
            .setContentText("¡Principessa! Hay una nueva versión con detalles de tu Rata Aspirable ❤️")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("${info.title}\n\n${info.message}\n\nNovedades v${info.versionName}:\n${info.releaseNotes}")
            )
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        try {
            notificationManager.notify(NOTIF_ID, notification)
        } catch (_: SecurityException) {
            // Notificación sin permiso concedido aún
        }
    }

    fun downloadAndInstall(
        context: Context,
        downloadUrl: String,
        onComplete: (() -> Unit)? = null
    ) {
        if (_isDownloading.value) return
        scope.launch {
            _isDownloading.value = true
            _downloadProgress.value = 0
            _downloadError.value = null
            try {
                var targetUrl = downloadUrl
                if (!targetUrl.endsWith(".apk", ignoreCase = true)) {
                    targetUrl = "https://raw.githubusercontent.com/krailynd/la-casa-de-enfrente/main/apk-compile/Nuestro_Refugio_Francesca_v1.0.apk"
                }

                val apkFile = java.io.File(context.cacheDir, "nuestro_refugio_update.apk")
                if (apkFile.exists()) apkFile.delete()

                var currentUrl = targetUrl
                var connection: HttpURLConnection? = null
                var redirects = 0
                while (redirects < 6) {
                    val conn = URL(currentUrl).openConnection() as HttpURLConnection
                    conn.instanceFollowRedirects = true
                    conn.connectTimeout = 20000
                    conn.readTimeout = 25000
                    val code = conn.responseCode
                    if (code == HttpURLConnection.HTTP_MOVED_PERM || code == HttpURLConnection.HTTP_MOVED_TEMP || code == 307 || code == 308) {
                        currentUrl = conn.getHeaderField("Location") ?: break
                        redirects++
                    } else if (code == 200) {
                        connection = conn
                        break
                    } else {
                        throw Exception("Código HTTP $code al descargar")
                    }
                }

                if (connection == null) {
                    throw Exception("No se pudo conectar a la descarga")
                }

                val totalBytes = connection.contentLengthLong.takeIf { it > 0 } ?: (82L * 1024 * 1024)
                var bytesReadTotal = 0L

                connection.inputStream.use { input ->
                    apkFile.outputStream().use { output ->
                        val buffer = ByteArray(64 * 1024)
                        var read: Int
                        while (input.read(buffer).also { read = it } != -1) {
                            output.write(buffer, 0, read)
                            bytesReadTotal += read
                            val progress = ((bytesReadTotal * 100) / totalBytes).toInt().coerceIn(1, 99)
                            _downloadProgress.value = progress
                        }
                    }
                }

                _downloadProgress.value = 100
                _isDownloading.value = false

                withContext(Dispatchers.Main) {
                    onComplete?.invoke()
                    installApk(context, apkFile)
                }
            } catch (e: Exception) {
                _isDownloading.value = false
                _downloadError.value = "Error al descargar: ${e.message}"
                withContext(Dispatchers.Main) {
                    openDownloadUrl(context, downloadUrl)
                }
            }
        }
    }

    fun installApk(context: Context, apkFile: java.io.File) {
        try {
            val contentUri = androidx.core.content.FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                apkFile
            )
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(contentUri, "application/vnd.android.package-archive")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            openDownloadUrl(context, "https://github.com/krailynd/la-casa-de-enfrente/releases/latest")
        }
    }

    fun openDownloadUrl(context: Context, url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (_: Exception) {
        }
    }
}
