package com.haddouche.episodeo.data.local

import androidx.room.*
import com.haddouche.episodeo.data.local.entities.*

@Dao
interface SeriesDao {
    // User Series
    @Query("SELECT * FROM user_series WHERE userId = :userId")
    suspend fun getUserSeries(userId: String): List<UserSeriesEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserSeries(series: UserSeriesEntity)
    
    @Query("DELETE FROM user_series WHERE userId = :userId")
    suspend fun clearUserSeries(userId: String)

    // User Lists
    @Query("SELECT * FROM user_lists WHERE userId = :userId")
    suspend fun getUserLists(userId: String): List<UserListEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserList(list: UserListEntity)

    @Query("DELETE FROM user_lists WHERE id = :listId")
    suspend fun deleteUserList(listId: String)
    
    @Query("DELETE FROM user_lists WHERE userId = :userId")
    suspend fun clearUserLists(userId: String)

    // Followed Lists
    @Query("SELECT * FROM followed_lists WHERE userId = :userId")
    suspend fun getFollowedLists(userId: String): List<FollowedListEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFollowedList(list: FollowedListEntity)

    @Query("DELETE FROM followed_lists WHERE listId = :listId")
    suspend fun deleteFollowedList(listId: String)
    
    @Query("DELETE FROM followed_lists WHERE userId = :userId")
    suspend fun clearFollowedLists(userId: String)

    // Cached Series Metadata
    @Query("SELECT * FROM cached_series WHERE id = :id")
    suspend fun getCachedSeries(id: Int): CachedSeriesEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCachedSeries(series: CachedSeriesEntity)
}
