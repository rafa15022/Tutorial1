package cm.a15022.athletelab.model

data class Challenge(
    var id: String = "",
    var userId: String = "",
    var groupId: String = "",
    var title: String = "",
    var sport: String = Sport.MUSCLE,
    var type: String = "weekly", // daily ou weekly
    var scope: String = "individual", // individual ou group
    var target: Double = 1.0,
    var progress: Double = 0.0,
    var status: String = "active", // active, completed, expired
    var dueDateMillis: Long = System.currentTimeMillis(),
    var createdAt: Long = System.currentTimeMillis()
)
