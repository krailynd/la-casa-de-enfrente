package app.opposite.ui

import android.content.Context
import app.opposite.music.MusicController

/**
 * Ambient Background Music Controller.
 * Plays random songs from our offline collection at low volume (0.22f) for cozy atmosphere.
 * Pauses automatically if the Spotify module player starts playing.
 */
object SoundManager {
    fun start(context: Context) {
        MusicController.startBackground(context)
    }

    fun stop() {
        MusicController.stopBackground()
    }

    fun toggle(context: Context): Boolean {
        return MusicController.toggleBackground(context)
    }

    fun isPlaying(): Boolean = MusicController.isBgPlaying
}
