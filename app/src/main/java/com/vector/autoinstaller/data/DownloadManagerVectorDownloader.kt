package com.vector.autoinstaller.data

import android.app.DownloadManager
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import com.vector.autoinstaller.domain.ModulePackage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

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
