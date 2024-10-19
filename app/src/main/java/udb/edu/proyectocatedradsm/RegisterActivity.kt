package udb.edu.proyectocatedradsm

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import udb.edu.proyectocatedradsm.network.APIService

class RegisterActivity : AppCompatActivity() {

    private lateinit var firstNameInput: EditText
    private lateinit var lastNameInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var confirmPasswordInput: EditText
    private lateinit var registerButton: Button
    private lateinit var backToLoginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Inicializar vistas
        firstNameInput = findViewById(R.id.firstNameInput)
        lastNameInput = findViewById(R.id.lastNameInput)
        emailInput = findViewById(R.id.emailInput)
        passwordInput = findViewById(R.id.passwordInput)
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput)
        registerButton = findViewById(R.id.registerButton)
        backToLoginButton = findViewById(R.id.backToLoginButton)

        backToLoginButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        // Configurar listener del botón de registro
        registerButton.setOnClickListener {
            val nombres = firstNameInput.text.toString().trim()
            val apellidos = lastNameInput.text.toString().trim()
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()
            val confirmPassword = confirmPasswordInput.text.toString().trim()

            if (nombres.isNotEmpty() && apellidos.isNotEmpty() && email.isNotEmpty() &&
                password.isNotEmpty() && confirmPassword.isNotEmpty()
            ) {
                if (password == confirmPassword) {
                    registerUser(nombres, apellidos, email, password, confirmPassword)
                } else {
                    showAlert("Error", "Las contraseñas no coinciden.")
                }
            } else {
                showAlert("Advertencia", "Por favor, complete todos los campos.")
            }
        }
    }

    private fun registerUser(nombres: String, apellidos: String, email: String, password: String, confirmar: String) {
        lifecycleScope.launch {
            try {
                // Llamada a la API para registrar el usuario
                val response = APIService.getInstance().registerUser(nombres, apellidos, email, password, confirmar)

                if (response.isSuccessful) {
                    val apiResponse = response.body()

                    if (apiResponse != null && apiResponse.status == 1) {
                        showAlert("Éxito", "Registro exitoso") {
                            // Redirigir al LoginActivity después del registro
                            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        showAlert("Error", apiResponse?.exception ?: "Error desconocido.")
                        Log.e("RegisterActivity", "Error: ${apiResponse?.exception}")
                    }
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Error desconocido"
                    showAlert("Error HTTP", errorBody)
                    Log.e("RegisterActivity", "Error HTTP: ${response.code()}, Body: $errorBody")
                }
            } catch (e: Exception) {
                showAlert("Error de red", "Ocurrió un problema: ${e.message}")
                Log.e("RegisterActivity", "Error de red", e)
            }
        }
    }

    private fun showAlert(title: String, message: String, onPositive: (() -> Unit)? = null) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
            onPositive?.invoke()
        }
        builder.create().show()
    }
}
