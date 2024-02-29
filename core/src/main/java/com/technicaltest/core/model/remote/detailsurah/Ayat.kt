package com.technicaltest.core.model.remote.detailsurah

data class Ayat(
    val audio: Audio,
    val nomorAyat: Int,
    val teksArab: String,
    val teksIndonesia: String,
    val teksLatin: String
)