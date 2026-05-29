package com.kodiremote.bridge.api

import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * High-level Kodi API methods for common operations
 */
@Singleton
class KodiApi @Inject constructor(
    private val jsonRpcClient: KodiJsonRpcClient
) {
    
    // ==================== Player Controls ====================
    
    /**
     * Play/pause toggle
     */
    suspend fun playPause(playerId: Int = -1): JsonObject? = withContext(Dispatchers.IO) {
        val params = if (playerId >= 0) {
            mapOf("playerid" to playerId)
        } else null
        jsonRpcClient.sendRequest("Player.PlayPause", params)
    }
    
    /**
     * Stop playback
     */
    suspend fun stop(playerId: Int = -1): JsonObject? = withContext(Dispatchers.IO) {
        val params = if (playerId >= 0) {
            mapOf("playerid" to playerId)
        } else null
        jsonRpcClient.sendRequest("Player.Stop", params)
    }
    
    /**
     * Seek to position
     */
    suspend fun seek(playerId: Int, position: SeekPosition): JsonObject? = withContext(Dispatchers.IO) {
        val params = mapOf(
            "playerid" to playerId,
            "value" to position.toJson()
        )
        jsonRpcClient.sendRequest("Player.Seek", params)
    }
    
    /**
     * Set playback speed
     */
    suspend fun setSpeed(playerId: Int, speed: Int): JsonObject? = withContext(Dispatchers.IO) {
        val params = mapOf(
            "playerid" to playerId,
            "speed" to speed
        )
        jsonRpcClient.sendRequest("Player.SetSpeed", params)
    }
    
    /**
     * Get active players
     */
    suspend fun getActivePlayers(): JsonObject? = withContext(Dispatchers.IO) {
        jsonRpcClient.sendRequest("Player.GetActivePlayers")
    }
    
    /**
     * Get player properties
     */
    suspend fun getPlayerProperties(
        playerId: Int,
        properties: List<String> = listOf("time", "totaltime", "percentage", "speed")
    ): JsonObject? = withContext(Dispatchers.IO) {
        val params = mapOf(
            "playerid" to playerId,
            "properties" to properties
        )
        jsonRpcClient.sendRequest("Player.GetProperties", params)
    }
    
    /**
     * Get currently playing item
     */
    suspend fun getItem(playerId: Int): JsonObject? = withContext(Dispatchers.IO) {
        val params = mapOf(
            "playerid" to playerId,
            "properties" to listOf("title", "showtitle", "season", "episode", "thumbnail")
        )
        jsonRpcClient.sendRequest("Player.GetItem", params)
    }
    
    // ==================== Volume Controls ====================
    
    /**
     * Set volume
     */
    suspend fun setVolume(volume: Int): JsonObject? = withContext(Dispatchers.IO) {
        val params = mapOf("volume" to volume)
        jsonRpcClient.sendRequest("Application.SetVolume", params)
    }
    
    /**
     * Volume up
     */
    suspend fun volumeUp(): JsonObject? = withContext(Dispatchers.IO) {
        jsonRpcClient.sendRequest("Application.SetVolume", mapOf("volume" to "increment"))
    }
    
    /**
     * Volume down
     */
    suspend fun volumeDown(): JsonObject? = withContext(Dispatchers.IO) {
        jsonRpcClient.sendRequest("Application.SetVolume", mapOf("volume" to "decrement"))
    }
    
    /**
     * Mute/unmute
     */
    suspend fun toggleMute(): JsonObject? = withContext(Dispatchers.IO) {
        jsonRpcClient.sendRequest("Application.SetMute", mapOf("mute" to "toggle"))
    }
    
    /**
     * Get volume
     */
    suspend fun getVolume(): JsonObject? = withContext(Dispatchers.IO) {
        jsonRpcClient.sendRequest("Application.GetProperties", mapOf("properties" to listOf("volume", "muted")))
    }
    
    // ==================== Navigation Controls ====================
    
    /**
     * Send input action
     */
    suspend fun sendInput(action: InputAction): JsonObject? = withContext(Dispatchers.IO) {
        jsonRpcClient.sendRequest("Input." + action.methodName)
    }
    
    /**
     * Send text input
     */
    suspend fun sendText(text: String, done: Boolean = true): JsonObject? = withContext(Dispatchers.IO) {
        val params = mapOf("text" to text, "done" to done)
        jsonRpcClient.sendRequest("Input.SendText", params)
    }
    
    /**
     * Execute built-in command
     */
    suspend fun executeBuiltin(command: String): JsonObject? = withContext(Dispatchers.IO) {
        jsonRpcClient.sendRequest("Input.ExecuteAction", mapOf("action" to command))
    }
    
    // ==================== System Controls ====================
    
    /**
     * Hibernate system
     */
    suspend fun hibernate(): JsonObject? = withContext(Dispatchers.IO) {
        jsonRpcClient.sendRequest("System.Hibernate")
    }
    
    /**
     * Suspend system
     */
    suspend fun suspend(): JsonObject? = withContext(Dispatchers.IO) {
        jsonRpcClient.sendRequest("System.Suspend")
    }
    
    /**
     * Reboot system
     */
    suspend fun reboot(): JsonObject? = withContext(Dispatchers.IO) {
        jsonRpcClient.sendRequest("System.Reboot")
    }
    
    /**
     * Shutdown system
     */
    suspend fun shutdown(): JsonObject? = withContext(Dispatchers.IO) {
        jsonRpcClient.sendRequest("System.Shutdown")
    }
    
    /**
     * Get system properties
     */
    suspend fun getSystemProperties(): JsonObject? = withContext(Dispatchers.IO) {
        jsonRpcClient.sendRequest(
            "System.GetProperties",
            mapOf("properties" to listOf("cansuspend", "canhibernate", "canreboot", "canshutdown"))
        )
    }
    
    // ==================== Library Controls ====================
    
    /**
     * Get video library
     */
    suspend fun getVideoLibrary(
        type: String = "movies",
        limits: Map<String, Int> = mapOf("start" to 0, "end" to 50)
    ): JsonObject? = withContext(Dispatchers.IO) {
        val params = mutableMapOf<String, Any>(
            "limits" to limits,
            "properties" to listOf("title", "year", "genre", "thumbnail", "playcount")
        )
        
        when (type) {
            "movies" -> params["sort"] = mapOf("method" to "title", "order" to "ascending")
            "tvshows" -> params["sort"] = mapOf("method" to "title", "order" to "ascending")
            "episodes" -> params["sort"] = mapOf("method" to "episode", "order" to "ascending")
        }
        
        jsonRpcClient.sendRequest("VideoLibrary.Get${type.replaceFirstChar { it.uppercase() }}", params)
    }
    
    /**
     * Clean video library
     */
    suspend fun cleanVideoLibrary(): JsonObject? = withContext(Dispatchers.IO) {
        jsonRpcClient.sendRequest("VideoLibrary.Clean")
    }
    
    /**
     * Scan video library
     */
    suspend fun scanVideoLibrary(): JsonObject? = withContext(Dispatchers.IO) {
        jsonRpcClient.sendRequest("VideoLibrary.Scan")
    }
    
    // ==================== Playlist Controls ====================
    
    /**
     * Get playlist
     */
    suspend fun getPlaylist(playlistId: Int): JsonObject? = withContext(Dispatchers.IO) {
        val params = mapOf(
            "playlistid" to playlistId,
            "properties" to listOf("title", "showtitle", "season", "episode", "thumbnail")
        )
        jsonRpcClient.sendRequest("Playlist.GetItems", params)
    }
    
    /**
     * Clear playlist
     */
    suspend fun clearPlaylist(playlistId: Int): JsonObject? = withContext(Dispatchers.IO) {
        jsonRpcClient.sendRequest("Playlist.Clear", mapOf("playlistid" to playlistId))
    }
    
    /**
     * Add to playlist
     */
    suspend fun addToPlaylist(playlistId: Int, itemId: Int): JsonObject? = withContext(Dispatchers.IO) {
        val params = mapOf(
            "playlistid" to playlistId,
            "item" to mapOf("episodeid" to itemId)
        )
        jsonRpcClient.sendRequest("Playlist.Add", params)
    }
    
    /**
     * Play playlist item
     */
    suspend fun playPlaylistItem(playlistId: Int, position: Int): JsonObject? = withContext(Dispatchers.IO) {
        val params = mapOf(
            "playlistid" to playlistId,
            "position" to position
        )
        jsonRpcClient.sendRequest("Player.Open", params)
    }
}

// ==================== Data Classes ====================

enum class SeekPosition {
    SMALL_FORWARD, SMALL_BACKWARD, BIG_FORWARD, BIG_BACKWARD;
    
    fun toJson(): String = when (this) {
        SMALL_FORWARD -> "smallforward"
        SMALL_BACKWARD -> "smallbackward"
        BIG_FORWARD -> "bigforward"
        BIG_BACKWARD -> "bigbackward"
    }
}

enum class InputAction(val methodName: String) {
    LEFT("Left"),
    RIGHT("Right"),
    UP("Up"),
    DOWN("Down"),
    SELECT("Select"),
    BACK("Back"),
    HOME("Home"),
    INFO("Info"),
    MENU("ContextMenu"),
    SHOW_OSD("ShowOSD"),
    FULLSCREEN("FullScreen")
}
