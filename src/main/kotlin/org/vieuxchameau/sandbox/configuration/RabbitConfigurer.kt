package org.vieuxchameau.sandbox.configuration

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.converter.CompositeMessageConverter
import org.springframework.messaging.converter.MappingJackson2MessageConverter
import org.springframework.messaging.converter.MarshallingMessageConverter
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory
import org.springframework.oxm.jaxb.Jaxb2Marshaller
import org.vieuxchameau.sandbox.exchangesrates.ExchangesRatesRequestMessage

@Configuration
class RabbitConfigurer : RabbitListenerConfigurer {

    @Bean
    fun producerJackson2MessageConverter(): Jackson2JsonMessageConverter {
        return Jackson2JsonMessageConverter(objectMapperKotlinFriendly())
    }

    @Bean
    fun consumerJackson2MessageConverter(): MappingJackson2MessageConverter {
        val mappingJackson2MessageConverter = MappingJackson2MessageConverter()
        mappingJackson2MessageConverter.objectMapper = objectMapperKotlinFriendly()
        return mappingJackson2MessageConverter
    }

    @Bean
    fun objectMapperKotlinFriendly(): ObjectMapper {
        return ObjectMapper().registerKotlinModule()
            .setSerializationInclusion(JsonInclude.Include.NON_ABSENT)
            .enable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    }

    @Bean
    fun messageHandlerMethodFactory(): DefaultMessageHandlerMethodFactory {
        val factory = DefaultMessageHandlerMethodFactory()
        val jaxb2Marshaller = Jaxb2Marshaller()
        jaxb2Marshaller.setClassesToBeBound(
            ExchangesRatesRequestMessage::class.java
        )
        val compositeMessageConverter = CompositeMessageConverter(
            listOf(
                consumerJackson2MessageConverter(), // for content_type application/json
                MarshallingMessageConverter(jaxb2Marshaller) // for content_type application/xml
            )
        )
        factory.setMessageConverter(compositeMessageConverter)
        return factory
    }

    override fun configureRabbitListeners(registrar: RabbitListenerEndpointRegistrar) {
        registrar.messageHandlerMethodFactory = messageHandlerMethodFactory()
    }
}