package org.vieuxchameau.sandbox.github

//import org.springframework.core.io.ClassPathResource
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers
import org.springframework.test.web.client.response.MockRestResponseCreators
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.support.RestGatewaySupport


@DirtiesContext
@TestPropertySource(
    properties = [
        "spring.cache.type=NONE", // Disable the cache for the test
        // Do not auto start the consumers
        "spring.rabbitmq.listener.direct.auto-startup=false",
        "spring.rabbitmq.listener.simple.auto-startup=false"]
)
@AutoConfigureMockMvc
@SpringBootTest
class GithubRestControllerITTest(
    @Autowired val mvc: MockMvc,
    @Autowired val restTemplate: RestTemplate
) {
    private lateinit var mockServer: MockRestServiceServer


    @BeforeEach
    fun setUp() {
        val gateway = RestGatewaySupport()
        gateway.restTemplate = restTemplate
        mockServer = MockRestServiceServer.createServer(gateway)
    }

    @Test
    fun `should fetch user from github api`() {
        mockServer.expect(MockRestRequestMatchers.requestTo("https://api.github.com/users/vieuxchameau"))
            .andRespond(MockRestResponseCreators.withSuccess(getJsonResponse("user"), MediaType.APPLICATION_JSON))
        // could be replaced by following
//            .andRespond(MockRestResponseCreators.withSuccess(ClassPathResource("github/user.json"), MediaType.APPLICATION_JSON))

        mvc.perform(
            get("/github/users/vieuxchameau")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(
                content()
                    .contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
            )
            .andExpect(jsonPath("$.login", `is`("VieuxChameau")))
            .andExpect(jsonPath("$.location", `is`("London, UK")))
    }

    private fun getJsonResponse(fileName: String): String {
        val resource = javaClass.getResourceAsStream("/github/$fileName.json")
        return resource
            .bufferedReader()
            .use {
                it.readText()
            }
    }
}