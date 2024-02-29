package com.technicaltest.core.service.datasource

import com.technicaltest.core.model.remote.daftarsurah.DaftarSurah
import com.technicaltest.core.model.remote.detailsurah.DetailSurah
import com.technicaltest.core.model.remote.detailtafsir.DetailTafsir
import retrofit2.Response

interface DataSource {

    suspend fun getDaftarSurah(): Response<DaftarSurah>

    suspend fun getDetailSurah(nomor: Int): Response<DetailSurah>

    suspend fun getDetailTafsir(nomor: Int): Response<DetailTafsir>
}