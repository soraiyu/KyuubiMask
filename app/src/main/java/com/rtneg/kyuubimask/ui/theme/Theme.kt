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
package com.rtneg.kyuubimask.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Material 3 color scheme for KyuubiMask Compose UI.
 *
 * Key semantic mappings used by [MainToggleScreen]:
 * - Masking ON background  → primaryContainer  (#1A237E dark blue)
 * - Masking ON text        → onPrimaryContainer (#FFFFFF)
 * - Masking OFF background → background         (#121212, semantically the "default/idle" state)
 * - Masking OFF text       → onSurface          (#D0D0D0)
 * - Masking OFF hint       → onSurfaceVariant   (#909090)
 */
private val KyuubiDarkColorScheme = darkColorScheme(
    primary = Color(0xFFFF6B35),           // Fox orange
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF1A237E),   // Masking-on full-screen background
    onPrimaryContainer = Color(0xFFFFFFFF),
    background = Color(0xFF121212),
    onBackground = Color(0xFFFFFFFF),
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFD0D0D0),          // Masking-off text
    onSurfaceVariant = Color(0xFF909090),   // Masking-off hint
)

/**
 * KyuubiMask Compose theme wrapper.
 * Wraps content with the app's Material 3 [darkColorScheme].
 */
@Composable
fun KyuubiMaskTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = KyuubiDarkColorScheme,
        content = content,
    )
}
