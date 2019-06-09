package org.vieuxchameau.sandbox.github

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate
import java.time.Duration

@Configuration
class GithubConfiguration {
    @Bean
    fun githubRestTemplate(
        builder: RestTemplateBuilder,
        @Value("\${sandbox.github.endpoint}") endpoint: String,
        @Value("\${sandbox.github.connectionTimeout}") connectionTimeout: Duration,
        @Value("\${sandbox.github.readTimeout}") readTimeout: Duration
    ): RestTemplate {
        return builder
            .rootUri(endpoint)
            .setConnectTimeout(connectionTimeout)
            .setReadTimeout(readTimeout)
            .build()
    }
}