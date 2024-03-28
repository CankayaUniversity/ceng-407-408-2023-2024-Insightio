package cankaya.insightio.infrastructure.api

import cankaya.insightio.application.services.CameraService
import cankaya.insightio.infrastructure.mongodb.impls.Camera
import cankaya.insightio.infrastructure.mongodb.impls.CameraStatus
import cankaya.insightio.infrastructure.mongodb.impls.CameraType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/cameras")
class CameraController(private val cameraService: CameraService) {
    @GetMapping("/all")
    fun getAllCameras(): ResponseEntity<List<Camera>> = ResponseEntity.ok(cameraService.findAllCameras())

    @GetMapping("/{id}")
    fun getCameraById(
        @PathVariable id: String,
    ): ResponseEntity<Camera> = cameraService.findCameraById(id)?.let { ResponseEntity.ok(it) } ?: ResponseEntity.notFound().build()

    @PostMapping
    fun createCamera(
        @RequestBody camera: Camera,
    ): ResponseEntity<Camera> = ResponseEntity.ok(cameraService.createCamera(camera))

    @PutMapping("/{id}")
    fun updateCamera(
        @PathVariable id: String,
        @RequestBody camera: Camera,
    ): ResponseEntity<Camera> = ResponseEntity.ok(cameraService.updateCamera(camera))

    @DeleteMapping("/{id}")
    fun deleteCamera(
        @PathVariable id: String,
    ): ResponseEntity<Void> {
        return if (cameraService.findCameraById(id) != null) {
            cameraService.deleteCamera(id)
            ResponseEntity.ok().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/type/{type}")
    fun getCamerasByType(
        @PathVariable type: CameraType,
    ): ResponseEntity<List<Camera>> {
        val cameras = cameraService.findByType(type)
        return if (cameras.isNotEmpty()) ResponseEntity.ok(cameras) else ResponseEntity.notFound().build()
    }

    @GetMapping("/status/{status}")
    fun getCamerasByStatus(
        @PathVariable status: CameraStatus,
    ): ResponseEntity<List<Camera>> {
        val cameras = cameraService.findByStatus(status)
        return if (cameras.isNotEmpty()) ResponseEntity.ok(cameras) else ResponseEntity.notFound().build()
    }

    @GetMapping("/name/{name}")
    fun getCameraByName(
        @PathVariable name: String,
    ): ResponseEntity<Camera> {
        val camera = cameraService.findByName(name)
        return camera?.let { ResponseEntity.ok(it) } ?: ResponseEntity.notFound().build()
    }
}
