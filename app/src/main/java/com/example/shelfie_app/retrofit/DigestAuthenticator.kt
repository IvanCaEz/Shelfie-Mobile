package com.example.shelfie_app.retrofit

import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import org.apache.commons.codec.binary.Hex
import org.apache.commons.codec.digest.DigestUtils
import java.util.Random

class DigestAuthenticator(private val credentials: Credentials) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        val challenge = response.challenges().find { it.scheme == "Digest" } ?: return null
        val realm = challenge.realm
        val nonce = challenge.authParams["nonce"]
        val algorithm = challenge.authParams["algorithm"]
        val qop = challenge.authParams["qop"]
        val nc = "00000001"
        val cnonce = generateCnonce()
        val uri = response.request.url.toUri().path
        val method = response.request.method

        val ha1 = DigestUtils.md5Hex("${credentials.username}:$realm:${credentials.password}")
        val ha2 = DigestUtils.md5Hex("$method:$uri")
        val responseDigest = DigestUtils.md5Hex("$ha1:$nonce:$nc:$cnonce:$qop:$ha2")
        val headerValue = "Digest username=\"${credentials.username}\", realm=\"$realm\", nonce=\"$nonce\", uri=\"$uri\", algorithm=\"$algorithm\", qop=\"$qop\", nc=\"$nc\", cnonce=\"$cnonce\", response=\"$responseDigest\""
        return response.request.newBuilder()
            .header("Authorization", headerValue)
            .build()
    }


    private fun generateCnonce(): String {
        val bytes = ByteArray(8)
        Random().nextBytes(bytes)
        return Hex.encodeHexString(bytes)
    }
}

data class Credentials(val username: String, val password: String)

data class AuthParams(
    var realm: String? = null,
    var nonce: String? = null,
    var opaque: String? = null,
    var qop: String? = null,
    var nonceCount: Int = 1
)