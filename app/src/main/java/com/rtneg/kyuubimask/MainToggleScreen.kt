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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

// Colors used by MainToggleScreen are defined in res/values/colors.xml.

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
    val backgroundRes = if (isMaskingEnabled) R.color.masking_on_background else R.color.masking_off_background
    val textColorRes = if (isMaskingEnabled) R.color.masking_on_text else R.color.masking_off_text
    val hintColorRes = if (isMaskingEnabled) R.color.masking_on_hint else R.color.masking_off_hint
    val background = colorResource(backgroundRes)
    val textColor = colorResource(textColorRes)
    val hintColor = colorResource(hintColorRes)

    val statusText = if (isMaskingEnabled) {
        stringResource(R.string.toggle_status_masking_on)
    } else {
        stringResource(R.string.toggle_status_masking_off)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = statusText,
            style = MaterialTheme.typography.headlineMedium,
            color = textColor,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(32.dp))

        Switch(
            checked = isMaskingEnabled,
            onCheckedChange = onToggle,
            modifier = Modifier.scale(2.0f),
        )

        Spacer(modifier = Modifier.height(48.dp))

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
    MainToggleScreen(isMaskingEnabled = true, onToggle = {})
}

@Preview(showBackground = true, name = "Masking OFF")
@Composable
private fun MainToggleScreenOffPreview() {
    MainToggleScreen(isMaskingEnabled = false, onToggle = {})
}
