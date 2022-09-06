package com.example.myspotify.util

import java.security.MessageDigest

class HashUtils {
    companion object {
        private const val HASH_ALGORITHM = "SHA-256"
        private const val HASH_FORMAT = "%02x"
    }

    fun sha256(password: String): String {
        return MessageDigest
            .getInstance(HASH_ALGORITHM)
            .digest(password.toByteArray())
            .fold("") { str, it -> str + HASH_FORMAT.format(it) }
    }
}