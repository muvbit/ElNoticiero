package com.muvbit.elnoticiero.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "news")
data class News(
    @PrimaryKey(autoGenerate = true)
    val id:Long?=null,
    @ColumnInfo(name = "idNews")
    val idNews:String?=null,
    @ColumnInfo(name = "title")
    val title:String?=null,
    @ColumnInfo(name = "summary")
    val summary:String?=null,
    @ColumnInfo(name = "text")
    val text: String?=null,
    @ColumnInfo(name = "authors")
    val authors:String?=null,
    @ColumnInfo(name = "category")
    val category:String?=null,
    @ColumnInfo(name = "publishedAt")
    val publishedAt:String?=null,
    @ColumnInfo(name = "urlImage")
    val urlImage:String?=null,
    @ColumnInfo(name = "url")
    val url:String?=null): Serializable {

}