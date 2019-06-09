package org.vieuxchameau.sandbox.github

import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class GithubRestClient(
    private val githubRestTemplate: RestTemplate
) {
    fun getUser(userName: String): GithubUser? {
        return githubRestTemplate.getForObject("/users/$userName", GithubUser::class.java)
    }
}