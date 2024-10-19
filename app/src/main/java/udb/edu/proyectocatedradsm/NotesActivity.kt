package udb.edu.proyectocatedradsm

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import udb.edu.proyectocatedradsm.network.APIService
import udb.edu.proyectocatedradsm.network.Dataset
import udb.edu.proyectocatedradsm.utils.SessionManager
import kotlin.properties.Delegates

class NotesActivity : AppCompatActivity() {

    private lateinit var recyclerViewNotes: RecyclerView
    private lateinit var notesAdapter: NotesAdapter
    private lateinit var fabAddNote: FloatingActionButton
    private lateinit var sessionManager: SessionManager
    private lateinit var logoutButton: Button
    private var notesList: MutableList<Dataset> = mutableListOf()
    private var usuarioId by Delegates.notNull<Int>()

    private lateinit var createNoteLauncher: ActivityResultLauncher<Intent>
    private lateinit var editNoteLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes)

        sessionManager = SessionManager(this)
        recyclerViewNotes = findViewById(R.id.recyclerViewNotes)
        fabAddNote = findViewById(R.id.fabAddNote)
        logoutButton = findViewById(R.id.buttonLogout)

        usuarioId = intent.getIntExtra("USER_ID", -1)

        if (usuarioId != -1) {
            initRecyclerView()
            loadNotes(usuarioId)
        } else {
            showAlert("Error", "Usuario no identificado")
        }

        createNoteLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                loadNotes(usuarioId)
            }
        }

        editNoteLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val noteId = result.data?.getIntExtra("NOTE_ID", -1) ?: return@registerForActivityResult
                val newTitle = result.data?.getStringExtra("NOTE_TITLE") ?: return@registerForActivityResult
                val newContent = result.data?.getStringExtra("NOTE_CONTENT") ?: return@registerForActivityResult

                val noteIndex = notesList.indexOfFirst { it.id_nota == noteId }
                if (noteIndex != -1) {
                    notesList[noteIndex].titulo = newTitle
                    notesList[noteIndex].contenido = newContent
                    notesAdapter.notifyItemChanged(noteIndex)
                    showAlert("Nota actualizada", "La nota ha sido actualizada correctamente.")
                }
            }
        }

        fabAddNote.setOnClickListener {
            val intent = Intent(this, CreateNoteActivity::class.java)
            intent.putExtra("USER_ID", usuarioId)
            createNoteLauncher.launch(intent)
        }

        logoutButton.setOnClickListener {
            logoutUser()
        }
    }

    private fun logoutUser() {
        // Limpia la sesión del usuario
        sessionManager.clearSession()

        // Redirige al LoginActivity y limpia el back stack
        val intent = Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()  // Cierra NotesActivity
    }

    private fun initRecyclerView() {
        notesAdapter = NotesAdapter(notesList, ::editNote, ::deleteNote)
        recyclerViewNotes.layoutManager = LinearLayoutManager(this)
        recyclerViewNotes.adapter = notesAdapter
    }

    private fun loadNotes(usuarioId: Int) {
        lifecycleScope.launch {
            try {
                val response = APIService.getInstance().readAllNotes(usuarioId)

                if (response.isSuccessful) {
                    val apiResponse = response.body()

                    if (apiResponse != null && apiResponse.status == 1) {
                        notesList.clear()
                        notesList.addAll(apiResponse.dataset ?: emptyList())
                        notesAdapter.notifyDataSetChanged()
                        showAlert("Notas cargadas", "Las notas se cargaron correctamente.")
                    } else {
                        showAlert("Error", apiResponse?.exception ?: "Error al cargar las notas.")
                    }
                } else {
                    showAlert("Error", "Error HTTP: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                showAlert("Error", "Ocurrió un problema: ${e.message}")
            }
        }
    }

    private fun editNote(note: Dataset) {
        val intent = Intent(this, EditNoteActivity::class.java)
        intent.putExtra("NOTE_ID", note.id_nota)
        intent.putExtra("NOTE_TITLE", note.titulo)
        intent.putExtra("NOTE_CONTENT", note.contenido)
        editNoteLauncher.launch(intent)
    }

    private fun deleteNote(note: Dataset) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Eliminar Nota")
        builder.setMessage("¿Estás seguro de que deseas eliminar esta nota?")
        builder.setPositiveButton("Sí") { _, _ ->
            lifecycleScope.launch {
                try {
                    val response = APIService.getInstance().deleteNote(note.id_nota)

                    if (response.isSuccessful) {
                        val apiResponse = response.body()

                        if (apiResponse != null && apiResponse.status == 1) {
                            notesList.remove(note)
                            notesAdapter.notifyDataSetChanged()
                            showAlert("Nota eliminada", "La nota ha sido eliminada correctamente.")
                        } else {
                            showAlert("Error", apiResponse?.exception ?: "Error al eliminar la nota.")
                        }
                    } else {
                        showAlert("Error", "Error HTTP: ${response.errorBody()?.string()}")
                    }
                } catch (e: Exception) {
                    showAlert("Error", "Ocurrió un problema: ${e.message}")
                }
            }
        }
        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }

    private fun showAlert(title: String, message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }
}
