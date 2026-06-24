package cm.a15022.athletelab.model

data class FeedPost(
    var id: String = "",
    var userId: String = "",
    var authorName: String = "",
    var sport: String = Sport.MUSCLE,
    var text: String = "",
    var result: String = "",
    var visibility: String = "public",
    var imageUrl: String = "",
    var createdAt: Long = System.currentTimeMillis()
)
