package com.vector.autoinstaller.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class InstallAppsUseCase(
    private val repository: VectorInstallerRepository
) {
    operator fun invoke(selectedApps: Set<String>): Flow<InstallerProgress> = flow {
        emit(InstallerProgress.CheckingInternet)
        if (!repository.hasInternet()) {
            emit(InstallerProgress.Error(InstallerError.NoInternet))
            return@flow
        }

        AppInstallerConstants.Apps.filter { it.displayName in selectedApps }.forEach { app ->
            emit(InstallerProgress.Downloading(app.displayName))
            if (!repository.downloadApp(app)) {
                emit(InstallerProgress.Error(InstallerError.DownloadFailed))
                return@flow
            }
        }
        emit(InstallerProgress.Success)
    }
}
