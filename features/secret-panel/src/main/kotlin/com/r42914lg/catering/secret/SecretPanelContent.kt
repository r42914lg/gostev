package com.r42914lg.catering.secret

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.r42914lg.catering.remoteconfig.RemoteConfigDebugInfo
import com.r42914lg.catering.remoteconfig.RemoteConfigKey
import org.koin.androidx.compose.koinViewModel

private val Ink = Color(0xFF171512)
private val Pine = Color(0xFF2E4B43)
private val Brick = Color(0xFFB23A2E)
private val Hairline = Color(0xFFE5E1D8)
private val Muted = Color(0xFF8A857C)

@Composable
fun SecretPanel(
    viewModel: SecretPanelViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Debug Panel",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Ink,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        state.configs.forEach { info ->
            ConfigItem(
                info = info,
                onUpdate = { newValue -> viewModel.updateOverride(info.configKey, newValue) },
                onClear = { viewModel.clearOverride(info.configKey) }
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Hairline)
        }
    }
}

@Composable
private fun ConfigItem(
    info: RemoteConfigDebugInfo,
    onUpdate: (String) -> Unit,
    onClear: () -> Unit
) {
    var textValue by remember(info.currentValue) { mutableStateOf(info.currentValue) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = info.configKey.key,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = Muted
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        OutlinedTextField(
            value = textValue,
            onValueChange = { textValue = it },
            modifier = Modifier.fillMaxWidth(),
            textStyle = MaterialTheme.typography.bodySmall,
            label = { Text("Value", fontSize = 10.sp) },
            trailingIcon = {
                if (info.isOverridden) {
                    IconButton(onClick = onClear) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear override",
                            tint = Brick,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(
                onClick = { onUpdate(textValue) },
                enabled = textValue != info.currentValue
            ) {
                Text("Apply Override", fontSize = 12.sp, color = Pine)
            }
        }
    }
}
