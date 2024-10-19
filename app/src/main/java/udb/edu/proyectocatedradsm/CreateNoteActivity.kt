package udb.edu.proyectocatedradsm

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import udb.edu.proyectocatedradsm.network.APIService

class CreateNoteActivity : AppCompatActivity() {

    private lateinit var noteTitleInput: EditText
    private lateinit var noteContentInput: EditText
    private lateinit var createNoteButton: Button
    private lateinit var backToNotesButton: Button
    private var usuarioId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_note)

        // Recuperar el ID del usuario desde el intent
        usuarioId = intent.getIntExtra("USER_ID", -1)

        // Inicializar las vistas
        noteTitleInput = findViewById(R.id.noteTitleInput)
        noteContentInput = findViewById(R.id.noteContentInput)
        createNoteButton = findViewById(R.id.createNoteButton)
        backToNotesButton = findViewById(R.id.backToNotesButton)

        // Botón para crear la nueva nota
        createNoteButton.setOnClickListener {
            val titulo = noteTitleInput.text.toString().trim()
            val contenido = noteContentInput.text.toString().trim()

            if (titulo.isNotEmpty() && contenido.isNotEmpty()) {
                createNote(titulo, contenido)
            } else {
                Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        backToNotesButton.setOnClickListener {
            finish()
        }
    }

    private fun createNote(titulo: String, contenido: String) {
        lifecycleScope.launch {
            try {
                val response = APIService.getInstance().createNote(titulo, contenido, usuarioId)

                if (response.isSuccessful) {
                    val apiResponse = response.body()

                    if (apiResponse != null && apiResponse.status == 1) {
                        Toast.makeText(this@CreateNoteActivity, "Nota creada correctamente", Toast.LENGTH_SHORT).show()
                        setResult(Activity.RESULT_OK)  // Indicar que la nota fue creada
                        finish()  // Volver a la pantalla principal
                    } else {
                        Toast.makeText(this@CreateNoteActivity, "Error: ${apiResponse?.exception}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@CreateNoteActivity, "Error HTTP: ${response.errorBody()?.string()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@CreateNoteActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
