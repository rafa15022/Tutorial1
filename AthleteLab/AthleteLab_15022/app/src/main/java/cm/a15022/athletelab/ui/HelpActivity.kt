package cm.a15022.athletelab.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import cm.a15022.athletelab.R

class HelpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_help)

        findViewById<Button>(R.id.goWorkoutsButton).setOnClickListener {
            openScreen("workouts")
        }

        findViewById<Button>(R.id.goCalendarButton).setOnClickListener {
            openScreen("calendar")
        }

        findViewById<Button>(R.id.goProgressButton).setOnClickListener {
            openScreen("progress")
        }

        findViewById<Button>(R.id.goChallengesButton).setOnClickListener {
            openScreen("challenges")
        }

        findViewById<Button>(R.id.goFeedButton).setOnClickListener {
            openScreen("feed")
        }

        findViewById<Button>(R.id.goCoachButton).setOnClickListener {
            openScreen("coach")
        }

        findViewById<Button>(R.id.backButton).setOnClickListener {
            finish()
        }
    }

    private fun openScreen(screen: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("open_screen", screen)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
        finish()
    }
}