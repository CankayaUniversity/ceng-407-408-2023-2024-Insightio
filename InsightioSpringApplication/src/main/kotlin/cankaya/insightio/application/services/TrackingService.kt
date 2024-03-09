package cankaya.insightio.application.services

import org.springframework.stereotype.Service
import java.io.File

@Service
class TrackingService(
    private val processBuilder: ProcessBuilder,
) {
    companion object {
        const val SCRIPT_LOCATION = "../InsightioTracker/ekin-dokunmayin.py"
        const val SCRIPT_ROOT_DIRECTORY = "../InsightioTracker/"
        const val SCRIPT_COMMAND = "python3"
        const val TRACKING_SCRIPT_COMMAND = "python3 $SCRIPT_LOCATION"
    }

    fun runPythonScriptWithExec(command: String = TRACKING_SCRIPT_COMMAND) {
        Runtime.getRuntime().exec(command)
    }

    fun runPythonScriptAsProcess(scriptLocation: String = SCRIPT_LOCATION): Boolean {
        val scriptDirectory = File(SCRIPT_ROOT_DIRECTORY)
        processBuilder.command(SCRIPT_COMMAND, scriptLocation)
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
