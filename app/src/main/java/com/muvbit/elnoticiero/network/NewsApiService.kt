package com.muvbit.elnoticiero.network

import com.muvbit.elnoticiero.model.NewsApiResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface NewsApiService {
    @Headers("apikey: 2jqS5i3pmgIN8hAI1AfGQx6dkJ8sIlVJ")
    @GET("search-news")
    suspend fun getNews(
        @Query("text") text: String?=null,
        @Query("source-countries") sourceCountries: String?=null,
        @Query("sort-direction") sortDirection: String?=null,
        @Query("sort") sort: String?=null,
        @Query("offset") offset: String?=null,
        @Query("number") number: String?=null,
        @Query("news-sources") newsSources: String?=null,
        @Query("min-sentiment") minSentiment: String?=null,
        @Query("max-sentiment") maxSentiment: String?=null,
        @Query("location-filter") locationFilter: String?=null,
        @Query("latest-publish-date") latestPublishDate: String?=null,
        @Query("language") language: String?=null,
        @Query("entities") entities: String?=null,
        @Query("earliest-publish-date") earliestPublishDate: String?=null,
        @Query("authors") authors: String?=null
    ): Response<NewsApiResponse>
}