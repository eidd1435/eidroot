package com.vector.autoinstaller.domain

data class AppPackage(
    val displayName: String,
    val fallbackUrl: String,
    val githubRepository: String,
    val assetNameContains: String,
    val excludedAssetText: String? = null,
    val downloadFileName: String,
    val apkFileName: String,
    val archiveApkEntry: String? = null,
    val browserPageUrl: String? = null
) {
    val publicDownloadPath = "/sdcard/Download/$downloadFileName"
    val publicApkPath = "/sdcard/Download/$apkFileName"
    val installPath = "/data/local/tmp/$apkFileName"
}
