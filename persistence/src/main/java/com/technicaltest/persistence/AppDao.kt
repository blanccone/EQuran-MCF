package com.technicaltest.persistence

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.technicaltest.core.model.local.AyatData
import com.technicaltest.core.model.remote.daftarsurah.Data
import com.technicaltest.core.model.remote.detailsurah.Ayat
import com.technicaltest.persistence.entity.AyatEntity

@Dao
internal interface AppDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAyat(ayatEntity: AyatEntity) : Long

    @Transaction
    @Query("SELECT * FROM tb_ayat")
    suspend fun getAyatList() : List<AyatData>

    @Delete
    suspend fun deleteAyat(ayatEntity: AyatEntity) : Int
}