package org.vieuxchameau.sandbox.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.service.Contact
import springfox.documentation.service.StringVendorExtension
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2

@Configuration
@EnableSwagger2
class SwaggerConfiguration {
    @Bean
    fun api(environment: Environment) = Docket(DocumentationType.SWAGGER_2)
            .apiInfo(apiInfo())
            .select()
            .apis(RequestHandlerSelectors.basePackage("org.vieuxchameau.sandbox"))
            .paths(PathSelectors.any())
            .build()

    private fun apiInfo() = ApiInfo(
            "Sandbox API",
            "Sandbox API",
            "0.0.1-SNAPSHOT",
            "Terms of service",
            Contact("VieuxChameau", "http://vieuxchameau.github.io/", "VieuxChameau@users.noreply.github.com"),
            "License of API: ",
            "API license URL: ",
            listOf(StringVendorExtension("VieuxChameau", "VieuxChameau")))
}