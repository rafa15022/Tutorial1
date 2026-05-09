package dam_A15022.imageapiapp

data class ExerciseImageResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<Exercise>
)