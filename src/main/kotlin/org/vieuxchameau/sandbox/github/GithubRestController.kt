package org.vieuxchameau.sandbox.github

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("github")
class GithubRestController(private val githubRestClient: GithubRestClient) {

    @GetMapping("/users/{userName}", produces = ["application/json"])
    fun showUser(@PathVariable userName: String) = githubRestClient.getUser(userName)
}