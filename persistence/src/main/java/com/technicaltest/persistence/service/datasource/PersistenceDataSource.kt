package com.technicaltest.persistence.service.datasource

import com.technicaltest.core.model.local.AyatData
import com.technicaltest.core.model.remote.daftarsurah.Data
import com.technicaltest.core.model.remote.detailsurah.Ayat

interface PersistenceDataSource {

    suspend fun insertAyat(surah: Data, ayat: Ayat) : Long

    suspend fun getAyatList() : List<AyatData>

    suspend fun deleteAyat(ayatData: AyatData) : Int
}