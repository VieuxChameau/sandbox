package org.vieuxchameau.sandbox.exchangesrates

import org.springframework.amqp.core.AmqpTemplate
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class ExchangesRatesPublisher(
    private val amqpTemplate: AmqpTemplate,
    @Value("\${sandbox.exchangesrates.onDemand.publish.exchange}") private val onDemandExchange: String,
    @Value("\${sandbox.exchangesrates.onDemand.publish.routingKey}") private val onDemandRoutingKey: String,
    @Value("\${sandbox.exchangesrates.delay.exchange}") private val delayExchange: String,
    @Value("\${sandbox.exchangesrates.delay.routingKey}") private val delayRoutingKey: String,
    @Value("\${sandbox.exchangesrates.scheduled.publish.exchange}") private val scheduledExchange: String,
    @Value("\${sandbox.exchangesrates.scheduled.publish.routingKey}") private val scheduledRoutingKey: String
) {
    fun publishRates(liveRates: ExchangesRatesResponse) {
        amqpTemplate.convertAndSend(scheduledExchange, scheduledRoutingKey, liveRates)
    }

    fun reschedule(baseCurrency: String) {
        amqpTemplate.convertAndSend(delayExchange, delayRoutingKey, baseCurrency)
    }

    fun publishOnDemandRates(liveRates: ExchangesRatesResponse) {
        amqpTemplate.convertAndSend(onDemandExchange, onDemandRoutingKey, liveRates)
    }
}