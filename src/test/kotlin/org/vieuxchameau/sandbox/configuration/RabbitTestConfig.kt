package org.vieuxchameau.sandbox.configuration

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.rabbitmq.client.Channel
import org.mockito.ArgumentMatchers.anyBoolean
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.willReturn
import org.mockito.Mockito.mock
import org.springframework.amqp.rabbit.annotation.EnableRabbit
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory
import org.springframework.amqp.rabbit.connection.Connection
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.test.TestRabbitTemplate
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import java.io.IOException

@EnableRabbit
@TestConfiguration
class RabbitTestConfig {
    @Bean
    @Throws(IOException::class)
    fun template(): TestRabbitTemplate {
        val testRabbitTemplate = TestRabbitTemplate(connectionFactory())
        testRabbitTemplate.messageConverter = jsonMessageConverter()
        return testRabbitTemplate
    }

    @Bean
    @Throws(IOException::class)
    fun connectionFactory(): ConnectionFactory {
        val factory = mock(ConnectionFactory::class.java)
        val connection = mock(Connection::class.java)
        val channel = mock(Channel::class.java)
        willReturn(connection).given(factory).createConnection()
        willReturn(channel).given(connection).createChannel(anyBoolean())
        given(channel.isOpen()).willReturn(true)
        return factory
    }

    @Bean
    @Throws(IOException::class)
    fun rabbitListenerContainerFactory(): SimpleRabbitListenerContainerFactory {
        val factory = SimpleRabbitListenerContainerFactory()
        factory.setConnectionFactory(connectionFactory())
        factory.setMessageConverter(jsonMessageConverter())
        return factory
    }

    @Bean
    fun jsonMessageConverter(): Jackson2JsonMessageConverter {
        return Jackson2JsonMessageConverter(jacksonObjectMapper())
    }
}