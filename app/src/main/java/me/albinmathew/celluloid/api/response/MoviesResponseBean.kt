package me.albinmathew.celluloid.api.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class MoviesResponseBean(
    @SerializedName("poster_path")
    var posterPath: String? = null,
    @SerializedName("adult")
    var adult: Boolean = false,
    @SerializedName("overview")
    var overview: String? = null,
    @SerializedName("release_date")
    var releaseDate: String? = null,
    @SerializedName("id")
    var id: Long = 0,
    @SerializedName("original_title")
    var originalTitle: String? = null,
    @SerializedName("original_language")
    var originalLanguage: String? = null,
    @SerializedName("title")
    var title: String? = null,
    @SerializedName("backdrop_path")
    var backdropPath: String? = null,
    @SerializedName("popularity")
    var popularity: Double = 0.0,
    @SerializedName("vote_count")
    var voteCount: Long = 0,
    @SerializedName("video")
    var video: Boolean = false,
    @SerializedName("vote_average")
    var voteAverage: Double = 0.0,
    @SerializedName("genre_ids")
    var genreIds: List<Int>? = null // Changed from IntArray to List<Int> for easier use
) : Parcelable
