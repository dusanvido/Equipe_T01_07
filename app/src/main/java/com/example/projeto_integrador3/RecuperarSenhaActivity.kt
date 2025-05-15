package com.example.projeto_integrador3

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class RecuperarSenhaActivity : AppCompatActivity() {

    private lateinit var editRecuperarEmail: TextInputEditText
    private lateinit var recuperarSenhaButton: Button
    private lateinit var botaoVoltar: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_recuperar_senha)

        editRecuperarEmail = findViewById(R.id.editRecuperarEmail)
        recuperarSenhaButton = findViewById(R.id.recuperarSenhaButton)
        botaoVoltar = findViewById(R.id.botaoVoltar)
        progressBar = findViewById(R.id.progressBar)

        auth = FirebaseAuth.getInstance()

        recuperarSenhaButton.setOnClickListener {
            val email = editRecuperarEmail.text.toString().trim()

            if (email.isEmpty()) {
                showSnackbar("Informe o e-mail para recuperação")
                return@setOnClickListener
            }

            progressBar.visibility = View.VISIBLE

            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    progressBar.visibility = View.GONE
                    if (task.isSuccessful) {
                        showSnackbar("Link de recuperação enviado para seu e-mail")
                    } else {
                        showSnackbar("Erro: ${task.exception?.localizedMessage}")
                    }
                }
        }

        botaoVoltar.setOnClickListener {
            finish()
        }
    }

    private fun showSnackbar(mensagem: String) {
        val snackbar = Snackbar.make(recuperarSenhaButton, mensagem, Snackbar.LENGTH_LONG)
        snackbar.setBackgroundTint(getColor(android.R.color.holo_red_dark))
        snackbar.setTextColor(getColor(android.R.color.white))
        snackbar.show()
    }
}
