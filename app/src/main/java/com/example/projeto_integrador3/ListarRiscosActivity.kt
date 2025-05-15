package com.example.projeto_integrador3

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class ListarRiscosActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var riscoAdapter: RiscoAdapter
    private val listaRiscos = mutableListOf<Risco>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listar_riscos)

        recyclerView = findViewById(R.id.recyclerViewRiscos)
        recyclerView.layoutManager = LinearLayoutManager(this)
        riscoAdapter = RiscoAdapter(listaRiscos)
        recyclerView.adapter = riscoAdapter

        carregarRiscos()
    }

    private fun carregarRiscos() {
        val db = FirebaseFirestore.getInstance()

        db.collection("riscos")
            .get()
            .addOnSuccessListener { documentos ->
                listaRiscos.clear()
                for (documento in documentos) {
                    val risco = documento.toObject(Risco::class.java)
                    listaRiscos.add(risco)
                }
                riscoAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao carregar riscos: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
