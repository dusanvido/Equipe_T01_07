package com.example.projeto_integrador3

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide


class RiscoAdapter(private val listaRiscos: List<Risco>) :
    RecyclerView.Adapter<RiscoAdapter.RiscoViewHolder>() {

    inner class RiscoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageRisco: ImageView = itemView.findViewById(R.id.imageRisco)
        val textTitulo: TextView = itemView.findViewById(R.id.textTitulo)
        val textDescricao: TextView = itemView.findViewById(R.id.textDescricao)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RiscoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_risco, parent, false)
        return RiscoViewHolder(view)
    }

    override fun onBindViewHolder(holder: RiscoViewHolder, position: Int) {
        val risco = listaRiscos[position]
        holder.textTitulo.text = risco.titulo
        holder.textDescricao.text = risco.descricao

        if (risco.fotoUrl.isNotEmpty()) {
            Glide.with(holder.imageRisco.context)
                .load(risco.fotoUrl)
                .centerCrop()
                .into(holder.imageRisco)
        } 
    }

    override fun getItemCount() = listaRiscos.size
}