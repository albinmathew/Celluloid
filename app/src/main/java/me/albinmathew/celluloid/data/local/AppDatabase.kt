package me.albinmathew.celluloid.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import me.albinmathew.celluloid.data.local.converter.ListIntConverter
import me.albinmathew.celluloid.data.local.dao.MovieDao
import me.albinmathew.celluloid.data.local.entity.MovieEntity

@Database(entities = [MovieEntity::class], version = 1, exportSchema = false)
@TypeConverters(ListIntConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
}
