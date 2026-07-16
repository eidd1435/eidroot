package com.vector.autoinstaller.domain

data class ModulePackage(
    val displayName: String,
    val downloadUrl: String,
    val githubRepository: String,
    val releaseAssetPrefix: String,
    val fileName: String,
    val publicPath: String,
    val installPath: String
)
