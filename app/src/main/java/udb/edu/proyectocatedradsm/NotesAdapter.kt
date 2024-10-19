package udb.edu.proyectocatedradsm

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import udb.edu.proyectocatedradsm.network.Dataset

class NotesAdapter(
    private val notesList: List<Dataset>,
    private val onEditClick: (Dataset) -> Unit,
    private val onDeleteClick: (Dataset) -> Unit
) : RecyclerView.Adapter<NotesAdapter.NoteViewHolder>() {

    // ViewHolder interno para mantener las vistas de cada nota
    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val noteTitle: TextView = itemView.findViewById(R.id.textNoteTitle)
        val noteContent: TextView = itemView.findViewById(R.id.textNoteContent)
        val editButton: Button = itemView.findViewById(R.id.buttonEditNote)
        val deleteButton: Button = itemView.findViewById(R.id.buttonDeleteNote)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.note_item, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notesList[position]

        // Asignar los valores de título y contenido
        holder.noteTitle.text = note.titulo
        holder.noteContent.text = note.contenido

        // Configurar el clic del botón de editar
        holder.editButton.setOnClickListener {
            onEditClick(note)
        }

        // Configurar el clic del botón de eliminar
        holder.deleteButton.setOnClickListener {
            onDeleteClick(note)
        }
    }

    // Retorna la cantidad de notas
    override fun getItemCount(): Int = notesList.size
}
