package com.technicaltest.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import com.technicaltest.persistence.entity.AyatEntity

@Database(
    entities = [AyatEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase(){
    internal abstract fun appDao() : AppDao
}