package com.haddouche.episodeo.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_series")
data class UserSeriesEntity(
    @PrimaryKey val seriesId: Int,
    val seriesStatus: String,
    val seriesRating: Int?,
    val userId: String 
)

@Entity(tableName = "user_lists")
data class UserListEntity(
    @PrimaryKey val id: String,
    val name: String,
    val ownerId: String,
    val isPublic: Boolean,
    val seriesIds: List<Int>,
    val userId: String 
)

@Entity(tableName = "followed_lists")
data class FollowedListEntity(
    @PrimaryKey val listId: String,
    val ownerId: String,
    val listName: String,
    val userId: String 
)

@Entity(tableName = "cached_series")
data class CachedSeriesEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val posterPath: String?,
    val synopsis: String,
    val releaseDate: String?
)
