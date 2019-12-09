package org.vieuxchameau.sandbox.exchangesrates

import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

private const val API_TOKEN = "VERY_SECRET_TOKEN"

class ExchangesRatesServiceTest {
    private val mockedExchangesRatesRestClient = mock<ExchangesRatesRestClient>()
    private lateinit var exchangesRatesService: ExchangesRatesService

    @BeforeEach
    fun setUp() {
        exchangesRatesService = ExchangesRatesService(mockedExchangesRatesRestClient, API_TOKEN)
    }

    @Test
    fun `should get lives rates with default when no parameters passed`() {
        val response = ExchangesRatesResponse(
            true, 1557255846, "USD", mapOf(
                "USDGBP" to 0.765355,
                "USDEUR" to 0.89424,
                "USDCAD" to 1.34815
            ), false, null
        )
        whenever(mockedExchangesRatesRestClient.getLiveRates(eq("USD"), eq(""), eq(API_TOKEN))).thenReturn(response)

        val liveRates = exchangesRatesService.getLiveRates()

        val expectedResponse = ExchangesRatesResponse(
            true, 1557255846, "USD", mapOf(
                "GBP" to 0.765355,
                "EUR" to 0.89424,
                "CAD" to 1.34815
            ), false, null
        )
        assertThat(liveRates).isEqualTo(expectedResponse)
    }

    @Test
    fun `should get lives rates with given parameters`() {
        val response = ExchangesRatesResponse(
            true, 1557255846, "EUR", mapOf(
                "EURGBP" to 0.86,
                "EURUSD" to 1.12,
                "EURCAD" to 1.51
            ), false, null
        )
        whenever(mockedExchangesRatesRestClient.getLiveRates(eq("EUR"), eq("GBP, USD, CAD"), eq(API_TOKEN))).thenReturn(
            response
        )

        val liveRates = exchangesRatesService.getLiveRates("EUR", listOf("GBP", "USD", "CAD"))

        val expectedResponse = ExchangesRatesResponse(
            true, 1557255846, "EUR", mapOf(
                "GBP" to 0.86,
                "USD" to 1.12,
                "CAD" to 1.51
            ), false, null
        )
        assertThat(liveRates).isEqualTo(expectedResponse)
    }

    @Test
    fun `should handle errors`() {
        val response = ExchangesRatesResponse(
            false,
            1557255846,
            null,
            emptyMap(),
            false,
            ExchangesRatesError(
                105,
                "Access Restricted - Your current Subscription Plan does not support Source Currency Switching."
            )
        )
        whenever(mockedExchangesRatesRestClient.getLiveRates(eq("GBP"), eq("EUR, USD, CAD"), eq(API_TOKEN))).thenReturn(
            response
        )

        val liveRates = exchangesRatesService.getLiveRates("GBP", listOf("EUR", "USD", "CAD"))

        val expectedResponse = ExchangesRatesResponse(
            false,
            1557255846,
            null,
            emptyMap(),
            false,
            ExchangesRatesError(
                105,
                "Access Restricted - Your current Subscription Plan does not support Source Currency Switching."
            )
        )
        assertThat(liveRates).isEqualTo(expectedResponse)
    }
}