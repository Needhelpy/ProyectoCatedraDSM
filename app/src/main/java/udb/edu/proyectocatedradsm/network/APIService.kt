package udb.edu.proyectocatedradsm.network

import com.google.gson.GsonBuilder
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface APIService {

    // API para el inicio de sesión (login)
    @FormUrlEncoded
    @POST("controller/usuario.php?action=logIn")
    suspend fun loginUser(
        @Field("correo") correo: String,
        @Field("clave") clave: String
    ): Response<APIResponseLogin>  // APIResponseLogin para la autenticación

    // API para el registro de usuarios
    @FormUrlEncoded
    @POST("controller/usuario.php?action=register")
    suspend fun registerUser(
        @Field("nombres") nombres: String,
        @Field("apellidos") apellidos: String,
        @Field("correo") correo: String,
        @Field("clave") clave: String,
        @Field("confirmar") confirmar: String
    ): Response<APIResponse>  // Se asume que el registro usa el formato general de APIResponse

    // Crear una nueva nota
    @FormUrlEncoded
    @POST("controller/notas.php?action=create")
    suspend fun createNote(
        @Field("titulo") titulo: String,
        @Field("contenido") contenido: String,
        @Field("usuario") usuario: Int
    ): Response<APIResponse>  // APIResponse para la creación de notas

    // Leer todas las notas de un usuario
    @FormUrlEncoded
    @POST("controller/notas.php?action=readAll")
    suspend fun readAllNotes(
        @Field("usuario") usuario: Int
    ): Response<APIResponseNotes>  // APIResponseNotes para leer todas las notas

    // Actualizar una nota existente
    @FormUrlEncoded
    @POST("controller/notas.php?action=update")
    suspend fun updateNote(
        @Field("id") id: Int,
        @Field("titulo") titulo: String,
        @Field("contenido") contenido: String
    ): Response<APIResponse>  // APIResponse para la actualización de notas

    // Eliminar una nota existente
    @FormUrlEncoded
    @POST("controller/notas.php?action=delete")
    suspend fun deleteNote(
        @Field("id") id: Int
    ): Response<APIResponse>  // APIResponse para eliminar una nota

    companion object {
        private var retrofit: Retrofit? = null

        fun getInstance(): APIService {
            if (retrofit == null) {
                val gson = GsonBuilder()
                    .setLenient()
                    .create()

                retrofit = Retrofit.Builder()
                    .baseUrl("http://10.0.2.2/ProyectoCatedraDSM/api/")  // Cambiar según la URL de tu servidor
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()
            }
            return retrofit!!.create(APIService::class.java)
        }
    }
}
