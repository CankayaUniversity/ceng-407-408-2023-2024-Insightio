package cankaya.insightio.infrastructure.api

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

// burası infra -> api, controller lar buraya ekleniliyor
// senin controller ını da ekleyebilirsin bu directory ye

@RestController
@RequestMapping("/hello")
@Api(value = "hello", description = "Sample hello world application")
class TestController {
    @ApiOperation(value = "Just to test the sample test api of My App Service")
    @RequestMapping(method = [RequestMethod.GET], value = ["/test"])
    fun test(): String {
        return "Hello to check Swagger UI"
    }
}