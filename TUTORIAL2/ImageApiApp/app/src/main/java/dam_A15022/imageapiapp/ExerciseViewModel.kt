package dam_A15022.imageapiapp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ExerciseViewModel : ViewModel() {

    val exercisesLiveData = MutableLiveData<List<Exercise>>()
    val loadingLiveData = MutableLiveData<Boolean>()
    val errorLiveData = MutableLiveData<String>()

    private val repository = ExerciseRepository()

    fun fetchExercises() {
        loadingLiveData.postValue(true)

        Thread {
            try {
                val exercises = repository.getExercises()
                exercisesLiveData.postValue(exercises)
            } catch (e: Exception) {
                e.printStackTrace()
                errorLiveData.postValue("Erro ao carregar exercícios")
            } finally {
                loadingLiveData.postValue(false)
            }
        }.start()
    }
}