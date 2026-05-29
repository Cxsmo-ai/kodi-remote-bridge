package com.kodiremote.bridge.api

import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Kodi JSON-RPC client for communicating with Kodi instances
 * Supports both WebSocket and HTTP protocols
 */
@Singleton
class KodiJsonRpcClient @Inject constructor(
    private val gson: Gson
) {
    private var webSocketClient: WebSocketClient? = null
    private var isConnected = false
    private val requestCallbacks = mutableMapOf<String, (JsonObject) -> Unit>()
    
    /**
     * Connect to Kodi instance via WebSocket
     */
    suspend fun connect(host: String, port: Int = 9090): Boolean = withContext(Dispatchers.IO) {
        try {
            val uri = URI("ws://$host:$port/jsonrpc")
            
            webSocketClient = object : WebSocketClient(uri) {
                override fun onOpen(handshake: ServerHandshake?) {
                    isConnected = true
                }
                
                override fun onMessage(message: String?) {
                    message?.let { handleResponse(it) }
                }
                
                override fun onClose(code: Int, reason: String?, remote: Boolean) {
                    isConnected = false
                    requestCallbacks.clear()
                }
                
                override fun onError(ex: Exception?) {
                    isConnected = false
                }
            }
            
            webSocketClient?.connect()
            true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Disconnect from Kodi instance
     */
    fun disconnect() {
        webSocketClient?.close()
        webSocketClient = null
        isConnected = false
        requestCallbacks.clear()
    }
    
    /**
     * Send JSON-RPC request to Kodi
     */
    suspend fun sendRequest(
        method: String,
        params: Any? = null,
        callback: ((JsonObject) -> Unit)? = null
    ): JsonObject? = withContext(Dispatchers.IO) {
        val requestId = UUID.randomUUID().toString()
        val request = JsonObject().apply {
            addProperty("jsonrpc", "2.0")
            addProperty("method", method)
            addProperty("id", requestId)
            if (params != null) {
                add("params", gson.toJsonTree(params))
            }
        }
        
        callback?.let { requestCallbacks[requestId] = it }
        
        return@withContext try {
            webSocketClient?.send(request.toString())
            null // Response will come via callback
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Handle incoming JSON-RPC response
     */
    private fun handleResponse(message: String) {
        try {
            val response = gson.fromJson(message, JsonObject::class.java)
            val requestId = response.get("id")?.asString
            
            requestId?.let {
                requestCallbacks[it]?.invoke(response)
                requestCallbacks.remove(it)
            }
        } catch (e: Exception) {
            // Handle notification messages (no id)
            try {
                val notification = gson.fromJson(message, JsonObject::class.java)
                val method = notification.get("method")?.asString
                method?.let { handleNotification(method, notification) }
            } catch (ex: Exception) {
                // Ignore parsing errors
            }
        }
    }
    
    /**
     * Handle Kodi notifications
     */
    private fun handleNotification(method: String, data: JsonObject) {
        // Handle various Kodi notifications
        when (method) {
            "Player.OnPlay" -> onPlayerPlay?.invoke(data)
            "Player.OnPause" -> onPlayerPause?.invoke(data)
            "Player.OnStop" -> onPlayerStop?.invoke(data)
            "Application.OnVolumeChanged" -> onVolumeChanged?.invoke(data)
            "Input.OnInputRequested" -> onInputRequested?.invoke(data)
        }
    }
    
    // Notification callbacks
    var onPlayerPlay: ((JsonObject) -> Unit)? = null
    var onPlayerPause: ((JsonObject) -> Unit)? = null
    var onPlayerStop: ((JsonObject) -> Unit)? = null
    var onVolumeChanged: ((JsonObject) -> Unit)? = null
    var onInputRequested: ((JsonObject) -> Unit)? = null
    
    fun isConnected(): Boolean = isConnected
}
