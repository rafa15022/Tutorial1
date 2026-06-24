package cm.a15022.athletelab.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import cm.a15022.athletelab.R
import cm.a15022.athletelab.auth.AuthActivity
import cm.a15022.athletelab.repository.AuthRepository
import cm.a15022.athletelab.utils.LocalStateManager
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView

class MainActivity : AppCompatActivity() {

    private val authRepository = AuthRepository()

    private lateinit var localState: LocalStateManager
    private lateinit var topBar: MaterialToolbar
    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        localState = LocalStateManager(this)
        topBar = findViewById(R.id.topBar)
        bottomNav = findViewById(R.id.bottomNav)

        topBar.setTitleTextColor(Color.WHITE)
        topBar.setSubtitleTextColor(Color.WHITE)
        topBar.overflowIcon?.setTint(Color.WHITE)
        topBar.setOnMenuItemClickListener { item -> handleMenu(item) }

        bottomNav.labelVisibilityMode = NavigationBarView.LABEL_VISIBILITY_LABELED

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    showFragment(HomeFragment(), getString(R.string.home), "home")
                    true
                }

                R.id.nav_workouts -> {
                    showFragment(WorkoutsFragment(), getString(R.string.workouts), "workouts")
                    true
                }

                R.id.nav_challenges -> {
                    showFragment(ChallengesFragment(), getString(R.string.challenges), "challenges")
                    true
                }

                R.id.nav_feed -> {
                    showFragment(FeedFragment(), getString(R.string.feed), "feed")
                    true
                }

                R.id.nav_profile -> {
                    showFragment(ProfileFragment(), getString(R.string.profile), "profile")
                    true
                }

                else -> false
            }
        }

        bottomNav.setOnItemReselectedListener { item ->
            Toast.makeText(
                this,
                getString(R.string.already_on_screen, item.title),
                Toast.LENGTH_SHORT
            ).show()
        }

        if (savedInstanceState == null) {
            val opened = openScreenFromIntent(intent)
            if (!opened) {
                openSavedScreen()
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        openScreenFromIntent(intent)
    }

    fun showFragment(fragment: Fragment, title: String, screenKey: String = title) {
        topBar.title = title
        topBar.setTitleTextColor(Color.WHITE)
        localState.saveLastScreen(screenKey)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commitAllowingStateLoss()
    }

    private fun openSavedScreen() {
        when (localState.getLastScreen()) {
            "workouts" -> bottomNav.selectedItemId = R.id.nav_workouts
            "challenges" -> bottomNav.selectedItemId = R.id.nav_challenges
            "feed" -> bottomNav.selectedItemId = R.id.nav_feed
            "profile" -> bottomNav.selectedItemId = R.id.nav_profile
            "calendar" -> showFragment(CalendarFragment(), getString(R.string.calendar), "calendar")
            "progress" -> showFragment(ProgressFragment(), getString(R.string.progress), "progress")
            "coach" -> showFragment(CoachFragment(), getString(R.string.coach), "coach")
            else -> bottomNav.selectedItemId = R.id.nav_home
        }
    }

    private fun openScreenFromIntent(intent: Intent?): Boolean {
        val screen = intent?.getStringExtra("open_screen") ?: return false

        when (screen) {
            "workouts" -> {
                bottomNav.selectedItemId = R.id.nav_workouts
                return true
            }

            "calendar" -> {
                showFragment(CalendarFragment(), getString(R.string.calendar), "calendar")
                return true
            }

            "progress" -> {
                showFragment(ProgressFragment(), getString(R.string.progress), "progress")
                return true
            }

            "challenges" -> {
                bottomNav.selectedItemId = R.id.nav_challenges
                return true
            }

            "feed" -> {
                bottomNav.selectedItemId = R.id.nav_feed
                return true
            }

            "coach" -> {
                showFragment(CoachFragment(), getString(R.string.coach), "coach")
                return true
            }
        }

        return false
    }

    private fun handleMenu(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_calendar -> {
                showFragment(CalendarFragment(), getString(R.string.calendar), "calendar")
                true
            }

            R.id.menu_progress -> {
                showFragment(ProgressFragment(), getString(R.string.progress), "progress")
                true
            }

            R.id.menu_coach -> {
                showFragment(CoachFragment(), getString(R.string.coach), "coach")
                true
            }

            R.id.menu_help -> {
                startActivity(Intent(this, HelpActivity::class.java))
                true
            }

            R.id.menu_about -> {
                startActivity(Intent(this, AboutActivity::class.java))
                true
            }

            R.id.menu_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }

            R.id.menu_logout -> {
                authRepository.logout()

                val intent = Intent(this, AuthActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)

                finish()
                true
            }

            else -> false
        }
    }
}
