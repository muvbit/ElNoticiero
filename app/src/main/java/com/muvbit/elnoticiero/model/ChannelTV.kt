package com.muvbit.elnoticiero.model

data class ChannelTV(
    val nombre: String,
    val url: String,
    val logo: String,
    val esPrioritario: Boolean = false,
    val epgId: String? = null,
    val quality: String? = null,
    val ambit: String? = null,
    val isFree: Boolean = true // Nuevo campo para canales gratuitos
)