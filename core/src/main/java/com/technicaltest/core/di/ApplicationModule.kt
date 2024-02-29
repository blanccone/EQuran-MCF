package com.technicaltest.core.di

import android.app.Application
import android.content.Context
import com.technicaltest.core.service.api.ApiService
import com.technicaltest.core.service.datasource.DataSource
import com.technicaltest.core.service.datasource.DataSourceImpl
import com.technicaltest.core.service.repository.Repository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {

    @Provides
    @Singleton
    fun provideContext(application: Application): Context = application

    @Provides
    @Singleton
    fun provideDataSource(apiService: ApiService): DataSource = DataSourceImpl(apiService)

    @Provides
    @Singleton
    fun provideRepository(dataSource: DataSource) = Repository(dataSource)
}