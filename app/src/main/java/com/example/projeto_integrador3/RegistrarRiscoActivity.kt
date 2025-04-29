package com.example.projeto_integrador3

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegistrarRiscoActivity : AppCompatActivity() {

    private lateinit var editTitulo: EditText
    private lateinit var editDescricao: EditText
    private lateinit var editData: EditText
    private lateinit var spinnerNivelRisco: Spinner
    private lateinit var carregarFotoButton: Button
    private lateinit var enviarButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_registrar_risco)

        // Inicializando as views
        editTitulo = findViewById(R.id.editTitulo)
        editDescricao = findViewById(R.id.editDescricao)
        editData = findViewById(R.id.editData)
        spinnerNivelRisco = findViewById(R.id.spinnerNivelRisco)
        carregarFotoButton = findViewById(R.id.carregarFotoButton)
        enviarButton = findViewById(R.id.enviarButton)

        // Configurando o Spinner para selecionar o Nível de Risco
        val niveisRisco = listOf("Baixo", "Médio", "Alto")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, niveisRisco)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerNivelRisco.adapter = adapter

        // Ação ao clicar no botão "CARREGAR FOTO" (ainda precisa ser implementado)
        carregarFotoButton.setOnClickListener {
            // Ação para carregar uma foto - Não implementada no momento
            Toast.makeText(this, "Função de carregar foto não implementada", Toast.LENGTH_SHORT).show()
        }

        // Ação ao clicar no botão "ENVIAR"
        enviarButton.setOnClickListener {
            val titulo = editTitulo.text.toString()
            val descricao = editDescricao.text.toString()
            val data = editData.text.toString()
            val nivelRisco = spinnerNivelRisco.selectedItem.toString()

            if (titulo.isNotEmpty() && descricao.isNotEmpty() && data.isNotEmpty()) {
                val db = FirebaseFirestore.getInstance()

                // Criando o objeto de risco para salvar no Firebase Firestore
                val risco = hashMapOf(
                    "titulo" to titulo,
                    "descricao" to descricao,
                    "dataRegistro" to data,
                    "nivelRisco" to nivelRisco,
                    "usuarioid" to FirebaseAuth.getInstance().currentUser?.uid
                )

                // Enviando o risco para o Firestore
                db.collection("riscos")
                    .add(risco)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Risco registrado com sucesso!", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Erro ao registrar risco: $e", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}


