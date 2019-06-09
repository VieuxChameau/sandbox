package org.vieuxchameau.sandbox.github

import com.nhaarman.mockitokotlin2.*
import org.junit.Before
import org.junit.Test
import org.springframework.web.client.RestTemplate

class GithubRestClientTest {
    private val mockedRestTemplate = mock<RestTemplate>()

    private lateinit var githubRestClient: GithubRestClient

    @Before
    fun setUp() {
        githubRestClient = GithubRestClient(mockedRestTemplate)
    }

    @Test
    fun `should call the right endpoint`() {
        githubRestClient.getUser("JohnDoe")

        verify(mockedRestTemplate, times(1)).getForObject<GithubUser>(eq("/users/JohnDoe"), isA())
    }
}