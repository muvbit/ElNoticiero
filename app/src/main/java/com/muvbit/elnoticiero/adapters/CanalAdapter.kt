package com.muvbit.elnoticiero.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.muvbit.elnoticiero.R
import com.muvbit.elnoticiero.model.Canal

class CanalAdapter(
    private val canales: List<Canal>,
    private val onClick: (Canal) -> Unit
) : RecyclerView.Adapter<CanalAdapter.CanalViewHolder>() {

    class CanalViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombre: TextView = view.findViewById(R.id.tvNombre)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CanalViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_canal, parent, false)
        return CanalViewHolder(view)
    }

    override fun onBindViewHolder(holder: CanalViewHolder, position: Int) {
        val canal = canales[position]
        holder.nombre.text = canal.nombre
        holder.itemView.setOnClickListener { onClick(canal) }
    }

    override fun getItemCount(): Int = canales.size
}