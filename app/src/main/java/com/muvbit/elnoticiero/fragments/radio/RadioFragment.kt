package com.muvbit.elnoticiero.fragments.radio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.muvbit.elnoticiero.R
import com.muvbit.elnoticiero.activities.MainActivity
import com.muvbit.elnoticiero.adapters.ChannelRadioAdapter
import com.muvbit.elnoticiero.databinding.FragmentRadioBinding
import com.muvbit.elnoticiero.model.ChannelRadio

class RadioFragment : Fragment() {

    private var _binding: FragmentRadioBinding? = null
    private val binding get() = _binding!!
    private lateinit var radioAdapter: ChannelRadioAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRadioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        loadRadioStations()

        val mainActivity = requireActivity() as MainActivity
        val mainActivityBinding = mainActivity.binding
        mainActivityBinding.bottomNav.menu.clear()
    }

    private fun setupRecyclerView() {
        radioAdapter = ChannelRadioAdapter(emptyList()) { emisora ->
            findNavController().navigate(
                RadioFragmentDirections.actionRadioFragmentToRadioPlayerFragment(
                    nombre = emisora.nombre,
                    url = emisora.url,
                    logo = emisora.logo
                )
            )
        }

        binding.recyclerRadio.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = radioAdapter
            setHasFixedSize(true)
        }
    }

    private fun loadRadioStations() {
        // Lista inicial de emisoras - puedes reemplazarla luego con tu lista
        val emisoras = listOf(
            ChannelRadio(
                id = "rne1",
                nombre = "Radio Nacional RNE",
                url = "https://crtve-rne1-es.cast.addradio.de/crtve/rne1/es/mp3/high",
                logo = "https://upload.wikimedia.org/wikipedia/commons/thumb/8/84/Radio_Nacional_de_Espa%C3%B1a.svg/1200px-Radio_Nacional_de_Espa%C3%B1a.svg.png",
                categoria = "General"
            ),
            ChannelRadio(
                id = "ser",
                nombre = "Cadena SER",
                url = "https://playerservices.streamtheworld.com/api/livestream-redirect/CADENASER.mp3",
                logo = "https://upload.wikimedia.org/wikipedia/commons/thumb/7/7d/Cadena_SER_%28marca%29.svg/1200px-Cadena_SER_%28marca%29.svg.png",
                categoria = "Noticias"
            ),
            ChannelRadio(
                id = "cope",
                nombre = "COPE",
                url = "https://wecast-b03-03.flumotion.com/copesedes/live-low.mp3",
                logo = "https://upload.wikimedia.org/wikipedia/commons/thumb/8/8a/Logotipo_de_la_COPE.svg/1200px-Logotipo_de_la_COPE.svg.png",
                categoria = "Noticias"
            ),
            ChannelRadio(
                id = "los40",
                nombre = "Los 40",
                url = "https://playerservices.streamtheworld.com/api/livestream-redirect/LOS40.mp3",
                logo = "https://upload.wikimedia.org/wikipedia/commons/thumb/2/2d/Los_40.svg/1200px-Los_40.svg.png",
                categoria = "Música"
            )
        )

        radioAdapter.updateList(emisoras)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}