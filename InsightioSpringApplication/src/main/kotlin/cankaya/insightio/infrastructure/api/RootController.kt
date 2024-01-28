package cankaya.insightio.infrastructure.api

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

// burası swagger için ben düzelteceğim

@RestController
@RequestMapping("/")
class RootController {
    @RequestMapping(method = [RequestMethod.GET])
    fun swaggerUi(): String {
        return "redirect:/swagger-ui.html"
    }
}