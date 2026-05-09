package dam_A15022.imageapiapp

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: ExerciseViewModel
    private lateinit var adapter: ExerciseAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var muscleSpinner: Spinner

    private lateinit var equipmentSpinner: Spinner

    private var selectedMuscle = "Todos"

    private var selectedEquipment = "Todos"
    private var allExercises: List<Exercise> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this)[ExerciseViewModel::class.java]

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val btnRefresh = findViewById<Button>(R.id.btnRefresh)
        progressBar = findViewById(R.id.progressBar)
        muscleSpinner = findViewById(R.id.muscleSpinner)
        equipmentSpinner = findViewById(R.id.equipmentSpinner)

        adapter = ExerciseAdapter()

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        viewModel.exercisesLiveData.observe(this) { exercises ->
            allExercises = exercises
            setupMuscleSpinner(exercises)
            setupEquipmentSpinner(exercises)
            applyFilters()
        }

        viewModel.loadingLiveData.observe(this) { loading ->
            progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }

        viewModel.errorLiveData.observe(this) { message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }

        btnRefresh.setOnClickListener {
            viewModel.fetchExercises()
        }

        viewModel.fetchExercises()
    }

    private fun capitalizeText(text: String?): String {
        if (text.isNullOrBlank()) {
            return "-"
        }

        return text.trim().split(" ").joinToString(" ") { word ->
            word.replaceFirstChar { it.uppercase() }
        }
    }

    private fun setupMuscleSpinner(exercises: List<Exercise>) {
        val muscles = mutableListOf<String>()
        muscles.add("Todos")

        exercises.forEach { exercise ->
            exercise.primaryMuscles.forEach { muscle ->
                val cleanMuscle = capitalizeText(muscle)

                if (!muscles.contains(cleanMuscle)) {
                    muscles.add(cleanMuscle)
                }
            }
        }

        val spinnerAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            muscles
        )

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        muscleSpinner.adapter = spinnerAdapter

        muscleSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedMuscle = muscles[position]
                applyFilters()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    private fun setupEquipmentSpinner(exercises: List<Exercise>) {
        val equipments = mutableListOf<String>()
        equipments.add("Todos")

        exercises.forEach { exercise ->
            val cleanEquipment = capitalizeText(exercise.equipment)

            if (cleanEquipment != "-" && !equipments.contains(cleanEquipment)) {
                equipments.add(cleanEquipment)
            }
        }

        val spinnerAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            equipments
        )

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        equipmentSpinner.adapter = spinnerAdapter

        equipmentSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedEquipment = equipments[position]
                applyFilters()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    private fun applyFilters() {
        val filteredExercises = allExercises.filter { exercise ->

            val matchesMuscle =
                selectedMuscle == "Todos" ||
                        exercise.primaryMuscles.any { muscle ->
                            capitalizeText(muscle) == selectedMuscle
                        }

            val matchesEquipment =
                selectedEquipment == "Todos" ||
                        capitalizeText(exercise.equipment) == selectedEquipment

            matchesMuscle && matchesEquipment
        }

        adapter.submitList(filteredExercises)
    }
}