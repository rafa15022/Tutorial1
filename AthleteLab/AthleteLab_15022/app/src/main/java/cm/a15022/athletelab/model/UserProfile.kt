package cm.a15022.athletelab.model

data class UserProfile(
    var userId: String = "",
    var name: String = "",
    var email: String = "",
    var phoneNumber: String = "",
    var phoneVerified: Boolean = false,
    var favoriteSport: String = Sport.MUSCLE,
    var premiumEnabled: Boolean = false,
    var avatarUri: String = "android_musculacao",
    var bio: String = "",
    var friends: List<String> = emptyList()
)