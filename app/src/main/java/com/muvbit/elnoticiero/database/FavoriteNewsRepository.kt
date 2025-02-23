package com.muvbit.elnoticiero.database

import com.muvbit.elnoticiero.model.FavoriteNews
import kotlinx.coroutines.flow.Flow

class FavoriteNewsRepository(private val favoriteNewsDao: FavoriteNewsDao) {

    suspend fun insert(favoriteNews: FavoriteNews) {
        favoriteNewsDao.insert(favoriteNews)
    }

    suspend fun delete(favoriteNews: FavoriteNews) {
        favoriteNewsDao.delete(favoriteNews)
    }

    fun getAllFavoriteNews(): Flow<List<FavoriteNews>> {
        return favoriteNewsDao.getAllFavoriteNews()
    }

    suspend fun getFavoriteNewsByIdNews(idNews: String?): FavoriteNews? {
        return favoriteNewsDao.getFavoriteNewsByIdNews(idNews)
    }

    suspend fun getFavoriteNewsByTitle(title: String): FavoriteNews? {
        return favoriteNewsDao.getFavoriteNewsByTitle(title)
    }
    suspend fun deleteAllFavoriteNews() {
        favoriteNewsDao.deleteAllFavoriteNews()
    }

}