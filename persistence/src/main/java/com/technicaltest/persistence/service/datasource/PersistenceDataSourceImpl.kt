package com.technicaltest.persistence.service.datasource

import com.technicaltest.core.model.local.AyatData
import com.technicaltest.core.model.remote.daftarsurah.Data
import com.technicaltest.core.model.remote.detailsurah.Ayat
import com.technicaltest.persistence.AppDatabase
import com.technicaltest.persistence.entity.AyatEntity
import javax.inject.Inject

class PersistenceDataSourceImpl @Inject constructor(
    private val appDatabase: AppDatabase
) : PersistenceDataSource {

    override suspend fun insertAyat(surah: Data, ayat: Ayat): Long {
        return appDatabase.appDao().insertAyat(
            AyatEntity.setEntity(surah, ayat)
        )
    }

    override suspend fun getAyatList(): List<AyatData> {
        return appDatabase.appDao().getAyatList()
    }

    override suspend fun deleteAyat(ayatData: AyatData): Int {
        return appDatabase.appDao().deleteAyat(
            AyatEntity.setEntity(ayatData)
        )
    }
}