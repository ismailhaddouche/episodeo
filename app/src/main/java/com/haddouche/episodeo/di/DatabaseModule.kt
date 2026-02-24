package com.haddouche.episodeo.di

import android.content.Context
import androidx.room.Room
import com.haddouche.episodeo.data.local.AppDatabase
import com.haddouche.episodeo.data.local.SeriesDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "episodeo_db"
        ).build()
    }

    @Provides
    fun provideSeriesDao(database: AppDatabase): SeriesDao {
        return database.seriesDao()
    }
}
