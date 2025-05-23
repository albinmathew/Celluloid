package me.albinmathew.celluloid.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import me.albinmathew.celluloid.data.local.entity.MovieEntity

@Dao
interface MovieDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(movie: MovieEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(movies: List<MovieEntity>)

    @Update
    suspend fun update(movie: MovieEntity)

    @Delete
    suspend fun delete(movie: MovieEntity)

    @Query("SELECT * FROM movies WHERE movie_id = :movieId")
    suspend fun getMovieByApiId(movieId: Long): MovieEntity?

    @Query("SELECT * FROM movies WHERE localId = :localId")
    suspend fun getMovieByLocalId(localId: Int): MovieEntity?

    @Query("SELECT * FROM movies ORDER BY popularity DESC")
    fun getAllMoviesSortedByPopularity(): Flow<List<MovieEntity>>

    @Query("SELECT * FROM movies ORDER BY vote_average DESC")
    fun getAllMoviesSortedByVoteAverage(): Flow<List<MovieEntity>>
    
    @Query("SELECT * FROM movies WHERE is_favorite = 1 ORDER BY title ASC")
    fun getFavoriteMovies(): Flow<List<MovieEntity>>

    @Query("SELECT * FROM movies ORDER BY fetched_at DESC")
    fun getAllMovies(): Flow<List<MovieEntity>>
}
