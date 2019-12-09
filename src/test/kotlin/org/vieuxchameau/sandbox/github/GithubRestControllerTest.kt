package org.vieuxchameau.sandbox.github

import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GithubRestControllerTest {
    private val mockedGithubRestClient = mock<GithubRestClient>()

    private lateinit var githubRestController: GithubRestController

    @BeforeEach
    fun setUp() {
        githubRestController = GithubRestController(mockedGithubRestClient)
    }

    @Test
    fun `should fetch given username`() {
        val expectedGithubUser = GithubUser().apply {
            login = "JohnDoe"
            location = "Vancouver, BC, Canada"
        }
        whenever(mockedGithubRestClient.getUser(eq("JohnDoe"))).thenReturn(expectedGithubUser)
        val user = githubRestController.showUser("JohnDoe")


        assertThat(user).isEqualToComparingFieldByField(expectedGithubUser)
    }
}