package com.example.globaltranslation.testing.fakes

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeAppPreferences {
    private val _lastVersionCode = MutableStateFlow(0)
    private val _allowCellularDownloads = MutableStateFlow(false)
    private val _isFirstLaunch = MutableStateFlow(true)
    
    val lastVersionCode: Flow<Int> = _lastVersionCode.asStateFlow()
    val allowCellularDownloads: Flow<Boolean> = _allowCellularDownloads.asStateFlow()
    val isFirstLaunch: Flow<Boolean> = _isFirstLaunch.asStateFlow()
    
    suspend fun updateVersionCode(versionCode: Int) {
        _lastVersionCode.value = versionCode
    }
    
    suspend fun setAllowCellularDownloads(allow: Boolean) {
        _allowCellularDownloads.value = allow
    }
    
    suspend fun markLaunched() {
        _isFirstLaunch.value = false
    }
    
    suspend fun hasAppBeenUpdated(currentVersionCode: Int): Boolean {
        return currentVersionCode > _lastVersionCode.value
    }
    
    suspend fun clearAll() {
        _lastVersionCode.value = 0
        _allowCellularDownloads.value = false
        _isFirstLaunch.value = true
    }
}