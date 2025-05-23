package me.albinmathew.celluloid.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import me.albinmathew.celluloid.data.local.converter.ListIntConverter

@Entity(tableName = "movies")
@TypeConverters(ListIntConverter::class)
data class MovieEntity(
    @PrimaryKey(autoGenerate = true)
    var localId: Int = 0,

    @ColumnInfo(name = "movie_id", index = true) // API's movie ID, should be unique
    val movieId: Long,

    @ColumnInfo(name = "title")
    val title: String?,

    @ColumnInfo(name = "poster_path") // Renamed from posterUrl
    val posterPath: String?,

    @ColumnInfo(name = "backdrop_path") // Assuming this should also be a path
    val backdropPath: String?,

    @ColumnInfo(name = "original_title")
    val originalTitle: String?,

    @ColumnInfo(name = "plot")
    val plot: String?,

    @ColumnInfo(name = "rating")
    val rating: Double?,

    @ColumnInfo(name = "release_date")
    val releaseDate: String?,

    @ColumnInfo(name = "genre_ids")
    val genreIds: List<Int>?,

    @ColumnInfo(name = "is_favorite", defaultValue = "0")
    var isFavorite: Boolean = false,

    // Fields for sorting, if needed directly in DB, otherwise can be derived
    // For example, if we fetch popular movies and want to preserve that order from API
    // Or if we want to sort by when it was fetched/added
    @ColumnInfo(name = "popularity")
    val popularity: Double? = null,

    @ColumnInfo(name = "vote_average") // This is the same as rating, but often TMDB has both
    val voteAverage: Double? = null,

    @ColumnInfo(name = "fetched_at", defaultValue = "0") // To order by fetch time or for cache expiry
    var fetchedAt: Long = System.currentTimeMillis()
)
