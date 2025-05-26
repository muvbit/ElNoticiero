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
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.hls.HlsMediaSource
import com.muvbit.elnoticiero.activities.MainActivity
import com.muvbit.elnoticiero.databinding.FragmentTvPlayerBinding

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
            canalUrl = args.url
            canalNombre = args.nombre
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTvPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    @OptIn(UnstableApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mainActivity = requireActivity() as MainActivity
        val mainActivityBinding = mainActivity.binding
        // Borramos el menu del bottomNav y le agregamos el personalizado para este fragment
        mainActivityBinding.bottomNav.menu.clear()
        mainActivityBinding.bottomNav.visibility=View.GONE
        mainActivityBinding.drawerToggle.visibility=View.GONE

        canalUrl?.let { url ->
            val httpDataSourceFactory = DefaultHttpDataSource.Factory()
                .setUserAgent("Mozilla/5.0 (Linux; Android 13; Móvil) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Mobile Safari/537.36")
                .setDefaultRequestProperties(
                    mapOf(
                        "Referer" to "https://tu-web-o-app.com",
                        "Origin" to "https://tu-web-o-app.com"
                    )
                )

            val mediaSourceFactory = HlsMediaSource.Factory(httpDataSourceFactory)

            player = ExoPlayer.Builder(requireContext())
                .setMediaSourceFactory(mediaSourceFactory)
                .build().also { exoPlayer ->
                    binding.playerView.player = exoPlayer

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
    override fun onResume() {
        super.onResume()
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

    }

    override fun onPause() {
        super.onPause()
        // Volver a permitir orientación automática cuando se sale del fragmento
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
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
