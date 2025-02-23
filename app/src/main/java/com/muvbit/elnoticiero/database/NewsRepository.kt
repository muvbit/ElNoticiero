package com.muvbit.elnoticiero.database

import com.muvbit.elnoticiero.model.News
import kotlinx.coroutines.flow.Flow

class NewsRepository(private val newsDao: NewsDao) {

    suspend fun insert(favoriteNews: News) {
        newsDao.insert(favoriteNews)
    }

    suspend fun delete(favoriteNews: News) {
        newsDao.delete(favoriteNews)
    }

   suspend fun getAllFavoriteNews(): List<News> {
        return newsDao.getAllFavoriteNews()
    }

    suspend fun getFavoriteNewsByIdNews(idNews: String?): News? {
        return newsDao.getFavoriteNewsByIdNews(idNews)
    }

    suspend fun getFavoriteNewsByTitle(title: String): News? {
        return newsDao.getFavoriteNewsByTitle(title)
    }
    suspend fun deleteAllFavoriteNews() {
        newsDao.deleteAllFavoriteNews()
    }

}