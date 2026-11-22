package app.opposite.music

data class Song(
    val id: String,
    val title: String,
    val artist: String = "Elvis · Para Francesca",
    val playlistId: String,
    val playlistName: String,
    val assetPath: String,
    val coverPath: String,
    val icon: String = "🎵",
    val durationSec: Int = 180,
)

data class Playlist(
    val id: String,
    val title: String,
    val subtitle: String,
    val badge: String,
    val colorHex: Long,
    val icon: String,
    val description: String,
    val coverPath: String = "",
    val songs: List<Song>,
)

object MusicCatalog {
    val playlistDulce = Playlist(
        id = "dulce",
        title = "Música Dulce",
        subtitle = "Melodías tiernas para mi niña hermosa",
        badge = "💖 Dulce",
        colorHex = 0xFFE04F5F,
        icon = "💖",
        description = "Canciones suaves, llenas de ternura y cariño para consentir a Principessa en cualquier momento del día.",
        coverPath = "covers/i_love_you.jpg",
        songs = listOf(
            Song(
                id = "dulce_hermosos_ojos",
                title = "Hermosos Ojos",
                artist = "Para Principessa · Elvis",
                playlistId = "dulce",
                playlistName = "Música Dulce",
                assetPath = "playlists/dulce/hermosos-ojos.mp3",
                coverPath = "covers/hermosos_ojos.jpg",
                icon = "👀",
                durationSec = 88,
            ),
            Song(
                id = "dulce_i_love_you",
                title = "I Love You",
                artist = "Para Principessa · Elvis",
                playlistId = "dulce",
                playlistName = "Música Dulce",
                assetPath = "playlists/dulce/i-love-you.mp3",
                coverPath = "covers/i_love_you.jpg",
                icon = "✨",
                durationSec = 247,
            ),
            Song(
                id = "dulce_te_amo",
                title = "Te Amo",
                artist = "Para Principessa · Elvis",
                playlistId = "dulce",
                playlistName = "Música Dulce",
                assetPath = "playlists/dulce/te-amo.mp3",
                coverPath = "covers/te_amo.jpg",
                icon = "💌",
                durationSec = 254,
            ),
        ),
    )

    val playlistAmor = Playlist(
        id = "amor",
        title = "Música Amor",
        subtitle = "El amor más puro y sincero que tengo",
        badge = "❤️ Amor",
        colorHex = 0xFFC81D31,
        icon = "❤️",
        description = "Letras profundas y ritmos dedicados a nuestra historia, nuestro nuevo comienzo y nuestro amor sincero.",
        coverPath = "covers/elegante_y_hermosa.jpg",
        songs = listOf(
            Song(
                id = "amor_elegante_hermosa",
                title = "Elegante y Hermosa",
                artist = "Para Principessa · Elvis",
                playlistId = "amor",
                playlistName = "Música Amor",
                assetPath = "playlists/amor/elegante-y-hermosa.mp3",
                coverPath = "covers/elegante_y_hermosa.jpg",
                icon = "🌹",
                durationSec = 298,
            ),
            Song(
                id = "amor_lo_nocturno_es_tuyo",
                title = "Lo Nocturno es Tuyo",
                artist = "Para Principessa · Elvis",
                playlistId = "amor",
                playlistName = "Música Amor",
                assetPath = "playlists/amor/lo-nocturno-es-tuyo.mp3",
                coverPath = "covers/lo_nocturno_es_tuyo.jpg",
                icon = "🌙",
                durationSec = 286,
            ),
            Song(
                id = "amor_mi_sueno_enorme",
                title = "Mi Sueño Enorme",
                artist = "Para Principessa · Elvis",
                playlistId = "amor",
                playlistName = "Música Amor",
                assetPath = "playlists/amor/mi-sueno-enorme.mp3",
                coverPath = "covers/mi_sueno_enorme.jpg",
                icon = "💫",
                durationSec = 298,
            ),
            Song(
                id = "amor_princesa_angel",
                title = "Princesa y Ángel",
                artist = "Para Principessa · Elvis",
                playlistId = "amor",
                playlistName = "Música Amor",
                assetPath = "playlists/amor/princesa-y-angel.mp3",
                coverPath = "covers/princesa_y_angel.jpg",
                icon = "👑",
                durationSec = 298,
            ),
        ),
    )

    val playlistNavidena = Playlist(
        id = "navidena",
        title = "Música Navideña",
        subtitle = "Nuestra primera Navidad juntos",
        badge = "🎄 Navideña",
        colorHex = 0xFF2D5A3E,
        icon = "🎄",
        description = "El espíritu cálido de diciembre, velitas en el refugio y villancicos con todo el amor para mi Blanquita.",
        coverPath = "covers/amando_tus_ojos.jpg",
        songs = listOf(
            Song(
                id = "navidena_amando_tus_ojos",
                title = "Amando tus Ojos",
                artist = "Para Principessa · Elvis",
                playlistId = "navidena",
                playlistName = "Música Navideña",
                assetPath = "playlists/navidena/amando-tus-ojos.mp3",
                coverPath = "covers/amando_tus_ojos.jpg",
                icon = "❄️",
                durationSec = 244,
            ),
            Song(
                id = "navidena_cuerpo_hermoso",
                title = "Cuerpo Hermoso",
                artist = "Para Principessa · Elvis",
                playlistId = "navidena",
                playlistName = "Música Navideña",
                assetPath = "playlists/navidena/cuerpo-hermoso.mp3",
                coverPath = "covers/cuerpo_hermoso.jpg",
                icon = "🕯️",
                durationSec = 88,
            ),
            Song(
                id = "navidena_preciosa_sonrisa",
                title = "Preciosa Sonrisa",
                artist = "Para Principessa · Elvis",
                playlistId = "navidena",
                playlistName = "Música Navideña",
                assetPath = "playlists/navidena/preciosa-sonrisa.mp3",
                coverPath = "covers/preciosa_sonrisa.jpg",
                icon = "⭐",
                durationSec = 248,
            ),
            Song(
                id = "navidena_universo_entero",
                title = "Universo Entero",
                artist = "Para Principessa · Elvis",
                playlistId = "navidena",
                playlistName = "Música Navideña",
                assetPath = "playlists/navidena/universo-entero.mp3",
                coverPath = "covers/universo_entero.jpg",
                icon = "🌌",
                durationSec = 88,
            ),
        ),
    )

    val playlistPasion = Playlist(
        id = "pasion",
        title = "Música Pasión",
        subtitle = "El calor y refugio de tus brazos",
        badge = "🔥 Pasión",
        colorHex = 0xFF9E2A2B,
        icon = "🔥",
        description = "Canciones íntimas, intensas y protectoras. Para recordarte que eres mi refugio y mi mundo entero.",
        coverPath = "covers/eres_mi_abrigo_y_refugio.jpg",
        songs = listOf(
            Song(
                id = "pasion_abrigo_refugio",
                title = "Eres mi Abrigo y Refugio",
                artist = "Para Principessa · Elvis",
                playlistId = "pasion",
                playlistName = "Música Pasión",
                assetPath = "playlists/pasion/eres-mi-abrigo-y-refugio.mp3",
                coverPath = "covers/eres_mi_abrigo_y_refugio.jpg",
                icon = "🔥",
                durationSec = 286,
            ),
            Song(
                id = "pasion_eres_mi_mundo",
                title = "Eres Mi Mundo",
                artist = "Para Principessa · Elvis",
                playlistId = "pasion",
                playlistName = "Música Pasión",
                assetPath = "playlists/pasion/eres-mi-mundo.mp3",
                coverPath = "covers/eres_mi_mundo.jpg",
                icon = "❤️‍🔥",
                durationSec = 306,
            ),
        ),
    )

    val playlists: List<Playlist> = listOf(
        playlistDulce,
        playlistAmor,
        playlistNavidena,
        playlistPasion,
    )

    val allSongs: List<Song> = playlists.flatMap { it.songs }

    fun findSong(id: String): Song? = allSongs.firstOrNull { it.id == id }

    fun findPlaylist(id: String): Playlist? = playlists.firstOrNull { it.id == id }

    fun randomSong(): Song = allSongs.random()
}
