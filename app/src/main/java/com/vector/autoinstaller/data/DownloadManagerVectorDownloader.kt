package com.vector.autoinstaller.data

import android.app.DownloadManager
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import com.vector.autoinstaller.domain.ModulePackage
import com.vector.autoinstaller.domain.AppPackage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.zip.ZipInputStream

class DownloadManagerVectorDownloader(
    context: Context
) {
    private val appContext = context.applicationContext
    private val downloadManager =
        appContext.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

    suspend fun download(modulePackage: ModulePackage): Boolean = withContext(Dispatchers.IO) {
        val destinationFile = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            modulePackage.fileName
        )
        runCatching { destinationFile.delete() }

        val latestUrl = resolveLatestReleaseUrl(modulePackage)
        val request = DownloadManager.Request(Uri.parse(latestUrl))
            .setTitle(modulePackage.fileName)
            .setDescription("${modulePackage.displayName} module")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)
            .setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                modulePackage.fileName
            )

        val downloadId = downloadManager.enqueue(request)
        waitForDownload(downloadId)
    }

    suspend fun downloadApp(appPackage: AppPackage): Boolean = withContext(Dispatchers.IO) {
        val downloadFile = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            appPackage.downloadFileName
        )
        val apkFile = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            appPackage.apkFileName
        )
        runCatching { downloadFile.delete() }
        if (downloadFile != apkFile) runCatching { apkFile.delete() }

        val request = DownloadManager.Request(Uri.parse(resolveLatestAppUrl(appPackage)))
            .setTitle(appPackage.displayName)
            .setDescription("${appPackage.displayName} application")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)
            .setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                appPackage.downloadFileName
            )

        if (!waitForDownload(downloadManager.enqueue(request))) return@withContext false
        val archiveEntry = appPackage.archiveApkEntry ?: return@withContext downloadFile.isFile
        extractApk(downloadFile, apkFile, archiveEntry)
    }

    private fun resolveLatestReleaseUrl(modulePackage: ModulePackage): String = runCatching {
        val apiUrl = URL("https://api.github.com/repos/${modulePackage.githubRepository}/releases/latest")
        val connection = apiUrl.openConnection() as HttpURLConnection
        connection.connectTimeout = 10_000
        connection.readTimeout = 10_000
        connection.setRequestProperty("Accept", "application/vnd.github+json")
        connection.setRequestProperty("User-Agent", "EID-ROOT-Android")
        try {
            if (connection.responseCode !in 200..299) return@runCatching modulePackage.downloadUrl
            val json = JSONObject(connection.inputStream.bufferedReader().use { it.readText() })
            val assets = json.getJSONArray("assets")
            for (index in 0 until assets.length()) {
                val asset = assets.getJSONObject(index)
                val name = asset.getString("name")
                if (name.startsWith(modulePackage.releaseAssetPrefix, ignoreCase = true) &&
                    name.endsWith(".zip", ignoreCase = true) &&
                    !name.contains("debug", ignoreCase = true)
                ) return@runCatching asset.getString("browser_download_url")
            }
            modulePackage.downloadUrl
        } finally {
            connection.disconnect()
        }
    }.getOrDefault(modulePackage.downloadUrl)

    private fun resolveLatestAppUrl(appPackage: AppPackage): String = runCatching {
        val apiUrl = URL("https://api.github.com/repos/${appPackage.githubRepository}/releases/latest")
        val connection = apiUrl.openConnection() as HttpURLConnection
        connection.connectTimeout = 10_000
        connection.readTimeout = 10_000
        connection.setRequestProperty("Accept", "application/vnd.github+json")
        connection.setRequestProperty("User-Agent", "EID-ROOT-Android")
        try {
            if (connection.responseCode !in 200..299) return@runCatching appPackage.fallbackUrl
            val assets = JSONObject(connection.inputStream.bufferedReader().use { it.readText() })
                .getJSONArray("assets")
            for (index in 0 until assets.length()) {
                val asset = assets.getJSONObject(index)
                val name = asset.getString("name")
                if (name.contains(appPackage.assetNameContains, ignoreCase = true) &&
                    (appPackage.excludedAssetText == null ||
                        !name.contains(appPackage.excludedAssetText, ignoreCase = true))
                ) return@runCatching asset.getString("browser_download_url")
            }
            appPackage.fallbackUrl
        } finally {
            connection.disconnect()
        }
    }.getOrDefault(appPackage.fallbackUrl)

    private fun extractApk(zipFile: File, outputFile: File, entryName: String): Boolean {
        return try {
            var extracted = false
            ZipInputStream(FileInputStream(zipFile)).use { zip ->
                while (!extracted) {
                    val entry = zip.nextEntry ?: break
                    if (!entry.isDirectory && entry.name.substringAfterLast('/') == entryName) {
                        FileOutputStream(outputFile).use { output -> zip.copyTo(output) }
                        extracted = outputFile.isFile && outputFile.length() > 0
                    }
                    zip.closeEntry()
                }
            }
            extracted
        } catch (_: Exception) {
            false
        }
    }

    private suspend fun waitForDownload(downloadId: Long): Boolean {
        repeat(MaxPollingAttempts) {
            val query = DownloadManager.Query().setFilterById(downloadId)
            downloadManager.query(query).use { cursor ->
                if (cursor != null && cursor.moveToFirst()) {
                    when (cursor.downloadStatus()) {
                        DownloadManager.STATUS_SUCCESSFUL -> {
                            return true
                        }

                        DownloadManager.STATUS_FAILED -> {
                            return false
                        }
                    }
                }
            }
            delay(PollingDelayMillis)
        }

        downloadManager.remove(downloadId)
        return false
    }

    private fun Cursor.downloadStatus(): Int =
        getInt(getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS))

    private companion object {
        const val MaxPollingAttempts = 600
        const val PollingDelayMillis = 1_000L
    }
}
