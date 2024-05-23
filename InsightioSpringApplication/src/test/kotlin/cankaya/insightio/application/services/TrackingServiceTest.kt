package cankaya.insightio.application.services
import cankaya.insightio.application.services.TrackingService.Companion.getBasePath
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.PrintStream

@ExtendWith(MockitoExtension::class)
class TrackingServiceTest {
    @Mock
    private lateinit var mockProcessBuilder: ProcessBuilder

    @InjectMocks
    private lateinit var sut: TrackingService

    @Mock
    private lateinit var mockProcess: Process

    @Test
    fun `when runPythonScriptAsProcess function is called, python script should be executed`() {

        // Given
        val serverParentDirectory = getBasePath()
        val venvPath = "$serverParentDirectory\\InsightioTracker\\venv\\Scripts\\python.exe"
        val scriptLocation = "$serverParentDirectory\\InsightioTracker\\main.py"
        `when`(
            mockProcessBuilder.command(venvPath, scriptLocation),
        ).thenReturn(mockProcessBuilder)
        `when`(
            mockProcessBuilder.directory(File(serverParentDirectory)),
        ).thenReturn(mockProcessBuilder)
        `when`(mockProcessBuilder.inheritIO()).thenReturn(mockProcessBuilder)
        `when`(mockProcessBuilder.start()).thenReturn(mockProcess)
        `when`(mockProcess.waitFor()).thenReturn(0)
        val outputStreamCaptor = ByteArrayOutputStream()
        System.setOut(PrintStream(outputStreamCaptor))

        // When
        sut.runPythonScriptAsProcess()

        // Then
        val capturedOutput = outputStreamCaptor.toString().trim()
        assert(capturedOutput == "Python script executed successfully.")
        verify(mockProcessBuilder, times(1)).start()
        verifyNoMoreInteractions(mockProcessBuilder, mockProcess)
    }
}
