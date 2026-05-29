package com.kodiremote.bridge.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.kodiremote.bridge.api.KodiApi
import com.kodiremote.bridge.api.KodiJsonRpcClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dependency injection module for the application
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .setLenient()
            .create()
    }
    
    @Provides
    @Singleton
    fun provideKodiJsonRpcClient(gson: Gson): KodiJsonRpcClient {
        return KodiJsonRpcClient(gson)
    }
    
    @Provides
    @Singleton
    fun provideKodiApi(kodiJsonRpcClient: KodiJsonRpcClient): KodiApi {
        return KodiApi(kodiJsonRpcClient)
    }
}
