package com.muvbit.elnoticiero.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.muvbit.elnoticiero.R
import com.muvbit.elnoticiero.databinding.ItemChannelBinding
import com.muvbit.elnoticiero.model.ChannelTV

class CanalTVAdapter(
    var canales: List<ChannelTV>,
    private val onClick: (ChannelTV) -> Unit
) : RecyclerView.Adapter<CanalTVAdapter.CanalTVViewHolder>() {

    class CanalTVViewHolder(val binding: ItemChannelBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CanalTVViewHolder {
        val binding = ItemChannelBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CanalTVViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CanalTVViewHolder, position: Int) {
        val canal = canales[position]
        println("DEBUG: Bind canal ${canal.nombre}") // Log para ver qué canales se están bindeando

        with(holder.binding) {
            tvNombre.text = canal.nombre
            println("DEBUG: Cargando logo: ${canal.logo}") // Verificar URL del logo

            Glide.with(root.context)
                .load(canal.logo)
                .placeholder(R.drawable.ic_tv)
                .error(R.drawable.ic_tv) // Añade manejo de error
                .into(ivLogo)
                .clearOnDetach() // Limpia al desacoplar

            root.setOnClickListener {
                println("DEBUG: Click en canal ${canal.nombre}")
                onClick(canal)
            }
        }
    }

    override fun getItemCount(): Int = canales.size
}