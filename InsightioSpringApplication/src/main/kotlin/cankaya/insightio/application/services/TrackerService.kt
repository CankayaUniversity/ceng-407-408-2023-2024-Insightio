package cankaya.insightio.application.services

import org.springframework.stereotype.Service


@Service
class TrackerService {
    companion object {
        const val scriptLocation = "../InsightioTracker/x.py"
        const val trackingScript = "python3 $scriptLocation"
    }

    fun runPythonScript(command: String = trackingScript) {
        Runtime.getRuntime().exec(command);
    }
}