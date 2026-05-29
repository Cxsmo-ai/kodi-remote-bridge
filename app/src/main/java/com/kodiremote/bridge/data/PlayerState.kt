package com.kodiremote.bridge.data

/**
 * Data class representing the current player state
 */
data class PlayerState(
    val playerId: Int = -1,
    val isPlaying: Boolean = false,
    val isPaused: Boolean = false,
    val speed: Int = 1,
    val currentTime: PlaybackTime = PlaybackTime(),
    val totalTime: PlaybackTime = PlaybackTime(),
    val percentage: Float = 0f,
    val currentMedia: MediaItem? = null,
    val volume: Int = 100,
    val isMuted: Boolean = false
) {
    val isPlayingOrPaused: Boolean
        get() = isPlaying || isPaused
    
    val formattedCurrentTime: String
        get() = currentTime.format()
    
    val formattedTotalTime: String
        get() = totalTime.format()
    
    val progressPercentage: Int
        get() = percentage.toInt()
}

data class PlaybackTime(
    val hours: Int = 0,
    val minutes: Int = 0,
    val seconds: Int = 0,
    val milliseconds: Int = 0
) {
    fun format(): String {
        return if (hours > 0) {
            String.format("%02d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%02d:%02d", minutes, seconds)
        }
    }
    
    fun toSeconds(): Int {
        return hours * 3600 + minutes * 60 + seconds
    }
    
    companion object {
        fun fromSeconds(seconds: Int): PlaybackTime {
            val hours = seconds / 3600
            val remaining = seconds % 3600
            val minutes = remaining / 60
            val secs = remaining % 60
            return PlaybackTime(hours, minutes, secs, 0)
        }
        
        fun fromJson(json: com.google.gson.JsonObject): PlaybackTime {
            return PlaybackTime(
                hours = json.get("hours")?.asInt ?: 0,
                minutes = json.get("minutes")?.asInt ?: 0,
                seconds = json.get("seconds")?.asInt ?: 0,
                milliseconds = json.get("milliseconds")?.asInt ?: 0
            )
        }
    }
}
