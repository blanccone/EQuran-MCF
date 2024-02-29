package com.technicaltest.core.service.api

import com.technicaltest.core.model.remote.daftarsurah.DaftarSurah
import com.technicaltest.core.model.remote.detailsurah.DetailSurah
import com.technicaltest.core.model.remote.detailtafsir.DetailTafsir
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("surat/")
    suspend fun getDaftarSurah(): Response<DaftarSurah>

    @GET("surat/{nomor}")
    suspend fun getDetailSurah(@Path("nomor") nomor: Int): Response<DetailSurah>

    @GET("tafsir/{nomor}")
    suspend fun getDetailTafsir(@Path("nomor") nomor: Int): Response<DetailTafsir>
}