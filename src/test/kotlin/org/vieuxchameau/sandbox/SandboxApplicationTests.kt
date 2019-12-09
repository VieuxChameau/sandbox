package org.vieuxchameau.sandbox

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource

@TestPropertySource(
    properties = [
        "spring.cache.type=NONE", // Disable the cache for the test
        // Do not auto start the consumers
        "spring.rabbitmq.listener.direct.auto-startup=false",
        "spring.rabbitmq.listener.simple.auto-startup=false"]
)
@SpringBootTest
class SandboxApplicationTests {

    @Test
    fun `verify application can load`() {
    }

}
