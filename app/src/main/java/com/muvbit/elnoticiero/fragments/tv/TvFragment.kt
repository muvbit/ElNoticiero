package com.muvbit.elnoticiero.fragments.tv

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.muvbit.elnoticiero.activities.MainActivity
import com.muvbit.elnoticiero.adapters.ChannelTVAdapter
import com.muvbit.elnoticiero.databinding.FragmentTvBinding
import com.muvbit.elnoticiero.model.ChannelTV
import com.muvbit.elnoticiero.network.tv.StreamOption
import com.muvbit.elnoticiero.network.tv.TDTChannelsResponse
import com.muvbit.elnoticiero.network.tv.TDTChannelsService
import com.muvbit.elnoticiero.resources.TvChannelList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.Normalizer

class TvFragment : Fragment() {

    private var _binding: FragmentTvBinding? = null
    private val binding get() = _binding!!
    private val fragmentJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + fragmentJob)

    private lateinit var canalAdapter: ChannelTVAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTvBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        fetchTVChannels()
        val mainActivity = requireActivity() as MainActivity
        val mainActivityBinding = mainActivity.binding
        // Borramos el menu del bottomNav y le agregamos el personalizado para este fragment
        mainActivityBinding.bottomNav.menu.clear()
    }

    private fun setupRecyclerView() {
        canalAdapter = ChannelTVAdapter(emptyList()) { canal ->
            findNavController().navigate(
                TvFragmentDirections.actionTvFragmentToTvPlayerFragment(
                    nombre = canal.nombre,
                    url = canal.url,
                    logo = canal.logo
                )
            )
        }

        binding.recyclerTv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = canalAdapter
            setHasFixedSize(true) // Para optimizar
        }
    }
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(TDTChannelsService.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val tdtService by lazy {
        retrofit.create(TDTChannelsService::class.java)
    }

    private fun fetchTVChannels() {
        uiScope.launch {
            try {
                println("DEBUG: Iniciando fetchTVChannels")
                val response = tdtService.getTVChannels()
                println("DEBUG: Respuesta recibida - Países: ${response.countries.size}")

                val canalesFiltrados = filtrarCanales(response)
                println("DEBUG: Canales filtrados: ${canalesFiltrados.size}")

                if (canalesFiltrados.isEmpty()) {
                    cargarCanalesPorDefecto()
                } else {
                    canalAdapter.canales = canalesFiltrados
                    canalAdapter.notifyDataSetChanged()
                }

            } catch (e: Exception) {
                e.printStackTrace()
                cargarCanalesPorDefecto()
            }
        }
    }

    private fun filtrarCanales(response: TDTChannelsResponse): List<ChannelTV> {
        return try {
            // Canales prioritarios con nombres alternativos

            val canalesGratuitos = TvChannelList.channels

            // Buscar España en la lista de países
            val spain = response.countries.find { it.name.equals("Spain", ignoreCase = true) }

            spain?.ambits
                ?.flatMap { ambit -> ambit.channels }
                ?.filter { channel ->
                    // Filtrar canales con opciones válidas
                    channel.options.any { option ->
                        isValidStreamOption(option)
                    }
                }
                ?.mapNotNull { channel ->

                    ChannelTV(
                        nombre = channel.name,
                        url = channel.options[0].url,
                        logo = channel.logo ?: "",
                        epgId = channel.epgId,
                        isFree = if (TvChannelList.isFree) {
                            // Si TvChannelList.isFree es true, entonces verificamos contra la lista canalesGratuitos
                            canalesGratuitos.any { freeChannelName ->
                                channel.name.equals(freeChannelName, ignoreCase = true)
                            }
                        } else {
                            // Si TvChannelList.isFree es false, entonces todos los canales se consideran "free" en este contexto
                            true
                        }
                    )
                }
                ?.distinctBy { it.nombre.lowercase() }
                ?.sortedWith(
                    compareByDescending<ChannelTV> { it.isFree } // Gratuitos primero
                        .thenByDescending { it.esPrioritario }
                        .thenBy { it.nombre }
                )
                ?: listOf()
        } catch (e: Exception) {
            e.printStackTrace()
            listOf()
        }
    }

    private fun isValidStreamOption(option: StreamOption): Boolean {
        return option.format.equals("m3u8", ignoreCase = true) &&
                !option.url.isNullOrBlank() &&
                option.url.startsWith("http") &&
                (option.url.endsWith(".m3u8") ||
                        option.url.contains(".m3u8") ||
                        option.url.contains("m3u8?"))
    }

    private fun normalizar(texto: String): String {
        return Normalizer.normalize(texto.lowercase(), Normalizer.Form.NFD)
            .replace(Regex("[^a-z0-9]"), "") // Elimina todo excepto letras y números
    }

    private fun cargarCanalesPorDefecto() {
        val canalesPorDefecto = listOf(
            ChannelTV(
                "La 1",
                "https://ztnr.rtve.es/ztnr/1688877.m3u8",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/8/83/Logo_TVE-Internacional.svg/2084px-Logo_TVE-Internacional.svg.png"
            ),
            ChannelTV(
                "La 2",
                "https://ztnr.rtve.es/ztnr/1688885.m3u8",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/1/1f/Logo_La_2.svg/330px-Logo_La_2.svg.png"
            ),
            ChannelTV(
                "Canal 24H",
                "https://ztnr.rtve.es/ztnr/1694255.m3u8",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/2/2b/Logo_Canal_24_horas.svg/2399px-Logo_Canal_24_horas.svg.png"
            ),

            // Canales Autonómicos
            ChannelTV(
                "TV3 Cataluña",
                "https://directes3-tv-cat.3catdirectes.cat/live-content/tv3-hls/master.m3u8",
                "https://upload.wikimedia.org/wikipedia/commons/c/c3/Logo_tv3x.png"
            ),
            ChannelTV(
                "Canal Sur Andalucía",
                "https://live-24-canalsur.interactvty.pro/9bb0f4edcb8946e79f5017ddca6c02b0/26af5488cda642ed2eddd27a6328c93b9c03e9181b9d0a825147a7d978e69202.m3u8",
                "https://upload.wikimedia.org/wikipedia/commons/1/10/Canal_Sur_Andaluc%C3%ADa_SAT.png"
            ),
            ChannelTV(
                "Telemadrid",
                "https://telemadrid-23-secure2.akamaized.net/master.m3u8",
                "https://upload.wikimedia.org/wikipedia/commons/0/01/Telemadrid-Sat-Logo_%282001-2006%29.png"
            ),
            ChannelTV(
                "Aragón TV",
                "https://cartv.streaming.aranova.es/hls/live/aragontv_canal1.m3u8",
                "https://upload.wikimedia.org/wikipedia/commons/d/d4/Logo_aragon_tv_2016.png"
            ),
            ChannelTV(
                "TV Galicia",
                "https://crtvg-europa.flumotion.cloud/playlist.m3u8",
                "https://upload.wikimedia.org/wikipedia/en/thumb/4/46/Television_de_Galicia.svg/640px-Television_de_Galicia.svg.png"
            ),
            ChannelTV(
                "ETB 1 País Vasco",
                "https://multimedia.eitb.eus/live-content/etb1hd-hls/master.m3u8",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/f/f4/ETB1_2022_logo.svg/2560px-ETB1_2022_logo.svg.png"
            ),
            ChannelTV(
                "TV Extremadura",
                "https://canalextremadura-live.flumotion.cloud/canalextremadura/live_all/playlist_dvr.m3u8",
                "https://upload.wikimedia.org/wikipedia/commons/7/7c/CEXMA_new.png"
            ),
            ChannelTV(
                "Castilla La Mancha TV",
                "https://cdnapisec.kaltura.com/p/2288691/sp/228869100/playManifest/entryId/1_sqa9madm/protocol/https/format/applehttp/a.m3u8",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/8/8a/CMMedia.svg/640px-CMMedia.svg.png"
            ),
            // Canales de Noticias Internacionales
            ChannelTV(
                "Euronews (Español)",
                "https://euronews-live-spa-es.fast.rakuten.tv/v1/master/0547f18649bd788bec7b67b746e47670f558b6b2/production-LiveChannel-6571/bitok/eyJzdGlkIjoiMDA0YjY0NTMtYjY2MC00ZTZkLTlkNzEtMTk3YTM3ZDZhZWIxIiwibWt0IjoiZXMiLCJjaCI6NjU3MSwicHRmIjoxfQ==/26034/euronews-es.m3u8",
                "https://upload.wikimedia.org/wikipedia/commons/3/39/Euronews._2016_alternative_logo.png"
            ),
            ChannelTV(
                "CNN Internacional",
                "https://d3696l48vwq25d.cloudfront.net/v1/master/3722c60a815c199d9c0ef36c5b73da68a62b09d1/cc-0g2918mubifjw/index.m3u8",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b8/CNN-International-Logo.svg/744px-CNN-International-Logo.svg.png"
            ),
            ChannelTV(
                "Bloomberg TV",
                "https://www.bloomberg.com/media-manifest/streams/eu.m3u8",
                "https://w7.pngwing.com/pngs/221/526/png-transparent-bloomberg-television-hd-logo-thumbnail.png"
            ),
            ChannelTV(
                "Negocios TV",
                "https://streaming013.gestec-video.com/hls/negociostv.m3u8",
                "https://www.negocios.com/wp-content/uploads/2021/05/td_696x0.png"
            ),
            ChannelTV(
                "El Confidencial TV",
                "https://daqnsnf5phf17.cloudfront.net/v1/master/3722c60a815c199d9c0ef36c5b73da68a62b09d1/cc-sde7fypd1420w-prod/fast-channel-elconfidencial/fast-channel-elconfidencial.m3u8",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/3/3f/Elconfidencial.jpg/330px-Elconfidencial.jpg"
            )



        )
        println("DEBUG: Cargando ${canalesPorDefecto.size} canales por defecto")
        canalAdapter.canales = canalesPorDefecto
        canalAdapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentJob.cancel()
        _binding = null
    }
}
