package org.vieuxchameau.sandbox.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Header
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.impl.DefaultClaims
import java.security.Key
import java.security.KeyStore
import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.*

const val KEYSTORE_FORMAT = "JKS"
const val KEYSTORE_PASSWORD = "SandboxSecurityFirst"
const val KEYSTORE_CLASSPATH_LOCATION = "/key/sandboxJwtKeystore.jks"
const val ALIAS = "sandboxJwtKey"
const val KEY_PASSWORD = "SandboxSecurityFirst"
const val ACCESS_TOKEN_VALIDITY_IN_SECONDS: Long = 60 * 5 // 5 minutes

class JWTTokenGenerator {
    fun getJwtToken(userName: String = "VieuxChameau", roles: List<String> = listOf("ROLE_SUPERADMIN")): String {
        val signingKey = getJwtSigningKey()
        return Jwts.builder()
            .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
            .setClaims(getClaims(userName, roles))
            .signWith(SignatureAlgorithm.RS256, signingKey)
            .compact()
    }

    private fun getClaims(userName: String, roles: List<String>): Claims {
        return DefaultClaims(
            mapOf(
                "user_id" to UUID.randomUUID().toString(),
                "user_name" to userName,
                "authorities" to roles
            )
        )
            .setId(UUID.randomUUID().toString())
            .setIssuedAt(Date())
            .setIssuer("VieuxChameau")
            .setExpiration(Timestamp.valueOf(LocalDateTime.now().plusSeconds(ACCESS_TOKEN_VALIDITY_IN_SECONDS)))
            .setSubject(userName)
    }

    private fun getJwtSigningKey(): Key {
        val keystore = KeyStore.getInstance(KEYSTORE_FORMAT)
        val resource = javaClass.getResourceAsStream(KEYSTORE_CLASSPATH_LOCATION)
        keystore.load(resource, KEYSTORE_PASSWORD.toCharArray())
        return keystore.getKey(ALIAS, KEY_PASSWORD.toCharArray())
    }
}