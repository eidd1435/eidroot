package com.vector.autoinstaller.data

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.concurrent.thread
import java.util.concurrent.TimeUnit

class RootShell {
    suspend fun hasRootAccess(): Boolean {
        val result = runAsRoot("id", timeoutSeconds = 20)
        return result.exitCode == 0 && result.output.contains("uid=0")
    }

    suspend fun installMagiskModule(zipPath: String): Boolean {
        val escapedZipPath = zipPath.shellEscape()
        val installCommands = listOf(
            "magisk --install-module $escapedZipPath",
            "ksud module install $escapedZipPath",
            "/data/adb/ksu/bin/ksud module install $escapedZipPath",
            "/data/adb/ksud module install $escapedZipPath",
            "ASH_STANDALONE=1 /data/adb/ksu/bin/busybox sh -c ${"/data/adb/ksu/bin/ksud module install $escapedZipPath".shellEscape()}"
        )

        installCommands.forEach { command ->
            val result = runAsRoot(command, timeoutSeconds = 300)
            Log.i(Tag, "Install command: $command\nexit=${result.exitCode}\n${result.output}")
            if (result.exitCode == 0) return true
        }

        return false
    }

    suspend fun deleteFile(path: String): Boolean {
        val command = "rm -f ${path.shellEscape()}"
        val result = runAsRoot(command, timeoutSeconds = 20)
        return result.exitCode == 0
    }

    suspend fun fileExists(path: String): Boolean {
        val command = "test -s ${path.shellEscape()}"
        val result = runAsRoot(command, timeoutSeconds = 20)
        return result.exitCode == 0
    }

    suspend fun copyFile(sourcePath: String, destinationPath: String): Boolean {
        val command = "cp ${sourcePath.shellEscape()} ${destinationPath.shellEscape()} && chmod 0644 ${destinationPath.shellEscape()}"
        val result = runAsRoot(command, timeoutSeconds = 60)
        Log.i(Tag, "Copy command exit=${result.exitCode}\n${result.output}")
        return result.exitCode == 0
    }

    suspend fun reboot(): Boolean {
        val result = runAsRoot("reboot", timeoutSeconds = 20)
        return result.exitCode == 0
    }

    private suspend fun runAsRoot(
        command: String,
        timeoutSeconds: Long
    ): ShellResult = withContext(Dispatchers.IO) {
        try {
            val process = ProcessBuilder("su", "-c", command)
                .redirectErrorStream(true)
                .start()

            val output = StringBuffer()
            val outputReader = thread(start = true) {
                BufferedReader(InputStreamReader(process.inputStream)).use { reader ->
                    while (true) {
                        val line = reader.readLine() ?: break
                        output.appendLine(line)
                    }
                }
            }
            val finished = process.waitFor(timeoutSeconds, TimeUnit.SECONDS)

            if (!finished) {
                process.destroyForcibly()
                outputReader.join(1_000)
                return@withContext ShellResult(exitCode = -1, output = output.toString())
            }

            outputReader.join(1_000)
            ShellResult(exitCode = process.exitValue(), output = output.toString())
        } catch (_: Exception) {
            ShellResult(exitCode = -1, output = "")
        }
    }

    private fun String.shellEscape(): String =
        "'${replace("'", "'\"'\"'")}'"

    private data class ShellResult(
        val exitCode: Int,
        val output: String
    )

    private companion object {
        const val Tag = "VectorInstaller"
    }
}
