package com.technicaltest.persistence.di

import android.content.Context
import androidx.room.Room
import com.technicaltest.persistence.AppDatabase
import com.technicaltest.persistence.service.datasource.PersistenceDataSource
import com.technicaltest.persistence.service.datasource.PersistenceDataSourceImpl
import com.technicaltest.persistence.service.repository.PersistenceRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PersistenceModule {

    @Provides
    @Singleton
    fun provideDatabase(context: Context) : AppDatabase = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "EQURAN_MCF"
    ).fallbackToDestructiveMigration().build()

    @Provides
    @Singleton
    fun providePersitenceDataSource(
        appDatabase: AppDatabase
    ): PersistenceDataSource = PersistenceDataSourceImpl(appDatabase)

    @Provides
    @Singleton
    fun providePersistenceRepository(
        dataSource: PersistenceDataSource
    ): PersistenceRepository = PersistenceRepository(dataSource)
}