package com.muvbit.elnoticiero.fragments.radio

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.OptIn
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.muvbit.elnoticiero.R
import com.muvbit.elnoticiero.databinding.FragmentRadioPlayerBinding
import com.muvbit.elnoticiero.player.AudioPlayerManager
import com.muvbit.elnoticiero.services.RadioPlayerService
import java.util.Random

class RadioPlayerFragment : Fragment() {

    private var _binding: FragmentRadioPlayerBinding? = null
    private val binding get() = _binding!!

    private var emisoraUrl: String? = null
    private var emisoraNombre: String? = null
    private var emisoraLogo: String? = null

    private var playerListener: Player.Listener? = null
    private val random = Random()
    private var isAnimating = false

    private val animationRunnable = object : Runnable {
        override fun run() {
            if (!isAnimating || _binding == null || !isAdded) {
                isAnimating = false // Asegura que se detenga si las condiciones no se cumplen
                return
            }

            val scale = 1.0f + random.nextFloat() * 0.2f
            val duration = 300L + random.nextInt(400)

            binding.ivLogoEmisora.animate() // Acceso seguro porque _binding ya fue verificado
                .scaleX(scale)
                .scaleY(scale)
                .setDuration(duration)
                .withEndAction {
                    if (!isAnimating || _binding == null || !isAdded) {
                        isAnimating = false
                        return@withEndAction
                    }

                    binding.ivLogoEmisora.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(duration)
                        .withEndAction {
                            if (isAnimating && _binding != null && isAdded &&
                                AudioPlayerManager.getCurrentPlayer()?.isPlaying == true
                            ) {
                                binding.ivLogoEmisora.post(this)
                            } else {
                                isAnimating = false
                            }
                        }
                }
        }
    }

    // Service connection
    private var isBound = false
    private var radioService: RadioPlayerService? = null
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as RadioPlayerService.LocalBinder
            radioService = binder.getService()
            isBound = true
            Log.d("RadioPlayerFragment", "RadioPlayerService conectado.")
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = false
            radioService = null
            Log.d("RadioPlayerFragment", "RadioPlayerService desconectado.")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val args = RadioPlayerFragmentArgs.fromBundle(it)
            emisoraUrl = args.url
            emisoraNombre = args.nombre
            emisoraLogo = args.logo
        }
        Log.d("RadioPlayerFragment", "onCreate: $emisoraNombre")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRadioPlayerBinding.inflate(inflater, container, false)
        Log.d("RadioPlayerFragment", "onCreateView")

        viewLifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                Log.d(
                    "RadioPlayerFragment",
                    "ViewLifecycleOwner onDestroy: Limpiando Player.Listener y animación."
                )
                AudioPlayerManager.getCurrentPlayer()?.removeListener(playerListener ?: return)
                playerListener = null

                isAnimating = false
                _binding?.ivLogoEmisora?.handler?.removeCallbacks(animationRunnable)
                _binding?.ivLogoEmisora?.clearAnimation() // Limpia animaciones de ViewAnimation
                _binding?.ivLogoEmisora?.animate()
                    ?.cancel() // Limpia animaciones de ViewPropertyAnimator
            }
        })
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("RadioPlayerFragment", "onViewCreated")

        binding.tvNombreEmisora.text = emisoraNombre
        Glide.with(this)
            .load(emisoraLogo)
            .error(R.drawable.ic_radio)
            .into(binding.ivLogoEmisora)

        setupPlayer()
        setupControls()
    }

    @OptIn(UnstableApi::class)
    private fun setupPlayer() {
        Log.d("RadioPlayerFragment", "setupPlayer para: $emisoraUrl")
        emisoraUrl?.let { url ->
            AudioPlayerManager.play(requireContext(), url, emisoraNombre ?: "Radio")

            playerListener = object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (_binding == null || !isAdded) {
                        Log.w(
                            "RadioPlayerFragment",
                            "Player.Listener: Binding null o Fragment no añadido en onPlaybackStateChanged."
                        )
                        return
                    }
                    Log.d("RadioPlayerFragment", "onPlaybackStateChanged: $playbackState")
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
                            binding.progressBar.visibility = View.GONE // Ocultar si finaliza
                            updatePlayPauseButton()
                            stopLogoAnimation()
                        }

                        Player.STATE_IDLE -> {
                            binding.progressBar.visibility = View.GONE
                            binding.btnPlayPause.isEnabled =
                                true
                            updatePlayPauseButton()
                            stopLogoAnimation()
                        }
                    }
                }

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    if (_binding == null || !isAdded) {
                        Log.w(
                            "RadioPlayerFragment",
                            "Player.Listener: Binding null o Fragment no añadido en onIsPlayingChanged."
                        )
                        return
                    }
                    Log.d("RadioPlayerFragment", "onIsPlayingChanged: $isPlaying")
                    updatePlayPauseButton()
                    if (isPlaying) {
                        startLogoAnimation()
                    } else {
                        stopLogoAnimation()
                    }
                }

                // Considera implementar onError para manejar errores de reproducción
                override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                    Log.e("RadioPlayerFragment", "Player Error: ${error.message}", error)
                    if (_binding == null || !isAdded) return
                    // Muestra un mensaje de error, detiene la animación, etc.
                    binding.progressBar.visibility = View.GONE
                    stopLogoAnimation()
                }
            }
            AudioPlayerManager.getCurrentPlayer()?.addListener(playerListener!!)
            updatePlayPauseButton() // Estado inicial del botón
        }
    }

    private fun stopLogoAnimation() {
        if (_binding == null || !isAdded) {
            Log.w("RadioPlayerFragment", "stopLogoAnimation: Binding null o Fragment no añadido.")
            isAnimating = false // Asegura que el estado sea correcto
            return
        }
        Log.d("RadioPlayerFragment", "stopLogoAnimation")
        isAnimating = false
        binding.ivLogoEmisora.handler?.removeCallbacks(animationRunnable)
        binding.ivLogoEmisora.clearAnimation() // Para animaciones de ViewAnimation (aunque aquí usas ViewPropertyAnimator)
        binding.ivLogoEmisora.animate().cancel()   // Para animaciones de ViewPropertyAnimator
        binding.ivLogoEmisora.scaleX = 1f
        binding.ivLogoEmisora.scaleY = 1f
    }

    private fun startLogoAnimation() {
        if (_binding == null || !isAdded) {
            Log.w("RadioPlayerFragment", "startLogoAnimation: Binding null o Fragment no añadido.")
            return
        }
        // Solo anima si está reproduciendo y no ya animando
        if (AudioPlayerManager.getCurrentPlayer()?.isPlaying == true && !isAnimating) {
            Log.d("RadioPlayerFragment", "startLogoAnimation")
            isAnimating = true
            binding.ivLogoEmisora.post(animationRunnable)
        } else {
            Log.d(
                "RadioPlayerFragment",
                "startLogoAnimation: No se inicia (isPlaying=${AudioPlayerManager.getCurrentPlayer()?.isPlaying}, isAnimating=$isAnimating)"
            )
        }
    }

    private fun updatePlayPauseButton() {
        if (_binding == null || !isAdded) {
            Log.w(
                "RadioPlayerFragment",
                "updatePlayPauseButton: Binding null o Fragment no añadido."
            )
            return
        }
        val isPlaying = AudioPlayerManager.getCurrentPlayer()?.isPlaying ?: false
        Log.d("RadioPlayerFragment", "updatePlayPauseButton: isPlaying=$isPlaying")
        binding.btnPlayPause.setImageResource(
            if (isPlaying) R.drawable.ic_media_pause else R.drawable.ic_media_play
        )
    }

    private fun setupControls() {
        if (_binding == null) { // Protección temprana
            Log.e("RadioPlayerFragment", "setupControls: Binding es null al inicio.")
            return
        }
        Log.d("RadioPlayerFragment", "setupControls")

        binding.btnPlayPause.setOnClickListener {
            Log.d("RadioPlayerFragment", "Play/Pause button clicked.")
            AudioPlayerManager.getCurrentPlayer()?.let { player ->
                if (player.isPlaying) {
                    player.pause()
                } else {
                    player.play()
                }
                // El estado del botón se actualizará a través del listener onIsPlayingChanged
            }
        }

        binding.btnStop.setOnClickListener {
            Log.d("RadioPlayerFragment", "Stop button clicked.")
            AudioPlayerManager.stop()
            view?.postDelayed({
                if (isAdded && !isDetached) { // Verifica el estado del fragmento
                    try {
                        if (!findNavController().popBackStack()) {
                            requireActivity().finish()
                        }
                    } catch (e: IllegalStateException) {
                        Log.e("RadioPlayerFragment", "Error al navegar hacia atrás: ${e.message}")
                        // Esto puede suceder si el NavController ya no es válido
                    }
                }
            }, 50) // Reducido el retraso, o considera quitarlo
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d("RadioPlayerFragment", "onStart: Binding to RadioPlayerService.")
        Intent(requireContext(), RadioPlayerService::class.java).also { intent ->
            try {
                requireActivity().bindService(intent, connection, Context.BIND_AUTO_CREATE)
            } catch (e: IllegalStateException) {
                Log.e(
                    "RadioPlayerFragment",
                    "Error al hacer bind al servicio en onStart: ${e.message}"
                )
                // Esto podría pasar si la actividad está en proceso de finalización
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("RadioPlayerFragment", "onResume")

        if (_binding != null && isAdded) {
            updatePlayPauseButton()
            if (AudioPlayerManager.getCurrentPlayer()?.isPlaying == true) {
                startLogoAnimation()
            } else {
                stopLogoAnimation()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        Log.d("RadioPlayerFragment", "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d("RadioPlayerFragment", "onStop: Unbinding from RadioPlayerService.")
        if (isBound) {
            try {
                requireActivity().unbindService(connection)
            } catch (e: IllegalArgumentException) {
                Log.e("RadioPlayerFragment", "Error al hacer unbind del servicio: ${e.message}")
                // Esto puede suceder si el servicio no estaba registrado o ya se desvinculó.
            }
            isBound = false
            radioService = null // Limpia la referencia al servicio
        }
        // No detenemos el servicio para que continúe en segundo plano
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("RadioPlayerFragment", "onDestroyView: _binding = null.")
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("RadioPlayerFragment", "onDestroy")
    }
}