package org.vieuxchameau.sandbox.exchangesrates

import org.apache.logging.log4j.LogManager
import org.springframework.web.bind.annotation.*

@RequestMapping("exchangesrates")
@RestController
class ExchangesRatesRestController(
    private val exchangesRatesService: ExchangesRatesService
) {
    private val logger = LogManager.getLogger(ExchangesRatesRestController::class.java)


    @GetMapping
    fun getCurrentExchangesRates(): ExchangesRatesResponse {
        logger.debug("Getting exchanges rates from USD")

        return exchangesRatesService.getLiveRates()
    }

    @GetMapping("/{from}")
    fun getCurrentExchangesRatesFor(
        @PathVariable("from") baseCurrency: String,
        @RequestParam("currencies") currencies: List<String>
    ): ExchangesRatesResponse {
        logger.debug("Getting exchanges rates from {} to {}", baseCurrency, currencies)
        return exchangesRatesService.getLiveRates(baseCurrency, currencies)
    }


    @PutMapping("/cache/clear")
    fun clearCacheRate() {
        exchangesRatesService.clearExchangeRateCache()
    }
}
