package cm.a15022.athletelab.model

data class Workout(
    var id: String = "",
    var userId: String = "",
    var sport: String = Sport.MUSCLE,
    var title: String = "",
    var dateMillis: Long = System.currentTimeMillis(),
    var durationMinutes: Double = 0.0,
    var notes: String = "",

    // Musculação
    var exercise: String = "",
    var sets: Int = 0,
    var reps: Int = 0,
    var loadKg: Double = 0.0,

    // Futebol
    var goals: Int = 0,
    var assists: Int = 0,
    var minutesPlayed: Int = 0,
    var position: String = "",
    var intensity: String = "",

    // Atletismo
    var distanceKm: Double = 0.0,
    var timeMinutes: Double = 0.0,
    var pace: String = "",
    var trainingType: String = ""
)
