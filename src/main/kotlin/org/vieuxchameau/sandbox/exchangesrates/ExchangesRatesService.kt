package org.vieuxchameau.sandbox.exchangesrates

import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

private const val CACHE_NAME = "erc" // Exchange Rate Cache (erc)

@Service
class ExchangesRatesService(
    private val client: ExchangesRatesRestClient,
    @Value("\${sandbox.exchangesrates.api.token}") private val apiToken: String
) {
    private val logger = LogManager.getLogger(ExchangesRatesService::class.java)

    @Cacheable(cacheNames = [CACHE_NAME], unless = "#result.success == false")
    fun getLiveRates(
        baseCurrency: String = "USD",
        currencies: List<String> = emptyList()
    ): ExchangesRatesResponse {
        logger.info("Fetch lives rates for {} to {}", baseCurrency, currencies)
        val rates = client.getLiveRates(baseCurrency, currencies.joinToString(), apiToken)
        return rates.updateQuotesKey()
    }

    @CacheEvict(cacheNames = [CACHE_NAME], allEntries = true)
    fun clearExchangeRateCache() {
        logger.info("Cache {} will now be emptied", CACHE_NAME)
    }
}


/**
 * For some reason the key of the quotes ("GBPAED": 5.578448) contains the source currency.
 * This method copy the ExchangesRatesResponse and remove the source currency from the key of the quotes
 */
fun ExchangesRatesResponse.updateQuotesKey(): ExchangesRatesResponse {
    if (!success) { // if it's an error response don't bother updating the quotes key
        return this
    }
    return this.copy(quotes = this.quotes?.mapKeys { it.key.removePrefix(this.source ?: "") })
}
