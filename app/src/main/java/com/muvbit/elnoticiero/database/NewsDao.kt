package com.muvbit.elnoticiero.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.muvbit.elnoticiero.model.News
import kotlinx.coroutines.flow.Flow

@Dao
interface NewsDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(favoriteNews: News)

    @Delete
    suspend fun delete(favoriteNews: News)

    @Query("SELECT * FROM news")
    suspend fun getAllFavoriteNews(): List<News>

    @Query("SELECT * FROM news WHERE idNews = :idNews")
    suspend fun getFavoriteNewsByIdNews(idNews: String?): News?

    @Query("SELECT * FROM news WHERE title = :title")
    suspend fun getFavoriteNewsByTitle(title: String): News?

    @Query("DELETE FROM news")
    suspend fun deleteAllFavoriteNews()

}