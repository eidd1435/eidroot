package com.vector.autoinstaller.presentation

data class InstallerUiState(
    val isRunning: Boolean = false,
    val statusText: String = "",
    val messageText: String = "",
    val rootStatus: RootUiStatus = RootUiStatus.Unknown,
    val selectedModules: Set<String> = com.vector.autoinstaller.domain.InstallerConstants.Modules.map { it.displayName }.toSet(),
    val selectedApps: Set<String> = com.vector.autoinstaller.domain.AppInstallerConstants.Apps.map { it.displayName }.toSet(),
    val section: InstallerSection = InstallerSection.Modules,
    val operation: InstallerOperation = InstallerOperation.Modules
)

enum class RootUiStatus { Unknown, Checking, Granted, Denied }
enum class InstallerSection { Modules, Apps }
enum class InstallerOperation { Modules, Apps }
