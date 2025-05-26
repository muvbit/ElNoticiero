package com.muvbit.elnoticiero.fragments.radio

import android.os.Bundle
import android.util.Log
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
            Log.d("RadioFragment", "Logo seleccionada: ${emisora.logo}")
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
                url = "https://rtvelivestream.rtve.es/rtvesec/rne/rne_r1_main.m3u8",
                logo = "https://upload.wikimedia.org/wikipedia/commons/thumb/4/46/Logo_RNE.svg/1200px-Logo_RNE.svg.png",
                categoria = "Noticias"
            ),
            ChannelRadio(
                id = "ser",
                nombre = "Cadena SER",
                url = "https://playerservices.streamtheworld.com/api/livestream-redirect/CADENASER.mp3",
                logo = "https://upload.wikimedia.org/wikipedia/commons/thumb/0/09/Cadena_SER_Spain.svg/2560px-Cadena_SER_Spain.svg.png",
                categoria = "Noticias"
            ),
            ChannelRadio(
                id = "cope",
                nombre = "COPE",
                url = "https://flucast09-h-cloud.flumotion.com/cope/net1.mp3",
                logo = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d4/Logo_de_la_Cadena_COPE.svg/1200px-Logo_de_la_Cadena_COPE.svg.png",
                categoria = "Noticias"
            ),
            ChannelRadio(
                id = "ondacero",
                nombre = "ONDA CERO",
                url = "https://atres-live.ondacero.es/live/ondacero/master.m3u8",
                logo = "https://upload.wikimedia.org/wikipedia/commons/thumb/b/bf/Onda_Cero_logo.svg/1200px-Onda_Cero_logo.svg.png",
                categoria = "Noticias"
            ),
            ChannelRadio(
                id = "cataluñaradio",
                nombre = "CATALUÑA RADIO",
                url = "https://directes-radio-int.3catdirectes.cat/live-content/catalunya-radio-hls/master.m3u8",
                logo = "https://upload.wikimedia.org/wikipedia/commons/thumb/0/0a/Catalunya_R%C3%A0dio.svg/500px-Catalunya_R%C3%A0dio.svg.png",
                categoria = "Noticias"
            ),
            ChannelRadio(
                id = "esradio",
                nombre = "esRadio",
                url = "https://libertaddigital-radio-live1.flumotion.com/libertaddigital/ld-live1-high.mp3",
                logo = "https://upload.wikimedia.org/wikipedia/commons/thumb/f/f9/EsRadio_logo.svg/2560px-EsRadio_logo.svg.png",
                categoria = "Noticias"
            ),
            ChannelRadio(
                id = "canalsur",
                nombre = "CANAL SUR RADIO",
                url = "https://rtva-live-radio.flumotion.com/rtva/csr.mp3",
                logo = "https://upload.wikimedia.org/wikipedia/commons/4/44/Canal_Sur_Radio.png",
                categoria = "Noticias"
            ),
            ChannelRadio(
                id = "radiomarca",
                nombre = "RADIO MARCA",
                url = "https://playerservices.streamtheworld.com/api/livestream-redirect/RADIOMARCA_NACIONAL.mp3",
                logo = "https://upload.wikimedia.org/wikipedia/commons/thumb/9/90/RadioMARCA.svg/1200px-RadioMARCA.svg.png",
                categoria = "Deportes"
            )
        )

        radioAdapter.updateList(emisoras)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}