package com.muvbit.elnoticiero.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.muvbit.elnoticiero.model.FavoriteNews
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteNewsDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(favoriteNews: FavoriteNews)

    @Delete
    suspend fun delete(favoriteNews: FavoriteNews)

    @Query("SELECT * FROM favorite_news")
    fun getAllFavoriteNews(): Flow<List<FavoriteNews>>

    @Query("SELECT * FROM favorite_news WHERE idNews = :idNews")
    suspend fun getFavoriteNewsByIdNews(idNews: String?): FavoriteNews?

    @Query("SELECT * FROM favorite_news WHERE title = :title")
    suspend fun getFavoriteNewsByTitle(title: String): FavoriteNews?

    @Query("DELETE FROM favorite_news")
    suspend fun deleteAllFavoriteNews()

}