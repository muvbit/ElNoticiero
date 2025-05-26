package com.muvbit.elnoticiero.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.muvbit.elnoticiero.R
import com.muvbit.elnoticiero.databinding.ItemRadioChannelBinding
import com.muvbit.elnoticiero.fragments.radio.RadioPlayerFragment
import com.muvbit.elnoticiero.model.ChannelRadio

class ChannelRadioAdapter(
    private var emisoras: List<ChannelRadio>,
    private val onItemClick: (ChannelRadio) -> Unit
) : RecyclerView.Adapter<ChannelRadioAdapter.RadioViewHolder>() {

    inner class RadioViewHolder(val binding: ItemRadioChannelBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RadioViewHolder {
        val binding = ItemRadioChannelBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RadioViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RadioViewHolder, position: Int) {
        val emisora = emisoras[position]

        with(holder.binding) {
            tvNombreEmisora.text = emisora.nombre
            Glide.with(root.context)
                .load(emisora.logo)
                .placeholder(R.drawable.ic_radio)
                .error(R.drawable.ic_radio) // Añade manejo de error
                .into(ivLogoEmisora)
                .clearOnDetach()

            Log.d("ChannelRadioAdapter", "Logo URL: ${emisora.logo}")

            root.setOnClickListener { onItemClick(emisora) }

            // Icono de favorito (opcional)
            ivFavorito.setImageResource(
                if (emisora.esFavorita) android.R.drawable.btn_star_big_on
                else android.R.drawable.btn_star_big_off
            )
        }
    }

    override fun getItemCount() = emisoras.size

    fun updateList(newList: List<ChannelRadio>) {
        emisoras = newList
        notifyDataSetChanged()
    }
}