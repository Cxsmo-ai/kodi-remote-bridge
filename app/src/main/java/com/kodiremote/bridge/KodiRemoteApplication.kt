package com.kodiremote.bridge

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Main application class for Kodi Remote Bridge
 */
@HiltAndroidApp
class KodiRemoteApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        // Application initialization
    }
}
