package com.haddouche.episodeo.data.local

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromListInt(list: List<Int>?): String {
        return list?.joinToString(",") ?: ""
    }

    @TypeConverter
    fun toListInt(data: String?): List<Int> {
        if (data.isNullOrEmpty()) return emptyList()
        return data.split(",").mapNotNull { it.toIntOrNull() }
    }
}
