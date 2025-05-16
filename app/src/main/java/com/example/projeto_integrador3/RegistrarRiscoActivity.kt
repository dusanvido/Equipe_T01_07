package com.example.projeto_integrador3

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import androidx.exifinterface.media.ExifInterface
import android.util.Log
import java.io.InputStream

class RegistrarRiscoActivity : AppCompatActivity() {

    private lateinit var editTitulo: EditText
    private lateinit var editDescricao: EditText
    private lateinit var editData: EditText
    private lateinit var spinnerNivelRisco: Spinner
    private lateinit var carregarFotoButton: Button
    private lateinit var enviarButton: Button
    private lateinit var relatoriosButton: Button
    private lateinit var imageViewFoto: ImageView

    private val PICK_IMAGE_REQUEST = 1
    private val CAMERA_REQUEST = 2
    private lateinit var photoUri: Uri
    private var photoUriInitialized = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_registrar_risco)

        // Restaura photoUri se activity foi recriada
        savedInstanceState?.getString("photo_uri")?.let {
            photoUri = Uri.parse(it)
            photoUriInitialized = true
        }

        // Inicializando as views
        editTitulo = findViewById(R.id.editTitulo)
        editDescricao = findViewById(R.id.editDescricao)
        editData = findViewById(R.id.editData)
        spinnerNivelRisco = findViewById(R.id.spinnerNivelRisco)
        carregarFotoButton = findViewById(R.id.carregarFotoButton)
        enviarButton = findViewById(R.id.enviarButton)
        relatoriosButton = findViewById(R.id.relatoriosButton)
        imageViewFoto = findViewById(R.id.imageViewFoto)

        // Configurando o Spinner para selecionar o Nível de Risco
        val niveisRisco = listOf("Baixo", "Médio", "Alto")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, niveisRisco)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerNivelRisco.adapter = adapter

        // Ação ao clicar no botão "CARREGAR FOTO"
        carregarFotoButton.setOnClickListener {
            // Abre diálogo para escolher entre Galeria ou Câmera
            escolherFoto()
        }

        // Ação ao clicar no botão "ENVIAR"
        enviarButton.setOnClickListener {
            val titulo = editTitulo.text.toString()
            val descricao = editDescricao.text.toString()
            val data = editData.text.toString()
            val nivelRisco = spinnerNivelRisco.selectedItem.toString()

            if (titulo.isNotEmpty() && descricao.isNotEmpty() && data.isNotEmpty()) {
                if (photoUriInitialized) {
                    // Upload da imagem
                    val storageRef = FirebaseStorage.getInstance().reference
                    val imageRef = storageRef.child("imagens/${UUID.randomUUID()}.jpg")

                    var latitude: Double? = null
                    var longitude: Double? = null

                    try {
                        val inputStream = contentResolver.openInputStream(photoUri)
                        val coordenadas = inputStream?.let { extrairCoordenadasImagem(it) }
                        latitude = coordenadas?.first
                        longitude = coordenadas?.second
                    } catch (e: Exception) {
                        Log.e("EXIF", "Erro ao extrair coordenadas: ${e.message}")
                    }

                    imageRef.putFile(photoUri)
                        .addOnSuccessListener {
                            imageRef.downloadUrl.addOnSuccessListener { uri ->
                                salvarRiscoNoFirestore(
                                    titulo,
                                    descricao,
                                    data,
                                    nivelRisco,
                                    uri.toString(),
                                    latitude,
                                    longitude
                                )
                            }
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Erro ao fazer upload da imagem: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    // Salvar mesmo sem imagem
                    salvarRiscoNoFirestore(
                        titulo,
                        descricao,
                        data,
                        nivelRisco,
                        null,
                        null,
                        null
                    )
                }
            } else {
                Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show()
            }
        }

        // Ação ao clicar no botão "RELATÓRIOS"
        relatoriosButton.setOnClickListener {
            val intent = Intent(this, RelatoriosActivity::class.java)
            startActivity(intent)
        }
    }

    private fun escolherFoto() {
        val options = arrayOf("Galeria", "Câmera")
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Escolha a fonte da foto")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> abrirGaleria()
                1 -> abrirCamera()
            }
        }
        builder.show()
    }

    private fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    private fun abrirCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            var photoFile: File? = null
            try {
                photoFile = criarArquivoImagem()
            } catch (ex: IOException) {
                Toast.makeText(this, "Erro ao criar arquivo da imagem", Toast.LENGTH_SHORT).show()
            }
            if (photoFile != null) {
                photoUri = FileProvider.getUriForFile(
                    this,
                    "${applicationContext.packageName}.provider",
                    photoFile
                )
                photoUriInitialized = true
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                startActivityForResult(intent, CAMERA_REQUEST)
            }
        } else {
            Toast.makeText(this, "Nenhum app de câmera encontrado", Toast.LENGTH_SHORT).show()
        }
    }

    @Throws(IOException::class)
    private fun criarArquivoImagem(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = getExternalFilesDir(null)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val inputStream = contentResolver.openInputStream(photoUri)
        val (latitude, longitude) = inputStream?.let { extrairCoordenadasImagem(it) } ?: Pair(null, null)

        when (requestCode) {
            PICK_IMAGE_REQUEST -> {
                if (resultCode == RESULT_OK && data != null && data.data != null) {
                    photoUri = data.data!!
                    photoUriInitialized = true
                    imageViewFoto.setImageURI(photoUri)
                }
            }
            CAMERA_REQUEST -> {
                if (resultCode == RESULT_OK && photoUriInitialized) {
                    imageViewFoto.setImageURI(photoUri)
                } else {
                    // Caso o usuário cancele, reseta a Uri para evitar uso incorreto
                    photoUriInitialized = false
                }
            }
        }
    }

    private fun salvarRiscoNoFirestore(
        titulo: String,
        descricao: String,
        data: String,
        nivelRisco: String,
        imagemUrl: String?,
        latitude: Double?,
        longitude: Double?
    ){
        val db = FirebaseFirestore.getInstance()

        val risco = hashMapOf(
            "titulo" to titulo,
            "descricao" to descricao,
            "dataRegistro" to data,
            "nivelRisco" to nivelRisco,
            "usuarioid" to FirebaseAuth.getInstance().currentUser?.uid,
            "imagemUrl" to imagemUrl,
            "latitude" to latitude,
            "longitude" to longitude
        )

        db.collection("riscos")
            .add(risco)
            .addOnSuccessListener {
                Toast.makeText(this, "Risco registrado com sucesso!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao registrar risco: $e", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (photoUriInitialized) {
            outState.putString("photo_uri", photoUri.toString())
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        savedInstanceState.getString("photo_uri")?.let {
            photoUri = Uri.parse(it)
            photoUriInitialized = true
        }
    }

    fun extrairCoordenadasImagem(inputStream: InputStream): Pair<Double?, Double?> {
        return try {
            val exif = ExifInterface(inputStream)

            val latLong = FloatArray(2)
            if (exif.getLatLong(latLong)) {
                Pair(latLong[0].toDouble(), latLong[1].toDouble())
            } else {
                Pair(null, null)
            }
        } catch (e: Exception) {
            Log.e("EXIF", "Erro ao ler coordenadas da imagem: ${e.message}")
            Pair(null, null)
        }
    }
}
