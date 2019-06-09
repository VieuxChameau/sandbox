package org.vieuxchameau.sandbox.exchangesrates

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam

@Component
@FeignClient(name = "exchangesrates", url = "\${sandbox.exchangesrates.api.endpoint}")
interface ExchangesRatesRestClient {
    @RequestMapping("/live", method = [RequestMethod.GET])
    fun getLiveRates(
        @RequestParam("source") from: String,
        @RequestParam("currencies") currencies: String,
        @RequestParam("access_key") apiToken: String
    ): ExchangesRatesResponse

    @RequestMapping("/historical", method = [RequestMethod.GET])
    fun getRatesByDay(
        @RequestParam("source") from: String,
        @RequestParam("currencies") currencies: String,
        @RequestParam("date") date: String,
        @RequestParam("access_key") apiToken: String
    ): ExchangesRatesResponse
}