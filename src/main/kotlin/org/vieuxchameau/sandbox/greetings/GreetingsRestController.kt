package org.vieuxchameau.sandbox.greetings

import org.apache.logging.log4j.LogManager
import org.springframework.web.bind.annotation.*

@RestController
class GreetingsRestController {
    private val log = LogManager.getLogger(GreetingsRestController::class.java)

    data class SimpleResponse(val text: String)

    data class HelloRequest(val name: String, val language: String)

    @GetMapping("/hello")
    fun sayHello() = getGreeting("Hello", "World")

    @GetMapping("/hello/{name}")
    fun sayHello(@PathVariable name: String) = SimpleResponse(getGreeting("Hello", name)) // <- returns JSON

    @PostMapping("/hellobabel", consumes = ["application/json"])
    fun sayHelloIn(@RequestBody request: HelloRequest): SimpleResponse {
        return SimpleResponse(
            getGreeting(
                when (request.language) {
                    "en" -> "Hello"
                    "fr" -> "Bonjour"
                    "it" -> "Ciao"
                    "es" -> "Hola"
                    else -> "Hello"
                }, request.name
            )
        )
    }

    private fun getGreeting(greetingWord: String, name: String): String {
        log.debug("Say {} {}", greetingWord, name)
        return "$greetingWord $name!"
    }
}