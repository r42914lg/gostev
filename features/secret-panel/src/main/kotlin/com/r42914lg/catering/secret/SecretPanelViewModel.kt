package com.r42914lg.catering.secret

import androidx.lifecycle.ViewModel
import com.r42914lg.catering.remoteconfig.RemoteConfig
import com.r42914lg.catering.remoteconfig.RemoteConfigDebugInfo
import com.r42914lg.catering.remoteconfig.RemoteConfigKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class SecretPanelState(
    val configs: List<RemoteConfigDebugInfo> = emptyList()
)

class SecretPanelViewModel(
    private val remoteConfig: RemoteConfig
) : ViewModel() {

    private val _state = MutableStateFlow(SecretPanelState())
    val state: StateFlow<SecretPanelState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        _state.update { it.copy(configs = remoteConfig.getDebugInfo()) }
    }

    fun updateOverride(configKey: RemoteConfigKey, value: String) {
        remoteConfig.setDebugOverride(configKey, value)
        refresh()
    }

    fun clearOverride(configKey: RemoteConfigKey) {
        remoteConfig.clearDebugOverride(configKey)
        refresh()
    }
}
