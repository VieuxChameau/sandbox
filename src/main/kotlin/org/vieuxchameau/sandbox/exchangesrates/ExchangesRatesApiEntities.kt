package org.vieuxchameau.sandbox.exchangesrates

import java.io.Serializable


data class ExchangesRatesResponse(
    val success: Boolean,
    val timestamp: Long?,
    val source: String?,
    val quotes: Map<String, Double>? = emptyMap(),
    val historical: Boolean? = false,
    val error: ExchangesRatesError?
) : Serializable

data class ExchangesRatesError(
    val code: Int,
    val info: String
) : Serializable