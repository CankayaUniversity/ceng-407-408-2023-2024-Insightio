package cankaya.insightio.application.services
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
        // ZEYNEP var isimlerini değiştirebilir misin, const variable'lar screaming snake case ile yazılır
        // fakat const olmayanlar upper camelcase şeklinde

        // sut demek system under test in kısaltması, her unit test bu sut un bir fonksiyonunu test eder
        // bundan dolayı aynı test içerisinde sut ten iki farklı şeyi çağıramazsın
        // sut.getBasePath() ve sut.runPythonScriptAsProcess() olmamalı

        // bu fonk companion object e çekersen (TrackingService de yazdım)
        // val SCRIPT_ROOT_DIRECTORY = getBasePath() şeklinde çağırabilirz

        // Given
        val SCRIPT_ROOT_DIRECTORY = sut.getBasePath()
        val VENV_PATH = "$SCRIPT_ROOT_DIRECTORY\\InsightioTracker\\venv\\Scripts\\python.exe"
        val SCRIPT_LOCATION = "$SCRIPT_ROOT_DIRECTORY\\InsightioTracker\\main.py"
        `when`(mockProcessBuilder.command(VENV_PATH, SCRIPT_LOCATION)).thenReturn(mockProcessBuilder)
        `when`(mockProcessBuilder.directory(File(SCRIPT_ROOT_DIRECTORY))).thenReturn(mockProcessBuilder)
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
