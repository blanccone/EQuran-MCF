package com.technicaltest.core.service.datasource

import com.technicaltest.core.model.remote.daftarsurah.DaftarSurah
import com.technicaltest.core.model.remote.detailsurah.DetailSurah
import com.technicaltest.core.model.remote.detailtafsir.DetailTafsir
import com.technicaltest.core.service.api.ApiService
import retrofit2.Response
import javax.inject.Inject

class DataSourceImpl @Inject constructor(private val api: ApiService): DataSource {

    override suspend fun getDaftarSurah(): Response<DaftarSurah> {
        return api.getDaftarSurah()
    }

    override suspend fun getDetailSurah(nomor: Int): Response<DetailSurah> {
        return api.getDetailSurah(nomor)
    }

    override suspend fun getDetailTafsir(nomor: Int): Response<DetailTafsir> {
        return api.getDetailTafsir(nomor)
    }
}