package com.example.globaltranslation.di

import android.content.Context
import com.example.globaltranslation.data.network.NetworkMonitor
import com.example.globaltranslation.data.network.NetworkState
import com.example.globaltranslation.data.preferences.AppPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Singleton

/**
 * Test module that provides fake implementations of AppPreferences and NetworkMonitor.
 * These override the default @Inject constructor implementations to use in-memory state.
 */
@Module
@InstallIn(SingletonComponent::class)
object TestPreferencesModule {
    
    /**
     * Provides a fake AppPreferences that uses in-memory state instead of DataStore.
     * This prevents test state from persisting across test runs.
     */
    @Provides
    @Singleton
    fun provideAppPreferences(
        @ApplicationContext context: Context
    ): AppPreferences {
        // Create anonymous subclass that overrides with in-memory state
        return object : AppPreferences(context) {
            private val _lastVersionCode = MutableStateFlow(0)
            private val _allowCellularDownloads = MutableStateFlow(false)
            private val _isFirstLaunch = MutableStateFlow(true)
            
            override val lastVersionCode: Flow<Int> get() = _lastVersionCode.asStateFlow()
            override val allowCellularDownloads: Flow<Boolean> get() = _allowCellularDownloads.asStateFlow()
            override val isFirstLaunch: Flow<Boolean> get() = _isFirstLaunch.asStateFlow()
            
            override suspend fun updateVersionCode(versionCode: Int) {
                _lastVersionCode.value = versionCode
            }
            
            override suspend fun setAllowCellularDownloads(allow: Boolean) {
                _allowCellularDownloads.value = allow
            }
            
            override suspend fun markLaunched() {
                _isFirstLaunch.value = false
            }
            
            override suspend fun hasAppBeenUpdated(currentVersionCode: Int): Boolean {
                return currentVersionCode > _lastVersionCode.value
            }
            
            override suspend fun clearAll() {
                _lastVersionCode.value = 0
                _allowCellularDownloads.value = false
                _isFirstLaunch.value = true
            }
        }
    }
    
    /**
     * Provides a fake NetworkMonitor that uses in-memory state for testing.
     * This prevents tests from depending on actual device network connectivity.
     */
    @Provides
    @Singleton
    fun provideNetworkMonitor(
        @ApplicationContext context: Context
    ): NetworkMonitor {
        // Create anonymous subclass that returns fake network state
        return object : NetworkMonitor(context) {
            private val _networkState = MutableStateFlow<NetworkState>(NetworkState.WiFi)
            
            override val networkState: Flow<NetworkState> get() = _networkState.asStateFlow()
            
            override fun getCurrentNetworkState(): NetworkState = _networkState.value
            
            override fun isOnWiFi(): Boolean = _networkState.value == NetworkState.WiFi
            
            override fun isConnected(): Boolean = _networkState.value != NetworkState.Disconnected
        }
    }
}
