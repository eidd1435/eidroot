package com.vector.autoinstaller.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class InstallVectorUseCase(
    private val repository: VectorInstallerRepository
) {
    suspend fun checkRootAccess(): Boolean = repository.hasRootAccess()

    operator fun invoke(selectedModules: Set<String>): Flow<InstallerProgress> = flow {
        emit(InstallerProgress.CheckingInternet)
        if (!repository.hasInternet()) {
            emit(InstallerProgress.Error(InstallerError.NoInternet))
            return@flow
        }

        emit(InstallerProgress.CheckingRoot)
        if (!repository.hasRootAccess()) {
            emit(InstallerProgress.Error(InstallerError.RootNotGranted))
            return@flow
        }
        emit(InstallerProgress.RootGranted)

        InstallerConstants.Modules.filter { it.displayName in selectedModules }.forEach { modulePackage ->
            emit(InstallerProgress.Downloading(modulePackage.displayName))
            if (!repository.downloadModule(modulePackage)) {
                emit(InstallerProgress.Error(InstallerError.DownloadFailed))
                return@flow
            }

            emit(InstallerProgress.Installing(modulePackage.displayName))
            if (!repository.installModule(modulePackage)) {
                emit(InstallerProgress.Error(InstallerError.InstallationFailed))
                return@flow
            }
        }

        emit(InstallerProgress.Success)
        emit(InstallerProgress.Rebooting)
        repository.rebootDevice()
    }
}
