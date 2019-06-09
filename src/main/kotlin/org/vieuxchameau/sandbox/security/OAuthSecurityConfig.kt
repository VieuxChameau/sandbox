package org.vieuxchameau.sandbox.security

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ResourceLoader
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore
import java.io.InputStream

@EnableResourceServer
@EnableWebSecurity//(debug = true)
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Configuration
class OAuthSecurityConfig(
    @Value("\${sandbox.oauth.jwt.verifierKey}") val jwtVerifierKeyPath: String,
    val resourceLoader: ResourceLoader
) : ResourceServerConfigurerAdapter() {
    override fun configure(resources: ResourceServerSecurityConfigurer) {
        // @formatter:off
        resources
                .tokenStore(tokenStore())
                .stateless(true)
        // @formatter:on
    }

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
    fun tokenStore() = JwtTokenStore(jwtAccessTokenConverter())

    @Bean
    fun jwtAccessTokenConverter(): JwtAccessTokenConverter {
        val converter = JwtAccessTokenConverter()
        val defaultAccessTokenConverter = DefaultAccessTokenConverter()
        defaultAccessTokenConverter.setUserTokenConverter(JwtUserAuthenticationConverter())
        converter.accessTokenConverter = defaultAccessTokenConverter

        val inputStream: InputStream = resourceLoader.getResource(jwtVerifierKeyPath).inputStream
        val publicKey = inputStream.bufferedReader().use { it.readText() }
        converter.setVerifierKey(publicKey)
        return converter
    }
}