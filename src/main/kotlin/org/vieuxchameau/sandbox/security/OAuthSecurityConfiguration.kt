package org.vieuxchameau.sandbox.security

import org.springframework.boot.autoconfigure.security.oauth2.resource.JwtAccessTokenConverterConfigurer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter

// https://docs.spring.io/spring-security-oauth2-boot/docs/current/reference/html/boot-features-security-oauth2-resource-server.html
@EnableResourceServer
@EnableWebSecurity(debug = true)
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Configuration
class OAuthSecurityConfiguration : ResourceServerConfigurerAdapter() {

    override fun configure(http: HttpSecurity) {
        // @formatter:off
        http
            .logout()
            .disable()
            .csrf()
            .disable() // We don't need CSRF for JWT based authentication
            .authorizeRequests()
            .antMatchers("/hellosecured/**").fullyAuthenticated()
            .anyRequest().permitAll()
            .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        // @formatter:on
    }

    @Bean
    fun jwtUserAwareAccessTokenConverterConfigurer(): JwtAccessTokenConverterConfigurer {
        return JwtAccessTokenConverterConfigurer { converter: JwtAccessTokenConverter ->
            converter.accessTokenConverter = DefaultAccessTokenConverter().apply {
                setUserTokenConverter(JwtUserAuthenticationConverter())
            }
        }
    }
}