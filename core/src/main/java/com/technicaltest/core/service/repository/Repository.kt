package com.technicaltest.core.service.repository

import com.technicaltest.core.service.util.NetworkBoundSource
import com.technicaltest.core.service.util.Resource
import com.technicaltest.core.model.remote.daftarsurah.DaftarSurah
import com.technicaltest.core.model.remote.detailsurah.DetailSurah
import com.technicaltest.core.model.remote.detailtafsir.DetailTafsir
import com.technicaltest.core.service.datasource.DataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response
import javax.inject.Inject

class Repository @Inject constructor(private val dataSource: DataSource) {

    fun getDaftarSurah(): Flow<Resource<DaftarSurah>> {
        return object : NetworkBoundSource<DaftarSurah>() {
            override suspend fun fetchFromRemote(): Response<DaftarSurah> {
                return dataSource.getDaftarSurah()
            }
        }.asFlow().flowOn(Dispatchers.IO)
    }

    fun getDetailSurah(nomor: Int): Flow<Resource<DetailSurah>> {
        return object : NetworkBoundSource<DetailSurah>() {
            override suspend fun fetchFromRemote(): Response<DetailSurah> {
                return dataSource.getDetailSurah(nomor)
            }
        }.asFlow().flowOn(Dispatchers.IO)
    }

    fun getDetailTafsir(nomor: Int): Flow<Resource<DetailTafsir>> {
        return object : NetworkBoundSource<DetailTafsir>() {
            override suspend fun fetchFromRemote(): Response<DetailTafsir> {
                return dataSource.getDetailTafsir(nomor)
            }
        }.asFlow().flowOn(Dispatchers.IO)
    }
}