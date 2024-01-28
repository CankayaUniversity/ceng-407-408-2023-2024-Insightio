package cankaya.insightio.infrastructure.api

import io.swagger.v3.oas.annotations.Hidden
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

// Used to redirect to the swagger-ui each time application is reached
@Hidden
@Controller
@RequestMapping("/")
class RootController {
    @GetMapping
    fun redirectToSwagger(): String {
        return "redirect:/swagger-ui/index.html"
    }
}