package com.vector.autoinstaller.domain

interface VectorInstallerRepository {
    suspend fun hasInternet(): Boolean
    suspend fun hasRootAccess(): Boolean
    suspend fun downloadModule(modulePackage: ModulePackage): Boolean
    suspend fun installModule(modulePackage: ModulePackage): Boolean
    suspend fun downloadApp(appPackage: AppPackage): Boolean
    suspend fun installApp(appPackage: AppPackage): Boolean
    suspend fun rebootDevice(): Boolean
}
