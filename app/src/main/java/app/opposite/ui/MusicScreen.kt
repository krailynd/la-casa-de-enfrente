package app.opposite.ui

import android.graphics.BitmapFactory
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.opposite.music.MusicCatalog
import app.opposite.music.MusicController
import app.opposite.music.Playlist
import app.opposite.music.Song
import app.opposite.ui.theme.C
import app.opposite.ui.theme.Sh
import app.opposite.ui.theme.T

@Composable
fun MusicScreen(acts: Acts) {
    val context = LocalContext.current
    var selectedPlaylist by remember { mutableStateOf<Playlist?>(null) }

    Column(
        Modifier
            .fillMaxSize()
            .background(C.bg)
            .statusBarsPadding()
    ) {
        // Top Header
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    "Refugio Musical 🎧",
                    style = T.Title.copy(color = C.ink, fontSize = 22.sp, fontWeight = FontWeight.Bold),
                )
                Text(
                    "Colección para Principessa · Hecho con amor por Elvis",
                    style = T.Eyebrow.copy(color = C.raspberry, fontSize = 11.sp),
                )
            }

            // Quick Shuffle All button
            Box(
                Modifier
                    .clip(Sh.Sm)
                    .background(Brush.linearGradient(listOf(C.red, Color(0xFF8A1322))))
                    .clickable {
                        MusicController.playRandomFromAll(context)
                        acts.toast("Reproduciendo canción aleatoria 🎲✨")
                    }
                    .padding(horizontal = 12.dp, vertical = 8.dp),
            ) {
                Text("Aleatorio 🎲", style = T.Name.copy(color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold))
            }
        }

        // Ambient Mode / Background music indicator banner
        AmbientModeBanner(acts)

        // Main Content: Playlists or Playlist Detail
        val pl = selectedPlaylist
        if (pl != null) {
            PlaylistDetailView(
                playlist = pl,
                onBack = { selectedPlaylist = null },
                acts = acts,
            )
        } else {
            PlaylistsOverview(
                onSelectPlaylist = { selectedPlaylist = it },
                acts = acts,
            )
        }
    }
}

@Composable
private fun AmbientModeBanner(acts: Acts) {
    val context = LocalContext.current
    val isBgOn = MusicController.isBgPlaying
    val isModuleOn = MusicController.isPlaying

    Box(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clip(Sh.Md)
            .background(
                if (isModuleOn) Color(0xFF2A1520)
                else if (isBgOn) Color(0xFF1E2F23)
                else Color(0xFF24141E)
            )
            .border(
                1.dp,
                if (isModuleOn) C.red.copy(alpha = 0.5f)
                else if (isBgOn) C.green.copy(alpha = 0.5f)
                else C.line,
                Sh.Md
            )
            .padding(horizontal = 12.dp, vertical = 8.dp),
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Text(
                    if (isModuleOn) "🎶" else if (isBgOn) "🍃" else "🔇",
                    fontSize = 18.sp,
                    modifier = Modifier.padding(end = 8.dp),
                )
                Column {
                    Text(
                        if (isModuleOn) "Reproductor activo (Fondo en silencio)"
                        else if (isBgOn) "Música de fondo suave activa (Vol. bajo)"
                        else "Música de fondo apagada",
                        style = T.Name.copy(
                            color = if (isModuleOn) C.warm else if (isBgOn) C.green else C.dim,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                        ),
                    )
                    Text(
                        if (isModuleOn) "Disfrutando canción completa con controles de notificación"
                        else "Melodías aleatorias a bajo volumen para ambientar el refugio",
                        style = T.Muted.copy(color = C.dim, fontSize = 10.sp),
                    )
                }
            }

            if (!isModuleOn) {
                Box(
                    Modifier
                        .clip(Sh.Sm)
                        .background(if (isBgOn) Color(0xFF384E3F) else Color(0xFF3E1F29))
                        .clickable {
                            val active = MusicController.toggleBackground(context)
                            acts.toast(if (active) "Música suave de fondo activada 🍃" else "Fondo silenciado")
                        }
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                ) {
                    Text(
                        if (isBgOn) "Silenciar" else "Activar fondo",
                        style = T.Eyebrow.copy(color = Color.White, fontSize = 10.sp),
                    )
                }
            }
        }
    }
}

@Composable
private fun PlaylistsOverview(onSelectPlaylist: (Playlist) -> Unit, acts: Acts) {
    val context = LocalContext.current
    val playlists = MusicCatalog.playlists

    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
    ) {
        item {
            Text(
                "NUESTRAS PLAYLISTS",
                style = T.Eyebrow.copy(color = C.dim, fontSize = 11.sp, letterSpacing = 1.sp),
                modifier = Modifier.padding(top = 12.dp, bottom = 8.dp),
            )
        }

        itemsIndexed(playlists) { _, playlist ->
            PlaylistCard(
                playlist = playlist,
                onClick = { onSelectPlaylist(playlist) },
                onPlayAll = {
                    val firstSong = playlist.songs.firstOrNull()
                    if (firstSong != null) {
                        MusicController.playSong(context, firstSong, playlist)
                        acts.toast("Reproduciendo ${playlist.title} ▶")
                    }
                },
            )
            Spacer(Modifier.height(10.dp))
        }

        item {
            Spacer(Modifier.height(12.dp))
            Text(
                "TODAS LAS CANCIONES (${MusicCatalog.allSongs.size})",
                style = T.Eyebrow.copy(color = C.dim, fontSize = 11.sp, letterSpacing = 1.sp),
                modifier = Modifier.padding(vertical = 8.dp),
            )
        }

        itemsIndexed(MusicCatalog.allSongs) { idx, song ->
            SongRow(
                index = idx + 1,
                song = song,
                isSelected = MusicController.currentSong?.id == song.id,
                isPlaying = MusicController.currentSong?.id == song.id && MusicController.isPlaying,
                onClick = {
                    MusicController.playSong(context, song)
                    acts.toast("Reproduciendo ${song.title} 🎵")
                },
            )
            Spacer(Modifier.height(4.dp))
        }

        item {
            Spacer(Modifier.height(90.dp)) // Padding for MiniPlayer
        }
    }
}

@Composable
private fun PlaylistCard(playlist: Playlist, onClick: () -> Unit, onPlayAll: () -> Unit) {
    Box(
        Modifier
            .fillMaxWidth()
            .clip(Sh.Lg)
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(playlist.colorHex).copy(alpha = 0.35f),
                        Color(0xFF1E0E15),
                    )
                )
            )
            .border(1.dp, Color(playlist.colorHex).copy(alpha = 0.5f), Sh.Lg)
            .clickable(onClick = onClick)
            .padding(14.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Icon Badge
            Box(
                Modifier
                    .size(54.dp)
                    .clip(Sh.Md)
                    .background(Color(playlist.colorHex).copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center,
            ) {
                Text(playlist.icon, fontSize = 26.sp)
            }

            Spacer(Modifier.width(14.dp))

            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        playlist.title,
                        style = T.Name.copy(color = C.ink, fontSize = 16.sp, fontWeight = FontWeight.Bold),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "${playlist.songs.size} temas",
                        style = T.Eyebrow.copy(color = C.dim, fontSize = 10.sp),
                    )
                }
                Text(
                    playlist.subtitle,
                    style = T.Muted.copy(color = C.raspberry, fontSize = 11.sp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    playlist.description,
                    style = T.Body.copy(color = C.dim, fontSize = 11.sp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }

            // Play Button
            Box(
                Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(C.red)
                    .clickable(onClick = onPlayAll),
                contentAlignment = Alignment.Center,
            ) {
                Text("▶", color = Color.White, fontSize = 14.sp)
            }
        }
    }
}

@Composable
private fun PlaylistDetailView(playlist: Playlist, onBack: () -> Unit, acts: Acts) {
    val context = LocalContext.current

    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
    ) {
        item {
            // Back button
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "← Volver a Playlists",
                    style = T.Name.copy(color = C.warm, fontSize = 14.sp),
                    modifier = Modifier
                        .clip(Sh.Sm)
                        .clickable(onClick = onBack)
                        .padding(vertical = 6.dp, horizontal = 4.dp),
                )
            }

            // Playlist Header Card
            Box(
                Modifier
                    .fillMaxWidth()
                    .clip(Sh.Lg)
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(playlist.colorHex).copy(alpha = 0.5f),
                                Color(0xFF1A0B12),
                            )
                        )
                    )
                    .border(1.dp, Color(playlist.colorHex), Sh.Lg)
                    .padding(18.dp),
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(playlist.icon, fontSize = 36.sp, modifier = Modifier.padding(end = 12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(playlist.badge, style = T.Eyebrow.copy(color = C.warm, fontSize = 11.sp))
                            Text(
                                playlist.title,
                                style = T.Title.copy(color = C.ink, fontSize = 22.sp, fontWeight = FontWeight.Bold),
                            )
                        }
                    }
                    Text(
                        playlist.description,
                        style = T.Body.copy(color = C.paperInk.copy(alpha = 0.85f), fontSize = 13.sp),
                        modifier = Modifier.padding(top = 10.dp),
                    )

                    Spacer(Modifier.height(14.dp))

                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        // Play All Button
                        Box(
                            Modifier
                                .weight(1f)
                                .clip(Sh.Sm)
                                .background(C.red)
                                .clickable {
                                    val first = playlist.songs.firstOrNull()
                                    if (first != null) {
                                        MusicController.playSong(context, first, playlist)
                                        acts.toast("Reproduciendo ${playlist.title} ▶")
                                    }
                                }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                "▶ Reproducir todo",
                                style = T.Name.copy(color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold),
                            )
                        }

                        // Shuffle Button
                        Box(
                            Modifier
                                .weight(1f)
                                .clip(Sh.Sm)
                                .background(Color(0xFF381520))
                                .border(1.dp, C.red, Sh.Sm)
                                .clickable {
                                    val randomSong = playlist.songs.random()
                                    MusicController.playSong(context, randomSong, playlist)
                                    acts.toast("Aleatorio: ${randomSong.title} 🔀")
                                }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                "🔀 Modo Aleatorio",
                                style = T.Name.copy(color = C.warm, fontSize = 13.sp, fontWeight = FontWeight.SemiBold),
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(14.dp))
            Text(
                "CANCIONES DE LA PLAYLIST",
                style = T.Eyebrow.copy(color = C.dim, fontSize = 11.sp, letterSpacing = 1.sp),
                modifier = Modifier.padding(vertical = 4.dp),
            )
        }

        itemsIndexed(playlist.songs) { index, song ->
            SongRow(
                index = index + 1,
                song = song,
                isSelected = MusicController.currentSong?.id == song.id,
                isPlaying = MusicController.currentSong?.id == song.id && MusicController.isPlaying,
                onClick = {
                    MusicController.playSong(context, song, playlist)
                    acts.toast("Reproduciendo ${song.title} 🎵")
                },
            )
            Spacer(Modifier.height(6.dp))
        }

        item {
            Spacer(Modifier.height(90.dp)) // Padding for MiniPlayer
        }
    }
}

@Composable
fun rememberAssetBitmap(path: String): ImageBitmap? {
    val context = LocalContext.current
    return remember(path) {
        try {
            context.assets.open(path).use { stream ->
                BitmapFactory.decodeStream(stream)?.asImageBitmap()
            }
        } catch (_: Exception) {
            null
        }
    }
}

@Composable
fun SongRow(
    index: Int,
    song: Song,
    isSelected: Boolean,
    isPlaying: Boolean,
    onClick: () -> Unit,
) {
    val coverBitmap = rememberAssetBitmap(song.coverPath)
    Box(
        Modifier
            .fillMaxWidth()
            .clip(Sh.Md)
            .background(if (isSelected) Color(0xFF351522) else C.card)
            .border(
                1.dp,
                if (isSelected) C.red.copy(alpha = 0.6f) else C.line.copy(alpha = 0.5f),
                Sh.Md
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Album Cover Thumbnail with animated equalizer overlay
            Box(
                Modifier
                    .size(46.dp)
                    .clip(Sh.Sm)
                    .border(1.dp, if (isSelected) C.red else Color(0x33F7EFE2), Sh.Sm)
                    .background(Color(0xFF200E17)),
                contentAlignment = Alignment.Center,
            ) {
                if (coverBitmap != null) {
                    Image(
                        bitmap = coverBitmap,
                        contentDescription = song.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                } else {
                    Text(song.icon, fontSize = 20.sp)
                }
                if (isPlaying) {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(Color(0x77000000)),
                        contentAlignment = Alignment.Center,
                    ) {
                        EqualizerWaveAnimation()
                    }
                }
            }

            Spacer(Modifier.width(12.dp))

            // Song Info
            Column(Modifier.weight(1f)) {
                Text(
                    song.title,
                    style = T.Name.copy(
                        color = if (isSelected) C.warm else C.ink,
                        fontSize = 14.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    "${song.artist} · ${formatTimeSec(song.durationSec)}",
                    style = T.Muted.copy(color = C.dim, fontSize = 11.sp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            // Play / Pause Indicator
            Text(
                if (isPlaying) "⏸" else if (isSelected) "▶" else "▷",
                style = T.Title.copy(
                    color = if (isSelected) C.red else C.dim,
                    fontSize = 16.sp,
                ),
                modifier = Modifier.padding(start = 8.dp),
            )
        }
    }
}

@Composable
fun MiniPlayerBar() {
    val context = LocalContext.current
    val song = MusicController.currentSong ?: return
    val isPlaying = MusicController.isPlaying
    val progress = if (MusicController.durationMs > 0) {
        (MusicController.positionMs.toFloat() / MusicController.durationMs).coerceIn(0f, 1f)
    } else 0f

    Box(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .shadow(12.dp, Sh.Lg)
            .clip(Sh.Lg)
            .background(
                Brush.horizontalGradient(
                    listOf(
                        Color(0xFF3B1522),
                        Color(0xFF220E17),
                    )
                )
            )
            .border(1.dp, C.red.copy(alpha = 0.5f), Sh.Lg)
            .clickable { MusicController.isPlayerExpanded = true }
    ) {
        Column {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Album Art thumbnail with fallback to spinning vinyl
                val coverBitmap = rememberAssetBitmap(song.coverPath)
                Box(
                    Modifier
                        .size(40.dp)
                        .clip(Sh.Sm)
                        .border(1.5.dp, C.red, Sh.Sm)
                        .background(Color(0xFF200E17)),
                    contentAlignment = Alignment.Center,
                ) {
                    if (coverBitmap != null) {
                        Image(
                            bitmap = coverBitmap,
                            contentDescription = song.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize(),
                        )
                    } else {
                        VinylCoverThumbnail(isPlaying = isPlaying)
                    }
                }

                Spacer(Modifier.width(10.dp))

                // Song info
                Column(Modifier.weight(1f)) {
                    Text(
                        song.title,
                        style = T.Name.copy(color = C.ink, fontSize = 13.sp, fontWeight = FontWeight.Bold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        "${song.artist} · ${song.playlistName}",
                        style = T.Muted.copy(color = C.raspberry, fontSize = 10.sp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                // Controls: Prev, Play/Pause, Next
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "⏮",
                        style = T.Title.copy(color = C.dim, fontSize = 14.sp),
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable { MusicController.prev(context) }
                            .padding(6.dp),
                    )
                    Box(
                        Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(C.red)
                            .clickable { MusicController.togglePlayPause(context) },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(if (isPlaying) "⏸" else "▶", color = Color.White, fontSize = 13.sp)
                    }
                    Text(
                        "⏭",
                        style = T.Title.copy(color = C.dim, fontSize = 14.sp),
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable { MusicController.next(context) }
                            .padding(6.dp),
                    )
                }
            }

            // Bottom progress line
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.5.dp),
                color = C.red,
                trackColor = Color(0x33C81D31),
            )
        }
    }
}

@Composable
fun FullScreenPlayer() {
    val context = LocalContext.current
    val song = MusicController.currentSong ?: return
    val isPlaying = MusicController.isPlaying
    val pos = MusicController.positionMs
    val dur = MusicController.durationMs.coerceAtLeast(1)
    val progress = (pos.toFloat() / dur).coerceIn(0f, 1f)

    Box(
        Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF3A121F),
                        Color(0xFF1B0810),
                        Color(0xFF10050A),
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(20.dp),
    ) {
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Header: down arrow & playlist name
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    "⌄",
                    style = T.Title.copy(color = C.ink, fontSize = 28.sp),
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { MusicController.isPlayerExpanded = false }
                        .padding(horizontal = 8.dp),
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "REPRODUCIENDO DE",
                        style = T.Eyebrow.copy(color = C.dim, fontSize = 10.sp, letterSpacing = 1.sp),
                    )
                    Text(
                        song.playlistName,
                        style = T.Name.copy(color = C.warm, fontSize = 13.sp, fontWeight = FontWeight.Bold),
                    )
                }
                Text("…", style = T.Title.copy(color = C.dim, fontSize = 20.sp), modifier = Modifier.padding(horizontal = 8.dp))
            }

            Spacer(Modifier.height(24.dp))

            // Big Album Cover Art Banner
            val coverBitmap = rememberAssetBitmap(song.coverPath)
            Box(
                Modifier
                    .size(240.dp)
                    .aspectRatio(1f)
                    .shadow(24.dp, Sh.Lg)
                    .clip(Sh.Lg)
                    .border(2.dp, C.red, Sh.Lg)
                    .background(Color(0xFF200E17)),
                contentAlignment = Alignment.Center,
            ) {
                if (coverBitmap != null) {
                    Image(
                        bitmap = coverBitmap,
                        contentDescription = song.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                } else {
                    BigVinylCover(isPlaying = isPlaying)
                }
            }

            Spacer(Modifier.height(32.dp))

            // Song Titles
            Column(
                Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start,
            ) {
                Text(
                    song.title,
                    style = T.Title.copy(color = C.ink, fontSize = 22.sp, fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    song.artist,
                    style = T.Name.copy(color = C.raspberry, fontSize = 14.sp),
                )
            }

            Spacer(Modifier.height(24.dp))

            // Progress Slider
            Slider(
                value = progress,
                onValueChange = { newProg ->
                    val newMs = (newProg * dur).toInt()
                    MusicController.seekTo(newMs)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = C.warm,
                    activeTrackColor = C.red,
                    inactiveTrackColor = Color(0x33C81D31),
                ),
            )

            // Timestamps
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(formatTimeMs(pos), style = T.Muted.copy(color = C.dim, fontSize = 12.sp))
                Text(formatTimeMs(dur), style = T.Muted.copy(color = C.dim, fontSize = 12.sp))
            }

            Spacer(Modifier.height(20.dp))

            // Main Playback Controls
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Shuffle
                Text(
                    "🔀",
                    fontSize = 20.sp,
                    color = if (MusicController.isShuffle) C.warm else C.dim,
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { MusicController.toggleShuffle() }
                        .padding(10.dp),
                )

                // Prev
                Text(
                    "⏮",
                    style = T.Title.copy(color = C.ink, fontSize = 26.sp),
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { MusicController.prev(context) }
                        .padding(10.dp),
                )

                // Play / Pause Big Button
                Box(
                    Modifier
                        .size(68.dp)
                        .shadow(16.dp, CircleShape)
                        .clip(CircleShape)
                        .background(C.red)
                        .clickable { MusicController.togglePlayPause(context) },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        if (isPlaying) "⏸" else "▶",
                        color = Color.White,
                        fontSize = 24.sp,
                    )
                }

                // Next
                Text(
                    "⏭",
                    style = T.Title.copy(color = C.ink, fontSize = 26.sp),
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { MusicController.next(context) }
                        .padding(10.dp),
                )

                // Repeat
                Text(
                    "🔁",
                    fontSize = 20.sp,
                    color = if (MusicController.isRepeat) C.warm else C.dim,
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { MusicController.toggleRepeat() }
                        .padding(10.dp),
                )
            }

            Spacer(Modifier.height(24.dp))

            // Footer note
            Box(
                Modifier
                    .fillMaxWidth()
                    .clip(Sh.Md)
                    .background(Color(0x33000000))
                    .padding(12.dp),
            ) {
                Text(
                    "✨ Música offline exclusiva de nuestro refugio. Mientras escuchas una canción de la playlist, la música ambiental de fondo se pausa automáticamente.",
                    style = T.Muted.copy(color = C.dim, fontSize = 11.sp),
                )
            }
        }
    }
}

@Composable
private fun VinylCoverThumbnail(isPlaying: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "mini_vinyl")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "mini_rot",
    )

    Box(
        Modifier
            .size(38.dp)
            .rotate(if (isPlaying) rotation else 0f)
            .clip(CircleShape)
            .background(Color(0xFF1E0E15))
            .border(1.5.dp, C.red, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text("🎵", fontSize = 18.sp)
    }
}

@Composable
private fun BigVinylCover(isPlaying: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "big_vinyl")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "big_rot",
    )

    Box(
        Modifier
            .size(240.dp)
            .aspectRatio(1f)
            .shadow(24.dp, CircleShape)
            .rotate(if (isPlaying) rotation else 0f)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    listOf(
                        Color(0xFF4A1828),
                        Color(0xFF200A13),
                        Color(0xFF10050A),
                    )
                )
            )
            .border(3.dp, C.red, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        // Grooves
        Box(
            Modifier
                .size(190.dp)
                .border(1.dp, Color(0x33F7EFE2), CircleShape)
        )
        Box(
            Modifier
                .size(140.dp)
                .border(1.dp, Color(0x22F7EFE2), CircleShape)
        )
        // Center label
        Box(
            Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(C.red, Color(0xFF6B121E)))),
            contentAlignment = Alignment.Center,
        ) {
            Text("❤️", fontSize = 32.sp)
        }
    }
}

@Composable
private fun EqualizerWaveAnimation() {
    val transition = rememberInfiniteTransition(label = "eq")
    val h1 by transition.animateFloat(
        initialValue = 4f, targetValue = 16f,
        animationSpec = infiniteRepeatable(tween(400), RepeatMode.Reverse), label = "h1"
    )
    val h2 by transition.animateFloat(
        initialValue = 14f, targetValue = 6f,
        animationSpec = infiniteRepeatable(tween(350), RepeatMode.Reverse), label = "h2"
    )
    val h3 by transition.animateFloat(
        initialValue = 8f, targetValue = 18f,
        animationSpec = infiniteRepeatable(tween(450), RepeatMode.Reverse), label = "h3"
    )

    Row(
        Modifier.height(18.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Box(Modifier.width(3.dp).height(h1.dp).clip(RoundedCornerShape(1.dp)).background(C.red))
        Box(Modifier.width(3.dp).height(h2.dp).clip(RoundedCornerShape(1.dp)).background(C.warm))
        Box(Modifier.width(3.dp).height(h3.dp).clip(RoundedCornerShape(1.dp)).background(C.raspberry))
    }
}

private fun formatTimeSec(seconds: Int): String {
    val m = seconds / 60
    val s = seconds % 60
    return String.format("%02d:%02d", m, s)
}

private fun formatTimeMs(ms: Int): String {
    return formatTimeSec(ms / 1000)
}
