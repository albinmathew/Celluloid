package me.albinmathew.celluloid.data.local.converter

import androidx.room.TypeConverter

class ListIntConverter {
    @TypeConverter
    fun fromListInt(list: List<Int>?): String? {
        return list?.joinToString(",")
    }

    @TypeConverter
    fun toListInt(string: String?): List<Int>? {
        return string?.split(",")?.mapNotNull { it.toIntOrNull() }
    }
}
