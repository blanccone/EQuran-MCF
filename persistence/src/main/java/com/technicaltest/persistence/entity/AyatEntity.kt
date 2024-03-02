package com.technicaltest.persistence.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.technicaltest.core.model.local.AyatData
import com.technicaltest.core.model.remote.daftarsurah.Data
import com.technicaltest.core.model.remote.detailsurah.Audio
import com.technicaltest.core.model.remote.detailsurah.Ayat
import com.technicaltest.core.util.Utils.generateUniqueId

@Entity(tableName = "tb_ayat")
internal data class AyatEntity(
    @PrimaryKey
    val id: String,
    val namaLatin: String,
    val tempatTurun: String,
    val arti: String,
    val audio: String,
    val nomorAyat: Int,
    val teksArab: String,
    val teksIndonesia: String,
    val teksLatin: String
) {
    companion object {
        fun setEntity(surah: Data, ayat: Ayat): AyatEntity {
            return AyatEntity(
                id = "${generateUniqueId("${surah.namaLatin}${ayat.nomorAyat}")}",
                namaLatin = surah.namaLatin,
                tempatTurun = surah.tempatTurun,
                arti = surah.arti,
                audio = ayat.audio.audio02,
                nomorAyat = ayat.nomorAyat,
                teksArab = ayat.teksArab,
                teksIndonesia = ayat.teksIndonesia,
                teksLatin = ayat.teksLatin
            )
        }
        fun setEntity(ayatData: AyatData): AyatEntity {
            return AyatEntity(
                id = "${generateUniqueId("${ayatData.namaLatin}${ayatData.nomorAyat}")}",
                namaLatin = ayatData.namaLatin,
                tempatTurun = ayatData.tempatTurun,
                arti = ayatData.arti,
                audio = ayatData.audio,
                nomorAyat = ayatData.nomorAyat,
                teksArab = ayatData.teksArab,
                teksIndonesia = ayatData.teksIndonesia,
                teksLatin = ayatData.teksLatin
            )
        }
    }

    fun getEntity() = AyatData(
        namaLatin,
        tempatTurun,
        arti,
        audio,
        nomorAyat,
        teksArab,
        teksIndonesia,
        teksLatin
    )
}
