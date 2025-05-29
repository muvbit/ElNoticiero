package com.muvbit.elnoticiero.fragments.tv

import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.OptIn
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.VideoSize
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.ui.AspectRatioFrameLayout
import com.muvbit.elnoticiero.R
import com.muvbit.elnoticiero.activities.MainActivity
import com.muvbit.elnoticiero.databinding.FragmentTvPlayerBinding
import com.muvbit.elnoticiero.player.AudioPlayerManager

class TvPlayerFragment : Fragment() {

    private var _binding: FragmentTvPlayerBinding? = null
    // Usamos binding seguro que puede ser null cuando la vista se destruye
    private val binding get() = _binding!!

    private var player: ExoPlayer? = null
    private var canalUrl: String? = null
    private var canalNombre: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val args = TvPlayerFragmentArgs.fromBundle(it)
            val autoplayer = AudioPlayerManager
            canalUrl = args.url
            canalNombre = args.nombre
            autoplayer.stop()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTvPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    private var isFullscreen = false
    private var originalOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED

    @OptIn(UnstableApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mainActivity = requireActivity() as MainActivity
        val mainActivityBinding = mainActivity.binding
        mainActivityBinding.bottomNav.menu.clear()
        mainActivityBinding.bottomAppBar.visibility = View.GONE
        mainActivityBinding.drawerToggle.visibility = View.GONE

        // Guardar la orientación original
        originalOrientation = requireActivity().requestedOrientation


        canalUrl?.let { url ->
            val httpDataSourceFactory = DefaultHttpDataSource.Factory()
                .setUserAgent("Mozilla/5.0 (Linux; Android 13; Móvil) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Mobile Safari/537.36")
                .setDefaultRequestProperties(
                    mapOf(
                        "Referer" to "https://www.muvment.com",
                        "Origin" to "https://www.muvment.com"
                    )
                )

            val mediaSourceFactory = HlsMediaSource.Factory(httpDataSourceFactory)

            player = ExoPlayer.Builder(requireContext())
                .setMediaSourceFactory(mediaSourceFactory)
                .build().also { exoPlayer ->
                    binding.playerView.player = exoPlayer
                    binding.playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM

                    // Listener para detectar las dimensiones del video
                    exoPlayer.addListener(object : Player.Listener {
                        override fun onVideoSizeChanged(videoSize: VideoSize) {
                            val width = videoSize.width
                            val height = videoSize.height
                            if (width > 0 && height > 0) {
                                adjustPlayerViewAspectRatio(width.toFloat(), height.toFloat())
                            }
                        }
                    })

                    val mediaItem = MediaItem.Builder()
                        .setUri(Uri.parse(url))
                        .setLiveConfiguration(
                            MediaItem.LiveConfiguration.Builder()
                                .setMaxPlaybackSpeed(1.02f)
                                .build()
                        )
                        .build()

                    exoPlayer.setMediaItem(mediaItem)
                    exoPlayer.prepare()
                    exoPlayer.play()
                }
        }
    }

    private fun adjustPlayerViewAspectRatio(videoWidth: Float, videoHeight: Float) {
        val view = binding.playerView
        val context = requireContext()

        // Obtener dimensiones de la pantalla
        val displayMetrics = context.resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels.toFloat()
        val screenHeight = displayMetrics.heightPixels.toFloat()

        // Calcular relaciones de aspecto
        val videoAspectRatio = videoWidth / videoHeight
        val screenAspectRatio = screenWidth / screenHeight

        if (videoAspectRatio > screenAspectRatio) {
            // Video más ancho que la pantalla - ajustar altura
            val newHeight = screenWidth / videoAspectRatio
            view.layoutParams.height = newHeight.toInt()
            view.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        } else {
            // Video más alto que la pantalla - ajustar ancho
            val newWidth = screenHeight * videoAspectRatio
            view.layoutParams.width = newWidth.toInt()
            view.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        }

        view.requestLayout()
    }


    override fun onResume() {
        super.onResume()
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

    }

    override fun onPause() {
        super.onPause()
        // Restaurar orientación original
        requireActivity().requestedOrientation = originalOrientation
    }

    override fun onStop() {
        super.onStop()
        player?.release()
        player = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Limpiar binding para evitar memory leaks
        _binding = null
    }
}
