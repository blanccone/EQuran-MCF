package com.technicaltest.core.model.remote.detailtafsir

data class Data(
    val arti: String,
    val audioFull: AudioFull,
    val deskripsi: String,
    val jumlahAyat: Int,
    val nama: String,
    val namaLatin: String,
    val nomor: Int,
    val suratSebelumnya: SuratSebelumnya,
    val suratSelanjutnya: SuratSelanjutnya,
    val tafsir: List<Tafsir>,
    val tempatTurun: String
)