package com.haddouche.episodeo.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.haddouche.episodeo.data.local.entities.*

@Database(entities = [UserSeriesEntity::class, UserListEntity::class, FollowedListEntity::class, CachedSeriesEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun seriesDao(): SeriesDao
}
