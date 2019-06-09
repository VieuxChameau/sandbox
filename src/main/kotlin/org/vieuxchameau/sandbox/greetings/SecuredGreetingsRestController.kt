package org.vieuxchameau.sandbox.greetings

import org.apache.logging.log4j.LogManager
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.vieuxchameau.sandbox.security.JwtUser

@RequestMapping("hellosecured")
@RestController
class SecuredGreetingsRestController {
    private val logger = LogManager.getLogger(SecuredGreetingsRestController::class.java)

    // the token need to have the correct role and the username has to match the one passed in the url
    @PreAuthorize("hasAuthority('ROLE_SUPERADMIN') AND #name == principal.userName")
    @GetMapping("/{name}")
    fun sayHello(
        @PathVariable name: String,
        @AuthenticationPrincipal user: JwtUser
    ): String {
        logger.info("Say hello to my little friend {} : {}", name, user)
        return "Hello $name"
    }
}