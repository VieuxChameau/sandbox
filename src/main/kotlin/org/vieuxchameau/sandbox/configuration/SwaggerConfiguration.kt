package org.vieuxchameau.sandbox.configuration

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import org.springdoc.core.GroupedOpenApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class SwaggerConfiguration {
    @Bean
    fun publicApi(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .packagesToScan("org.vieuxchameau.sandbox")
            .setGroup("springshop-public")
            .pathsToMatch("/**")
            .build()
    }

    @Bean
    fun sandboxOpenAPI(): OpenAPI {
        return OpenAPI()
            .info(
                Info().title("Sandbox API")
                    .description("Sandbox API")
                    .version("0.0.1-SNAPSHOT")
                    .contact(Contact().apply {
                        name = "VieuxChameau"
                        email = "VieuxChameau@users.noreply.github.com"
                        url = "http://vieuxchameau.github.io/"
                    })
            )
    }
}