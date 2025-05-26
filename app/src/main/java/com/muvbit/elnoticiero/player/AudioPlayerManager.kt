package com.muvbit.elnoticiero.player

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer

object AudioPlayerManager {
    private var player: ExoPlayer? = null
    private var currentPlayingUrl: String? = null

    @OptIn(UnstableApi::class)
    fun play(context: Context, url: String, title: String) {
        // Si ya está reproduciendo esta URL, no hacer nada
        if (currentPlayingUrl == url) return

        // Detener reproducción actual si hay una
        stop()

        // Crear nuevo reproductor
        player = ExoPlayer.Builder(context)
            .build()
            .apply {
                val dataSourceFactory = DefaultHttpDataSource.Factory()
                    .setUserAgent("Mozilla/5.0 (Linux; Android 13; Móvil) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Mobile Safari/537.36")

                val mediaItem = MediaItem.fromUri(url)
                setMediaItem(mediaItem)
                prepare()
                play()

                currentPlayingUrl = url
            }
    }

    fun stop() {
        player?.let {
            it.stop()
            it.release()
        }
        player = null
        currentPlayingUrl = null
    }

    fun isPlaying(url: String): Boolean {
        return currentPlayingUrl == url && player?.isPlaying == true
    }

    fun getCurrentPlayer(): ExoPlayer? = player
}