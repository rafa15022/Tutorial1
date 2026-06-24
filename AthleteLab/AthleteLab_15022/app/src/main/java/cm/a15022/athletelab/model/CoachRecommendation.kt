package cm.a15022.athletelab.model

data class CoachRecommendation(
    var id: String = "",
    var userId: String = "",
    var sport: String = Sport.MUSCLE,
    var recommendation: String = "",
    var goalPrediction: String = "",
    var smartChallenge: String = "",
    var createdAt: Long = System.currentTimeMillis()
)
