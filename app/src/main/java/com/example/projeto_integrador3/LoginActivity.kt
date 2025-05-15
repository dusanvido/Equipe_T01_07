package com.example.projeto_integrador3

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException

class LoginActivity : AppCompatActivity() {

    private lateinit var editEmail: TextInputEditText
    private lateinit var editSenha: TextInputEditText
    private lateinit var loginButton: Button
    private lateinit var botaoVoltar: Button
    private lateinit var esqueceuSenhaButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_login)

        // Inicializar os elementos da tela
        editEmail = findViewById(R.id.editEmail)
        editSenha = findViewById(R.id.editSenha)
        loginButton = findViewById(R.id.loginButton)
        botaoVoltar = findViewById(R.id.botaoVoltar)
        esqueceuSenhaButton = findViewById(R.id.esqueceuSenhaButton)
        progressBar = findViewById(R.id.progressBar)

        auth = FirebaseAuth.getInstance()

        // Clique no botão ENTRAR
        loginButton.setOnClickListener {
            val email = editEmail.text.toString().trim()
            val senha = editSenha.text.toString().trim()

            if (email.isEmpty() || senha.isEmpty()) {
                showSnackbar("Preencha todos os campos")
                return@setOnClickListener
            }

            progressBar.visibility = View.VISIBLE

            auth.signInWithEmailAndPassword(email, senha)
                .addOnCompleteListener { task ->
                    progressBar.visibility = View.GONE

                    if (task.isSuccessful) {
                        val intent = Intent(this, RegistrarRiscoActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        val mensagemErro = when (val exception = task.exception) {
                            is FirebaseAuthInvalidUserException -> "E-mail não cadastrado"
                            is FirebaseAuthInvalidCredentialsException -> "Senha incorreta ou e-mail inválido"
                            else -> "Erro ao fazer login: ${exception?.localizedMessage}"
                        }

                        showSnackbar(mensagemErro)
                    }
                }
        }

        // Clique no botão VOLTAR
        botaoVoltar.setOnClickListener {
            finish()
        }

        // Clique no botão ESQUECEU SENHA
        esqueceuSenhaButton.setOnClickListener {
            val intent = Intent(this, RecuperarSenhaActivity::class.java)
            intent.putExtra("email_usuario", editEmail.text.toString().trim())
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser != null) {
            startActivity(Intent(this, RegistrarRiscoActivity::class.java))
            finish()
        }
    }


    private fun showSnackbar(mensagem: String) {
        val snackbar = Snackbar.make(loginButton, mensagem, Snackbar.LENGTH_LONG)
        snackbar.setBackgroundTint(getColor(android.R.color.holo_red_dark))
        snackbar.setTextColor(getColor(android.R.color.white))
        snackbar.show()
    }
}