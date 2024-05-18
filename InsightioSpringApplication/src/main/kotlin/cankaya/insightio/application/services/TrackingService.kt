package cankaya.insightio.application.services

import org.springframework.stereotype.Service
import java.io.File
import java.nio.file.Paths

@Service
class TrackingService(
    private val processBuilder: ProcessBuilder,
) {
    fun getBasePath(): String {
        return Paths.get("").toAbsolutePath().toString()
    }

    fun runPythonScriptAsProcess(): Boolean {
        val SCRIPT_ROOT_DIRECTORY = getBasePath()
        val VENV_PATH = "$SCRIPT_ROOT_DIRECTORY\\InsightioTracker\\venv\\Scripts\\python.exe"
        val SCRIPT_LOCATION = "$SCRIPT_ROOT_DIRECTORY\\InsightioTracker\\main.py"

        processBuilder.command(VENV_PATH, SCRIPT_LOCATION)
        processBuilder.directory(File(SCRIPT_ROOT_DIRECTORY))
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
