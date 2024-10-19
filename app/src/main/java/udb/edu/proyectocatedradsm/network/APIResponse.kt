package udb.edu.proyectocatedradsm.network

data class APIResponse(
    val status: Int,
    val message: String,
    val exception: String?,
    val dataset: UserDataset?  // Dataset es ahora un objeto que contiene id_usuario
)
