package com.r42914lg.catering.secret

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.r42914lg.catering.designsys.CateringTheme
import com.r42914lg.catering.remoteconfig.RemoteConfigDebugInfo
import org.koin.androidx.compose.koinViewModel

@Composable
fun SecretPanel(
    viewModel: SecretPanelViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(CateringTheme.spacing.l)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = stringResource(R.string.secret_title),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = CateringTheme.colors.onBackground,
            modifier = Modifier.padding(bottom = CateringTheme.spacing.l)
        )

        state.configs.forEach { info ->
            ConfigItem(
                info = info,
                onUpdate = { newValue -> viewModel.updateOverride(info.configKey, newValue) },
                onClear = { viewModel.clearOverride(info.configKey) }
            )
            HorizontalDivider(
                modifier = Modifier.padding(vertical = CateringTheme.spacing.m),
                color = CateringTheme.colors.divider
            )
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
            color = CateringTheme.colors.muted
        )
        
        Spacer(modifier = Modifier.height(CateringTheme.spacing.xs))
        
        OutlinedTextField(
            value = textValue,
            onValueChange = { textValue = it },
            modifier = Modifier.fillMaxWidth(),
            textStyle = MaterialTheme.typography.bodySmall,
            label = { Text(stringResource(R.string.secret_label_value), fontSize = 10.sp) },
            trailingIcon = {
                if (info.isOverridden) {
                    IconButton(onClick = onClear) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.secret_desc_clear),
                            tint = CateringTheme.colors.error,
                            modifier = Modifier.size(CateringTheme.spacing.l)
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
                Text(
                    text = stringResource(R.string.secret_apply),
                    fontSize = 12.sp,
                    color = CateringTheme.colors.brand
                )
            }
        }
    }
}
