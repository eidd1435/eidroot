package com.vector.autoinstaller.presentation

data class InstallerUiState(
    val isRunning: Boolean = false,
    val statusText: String = "",
    val messageText: String = "",
    val rootStatus: RootUiStatus = RootUiStatus.Unknown,
    val selectedModules: Set<String> = com.vector.autoinstaller.domain.InstallerConstants.Modules.map { it.displayName }.toSet()
)

enum class RootUiStatus { Unknown, Checking, Granted, Denied }
