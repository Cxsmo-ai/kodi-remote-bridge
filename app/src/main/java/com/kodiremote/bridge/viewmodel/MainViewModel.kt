package com.kodiremote.bridge.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kodiremote.bridge.api.InputAction
import com.kodiremote.bridge.api.KodiApi
import com.kodiremote.bridge.api.KodiJsonRpcClient
import com.kodiremote.bridge.api.SeekPosition
import com.kodiremote.bridge.data.KodiConnection
import com.kodiremote.bridge.data.PlayerState
import com.kodiremote.bridge.data.PlaybackTime
import com.google.gson.JsonObject
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Main ViewModel for Kodi Remote Bridge
 */
class MainViewModel @Inject constructor(
    val kodiApi: KodiApi,
    private val jsonRpcClient: KodiJsonRpcClient
) : ViewModel() {
    
    private val _connectionState = MutableLiveData<Boolean>()
    val connectionState: LiveData<Boolean> = _connectionState
    
    private val _playerState = MutableLiveData<PlayerState>()
    val playerState: LiveData<PlayerState> = _playerState
    
    private val _volume = MutableLiveData<Int>()
    val volume: LiveData<Int> = _volume
    
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private var currentConnection: KodiConnection? = null
    private var currentPlayerId: Int = -1
    
    init {
        // Load saved connection
        loadSavedConnection()
    }
    
    fun connect(connection: KodiConnection) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val success = jsonRpcClient.connect(connection.host, connection.port)
                if (success) {
                    currentConnection = connection.copy(isConnected = true)
                    _connectionState.value = true
                    setupNotifications()
                    refreshState()
                } else {
                    _error.value = "Failed to connect to Kodi"
                }
            } catch (e: Exception) {
                _error.value = "Connection error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun disconnect() {
        jsonRpcClient.disconnect()
        currentConnection = currentConnection?.copy(isConnected = false)
        _connectionState.value = false
    }
    
    private fun setupNotifications() {
        jsonRpcClient.onPlayerPlay = { refreshPlayerState() }
        jsonRpcClient.onPlayerPause = { refreshPlayerState() }
        jsonRpcClient.onPlayerStop = { refreshPlayerState() }
        jsonRpcClient.onVolumeChanged = { refreshVolume() }
    }
    
    fun refreshState() {
        refreshPlayerState()
        refreshVolume()
    }
    
    private fun refreshPlayerState() {
        viewModelScope.launch {
            try {
                val activePlayers = kodiApi.getActivePlayers()
                if (activePlayers != null && activePlayers.has("result")) {
                    val players = activePlayers.getAsJsonArray("result")
                    if (players.size() > 0) {
                        val player = players.get(0).asJsonObject
                        currentPlayerId = player.get("playerid").asInt
                        
                        val properties = kodiApi.getPlayerProperties(currentPlayerId)
                        val item = kodiApi.getItem(currentPlayerId)
                        
                        val state = parsePlayerState(properties, item)
                        _playerState.value = state
                    } else {
                        _playerState.value = PlayerState()
                    }
                } else {
                    _playerState.value = PlayerState()
                }
            } catch (e: Exception) {
                _error.value = "Failed to refresh player state"
            }
        }
    }
    
    private fun parsePlayerState(properties: JsonObject?, item: JsonObject?): PlayerState {
        val result = properties?.getAsJsonObject("result")
        val itemResult = item?.getAsJsonObject("result")
        
        val isPlaying = result?.get("speed")?.asInt != 0
        val isPaused = result?.get("speed")?.asInt == 0
        
        val timeJson = result?.getAsJsonObject("time")
        val totalTimeJson = result?.getAsJsonObject("totaltime")
        
        val currentTime = if (timeJson != null) PlaybackTime.fromJson(timeJson) else PlaybackTime()
        val totalTime = if (totalTimeJson != null) PlaybackTime.fromJson(totalTimeJson) else PlaybackTime()
        
        val percentage = result?.get("percentage")?.asFloat ?: 0f
        val speed = result?.get("speed")?.asInt ?: 1
        
        val mediaItem = parseMediaItem(itemResult)
        
        return PlayerState(
            playerId = currentPlayerId,
            isPlaying = isPlaying,
            isPaused = isPaused,
            speed = speed,
            currentTime = currentTime,
            totalTime = totalTime,
            percentage = percentage,
            currentMedia = mediaItem
        )
    }
    
    private fun parseMediaItem(itemJson: JsonObject?): com.kodiremote.bridge.data.MediaItem? {
        val item = itemJson?.getAsJsonObject("item") ?: return null
        
        val type = when (item.get("type")?.asString) {
            "movie" -> com.kodiremote.bridge.data.MediaType.MOVIE
            "episode" -> com.kodiremote.bridge.data.MediaType.EPISODE
            "tvshow" -> com.kodiremote.bridge.data.MediaType.TVSHOW
            "song" -> com.kodiremote.bridge.data.MediaType.MUSIC
            else -> com.kodiremote.bridge.data.MediaType.UNKNOWN
        }
        
        return com.kodiremote.bridge.data.MediaItem(
            id = item.get("id")?.asInt ?: 0,
            type = type,
            title = item.get("title")?.asString ?: "",
            showTitle = item.get("showtitle")?.asString,
            season = item.get("season")?.asInt,
            episode = item.get("episode")?.asInt,
            year = item.get("year")?.asInt,
            genre = item.get("genre")?.asJsonArray?.map { it.asString },
            thumbnail = item.get("thumbnail")?.asString,
            plot = item.get("plot")?.asString,
            rating = item.get("rating")?.asFloat
        )
    }
    
    private fun refreshVolume() {
        viewModelScope.launch {
            try {
                val volumeData = kodiApi.getVolume()
                val result = volumeData?.getAsJsonObject("result")
                if (result != null) {
                    val vol = result.get("volume")?.asInt ?: 100
                    val muted = result.get("muted")?.asBoolean ?: false
                    _volume.value = vol
                    
                    val currentState = _playerState.value ?: PlayerState()
                    _playerState.value = currentState.copy(
                        volume = vol,
                        isMuted = muted
                    )
                }
            } catch (e: Exception) {
                _error.value = "Failed to refresh volume"
            }
        }
    }
    
    // ==================== Player Controls ====================
    
    fun playPause() {
        viewModelScope.launch {
            try {
                kodiApi.playPause(currentPlayerId)
                refreshPlayerState()
            } catch (e: Exception) {
                _error.value = "Failed to play/pause"
            }
        }
    }
    
    fun stop() {
        viewModelScope.launch {
            try {
                kodiApi.stop(currentPlayerId)
                refreshPlayerState()
            } catch (e: Exception) {
                _error.value = "Failed to stop"
            }
        }
    }
    
    fun seek(position: SeekPosition) {
        viewModelScope.launch {
            try {
                kodiApi.seek(currentPlayerId, position)
                refreshPlayerState()
            } catch (e: Exception) {
                _error.value = "Failed to seek"
            }
        }
    }
    
    // ==================== Volume Controls ====================
    
    fun setVolume(volume: Int) {
        viewModelScope.launch {
            try {
                kodiApi.setVolume(volume)
                refreshVolume()
            } catch (e: Exception) {
                _error.value = "Failed to set volume"
            }
        }
    }
    
    fun toggleMute() {
        viewModelScope.launch {
            try {
                kodiApi.toggleMute()
                refreshVolume()
            } catch (e: Exception) {
                _error.value = "Failed to toggle mute"
            }
        }
    }
    
    // ==================== Input Controls ====================
    
    fun sendInput(action: InputAction) {
        viewModelScope.launch {
            try {
                kodiApi.sendInput(action)
            } catch (e: Exception) {
                _error.value = "Failed to send input"
            }
        }
    }
    
    // ==================== Library Controls ====================
    
    fun browseMovies() {
        viewModelScope.launch {
            try {
                kodiApi.getVideoLibrary("movies")
            } catch (e: Exception) {
                _error.value = "Failed to browse movies"
            }
        }
    }
    
    fun browseTvShows() {
        viewModelScope.launch {
            try {
                kodiApi.getVideoLibrary("tvshows")
            } catch (e: Exception) {
                _error.value = "Failed to browse TV shows"
            }
        }
    }
    
    // ==================== System Controls ====================
    
    fun showPowerMenu() {
        // Show power menu dialog
    }
    
    fun shutdown() {
        viewModelScope.launch {
            try {
                kodiApi.shutdown()
            } catch (e: Exception) {
                _error.value = "Failed to shutdown"
            }
        }
    }
    
    // ==================== Connection Management ====================
    
    private fun loadSavedConnection() {
        // Load saved connection from preferences
        // This would be implemented with SharedPreferences or DataStore
    }
    
    fun saveConnection(connection: KodiConnection) {
        // Save connection to preferences
        currentConnection = connection
    }
}
