package cm.a15022.athletelab

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class AthleteLabApp : Application() {
    override fun onCreate() {
        super.onCreate()

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
}