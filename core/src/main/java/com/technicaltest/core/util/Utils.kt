package com.blanccone.mimecloud.util

import android.util.Base64
import com.technicaltest.equranmcf.BuildConfig
import java.io.UnsupportedEncodingException
import java.nio.charset.StandardCharsets
import java.security.MessageDigest

object Utils {
    val apiURL: String
        get() = decodeBase64(BuildConfig.BASE_URL)

    fun decodeBase64(encoded: String): String {
        val dataDec = Base64.decode(encoded, Base64.DEFAULT)
        var decodedString = ""
        try {
            decodedString = String(dataDec, StandardCharsets.UTF_8)
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        } finally {
            return decodedString
        }
    }

    fun generateUniqueId(inputString: String): Long {
        // Menggunakan SHA-256 sebagai contoh, bisa diganti dengan algoritma lain jika diinginkan
        val md = MessageDigest.getInstance("SHA-256")
        val hashedBytes = md.digest(inputString.toByteArray())

        // Mengambil 8 byte pertama dari hasil hash sebagai long
        val uniqueId = java.nio.ByteBuffer.wrap(hashedBytes, 0, 8).long

        return uniqueId
    }
}