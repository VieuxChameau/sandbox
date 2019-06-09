package org.vieuxchameau.sandbox.security

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.springframework.security.core.authority.SimpleGrantedAuthority

class JwtUserAuthenticationConverterTest {
    @Test
    fun `should extract authentication`() {
        val converter = JwtUserAuthenticationConverter()

        val claims = mapOf(
            "user_id" to 42,
            "user_name" to "VieuxChameau",
            "authorities" to listOf("ROLE_ADMIN")
        )

        val authentication = converter.extractAuthentication(claims)

        val expectedJwtUser = JwtUser("42", "VieuxChameau", null)
        assertThat(authentication.principal).isEqualTo(expectedJwtUser)
        assertThat(authentication.authorities).isEqualTo(listOf(SimpleGrantedAuthority("ROLE_ADMIN")))
    }
}