package com.blanccone.mimecloud.util

import java.security.MessageDigest

object Utils {

    fun generateUniqueId(inputString: String): Long {
        // Menggunakan SHA-256 sebagai contoh, bisa diganti dengan algoritma lain jika diinginkan
        val md = MessageDigest.getInstance("SHA-256")
        val hashedBytes = md.digest(inputString.toByteArray())

        // Mengambil 8 byte pertama dari hasil hash sebagai long
        val uniqueId = java.nio.ByteBuffer.wrap(hashedBytes, 0, 8).long

        return uniqueId
    }
}