package udb.edu.proyectocatedradsm.network

data class APIResponseNotes(
    val status: Int,
    val message: String,
    val exception: String?,
    val dataset: List<Dataset>?  // Dataset es una lista de objetos Dataset (notas)
)

data class Dataset(
    var id_nota: Int,       // El identificador de la nota
    var titulo: String,     // El título de la nota
    var contenido: String   // El contenido de la nota
)
