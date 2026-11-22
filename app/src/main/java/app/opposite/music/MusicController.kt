package app.opposite.music

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

object MusicController {
    // ------------------------------------------------------------------------
    // Background Ambient Music (Aleatorio a bajo volumen, sin notificación)
    // ------------------------------------------------------------------------
    private var bgPlayer: MediaPlayer? = null
    var isBgPlaying by mutableStateOf(false)
        private set

    // ------------------------------------------------------------------------
    // Spotify Module Player (Canción elegida, volumen 1.0f, con notificación)
    // ------------------------------------------------------------------------
    private var modulePlayer: MediaPlayer? = null
    var currentSong by mutableStateOf<Song?>(null)
        private set
    var currentPlaylist by mutableStateOf<Playlist?>(null)
        private set
    var isPlaying by mutableStateOf(false)
        private set
    var positionMs by mutableIntStateOf(0)
        private set
    var durationMs by mutableIntStateOf(0)
        private set
    var isShuffle by mutableStateOf(false)
        private set
    var isRepeat by mutableStateOf(false)
        private set
    var isPlayerExpanded by mutableStateOf(false)

    private var progressJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main)

    // Current queue
    private var queue: List<Song> = emptyList()
    private var queueIndex: Int = 0

    // ------------------------------------------------------------------------
    // Background Music Controls
    // ------------------------------------------------------------------------
    fun startBackground(context: Context) {
        if (isPlaying) {
            // Regla estricta: No sonar fondo si el módulo Spotify está activo
            return
        }
        stopBackground()
        try {
            val song = MusicCatalog.randomSong()
            val afd = context.assets.openFd(song.assetPath)
            bgPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                afd.close()
                // Bajo volumen, suave y cálido (0.22f)
                setVolume(0.22f, 0.22f)
                setOnCompletionListener {
                    // Siguiente canción aleatoria en fondo continuo
                    startBackground(context)
                }
                prepare()
                start()
            }
            isBgPlaying = true
        } catch (_: Exception) {
            isBgPlaying = false
        }
    }

    fun stopBackground() {
        try {
            bgPlayer?.stop()
            bgPlayer?.release()
        } catch (_: Exception) {
        } finally {
            bgPlayer = null
            isBgPlaying = false
        }
    }

    fun toggleBackground(context: Context): Boolean {
        return if (isBgPlaying) {
            stopBackground()
            false
        } else {
            // Si el módulo está reproduciendo, lo pausamos para dar paso al fondo
            if (isPlaying) {
                pause(context)
            }
            startBackground(context)
            true
        }
    }

    // ------------------------------------------------------------------------
    // Spotify Module Player Controls
    // ------------------------------------------------------------------------
    fun playSong(context: Context, song: Song, playlist: Playlist? = null) {
        // REGLA CRÍTICA DE ORO: La música de fondo SE APAGA automáticamente
        stopBackground()

        val pl = playlist ?: MusicCatalog.findPlaylist(song.playlistId) ?: MusicCatalog.playlistAmor
        currentPlaylist = pl
        currentSong = song

        // Setup queue
        queue = if (isShuffle) pl.songs.shuffled() else pl.songs
        queueIndex = queue.indexOfFirst { it.id == song.id }.coerceAtLeast(0)

        startPlayback(context, song)
    }

    fun playRandomFromAll(context: Context) {
        stopBackground()
        val pl = MusicCatalog.playlists.random()
        val song = pl.songs.random()
        playSong(context, song, pl)
    }

    private fun startPlayback(context: Context, song: Song) {
        try {
            modulePlayer?.stop()
            modulePlayer?.release()
        } catch (_: Exception) {
        }

        try {
            val afd = context.assets.openFd(song.assetPath)
            modulePlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                afd.close()
                setVolume(1.0f, 1.0f)
                setOnPreparedListener { mp ->
                    this@MusicController.durationMs = mp.duration
                    this@MusicController.positionMs = 0
                    mp.start()
                    this@MusicController.isPlaying = true
                    startProgressTracker()
                    MusicPlaybackService.startOrUpdate(context, MusicPlaybackService.ACTION_UPDATE)
                }
                setOnCompletionListener {
                    onSongCompleted(context)
                }
                prepareAsync()
            }
        } catch (_: Exception) {
            isPlaying = false
        }
    }

    private fun onSongCompleted(context: Context) {
        if (isRepeat) {
            currentSong?.let { startPlayback(context, it) }
        } else {
            next(context)
        }
    }

    fun resume(context: Context) {
        // Asegurar que música de fondo esté apagada
        stopBackground()
        modulePlayer?.let { mp ->
            if (!mp.isPlaying) {
                mp.start()
                isPlaying = true
                startProgressTracker()
                MusicPlaybackService.startOrUpdate(context, MusicPlaybackService.ACTION_UPDATE)
            }
        }
    }

    fun pause(context: Context) {
        modulePlayer?.let { mp ->
            if (mp.isPlaying) {
                mp.pause()
                isPlaying = false
                stopProgressTracker()
                MusicPlaybackService.startOrUpdate(context, MusicPlaybackService.ACTION_UPDATE)
            }
        }
    }

    fun togglePlayPause(context: Context) {
        if (isPlaying) {
            pause(context)
        } else {
            if (modulePlayer != null) {
                resume(context)
            } else if (currentSong != null) {
                startPlayback(context, currentSong!!)
            } else {
                playRandomFromAll(context)
            }
        }
    }

    fun next(context: Context) {
        if (queue.isEmpty()) return
        queueIndex = (queueIndex + 1) % queue.size
        val nextSong = queue[queueIndex]
        currentSong = nextSong
        startPlayback(context, nextSong)
    }

    fun prev(context: Context) {
        modulePlayer?.let { mp ->
            if (mp.currentPosition > 3000) {
                mp.seekTo(0)
                positionMs = 0
                return
            }
        }
        if (queue.isEmpty()) return
        queueIndex = if (queueIndex - 1 < 0) queue.size - 1 else queueIndex - 1
        val prevSong = queue[queueIndex]
        currentSong = prevSong
        startPlayback(context, prevSong)
    }

    fun seekTo(msec: Int) {
        modulePlayer?.seekTo(msec)
        positionMs = msec
    }

    fun toggleShuffle() {
        isShuffle = !isShuffle
        val pl = currentPlaylist
        if (pl != null) {
            queue = if (isShuffle) pl.songs.shuffled() else pl.songs
            currentSong?.let { s ->
                queueIndex = queue.indexOfFirst { it.id == s.id }.coerceAtLeast(0)
            }
        }
    }

    fun toggleRepeat() {
        isRepeat = !isRepeat
    }

    fun stopModulePlayer(context: Context) {
        stopProgressTracker()
        try {
            modulePlayer?.stop()
            modulePlayer?.release()
        } catch (_: Exception) {
        } finally {
            modulePlayer = null
            isPlaying = false
            currentSong = null
            MusicPlaybackService.stop(context)
        }
    }

    private fun startProgressTracker() {
        progressJob?.cancel()
        progressJob = scope.launch {
            while (isActive && isPlaying) {
                modulePlayer?.let { mp ->
                    try {
                        if (mp.isPlaying) {
                            positionMs = mp.currentPosition
                        }
                    } catch (_: Exception) {}
                }
                delay(400)
            }
        }
    }

    private fun stopProgressTracker() {
        progressJob?.cancel()
        progressJob = null
    }

    fun releaseAll(context: Context) {
        stopBackground()
        stopModulePlayer(context)
    }
}
