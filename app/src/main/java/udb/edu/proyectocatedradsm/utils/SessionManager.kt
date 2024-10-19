package udb.edu.proyectocatedradsm.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    fun saveUserSession(userId: Int) {
        val editor = prefs.edit()
        editor.putInt("USER_ID", userId)
        editor.putBoolean("LOGGED_IN", true)
        editor.apply()
    }

    fun getUserId(): Int {
        return prefs.getInt("USER_ID", -1)  // Retorna -1 si no se encuentra el ID
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean("LOGGED_IN", false)  // Retorna false si no está logueado
    }

    fun clearSession() {
        val editor = prefs.edit()
        editor.clear()
        editor.apply()
    }

}
