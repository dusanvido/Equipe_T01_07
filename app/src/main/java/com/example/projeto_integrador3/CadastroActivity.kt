package com.example.projeto_integrador3

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CadastroActivity : AppCompatActivity() {

    private lateinit var editNome: EditText
    private lateinit var editCPF: EditText
    private lateinit var editEmailCadastro: EditText
    private lateinit var editSenhaCadastro: EditText
    private lateinit var cadastrarButton: Button

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_cadastro)

        // Inicializando os componentes
        editNome = findViewById(R.id.editNome)
        editCPF = findViewById(R.id.editCPF)
        editEmailCadastro = findViewById(R.id.editEmailCadastro)
        editSenhaCadastro = findViewById(R.id.editSenhaCadastro)
        cadastrarButton = findViewById(R.id.cadastrarButton)

        // Firebase
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        cadastrarButton.setOnClickListener {
            val nome = editNome.text.toString().trim()
            val cpf = editCPF.text.toString().trim()
            val email = editEmailCadastro.text.toString().trim()
            val senha = editSenhaCadastro.text.toString().trim()

            if (nome.isNotEmpty() && cpf.isNotEmpty() && email.isNotEmpty() && senha.isNotEmpty()) {
                auth.createUserWithEmailAndPassword(email, senha)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val userId = auth.currentUser?.uid

                            val dadosUsuario = hashMapOf(
                                "nome" to nome,
                                "cpf" to cpf,
                                "email" to email
                            )

                            if (userId != null) {
                                firestore.collection("usuarios").document(userId)
                                    .set(dadosUsuario)
                                    .addOnSuccessListener {
                                        Toast.makeText(this, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show()
                                        startActivity(Intent(this, RegistrarRiscoActivity::class.java))
                                        finish()
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(this, "Erro ao salvar dados: ${e.message}", Toast.LENGTH_LONG).show()
                                    }
                            }
                        } else {
                            Toast.makeText(this, "Erro ao cadastrar: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
