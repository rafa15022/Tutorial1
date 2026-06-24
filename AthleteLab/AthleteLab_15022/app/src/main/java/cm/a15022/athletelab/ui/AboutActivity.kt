package cm.a15022.athletelab.ui

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import cm.a15022.athletelab.R

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_about)

        findViewById<Button>(R.id.backButton).setOnClickListener {
            finish()
        }
    }
}