package org.vieuxchameau.sandbox.exchangesrates

import org.apache.logging.log4j.LogManager
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component

@Component
class ExchangesRatesConsumer(
    private val exchangesRatesService: ExchangesRatesService,
    private val exchangesRatesPublisher: ExchangesRatesPublisher
) {
    private val logger = LogManager.getLogger(ExchangesRatesConsumer::class.java)

    @RabbitListener(queues = ["\${sandbox.exchangesrates.scheduled.consumeFrom}"])
    fun scheduledFetch(@Payload baseCurrency: String) {
        logger.debug("Time for scheduled fetch")

        val liveRates = exchangesRatesService.getLiveRates(baseCurrency)

        exchangesRatesPublisher.publishRates(liveRates)
        exchangesRatesPublisher.reschedule(baseCurrency)
    }


    @RabbitListener(queues = ["\${sandbox.exchangesrates.onDemand.consumeFrom}"])
    fun receive(@Payload message: ExchangesRatesRequestMessage) {
        logger.info("Received {}", message)
        val liveRates = exchangesRatesService.getLiveRates(message.baseCurrency, message.currencies)

        exchangesRatesPublisher.publishOnDemandRates(liveRates)
    }
}