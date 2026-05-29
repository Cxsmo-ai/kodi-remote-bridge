package com.kodiremote.bridge.data

/**
 * Data class representing a Kodi connection
 */
data class KodiConnection(
    val id: String,
    val name: String,
    val host: String,
    val port: Int = 9090,
    val username: String? = null,
    val password: String? = null,
    val isConnected: Boolean = false,
    val lastConnected: Long = 0L
) {
    fun getWebSocketUrl(): String = "ws://$host:$port/jsonrpc"
    fun getHttpUrl(): String = "http://$host:$port/jsonrpc"
    
    companion object {
        fun createDefault(name: String, host: String): KodiConnection {
            return KodiConnection(
                id = java.util.UUID.randomUUID().toString(),
                name = name,
                host = host,
                port = 9090
            )
        }
    }
}
