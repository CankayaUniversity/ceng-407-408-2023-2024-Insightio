package cankaya.insightio.application.services

import org.springframework.stereotype.Service
import java.io.File

@Service
class TrackingService(
    private val processBuilder: ProcessBuilder,
) {
    companion object {
        const val SCRIPT_LOCATION = "..\\InsightioTracker\\main.py"
        const val SCRIPT_ROOT_DIRECTORY = "..\\InsightioTracker\\"
        const val VENV_PATH = "..\\InsightioTracker\\venv\\Scripts\\python.exe"  // Path to the Python executable in the virtual environment
    }

    fun runPythonScriptWithExec() {
        val command = "\"$VENV_PATH\" \"$SCRIPT_LOCATION\""
        Runtime.getRuntime().exec(command)
    }

    fun runPythonScriptAsProcess(): Boolean {
        val scriptDirectory = File(SCRIPT_ROOT_DIRECTORY)
        processBuilder.command(VENV_PATH, SCRIPT_LOCATION)
        processBuilder.directory(scriptDirectory)
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
