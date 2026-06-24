package cm.a15022.athletelab.utils

import android.content.Context
import android.widget.ImageView
import cm.a15022.athletelab.R
import cm.a15022.athletelab.model.Sport

object UiUtils {
    fun setSportIcon(imageView: ImageView, sport: String) {
        val res = when (sport) {
            Sport.FOOTBALL -> R.drawable.ic_football
            Sport.RUNNING -> R.drawable.ic_run
            Sport.MUSCLE -> R.drawable.ic_dumbell_laranja
            "Musculação" -> R.drawable.ic_dumbell_laranja
            else -> R.drawable.ic_dumbell_laranja
        }

        imageView.setImageResource(res)
    }

    fun getSportLabel(context: Context, sport: String): String {
        return when (sport) {
            Sport.MUSCLE, "Musculação" -> context.getString(R.string.sport_muscle)
            Sport.FOOTBALL, "Futebol" -> context.getString(R.string.sport_football)
            Sport.RUNNING, "Atletismo" -> context.getString(R.string.sport_running)
            else -> sport
        }
    }

    fun getChallengeTypeLabel(context: Context, type: String): String {
        return when (type) {
            "daily" -> context.getString(R.string.daily)
            "weekly" -> context.getString(R.string.weekly)
            else -> type
        }
    }

    fun getChallengeScopeLabel(context: Context, scope: String): String {
        return when (scope) {
            "individual" -> context.getString(R.string.individual)
            "group" -> context.getString(R.string.group)
            else -> scope
        }
    }

    fun getChallengeStatusLabel(context: Context, status: String): String {
        return when (status) {
            "active" -> context.getString(R.string.active)
            "completed" -> context.getString(R.string.completed_status)
            else -> status
        }
    }
}
