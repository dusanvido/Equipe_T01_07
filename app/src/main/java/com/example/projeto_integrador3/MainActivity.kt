package com.example.projeto_integrador3

import android.os.Bundle
import android.widget.Toast
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Toast.makeText(this, "Entrou na MainActivity!", Toast.LENGTH_SHORT).show()

        enableEdgeToEdge()
        setContentView(R.layout.activity_main_login)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        FirebaseApp.initializeApp(this)
        val db = FirebaseFirestore.getInstance()

        val risco = hashMapOf(
            "descricao" to "Fio desencapado próximo à escada",
            "nivel" to "Alto",
            "localizacao" to "Setor A, andar 2"
        )

        db.collection("riscos")
            .add(risco)
            .addOnSuccessListener { documentReference ->
                Log.d("FIRESTORE", "Documento adicionado com ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w("FIRESTORE", "Erro ao adicionar documento", e)
            }
    }
}

