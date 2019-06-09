package org.vieuxchameau.sandbox.security

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter


class JwtUserAuthenticationConverter : DefaultUserAuthenticationConverter() {

    override fun extractAuthentication(map: Map<String, *>): Authentication {
        val authentication = super.extractAuthentication(map)
        val user = JwtUser.fromJwtMap(map)
        return UsernamePasswordAuthenticationToken(user, authentication.credentials, authentication.authorities)
    }

}

data class JwtUser(val id: String, val userName: String, val masqueraderId: String?) {
    companion object {
        fun fromJwtMap(map: Map<String, *>) =
            JwtUser(
                map["user_id"].toString(),
                map["user_name"].toString(),
                map["masquerader_id"]?.toString()
            )
    }
}