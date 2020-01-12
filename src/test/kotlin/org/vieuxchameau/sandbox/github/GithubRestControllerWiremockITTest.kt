package org.vieuxchameau.sandbox.github

//import org.springframework.core.io.ClassPathResource
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.*
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import org.springframework.http.MediaType
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*


@DirtiesContext
@TestPropertySource(
    properties = [
        "spring.cache.type=NONE", // Disable the cache for the test
        // Do not auto start the consumers
        "spring.rabbitmq.listener.direct.auto-startup=false",
        "spring.rabbitmq.listener.simple.auto-startup=false",
        "sandbox.github.endpoint=http://localhost:\${wiremock.server.port}"]
)
@AutoConfigureMockMvc
@AutoConfigureWireMock(port = 0)
@SpringBootTest
class GithubRestControllerWiremockITTest(
    @Autowired val mvc: MockMvc
) {
    @BeforeEach
    fun setUp() {
        WireMock.reset()
    }

    @Test
    fun `should fetch user from wiremocked github api`() {
        // Stubbing WireMock
        stubFor(
            get(urlEqualTo("/users/vieuxchameau"))
                .willReturn(aResponse().withHeader("Content-Type", "application/json").withBody(getJsonResponse("user")))
        );

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