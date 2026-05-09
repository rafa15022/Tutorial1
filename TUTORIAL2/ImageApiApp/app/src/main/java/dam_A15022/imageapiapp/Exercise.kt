package dam_A15022.imageapiapp

data class Exercise(
    val id: String,
    val name: String,
    val force: String?,
    val level: String?,
    val mechanic: String?,
    val equipment: String?,
    val primaryMuscles: List<String>,
    val secondaryMuscles: List<String>,
    val instructions: List<String>,
    val category: String?,
    val images: List<String>
)