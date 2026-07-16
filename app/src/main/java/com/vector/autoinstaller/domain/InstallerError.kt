package com.vector.autoinstaller.domain

sealed class InstallerError(message: String) : Exception(message) {
    data object NoInternet : InstallerError("No internet.")
    data object RootNotGranted : InstallerError("Root not granted.")
    data object DownloadFailed : InstallerError("Download failed.")
    data object InstallationFailed : InstallerError("Installation failed.")
}
