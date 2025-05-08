package com.example.projeto_integrador3

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var editEmail: EditText
    private lateinit var editSenha: EditText
    private lateinit var loginButton: Button
    private lateinit var cadastrarButton: Button
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_login)

        editEmail = findViewById(R.id.editEmail)
        editSenha = findViewById(R.id.editSenha)
        loginButton = findViewById(R.id.loginButton)
        cadastrarButton = findViewById(R.id.cadastrarButton)

        auth = FirebaseAuth.getInstance()

        loginButton.setOnClickListener {
            val email = editEmail.text.toString().trim()
            val senha = editSenha.text.toString().trim()

            if (email.isNotEmpty() && senha.isNotEmpty()) {
                auth.signInWithEmailAndPassword(email, senha)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Login OK -> Ir para registrar risco
                            val intent = Intent(this, RegistrarRiscoActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(
                                this,
                                "Erro ao fazer login: ${task.exception?.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            }
        }

        cadastrarButton.setOnClickListener {
            val intent = Intent(this, CadastroActivity::class.java)
            startActivity(intent)
        }
    }
}


