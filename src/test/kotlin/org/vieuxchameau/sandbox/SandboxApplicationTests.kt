package org.vieuxchameau.sandbox

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit4.SpringRunner

@TestPropertySource(
    properties = [
        "spring.cache.type=NONE", // Disable the cache for the test
        // Do not auto start the consumers
        "spring.rabbitmq.listener.direct.auto-startup=false",
        "spring.rabbitmq.listener.simple.auto-startup=false"]
)
@RunWith(SpringRunner::class)
@SpringBootTest
class SandboxApplicationTests {

    @Test
    fun `verify application can load`() {
    }

}
