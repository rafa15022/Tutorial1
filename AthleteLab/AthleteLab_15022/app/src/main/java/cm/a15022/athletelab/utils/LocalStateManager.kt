package cm.a15022.athletelab.utils

import android.content.Context

class LocalStateManager(context: Context) {
    private val prefs = context.getSharedPreferences("athletelab_local_state", Context.MODE_PRIVATE)

    fun saveLastSport(sport: String) {
        prefs.edit().putString("last_sport", sport).apply()
    }

    fun getLastSport(): String = prefs.getString("last_sport", "Musculação") ?: "Musculação"

    fun saveLastScreen(screen: String) {
        prefs.edit().putString("last_screen", screen).apply()
    }

    fun getLastScreen(): String = prefs.getString("last_screen", "home") ?: "home"
}
