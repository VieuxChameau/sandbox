package org.vieuxchameau.sandbox.exchangesrates

import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.amqp.core.AmqpTemplate

class ExchangesRatesPublisherTest {
    private val mockedAmqpTemplate = mock<AmqpTemplate>()
    private lateinit var exchangesRatesPublisher: ExchangesRatesPublisher

    @BeforeEach
    fun setUp() {
        exchangesRatesPublisher = ExchangesRatesPublisher(
            mockedAmqpTemplate,
            "onDemandExchange",
            "onDemandRoutingKey",
            "delayExchange",
            "delayRoutingKey",
            "scheduledExchange",
            "scheduledRoutingKey"
        )
    }

    @Test
    fun `should publish scheduled rates to the right exchange`() {
        val livesRates = ExchangesRatesResponse(
            true, 1557255846, "USD", mapOf(
                "GBP" to 0.765355,
                "EUR" to 0.89424,
                "CAD" to 1.34815
            ), false, null
        )
        exchangesRatesPublisher.publishRates(livesRates)

        verify(mockedAmqpTemplate).convertAndSend(eq("scheduledExchange"), eq("scheduledRoutingKey"), eq(livesRates))
    }

    @Test
    fun `should publish on demand rates to the right exchange`() {
        val livesRates = ExchangesRatesResponse(
            true, 1557255846, "USD", mapOf(
                "GBP" to 0.765355,
                "EUR" to 0.89424,
                "CAD" to 1.34815
            ), false, null
        )
        exchangesRatesPublisher.publishOnDemandRates(livesRates)

        verify(mockedAmqpTemplate).convertAndSend(eq("onDemandExchange"), eq("onDemandRoutingKey"), eq(livesRates))
    }

    @Test
    fun `should publish rescheduling base rate to the right exchange`() {
        val baseCurrency = "USD"
        exchangesRatesPublisher.reschedule(baseCurrency)

        verify(mockedAmqpTemplate).convertAndSend(eq("delayExchange"), eq("delayRoutingKey"), eq(baseCurrency))
    }
}