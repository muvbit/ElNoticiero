package com.muvbit.elnoticiero.model

import com.google.gson.annotations.SerializedName

data class NewsApiResponse(
    @SerializedName("news")
    val data: List<NewsApiData> = emptyList()
)

data class NewsApiData(
    @SerializedName("title")
    val title: String = "",
    @SerializedName("summary")
    val summary: String = "",
    @SerializedName("text")
    val text: String = "",
    @SerializedName("author")
    val author: String = "",
    @SerializedName("published_date")
    val publishedAt: String = "",
    @SerializedName("url")
    val url: String = "",
    @SerializedName("category")
    val category: String = "",
    @SerializedName("image")
    val imageUrl: String = ""
)