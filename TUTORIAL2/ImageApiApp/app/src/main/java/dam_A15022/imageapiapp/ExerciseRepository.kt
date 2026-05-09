package dam_A15022.imageapiapp

import com.google.gson.Gson
import java.io.InputStreamReader
import java.net.URL

class ExerciseRepository {

    fun getExercises(): List<Exercise> {
        val url = URL("https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/dist/exercises.json")

        url.openStream().use {
            val exercises = Gson().fromJson(
                InputStreamReader(it, "UTF-8"),
                Array<Exercise>::class.java
            )

            return exercises.toList()
                .filter { exercise -> exercise.images.isNotEmpty() }
        }
    }
}