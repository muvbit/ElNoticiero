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
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.annotation.OptIn
import androidx.fragment.app.Fragment
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.muvbit.elnoticiero.R
import com.muvbit.elnoticiero.activities.MainActivity
import com.muvbit.elnoticiero.databinding.FragmentRadioPlayerBinding
import com.muvbit.elnoticiero.player.AudioPlayerManager
import com.muvbit.elnoticiero.services.RadioPlayerService
import java.util.Random

class RadioPlayerFragment : Fragment() {

    private var _binding: FragmentRadioPlayerBinding? = null
    private val binding get() = _binding!!
    private var player: ExoPlayer? = null
    private var emisoraUrl: String? = null
    private var emisoraNombre: String? = null
    private var emisoraLogo: String? = null
    private lateinit var pulseAnimation: Animation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val args = RadioPlayerFragmentArgs.fromBundle(it)
            emisoraUrl = args.url
            emisoraNombre = args.nombre
            emisoraLogo = args.logo
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
        Glide.with(this).apply {
            load(emisoraLogo).into(binding.ivLogoEmisora)
        }

        pulseAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.pulse)

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
                            startLogoAnimation()
                        }
                        Player.STATE_ENDED -> {
                            updatePlayPauseButton()
                            stopLogoAnimation()
                        }
                    }
                }
            })

            updatePlayPauseButton()
        }
    }
    private val random = Random()

    private var isAnimating = false
    private val animationRunnable = object : Runnable {
        override fun run() {
            if (!isAnimating || _binding == null) return

            val scale = 1.0f + random.nextFloat() * 0.2f
            val duration = 300L + random.nextInt(400)

            binding.ivLogoEmisora.animate()
                .scaleX(scale)
                .scaleY(scale)
                .setDuration(duration)
                .withEndAction {
                    if (!isAnimating || _binding == null) return@withEndAction

                    binding.ivLogoEmisora.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(duration)
                        .withEndAction {
                            if (isAnimating && _binding != null &&
                                AudioPlayerManager.getCurrentPlayer()?.isPlaying == true) {
                                binding.ivLogoEmisora.post(this)
                            }
                        }
                }
        }
    }



    private fun stopLogoAnimation() {
        isAnimating = false
        binding.ivLogoEmisora.clearAnimation()
        binding.ivLogoEmisora.animate().cancel()
        binding.ivLogoEmisora.scaleX = 1f
        binding.ivLogoEmisora.scaleY = 1f
    }


    private fun startLogoAnimation() {
        if (_binding == null) return

        isAnimating = true
        binding.ivLogoEmisora.post(animationRunnable)
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

            updateBottomBar()

            // Ejecutar con un pequeño retraso para mejor experiencia de usuario
            binding.root.postDelayed({
                if (!findNavController().popBackStack()) {
                    requireActivity().finish()
                }
            }, 200) // 200ms de retraso
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

    private fun updateBottomBar(){
        val mainActivity = requireActivity() as MainActivity
        val mainActivityBinding = mainActivity.binding
        mainActivityBinding.bottomNav.menu.clear()
        mainActivityBinding.bottomNav.visibility = View.VISIBLE
        mainActivityBinding.drawerToggle.visibility = View.VISIBLE
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
        stopLogoAnimation()
        updateBottomBar()
        super.onDestroyView()
        radioService?.stop()
        _binding = null
    }

    override fun onPause() {
        super.onPause()
        stopLogoAnimation()
        // Opcional: Pausar la reproducción cuando la app está en segundo plano
        // player?.pause()
    }


}