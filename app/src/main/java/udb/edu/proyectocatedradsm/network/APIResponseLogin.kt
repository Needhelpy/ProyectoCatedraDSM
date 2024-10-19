package udb.edu.proyectocatedradsm.network

data class APIResponseLogin(
    val status: Int,
    val message: String,
    val exception: String?,
    val dataset: UserDataset?  // Dataset es un objeto con el id_usuario
)

data class UserDataset(
    val id_usuario: Int  // Campo que contiene el ID del usuario autenticado
)
