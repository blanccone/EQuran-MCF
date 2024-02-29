package com.technicaltest.core.model.remote.detailsurah

data class Data(
    val arti: String,
    val audioFull: AudioFull,
    val ayat: List<Ayat>?,
    val deskripsi: String,
    val jumlahAyat: Int,
    val nama: String,
    val namaLatin: String,
    val nomor: Int,
    val suratSebelumnya: SuratSebelumnya,
    val suratSelanjutnya: SuratSelanjutnya,
    val tempatTurun: String
)