/*
 * Copyright 2026 KyuubiMask Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.rtneg.kyuubimask

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rtneg.kyuubimask.ui.theme.KyuubiMaskTheme

private const val SWITCH_SCALE = 1.5f
private val SWITCH_SPACER_HEIGHT = 24.dp
private val HINT_SPACER_HEIGHT = 32.dp

/**
 * Full-screen toggle composable for enabling / disabling notification masking.
 *
 * @param isMaskingEnabled Current masking state read from [PreferencesRepository].
 * @param onToggle         Callback invoked with the new Boolean value when the switch changes.
 */
@Composable
fun MainToggleScreen(
    isMaskingEnabled: Boolean,
    onToggle: (Boolean) -> Unit,
) {
    val background = if (isMaskingEnabled) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.background
    val textColor = if (isMaskingEnabled) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
    // Use onPrimaryContainer at reduced opacity for a subtle hint on the dark-blue container;
    // this approximates the visual intent of the previous distinct #B0BEC5 hint color.
    val hintColor = if (isMaskingEnabled) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant
    val statusText = stringResource(if (isMaskingEnabled) R.string.toggle_status_masking_on else R.string.toggle_status_masking_off)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = statusText,
            style = MaterialTheme.typography.titleMedium,
            color = textColor,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(SWITCH_SPACER_HEIGHT))

        Switch(
            checked = isMaskingEnabled,
            onCheckedChange = onToggle,
            modifier = Modifier.scale(SWITCH_SCALE),
        )

        Spacer(modifier = Modifier.height(HINT_SPACER_HEIGHT))

        Text(
            text = stringResource(R.string.toggle_hint),
            style = MaterialTheme.typography.bodySmall,
            color = hintColor,
        )
    }
}

@Preview(showBackground = true, name = "Masking ON")
@Composable
private fun MainToggleScreenOnPreview() {
    KyuubiMaskTheme { MainToggleScreen(isMaskingEnabled = true, onToggle = {}) }
}

@Preview(showBackground = true, name = "Masking OFF")
@Composable
private fun MainToggleScreenOffPreview() {
    KyuubiMaskTheme { MainToggleScreen(isMaskingEnabled = false, onToggle = {}) }
}
