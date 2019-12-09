package org.vieuxchameau.sandbox.exchangesrates

import com.nhaarman.mockitokotlin2.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ExchangesRatesConsumerTest {
    private val mockedExchangesRatesService = mock<ExchangesRatesService>()
    private val mockedExchangesRatesPublisher = mock<ExchangesRatesPublisher>()

    private lateinit var exchangesRatesConsumer: ExchangesRatesConsumer

    @BeforeEach
    fun setUp() {
        exchangesRatesConsumer = ExchangesRatesConsumer(
            mockedExchangesRatesService, mockedExchangesRatesPublisher
        )
    }

    @Test
    fun `should fetch rates, publish them and reshedule`() {
        val baseCurrency = "USD"
        val livesRates = ExchangesRatesResponse(
            true, 1557255846, baseCurrency, mapOf(
                "GBP" to 0.765355,
                "EUR" to 0.89424,
                "CAD" to 1.34815
            ), false, null
        )
        val currencies: List<String> = listOf()
        whenever(mockedExchangesRatesService.getLiveRates(eq(baseCurrency), eq(currencies))).thenReturn(livesRates)

        exchangesRatesConsumer.scheduledFetch(baseCurrency)

        verify(mockedExchangesRatesPublisher, times(1)).publishRates(
            eq(livesRates)
        )
        verify(mockedExchangesRatesPublisher, times(1)).reschedule(eq(baseCurrency))
    }


    @Test
    fun `should fetch rates on demand and publish them`() {
        val livesRates = ExchangesRatesResponse(
            true, 1557255846, "USD", mapOf(
                "GBP" to 0.765355,
                "EUR" to 0.89424,
                "CAD" to 1.34815
            ), false, null
        )

        val message = ExchangesRatesRequestMessage().apply {
            baseCurrency = "USD"
            currencies = listOf("EUR", "GBP", "CAD")
        }

        whenever(mockedExchangesRatesService.getLiveRates(eq("USD"), eq(listOf("EUR", "GBP", "CAD")))).thenReturn(
            livesRates
        )

        exchangesRatesConsumer.receive(message)

        verify(mockedExchangesRatesPublisher, only()).publishOnDemandRates(eq(livesRates))
    }
}