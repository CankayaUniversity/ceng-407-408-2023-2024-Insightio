package cankaya.insightio.application.services

import org.springframework.stereotype.Service
import java.io.File
import java.nio.file.Paths

// Service for running our python tracking script
@Service
class TrackingService(
    private val processBuilder: ProcessBuilder,
) {
    // ZEYNEP bunu companion object e çıkartabilir misin
//    companion object {
//        fun getBasePath(): String {
//            return Paths.get("").toAbsolutePath().toString()
//        }
//    }

    fun getBasePath(): String {
        return Paths.get("").toAbsolutePath().parent.toString()
    }

    fun runPythonScriptAsProcess(): Boolean {
        // ZEYNEP var isimlerini değiştirebilir misin, const variable'lar screaming snake case ile yazılır
        // fakat const olmayanlar upper camelcase şeklinde

        // val rootDirectory = getBasePath()
        // val venvPath = "$rootDirectory\\InsightioTracker\\venv\\Scripts\\python.exe"
        // val scriptLocation = "$rootDirectory\\InsightioTracker\\main.py"
        val SERVER_PARENT_DIR = getBasePath()
        val TRACKER_DIR = "$SERVER_PARENT_DIR\\InsightioTracker"
        val VENV_PATH = "$TRACKER_DIR\\venv\\Scripts\\python.exe"
        val SCRIPT_LOCATION = "$TRACKER_DIR\\main.py"

        processBuilder.command(VENV_PATH, SCRIPT_LOCATION)
        processBuilder.directory(File(TRACKER_DIR))
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
