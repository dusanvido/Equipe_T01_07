package com.example.projeto_integrador3

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class Relatorio(
    val titulo: String = "",
    val dataRegistro: String = "",
    val nivelRisco: String = ""
)

class RelatorioAdapter(private val relatorios: List<Relatorio>) :
    RecyclerView.Adapter<RelatorioAdapter.RelatorioViewHolder>() {

    class RelatorioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitulo: TextView = itemView.findViewById(R.id.tvTitulo)
        val tvData: TextView = itemView.findViewById(R.id.tvData)
        val tvNivelRisco: TextView = itemView.findViewById(R.id.tvNivelRisco)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RelatorioViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_relatorio, parent, false)
        return RelatorioViewHolder(view)
    }

    override fun onBindViewHolder(holder: RelatorioViewHolder, position: Int) {
        val relatorio = relatorios[position]
        holder.tvTitulo.text = relatorio.titulo
        holder.tvData.text = relatorio.dataRegistro
        holder.tvNivelRisco.text = relatorio.nivelRisco
    }

    override fun getItemCount(): Int = relatorios.size
}