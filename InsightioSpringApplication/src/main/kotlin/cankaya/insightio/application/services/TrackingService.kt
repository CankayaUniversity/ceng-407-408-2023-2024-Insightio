package cankaya.insightio.application.services

import org.springframework.stereotype.Service
import java.io.File
import java.nio.file.Paths

// Service for running our python tracking script
@Service
class TrackingService(
    private val processBuilder: ProcessBuilder,
) {
    companion object {
        fun getBasePath(): String {
            return Paths.get("").toAbsolutePath().parent.toString()
        }
    }

    fun runPythonScriptAsProcess(): Boolean {
        val serverParentDirectory = getBasePath()
        val trackerDirectory = "$serverParentDirectory\\InsightioTracker"
        val venvPath = "$trackerDirectory\\venv\\Scripts\\python.exe"
        val scriptLocation = "$trackerDirectory\\main.py"

        processBuilder.command(venvPath, scriptLocation)
        processBuilder.directory(File(trackerDirectory))
        processBuilder.inheritIO()

        try {
            val process = processBuilder.start()
            val exitCode = process.waitFor()

            if (exitCode == 0) {
                println("Python script executed successfully.")
                return true
            } else {
                println("Failed to execute Python script. Exit code: $exitCode")
                return false
            }
        } catch (exception: Exception) {
            println("Error executing Python script, error: ${exception.message}")
            throw exception
        }
    }
}
