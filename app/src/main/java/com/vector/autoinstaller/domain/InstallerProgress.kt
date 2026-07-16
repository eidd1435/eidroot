package com.vector.autoinstaller.domain

sealed interface InstallerProgress {
    data object Idle : InstallerProgress
    data object CheckingInternet : InstallerProgress
    data object CheckingRoot : InstallerProgress
    data object RootGranted : InstallerProgress
    data class Downloading(val moduleName: String) : InstallerProgress
    data class Installing(val moduleName: String) : InstallerProgress
    data object Rebooting : InstallerProgress
    data object Success : InstallerProgress
    data class Error(val error: InstallerError) : InstallerProgress
}
