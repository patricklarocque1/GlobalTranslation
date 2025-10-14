package com.example.globaltranslation.testing.fakes

import com.example.globaltranslation.data.network.NetworkState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeNetworkMonitor {
    private val _networkState = MutableStateFlow<NetworkState>(NetworkState.WiFi)
    
    val networkState: Flow<NetworkState> = _networkState.asStateFlow()
    
    fun setNetworkState(state: NetworkState) {
        _networkState.value = state
    }
    
    fun setWiFiConnected() {
        _networkState.value = NetworkState.WiFi
    }
    
    fun setCellularConnected() {
        _networkState.value = NetworkState.Cellular
    }
    
    fun setDisconnected() {
        _networkState.value = NetworkState.Disconnected
    }
}