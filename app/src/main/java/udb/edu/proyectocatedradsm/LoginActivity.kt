package udb.edu.proyectocatedradsm

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import udb.edu.proyectocatedradsm.network.APIService
import udb.edu.proyectocatedradsm.utils.SessionManager

class LoginActivity : AppCompatActivity() {

    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginButton: Button
    private lateinit var registerLink: TextView
    private lateinit var sessionManager: SessionManager  // Instancia de SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Inicializar SessionManager
        sessionManager = SessionManager(this)

        // Verificar si el usuario ya tiene una sesión activa
        checkUserSession()

        // Inicializar vistas
        emailInput = findViewById(R.id.emailInput)
        passwordInput = findViewById(R.id.passwordInput)
        loginButton = findViewById(R.id.loginButton)
        registerLink = findViewById(R.id.registerLink)

        // Listener para iniciar sesión
        loginButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
            } else {
                showAlert("Error", "Por favor, complete todos los campos")
            }
        }

        // Listener para redirigir a la pantalla de registro
        registerLink.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loginUser(email: String, password: String) {
        lifecycleScope.launch {
            try {
                val response = APIService.getInstance().loginUser(email, password)

                if (response.isSuccessful) {
                    val apiResponse = response.body()

                    if (apiResponse != null && apiResponse.status == 1) {
                        showAlert("Éxito", "Inicio de sesión exitoso")

                        // Guardar el ID del usuario en SharedPreferences
                        val usuarioId = apiResponse.dataset?.id_usuario
                        if (usuarioId != null) {
                            sessionManager.saveUserSession(usuarioId)

                            // Redirigir a NotesActivity con la sesión del usuario
                            val intent = Intent(this@LoginActivity, NotesActivity::class.java)
                            intent.putExtra("USER_ID", usuarioId)
                            startActivity(intent)
                            finish()  // Cerrar la actividad de login
                        }
                    } else {
                        val errorMessage = apiResponse?.exception ?: "Error desconocido"
                        showAlert("Error", errorMessage)
                        Log.e("LoginActivity", "Error en la autenticación: $errorMessage")
                    }
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Error desconocido"
                    showAlert("Error HTTP", errorBody)
                    Log.e("LoginActivity", "Error HTTP: ${response.code()}, Error Body: $errorBody")
                }
            } catch (e: Exception) {
                Log.e("LoginActivity", "Error de red", e)
                showAlert("Error de Red", e.message ?: "Error desconocido")
            }
        }
    }

    // Verificar si ya existe una sesión activa
    private fun checkUserSession() {
        if (sessionManager.isLoggedIn()) {
            // Si el usuario ya está autenticado, redirigir a NotesActivity
            val intent = Intent(this@LoginActivity, NotesActivity::class.java)
            intent.putExtra("USER_ID", sessionManager.getUserId())
            startActivity(intent)
            finish()  // Cerrar la actividad de login
        }
    }

    // Función para mostrar una alerta personalizada
    private fun showAlert(title: String, message: String) {
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()  // Cierra la alerta
        }
        val dialog: android.app.AlertDialog = builder.create()
        dialog.show()
    }
}
