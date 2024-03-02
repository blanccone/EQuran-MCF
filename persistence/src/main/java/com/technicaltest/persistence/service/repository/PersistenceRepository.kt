package com.technicaltest.persistence.service.repository

import com.technicaltest.core.model.local.AyatData
import com.technicaltest.core.model.remote.daftarsurah.Data
import com.technicaltest.core.model.remote.detailsurah.Ayat
import com.technicaltest.core.service.util.DatabaseBoundSource
import com.technicaltest.core.service.util.Resource
import com.technicaltest.persistence.service.datasource.PersistenceDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class PersistenceRepository @Inject constructor(
    private val dataSource: PersistenceDataSource
) {

    fun insertAyat(surah: Data, ayat: Ayat) : Flow<Resource<Long>> =
    object : DatabaseBoundSource<Long, Long>(QUERY_INSERT_SINGLE) {
        override suspend fun fetchFromLocal(): Long {
            return dataSource.insertAyat(surah, ayat)
        }

        override suspend fun postProcess(originalData: Long): Long {
            return originalData
        }
    }.asFlow().flowOn(Dispatchers.IO)

    fun getAyatList() : Flow<Resource<List<AyatData>>> =
        object : DatabaseBoundSource<List<AyatData>, List<AyatData>>(QUERY_SELECT_MULTIPLE) {
            override suspend fun fetchFromLocal(): List<AyatData> {
                return dataSource.getAyatList()
            }

            override suspend fun postProcess(originalData: List<AyatData>): List<AyatData> {
                return originalData
            }
        }.asFlow().flowOn(Dispatchers.IO)

    fun deleteAyat(ayatData: AyatData) : Flow<Resource<Int>> =
        object : DatabaseBoundSource<Int, Int>(QUERY_DELETE) {
            override suspend fun fetchFromLocal(): Int {
                return dataSource.deleteAyat(ayatData)
            }

            override suspend fun postProcess(originalData: Int): Int {
                return originalData
            }
        }.asFlow().flowOn(Dispatchers.IO)
}