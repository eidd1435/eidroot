package com.vector.autoinstaller.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = VectorBlue,
    secondary = VectorIndigo,
    background = VectorBackground,
    surface = VectorSurface,
    onPrimary = VectorSurface,
    onSecondary = VectorSurface,
    onBackground = VectorText,
    onSurface = VectorText
)

@Composable
fun VectorAutoInstallerTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        content = content
    )
}
