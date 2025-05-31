package com.muvbit.elnoticiero.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.muvbit.elnoticiero.R
import com.muvbit.elnoticiero.databinding.ItemTvChannelBinding
import com.muvbit.elnoticiero.model.ChannelTV

class ChannelTVAdapter(
    var canales: List<ChannelTV>,
    val onItemClick: (ChannelTV) -> Unit
) : RecyclerView.Adapter<ChannelTVAdapter.ChannelViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChannelViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tv_channel, parent, false)
        return ChannelViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChannelViewHolder, position: Int) {
        val canal = canales[position]
        holder.bind(canal)
    }

    override fun getItemCount() = canales.size

    inner class ChannelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(canal: ChannelTV) {
            val binding = ItemTvChannelBinding.bind(itemView)
            binding.run {
                // Configuración de vistas
                tvNombre.text = canal.nombre
                Glide.with(itemView).load(canal.logo).into(ivLogo)

                // Mostrar icono de bloqueo para canales premium
                if (!canal.isFree) {
                    lockIcon.visibility = View.VISIBLE
                    itemView.alpha = 0.6f // Hacerlo semitransparente
                    itemView.isClickable = false
                } else {
                    lockIcon.visibility = View.GONE
                    itemView.alpha = 1.0f
                    itemView.isClickable = true
                }

                itemView.setOnClickListener {
                    if (canal.isFree) {
                        onItemClick(canal)
                    } else {
                        showPremiumDialog(itemView.context)
                    }
                }
            }
        }
    }

    private fun showPremiumDialog(context: Context) {
        MaterialAlertDialogBuilder(context, R.style.BlackButtonsDialog)
            .setTitle(R.string.suscribeTitle)
            .setMessage(R.string.suscribeMessage)
            .setPositiveButton(R.string.suscribeAcceptButton) { dialog, _ ->
                // Navegar a pantalla de suscripción
                dialog.dismiss()
            }
            .setNegativeButton(R.string.suscribeCancelButton, null)
            .show()
    }
}