package com.muvbit.elnoticiero.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_news")
data class FavoriteNews(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "idNews")
    val idNews: String?,
    @ColumnInfo(name = "title")
    val title: String?,
    @ColumnInfo(name = "summary")
    val summary: String?,
    @ColumnInfo(name = "text")
    val text: String?,
    @ColumnInfo(name = "authors")
    val authors: String?,
    @ColumnInfo(name = "category")
    val category: String?,
    @ColumnInfo(name = "date")
    val date: String?,
    @ColumnInfo(name = "urlImage")
    val urlImage: String?
)