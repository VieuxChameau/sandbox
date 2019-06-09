package org.vieuxchameau.sandbox.security

import io.jsonwebtoken.Jwts
import org.assertj.core.api.Assertions.*

import org.junit.Test

class JWTTokenGeneratorTest {
    @Test
    fun `should generate a jwt token`() {
        val jwtTokenGenerator = JWTTokenGenerator()

        val jwtToken = jwtTokenGenerator.getJwtToken()

        println(jwtToken)
        val isTokenSigned = Jwts.parser().isSigned(jwtToken)
        assertThat(isTokenSigned).isTrue()
    }
}