package com.vector.autoinstaller.data

import android.content.Context
import com.vector.autoinstaller.domain.ModulePackage
import com.vector.autoinstaller.domain.AppPackage
import com.vector.autoinstaller.domain.VectorInstallerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class VectorInstallerRepositoryImpl(
    context: Context
) : VectorInstallerRepository {
    private val connectivityChecker = AndroidConnectivityChecker(context)
    private val downloader = DownloadManagerVectorDownloader(context)
    private val rootShell = RootShell()

    override suspend fun hasInternet(): Boolean = withContext(Dispatchers.Default) {
        connectivityChecker.hasInternet()
    }

    override suspend fun hasRootAccess(): Boolean =
        rootShell.hasRootAccess()

    override suspend fun downloadModule(modulePackage: ModulePackage): Boolean =
        rootShell.deleteFile(modulePackage.publicPath) &&
            rootShell.deleteFile(modulePackage.installPath) &&
            downloader.download(modulePackage) &&
            rootShell.fileExists(modulePackage.publicPath) &&
            rootShell.copyFile(
                sourcePath = modulePackage.publicPath,
                destinationPath = modulePackage.installPath
            ) &&
            rootShell.fileExists(modulePackage.installPath)

    override suspend fun installModule(modulePackage: ModulePackage): Boolean =
        rootShell.installMagiskModule(modulePackage.installPath)

    override suspend fun downloadApp(appPackage: AppPackage): Boolean =
        rootShell.deleteFile(appPackage.publicDownloadPath) &&
            rootShell.deleteFile(appPackage.publicApkPath) &&
            rootShell.deleteFile(appPackage.installPath) &&
            downloader.downloadApp(appPackage) &&
            rootShell.fileExists(appPackage.publicApkPath) &&
            rootShell.copyFile(appPackage.publicApkPath, appPackage.installPath) &&
            rootShell.fileExists(appPackage.installPath)

    override suspend fun installApp(appPackage: AppPackage): Boolean =
        rootShell.installApk(appPackage.installPath)

    override suspend fun rebootDevice(): Boolean =
        rootShell.reboot()
}
