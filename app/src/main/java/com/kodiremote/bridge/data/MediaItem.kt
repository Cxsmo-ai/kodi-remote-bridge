package com.kodiremote.bridge.data

/**
 * Data class representing a media item (movie, TV show, episode, etc.)
 */
data class MediaItem(
    val id: Int,
    val type: MediaType,
    val title: String,
    val showTitle: String? = null,
    val season: Int? = null,
    val episode: Int? = null,
    val year: Int? = null,
    val genre: List<String>? = null,
    val thumbnail: String? = null,
    val fanart: String? = null,
    val plot: String? = null,
    val rating: Float? = null,
    val duration: Int? = null, // in seconds
    val playCount: Int = 0
) {
    val displayTitle: String
        get() = when (type) {
            MediaType.MOVIE -> title
            MediaType.TVSHOW -> title
            MediaType.EPISODE -> {
                if (season != null && episode != null) {
                    "S${season.toString().padStart(2, '0')}E${episode.toString().padStart(2, '0')} - $title"
                } else {
                    title
                }
            }
            MediaType.MUSIC -> title
            MediaType.UNKNOWN -> title
        }
    
    val subtitle: String?
        get() = when (type) {
            MediaType.MOVIE -> year?.toString()
            MediaType.TVSHOW -> genre?.firstOrNull()
            MediaType.EPISODE -> showTitle
            MediaType.MUSIC -> genre?.firstOrNull()
            MediaType.UNKNOWN -> null
        }
}

enum class MediaType {
    MOVIE, TVSHOW, EPISODE, MUSIC, UNKNOWN
}
