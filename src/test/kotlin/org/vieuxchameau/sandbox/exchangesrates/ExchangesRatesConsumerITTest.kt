package org.vieuxchameau.sandbox.exchangesrates

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.rabbit.test.RabbitListenerTest
import org.springframework.amqp.rabbit.test.TestRabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit4.SpringRunner
import org.vieuxchameau.sandbox.configuration.RabbitTestConfig

const val SCHEDULED_QUEUE_NAME = "scheduledRates"
const val ONDEMAND_QUEUE_NAME = "ondemandRates"

@DirtiesContext
@ContextConfiguration(classes = [RabbitTestConfig::class])
@TestPropertySource(
    properties = [
        "spring.cache.type=NONE", // Disable the cache for the test
        "sandbox.exchangesrates.scheduled.consumeFrom=$SCHEDULED_QUEUE_NAME",
        "sandbox.exchangesrates.onDemand.consumeFrom=$ONDEMAND_QUEUE_NAME"]
)
@RabbitListenerTest
@SpringBootTest
@RunWith(SpringRunner::class)
class ExchangesRatesConsumerITTest {
    @Autowired
    lateinit var template: TestRabbitTemplate

    @MockBean
    lateinit var exchangesRatesPublisher: ExchangesRatesPublisher

    @Test
    fun `should listen for message and fetch rate`() {

        val message = ExchangesRatesRequestMessage().apply {
            baseCurrency = "USD"
            currencies = listOf("GBP", "EUR")
        }

        template.convertAndSend(ONDEMAND_QUEUE_NAME, message)

        // TODO how to verify the publish, need to create a listener in the test config?
        verify(exchangesRatesPublisher, times(1)).publishOnDemandRates(any())
    }
}