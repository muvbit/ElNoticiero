package com.muvbit.elnoticiero.model

data class ChannelRadio(
    val id: String,
    val nombre: String,
    val url: String,
    val logo: String,
    val categoria: String = "General",
    val esFavorita: Boolean = false
)