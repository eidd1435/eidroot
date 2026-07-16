package com.vector.autoinstaller.domain

object InstallerConstants {
    val Modules = listOf(
        ModulePackage(
            displayName = "ReZygisk",
            downloadUrl = "https://github.com/PerformanC/ReZygisk/releases/download/v1.0.0/ReZygisk-v1.0.0-release.zip",
            githubRepository = "PerformanC/ReZygisk",
            releaseAssetPrefix = "ReZygisk-",
            fileName = "ReZygisk.zip",
            publicPath = "/sdcard/Download/ReZygisk.zip",
            installPath = "/data/local/tmp/ReZygisk.zip"
        ),
        ModulePackage(
            displayName = "Vector",
            downloadUrl = "https://github.com/JingMatrix/Vector/releases/download/v2.0/Vector-v2.0-3021-Release.zip",
            githubRepository = "JingMatrix/Vector",
            releaseAssetPrefix = "Vector-",
            fileName = "Vector.zip",
            publicPath = "/sdcard/Download/Vector.zip",
            installPath = "/data/local/tmp/Vector.zip"
        ),
        ModulePackage(
            displayName = "NoHello",
            downloadUrl = "https://github.com/MhmRdd/NoHello/releases/download/0.0.7/Nohello-v0.0.7-53-4d53ecf-release.zip",
            githubRepository = "MhmRdd/NoHello",
            releaseAssetPrefix = "Nohello-",
            fileName = "NoHello.zip",
            publicPath = "/sdcard/Download/NoHello.zip",
            installPath = "/data/local/tmp/NoHello.zip"
        ),
        ModulePackage(
            displayName = "HMA-OSS Zygisk",
            downloadUrl = "https://github.com/frknkrc44/HMA-OSS/releases/download/oss-164/HMA-OSS-ZYGISK-oss-164-release.zip",
            githubRepository = "frknkrc44/HMA-OSS",
            releaseAssetPrefix = "HMA-OSS-ZYGISK-",
            fileName = "HMA-OSS-ZYGISK.zip",
            publicPath = "/sdcard/Download/HMA-OSS-ZYGISK.zip",
            installPath = "/data/local/tmp/HMA-OSS-ZYGISK.zip"
        )
    )
}
