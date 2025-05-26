package com.muvbit.elnoticiero.fragments.radio

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.OptIn
import androidx.fragment.app.Fragment
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import com.muvbit.elnoticiero.R
import com.muvbit.elnoticiero.activities.MainActivity
import com.muvbit.elnoticiero.databinding.FragmentRadioPlayerBinding
import com.muvbit.elnoticiero.player.AudioPlayerManager
import com.muvbit.elnoticiero.services.RadioPlayerService

class RadioPlayerFragment : Fragment() {

    private var _binding: FragmentRadioPlayerBinding? = null
    private val binding get() = _binding!!
    private var player: ExoPlayer? = null
    private var emisoraUrl: String? = null
    private var emisoraNombre: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val args = RadioPlayerFragmentArgs.fromBundle(it)
            emisoraUrl = args.url
            emisoraNombre = args.nombre
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRadioPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mainActivity = requireActivity() as MainActivity
        val mainActivityBinding = mainActivity.binding
        mainActivityBinding.bottomNav.menu.clear()
        mainActivityBinding.bottomNav.visibility = View.GONE
        mainActivityBinding.drawerToggle.visibility = View.GONE

        binding.tvNombreEmisora.text = emisoraNombre

        setupPlayer()
        setupControls()
    }

    @OptIn(UnstableApi::class)
    private fun setupPlayer() {
        emisoraUrl?.let { url ->
            AudioPlayerManager.play(requireContext(), url, emisoraNombre ?: "")

            // Configurar listener para actualizar UI
            AudioPlayerManager.getCurrentPlayer()?.addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    when (playbackState) {
                        Player.STATE_BUFFERING -> {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.btnPlayPause.isEnabled = false
                        }
                        Player.STATE_READY -> {
                            binding.progressBar.visibility = View.GONE
                            binding.btnPlayPause.isEnabled = true
                            updatePlayPauseButton()
                        }
                        Player.STATE_ENDED -> {
                            updatePlayPauseButton()
                        }
                    }
                }
            })

            updatePlayPauseButton()
        }
    }

    private fun updatePlayPauseButton() {
        val isPlaying = AudioPlayerManager.getCurrentPlayer()?.isPlaying ?: false
        binding.btnPlayPause.setImageResource(
            if (isPlaying) R.drawable.ic_media_pause else R.drawable.ic_media_play
        )
    }

    private fun setupControls() {
        binding.btnPlayPause.setOnClickListener {
            AudioPlayerManager.getCurrentPlayer()?.let { player ->
                if (player.isPlaying) {
                    player.pause()
                } else {
                    player.play()
                }
                updatePlayPauseButton()
            }
        }

        binding.btnStop.setOnClickListener {
            AudioPlayerManager.stop()
            updatePlayPauseButton()
        }
    }
    private var isBound = false
    private var radioService: RadioPlayerService? = null
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as RadioPlayerService.LocalBinder
            radioService = binder.getService()
            isBound = true
            emisoraUrl?.let { radioService?.playStream(it) }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = false
            radioService = null
        }
    }

    override fun onStart() {
        super.onStart()
        Intent(requireContext(), RadioPlayerService::class.java).also { intent ->
            requireActivity().bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        if (isBound) {
            requireActivity().unbindService(connection)
            isBound = false
        }
        // No detenemos el servicio para que continúe en segundo plano
    }

    override fun onDestroyView() {
        super.onDestroyView()
        radioService?.stop()
        _binding = null
    }

    override fun onPause() {
        super.onPause()
        // Opcional: Pausar la reproducción cuando la app está en segundo plano
        // player?.pause()
    }


}