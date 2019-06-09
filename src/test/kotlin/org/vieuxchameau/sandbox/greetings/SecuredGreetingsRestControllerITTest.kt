package org.vieuxchameau.sandbox.greetings

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.vieuxchameau.sandbox.security.JWTTokenGenerator

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
@RunWith(SpringRunner::class)
class SecuredGreetingsRestControllerITTest {

    @Autowired
    lateinit var mvc: MockMvc

    private val jwtTokenGenerator = JWTTokenGenerator()

    @Test
    fun `should successfully get greetings`() {
        val jwtToken = jwtTokenGenerator.getJwtToken()
        mvc.perform(
            MockMvcRequestBuilders.get("/hellosecured/VieuxChameau")
                .header("Authorization", "Bearer $jwtToken")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().string("Hello VieuxChameau"))
    }

    @Test
    fun `should be forbidden with wrong authorities`() {
        val jwtToken = jwtTokenGenerator.getJwtToken(roles = listOf("ROLE_USER"))
        mvc.perform(
            MockMvcRequestBuilders.get("/hellosecured/VieuxChameau")
                .header("Authorization", "Bearer $jwtToken")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isForbidden)
    }

    @Test
    fun `should be forbidden with wrong username`() {
        val jwtToken = jwtTokenGenerator.getJwtToken(userName = "JohnDoe")
        mvc.perform(
            MockMvcRequestBuilders.get("/hellosecured/VieuxChameau")
                .header("Authorization", "Bearer $jwtToken")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isForbidden)
    }

    @Test
    fun `should be unauthorized without token`() {
        mvc.perform(
            MockMvcRequestBuilders.get("/hellosecured/vieuxchameau")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }
}