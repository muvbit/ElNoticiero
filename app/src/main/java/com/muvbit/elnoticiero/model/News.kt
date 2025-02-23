package com.muvbit.elnoticiero.model

import java.io.Serializable


data class News(
    val idNews:String?=null,
    val title:String?=null,
    val summary:String?=null,
    val text: String?=null,
    val authors:String?=null,
    val category:String?=null,
    val publishedAt:String?=null,
    val urlImage:String?=null,
    val url:String?=null): Serializable {

}