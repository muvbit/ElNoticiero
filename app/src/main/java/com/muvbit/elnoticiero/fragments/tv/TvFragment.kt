package com.muvbit.elnoticiero.fragments.tv

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.muvbit.elnoticiero.R
import com.muvbit.elnoticiero.databinding.FragmentTvBinding
import com.muvbit.elnoticiero.model.Canal

class TvFragment : Fragment() {

    private var _binding: FragmentTvBinding? = null
    private val binding get() = _binding!!

    private val canalesTV = listOf(
        CanalTV("TVE 1", "https://ztnr.rtve.es/ztnr/1688877.m3u8"),
        CanalTV("Canal 24H", "https://rtvelivestream.akamaized.net/rtve/24h_Live/index.m3u8"),
        CanalTV("TV3", "https://ccma-tva.akamaized.net/hls/live/2018379/tv3cat/tv3cat.m3u8"),
        CanalTV("Canal Sur", "https://cdnlive.shooowit.net/rtvalive/smil:directo.smil/playlist.m3u8"),
        CanalTV("Aragón TV", "https://streaming-aragon-tv.flumotion.com/playlist.m3u8")
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTvBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.recyclerTv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = CanalTVAdapter(canalesTV) { canal ->
                findNavController().navigate(
                    TvFragmentDirections.actionTvFragmentToTvPlayerFragment(
                        nombre = canal.nombre,
                        url = canal.url
                    )
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    data class CanalTV(val nombre: String, val url: String)

    class CanalTVAdapter(
        private val canales: List<CanalTV>,
        private val onClick: (CanalTV) -> Unit
    ) : RecyclerView.Adapter<CanalTVViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CanalTVViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(android.R.layout.simple_list_item_1, parent, false)
            return CanalTVViewHolder(view)
        }

        override fun onBindViewHolder(holder: CanalTVViewHolder, position: Int) {
            val canal = canales[position]
            holder.bind(canal, onClick)
        }

        override fun getItemCount() = canales.size
    }

    class CanalTVViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(canal: CanalTV, onClick: (CanalTV) -> Unit) {
            (itemView as? android.widget.TextView)?.text = canal.nombre
            itemView.setOnClickListener { onClick(canal) }
        }
    }
}