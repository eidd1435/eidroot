package com.vector.autoinstaller.domain

object AppInstallerConstants {
    val Apps = listOf(
        AppPackage(
            displayName = "KernelSU Manager",
            fallbackUrl = "https://github.com/tiann/KernelSU/releases/download/v3.2.5/KernelSU_v3.2.5_32525-release.apk",
            githubRepository = "tiann/KernelSU",
            assetNameContains = "KernelSU_",
            excludedAssetText = "debug",
            downloadFileName = "KernelSU.apk",
            apkFileName = "KernelSU.apk"
        ),
        AppPackage(
            displayName = "GpsSetter",
            fallbackUrl = "https://github.com/Android1500/GpsSetter/releases/download/v1.2.10/app-release.apk",
            githubRepository = "Android1500/GpsSetter",
            assetNameContains = "app-release.apk",
            downloadFileName = "GpsSetter.apk",
            apkFileName = "GpsSetter.apk"
        ),
        AppPackage(
            displayName = "Magisk",
            fallbackUrl = "https://github.com/topjohnwu/Magisk/releases/download/v30.7/Magisk-v30.7.apk",
            githubRepository = "topjohnwu/Magisk",
            assetNameContains = "Magisk-v",
            excludedAssetText = "debug",
            downloadFileName = "Magisk.apk",
            apkFileName = "Magisk.apk"
        ),
        AppPackage(
            displayName = "HMA-OSS Manager",
            fallbackUrl = "https://github.com/frknkrc44/HMA-OSS/releases/download/oss-164/HMA-OSS-ZYGISK-oss-164-release.zip",
            githubRepository = "frknkrc44/HMA-OSS",
            assetNameContains = "HMA-OSS-ZYGISK-",
            excludedAssetText = "debug",
            downloadFileName = "HMA-OSS-Zygisk.zip",
            apkFileName = "HMA-OSS-Manager.apk",
            archiveApkEntry = "manager.apk"
        )
    )
}
