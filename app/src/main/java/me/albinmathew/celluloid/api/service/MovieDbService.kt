package me.albinmathew.celluloid.api.service

import me.albinmathew.celluloid.api.base.BaseMovieBean
import me.albinmathew.celluloid.api.base.BaseReviewBean
import me.albinmathew.celluloid.api.base.BaseVideoBean
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieDbService {

    @GET("3/discover/movie")
    suspend fun getMoviesList(
        @Query("sort_by") sortBy: String,
        @Query("api_key") apiKey: String,
        @Query("page") page: Int,
        @Query("include_adult") includeAdult: Boolean
    ): BaseMovieBean

    // The getGenreList method in the original Java interface did not have a return type or a callback,
    // implying it might have been used with synchronous Retrofit 1.x or its response was ignored.
    // For Retrofit 2 with coroutines, a response type is needed.
    // Assuming it was intended to fetch a list of genres, a proper response type would be needed.
    // For now, I'll comment it out as its usage is unclear without a response type or further context.
    // If it's needed, a data class for Genre/GenreList would need to be created/used.
    // @GET("genre/movie/list")
    // suspend fun getGenreList(@Query("api_key") apiKey: String): GenreListResponse // Example, GenreListResponse would need to be defined

    @GET("3/movie/{id}/videos")
    suspend fun getVideosList(
        @Path("id") movieId: Long,
        @Query("api_key") apiKey: String
    ): BaseVideoBean

    @GET("3/movie/{id}/reviews")
    suspend fun getReviewsList( // This was the original name
        @Path("id") movieId: Long,
        @Query("api_key") apiKey: String,
        @Query("page") page: Int
    ): BaseReviewBean

    // New methods as per current subtask
    @GET("3/movie/{id}/reviews") // Path is the same as getReviewsList, query params differ slightly based on original service
    suspend fun getMovieReviews(
        @Path("id") movieId: Long,
        @Query("api_key") apiKey: String
        // Omitting page for simplicity, API usually defaults to page 1
    ): BaseReviewBean // Assuming this should return BaseReviewBean as per original structure for reviews

    @GET("3/movie/{id}/videos") // Path is the same as original getVideosList
    suspend fun getMovieVideos(
        @Path("id") movieId: Long,
        @Query("api_key") apiKey: String
    ): BaseVideoBean
}
