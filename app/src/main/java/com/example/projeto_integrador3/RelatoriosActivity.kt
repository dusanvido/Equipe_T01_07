package com.example.projeto_integrador3

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RelatoriosActivity : AppCompatActivity() {

    private lateinit var etFiltroData: EditText
    private lateinit var chipBaixo: Chip
    private lateinit var chipMedio: Chip
    private lateinit var chipAlto: Chip
    private lateinit var actvTiposRisco: AutoCompleteTextView
    private lateinit var btnMostrarTodos: Button
    private lateinit var btnRelatoriosFiltro: Button
    private lateinit var tvRelatoriosFiltro: TextView
    private lateinit var rvRelatorios: RecyclerView

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private val listaRelatorios = mutableListOf<Relatorio>()
    private lateinit var adapter: RelatorioAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_relatorios)

        // Inicialização do Firebase
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Inicialização das Views
        etFiltroData = findViewById(R.id.etFiltroData)
        chipBaixo = findViewById(R.id.chipBaixo)
        chipMedio = findViewById(R.id.chipMedio)
        chipAlto = findViewById(R.id.chipAlto)
        actvTiposRisco = findViewById(R.id.actvTiposRisco)
        btnMostrarTodos = findViewById(R.id.btnMostrarTodos)
        btnRelatoriosFiltro = findViewById(R.id.btnRelatoriosFiltro)
        tvRelatoriosFiltro = findViewById(R.id.tvRelatoriosFiltro)
        rvRelatorios = findViewById(R.id.rvRelatorios)

        // Configurar o RecyclerView
        adapter = RelatorioAdapter(listaRelatorios)
        rvRelatorios.layoutManager = LinearLayoutManager(this)
        rvRelatorios.adapter = adapter

        // Preencher dropdown de tipos de risco
        val tiposRisco = listOf("Estrutural", "Químico", "Elétrico")
        val adapterTipos = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, tiposRisco)
        actvTiposRisco.setAdapter(adapterTipos)

        // Botão para mostrar todos os relatórios do usuário logado
        btnMostrarTodos.setOnClickListener {
            buscarRelatorios()
        }

        // Botão para aplicar os filtros
        btnRelatoriosFiltro.setOnClickListener {
            buscarRelatoriosComFiltro()
        }
    }

    private fun buscarRelatorios() {
        val userId = auth.currentUser?.uid ?: return
        firestore.collection("riscos")
            .whereEqualTo("usuarioid", userId)
            .get()
            .addOnSuccessListener { documentos ->
                listaRelatorios.clear()
                for (doc in documentos) {
                    val relatorio = Relatorio(
                        titulo = doc.getString("descricao") ?: "Sem descrição",
                        dataRegistro = doc.getString("dataRegistro") ?: "Data não disponível",
                        nivelRisco = doc.getString("nivelRisco") ?: "Não especificado"
                    )
                    listaRelatorios.add(relatorio)
                }
                adapter.notifyDataSetChanged()
                rvRelatorios.visibility = View.VISIBLE
                tvRelatoriosFiltro.text = "${listaRelatorios.size} relatório(s) encontrado(s)"
            }
    }

    private fun buscarRelatoriosComFiltro() {
        val userId = auth.currentUser?.uid ?: return
        val tipoRiscoSelecionado = actvTiposRisco.text.toString()
        val nivelRiscoSelecionado = when {
            chipBaixo.isChecked -> "Baixo"
            chipMedio.isChecked -> "Médio"
            chipAlto.isChecked -> "Alto"
            else -> null
        }
        val dataFiltro = etFiltroData.text.toString()

        var query = firestore.collection("riscos")
            .whereEqualTo("usuarioid", userId)

        if (tipoRiscoSelecionado.isNotEmpty()) {
            query = query.whereEqualTo("categoria", tipoRiscoSelecionado)
        }
        if (nivelRiscoSelecionado != null) {
            query = query.whereEqualTo("nivelRisco", nivelRiscoSelecionado)
        }

        query.get()
            .addOnSuccessListener { documentos ->
                listaRelatorios.clear()
                for (doc in documentos) {
                    val relatorio = Relatorio(
                        titulo = doc.getString("descricao") ?: "Sem descrição",
                        dataRegistro = doc.getString("dataRegistro") ?: "Data não disponível",
                        nivelRisco = doc.getString("nivelRisco") ?: "Não especificado"
                    )
                    listaRelatorios.add(relatorio)
                }
                adapter.notifyDataSetChanged()
                rvRelatorios.visibility = View.VISIBLE
                tvRelatoriosFiltro.text = "${listaRelatorios.size} relatório(s) encontrado(s)"
            }
    }
}


