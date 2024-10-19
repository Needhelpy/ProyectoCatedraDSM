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

class EditNoteActivity : AppCompatActivity() {

    private lateinit var editNoteTitleInput: EditText
    private lateinit var editNoteContentInput: EditText
    private lateinit var updateNoteButton: Button
    private lateinit var backToNotesButton: Button
    private var noteId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_note)

        // Recuperar los datos de la nota a editar
        noteId = intent.getIntExtra("NOTE_ID", -1)
        val noteTitle = intent.getStringExtra("NOTE_TITLE")
        val noteContent = intent.getStringExtra("NOTE_CONTENT")

        // Initialize views
        editNoteTitleInput = findViewById(R.id.editNoteTitleInput)
        editNoteContentInput = findViewById(R.id.editNoteContentInput)
        updateNoteButton = findViewById(R.id.updateNoteButton)
        backToNotesButton = findViewById(R.id.backToNotesButton)

        // Poner los valores actuales en los campos
        editNoteTitleInput.setText(noteTitle)
        editNoteContentInput.setText(noteContent)

        // Actualizar la nota al hacer clic en el botón
        updateNoteButton.setOnClickListener {
            val newTitle = editNoteTitleInput.text.toString().trim()
            val newContent = editNoteContentInput.text.toString().trim()

            if (newTitle.isNotEmpty() && newContent.isNotEmpty()) {
                updateNote(noteId, newTitle, newContent)
            } else {
                Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        backToNotesButton.setOnClickListener {
            finish()
        }
    }

    private fun updateNote(id: Int, title: String, content: String) {
        lifecycleScope.launch {
            try {
                val response = APIService.getInstance().updateNote(id, title, content)

                if (response.isSuccessful) {
                    val apiResponse = response.body()

                    if (apiResponse != null && apiResponse.status == 1) {
                        // Devolver el resultado a NotesActivity
                        val resultIntent = intent
                        resultIntent.putExtra("NOTE_ID", id)
                        resultIntent.putExtra("NOTE_TITLE", title)
                        resultIntent.putExtra("NOTE_CONTENT", content)
                        setResult(Activity.RESULT_OK, resultIntent)

                        Toast.makeText(this@EditNoteActivity, "Nota actualizada correctamente", Toast.LENGTH_SHORT).show()
                        finish() // Cierra la actividad de edición
                    } else {
                        Toast.makeText(this@EditNoteActivity, "Error: ${apiResponse?.exception}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@EditNoteActivity, "Error HTTP: ${response.errorBody()?.string()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@EditNoteActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
