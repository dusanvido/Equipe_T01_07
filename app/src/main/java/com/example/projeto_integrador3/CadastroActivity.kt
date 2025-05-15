package com.example.projeto_integrador3

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class CadastroActivity : AppCompatActivity() {

    private lateinit var layoutNome: TextInputLayout
    private lateinit var layoutCPF: TextInputLayout
    private lateinit var layoutEmail: TextInputLayout
    private lateinit var layoutSenha: TextInputLayout

    private lateinit var editNome: TextInputEditText
    private lateinit var editCPF: TextInputEditText
    private lateinit var editEmail: TextInputEditText
    private lateinit var editSenha: TextInputEditText

    private lateinit var cadastrarButton: Button
    private lateinit var botaoVoltar: Button

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_cadastro)

        layoutNome = findViewById(R.id.layoutNome)
        layoutCPF = findViewById(R.id.layoutCPF)
        layoutEmail = findViewById(R.id.layoutEmail)
        layoutSenha = findViewById(R.id.layoutSenha)

        editNome = findViewById(R.id.editNome)
        editCPF = findViewById(R.id.editCPF)
        editEmail = findViewById(R.id.editEmailCadastro)
        editSenha = findViewById(R.id.editSenhaCadastro)

        cadastrarButton = findViewById(R.id.cadastrarButton)
        botaoVoltar = findViewById(R.id.botaoVoltar)

        auth = FirebaseAuth.getInstance()

        cadastrarButton.setOnClickListener {
            val nome = editNome.text.toString().trim()
            val cpf = editCPF.text.toString().trim()
            val email = editEmail.text.toString().trim()
            val senha = editSenha.text.toString().trim()

            // Validação dos campos
            if (nome.isEmpty() || cpf.isEmpty() || email.isEmpty() || senha.isEmpty()) {
                showSnackbar("Preencha todos os campos obrigatórios")
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, senha)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        showSnackbar("Conta criada com sucesso!")
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    } else {
                        showSnackbar("Erro ao cadastrar: ${task.exception?.localizedMessage}")
                    }
                }
        }

        botaoVoltar.setOnClickListener {
            finish()
        }
    }

    private fun showSnackbar(mensagem: String) {
        val snackbar = Snackbar.make(cadastrarButton, mensagem, Snackbar.LENGTH_LONG)
        snackbar.setBackgroundTint(getColor(android.R.color.holo_red_dark))
        snackbar.setTextColor(getColor(android.R.color.white))
        snackbar.show()
    }
}
