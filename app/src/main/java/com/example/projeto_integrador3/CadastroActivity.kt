package com.example.projeto_integrador3

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class CadastroActivity : AppCompatActivity() {

    private lateinit var editEmailCadastro: EditText
    private lateinit var editSenhaCadastro: EditText
    private lateinit var cadastrarButton: Button
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_cadastro)

        editEmailCadastro = findViewById(R.id.editEmailCadastro)
        editSenhaCadastro = findViewById(R.id.editSenhaCadastro)
        cadastrarButton = findViewById(R.id.cadastrarButton)

        auth = FirebaseAuth.getInstance()

        cadastrarButton.setOnClickListener {
            val email = editEmailCadastro.text.toString().trim()
            val senha = editSenhaCadastro.text.toString().trim()

            if (email.isNotEmpty() && senha.isNotEmpty()) {
                auth.createUserWithEmailAndPassword(email, senha)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show()

                            // Após cadastro -> Ir para registrar risco
                            val intent = Intent(this, RegistrarRiscoActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(
                                this,
                                "Erro ao cadastrar: ${task.exception?.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}


