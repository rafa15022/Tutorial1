package cm.a15022.athletelab.ui

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import cm.a15022.athletelab.R
import cm.a15022.athletelab.model.Sport
import cm.a15022.athletelab.model.Workout
import cm.a15022.athletelab.repository.WorkoutRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.max

class AddWorkoutActivity : AppCompatActivity() {

    private val workoutRepository = WorkoutRepository()

    private val typeOfficialMatch = "official_match"
    private val typeTraining = "training"
    private val typeOfficialRace = "official_race"

    private fun getFootballPositions(): Array<String> {
        return arrayOf(
            getString(R.string.goalkeeper),
            getString(R.string.centre_back),
            getString(R.string.right_back),
            getString(R.string.left_back),
            getString(R.string.defensive_midfielder),
            getString(R.string.central_midfielder),
            getString(R.string.attacking_midfielder),
            getString(R.string.right_midfielder),
            getString(R.string.left_midfielder),
            getString(R.string.right_winger),
            getString(R.string.left_winger),
            getString(R.string.striker)
        )
    }

    private lateinit var dateButton: Button
    private lateinit var startTimeButton: Button
    private lateinit var endTimeButton: Button

    private val startCalendar = Calendar.getInstance()
    private val endCalendar = Calendar.getInstance()

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    private lateinit var sportSpinner: Spinner
    private lateinit var typeSpinner: Spinner

    private lateinit var matchResultInput: EditText
    private lateinit var titleInput: EditText
    private lateinit var field1Input: EditText
    private lateinit var field2Input: EditText
    private lateinit var field3Input: EditText
    private lateinit var field4Input: EditText
    private lateinit var notesInput: EditText

    private lateinit var exercisesSection: LinearLayout
    private lateinit var exercisesContainer: LinearLayout
    private lateinit var simpleFieldsSection: LinearLayout
    private lateinit var addExerciseButton: Button
    private lateinit var saveButton: Button
    private lateinit var autoResultText: TextView

    private var currentSport = Sport.MUSCLE
    private var currentType = ""
    private var editingWorkoutId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_workout)

        val uid = intent.getStringExtra("userId")
            ?: FirebaseAuth.getInstance().currentUser?.uid
            ?: run {
                Toast.makeText(this, getString(R.string.invalid_session), Toast.LENGTH_SHORT).show()
                finish()
                return
            }

        dateButton = findViewById(R.id.dateButton)
        startTimeButton = findViewById(R.id.startTimeButton)
        endTimeButton = findViewById(R.id.endTimeButton)

        val selectedDateMillis = intent.getLongExtra("selectedDateMillis", 0L)

        if (selectedDateMillis > 0L) {
            startCalendar.timeInMillis = selectedDateMillis
        } else {
            startCalendar.timeInMillis = System.currentTimeMillis()
        }

        startCalendar.set(Calendar.SECOND, 0)
        startCalendar.set(Calendar.MILLISECOND, 0)

        endCalendar.timeInMillis = startCalendar.timeInMillis
        endCalendar.add(Calendar.HOUR_OF_DAY, 1)

        dateButton.setOnClickListener {
            showDatePicker()
        }

        startTimeButton.setOnClickListener {
            showStartTimePicker()
        }

        endTimeButton.setOnClickListener {
            showEndTimePicker()
        }

        sportSpinner = findViewById(R.id.sportSpinner)
        typeSpinner = findViewById(R.id.typeSpinner)

        titleInput = findViewById(R.id.titleInput)
        matchResultInput = findViewById(R.id.matchResultInput)
        field1Input = findViewById(R.id.field1Input)
        field2Input = findViewById(R.id.field2Input)
        field3Input = findViewById(R.id.field3Input)
        field4Input = findViewById(R.id.field4Input)
        notesInput = findViewById(R.id.notesInput)

        exercisesSection = findViewById(R.id.exercisesSection)
        exercisesContainer = findViewById(R.id.exercisesContainer)
        simpleFieldsSection = findViewById(R.id.simpleFieldsSection)
        addExerciseButton = findViewById(R.id.addExerciseButton)
        saveButton = findViewById(R.id.saveButton)
        autoResultText = findViewById(R.id.autoResultText)

        updateDateTimeButtons()
        setupSportSpinner()
        setupListeners(uid)

        editingWorkoutId = intent.getStringExtra("workoutId") ?: ""

        if (editingWorkoutId.isNotBlank()) {
            loadWorkoutForEdit(editingWorkoutId)
        } else {
            showMuscleFields()
        }
    }

    private fun setupSportSpinner() {
        val sportsLabels = listOf(
            getString(R.string.sport_muscle),
            getString(R.string.sport_football),
            getString(R.string.sport_running)
        )

        sportSpinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            sportsLabels
        )

        sportSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when (position) {
                    0 -> {
                        currentSport = Sport.MUSCLE
                        currentType = ""
                        showMuscleFields()
                    }

                    1 -> {
                        currentSport = Sport.FOOTBALL
                        setupTypeSpinner(
                            listOf(getString(R.string.official_match), getString(R.string.training)),
                            listOf(typeOfficialMatch, typeTraining)
                        )
                    }

                    2 -> {
                        currentSport = Sport.RUNNING
                        setupTypeSpinner(
                            listOf(getString(R.string.official_race), getString(R.string.training)),
                            listOf(typeOfficialRace, typeTraining)
                        )
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupTypeSpinner(labels: List<String>, internalTypes: List<String>) {
        typeSpinner.visibility = View.VISIBLE

        typeSpinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            labels
        )

        typeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                currentType = internalTypes[position]

                when {
                    currentSport == Sport.FOOTBALL && currentType == typeOfficialMatch -> showFootballMatchFields()
                    currentSport == Sport.FOOTBALL && currentType == typeTraining -> showFootballTrainingFields()
                    currentSport == Sport.RUNNING && currentType == typeOfficialRace -> showRunningRaceFields()
                    currentSport == Sport.RUNNING && currentType == typeTraining -> showRunningTrainingFields()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupListeners(uid: String) {
        addExerciseButton.setOnClickListener {
            addExerciseRow()
        }

        field2Input.addTextChangedListener {
            updateAutoResult()
        }

        field3Input.addTextChangedListener {
            updateAutoResult()
        }

        saveButton.setOnClickListener {
            saveWorkout(uid)
        }
    }

    private fun showAllSimpleFields() {
        field1Input.visibility = View.VISIBLE
        field2Input.visibility = View.VISIBLE
        field3Input.visibility = View.VISIBLE
        field4Input.visibility = View.VISIBLE
    }

    private fun showMuscleFields() {
        resetPositionField()
        typeSpinner.visibility = View.GONE
        exercisesSection.visibility = View.VISIBLE
        simpleFieldsSection.visibility = View.GONE
        autoResultText.text = ""

        exercisesContainer.removeAllViews()
        addExerciseRow()
    }

    private fun showFootballMatchFields() {
        exercisesSection.visibility = View.GONE
        simpleFieldsSection.visibility = View.VISIBLE
        autoResultText.text = ""

        showAllSimpleFields()

        titleInput.hint = getString(R.string.match_title)
        matchResultInput.visibility = View.VISIBLE
        matchResultInput.hint = getString(R.string.match_result_hint)

        field1Input.hint = getString(R.string.goals)
        field2Input.hint = getString(R.string.assists)
        field3Input.hint = getString(R.string.played_minutes)
        field4Input.hint = getString(R.string.position)

        clearSimpleFields()
        matchResultInput.setText("")

        field4Input.setText("")
        field4Input.isFocusable = false
        field4Input.isClickable = true
        field4Input.inputType = InputType.TYPE_NULL

        field4Input.setOnClickListener {
            showFootballPositionDialog()
        }
    }

    private fun showFootballPositionDialog() {
        val footballPositions = getFootballPositions()

        AlertDialog.Builder(this)
            .setTitle(getString(R.string.choose_position))
            .setItems(footballPositions) { _, which ->
                field4Input.setText(footballPositions[which])
            }
            .show()
    }

    private fun resetPositionField() {
        titleInput.hint = getString(R.string.workout_title_hint)

        matchResultInput.visibility = View.GONE
        matchResultInput.setText("")

        field4Input.isFocusable = true
        field4Input.isFocusableInTouchMode = true
        field4Input.isClickable = true
        field4Input.inputType = InputType.TYPE_CLASS_TEXT
        field4Input.setOnClickListener(null)
    }

    private fun showFootballTrainingFields() {
        resetPositionField()
        exercisesSection.visibility = View.VISIBLE
        simpleFieldsSection.visibility = View.GONE
        autoResultText.text = ""

        exercisesContainer.removeAllViews()
        addExerciseRow()
    }

    private fun showRunningRaceFields() {
        resetPositionField()
        exercisesSection.visibility = View.GONE
        simpleFieldsSection.visibility = View.VISIBLE

        showAllSimpleFields()

        titleInput.hint = getString(R.string.race_title)

        field1Input.hint = getString(R.string.race_type)
        field2Input.hint = getString(R.string.distance_km)
        field3Input.visibility = View.GONE
        field4Input.hint = getString(R.string.final_position_hint)

        clearSimpleFields()
        updateAutoResult()
    }

    private fun showRunningTrainingFields() {
        resetPositionField()
        exercisesSection.visibility = View.GONE
        simpleFieldsSection.visibility = View.VISIBLE

        showAllSimpleFields()

        titleInput.hint = getString(R.string.workout_title_hint)

        field1Input.hint = getString(R.string.training_type)
        field2Input.hint = getString(R.string.distance_km)
        field3Input.visibility = View.GONE
        field4Input.hint = getString(R.string.observation)

        clearSimpleFields()
        updateAutoResult()
    }

    private fun updateDateTimeButtons() {
        dateButton.text = getString(R.string.date_label, dateFormat.format(startCalendar.time))
        startTimeButton.text = getString(R.string.start_label, timeFormat.format(startCalendar.time))
        endTimeButton.text = getString(R.string.end_label, timeFormat.format(endCalendar.time))
    }

    private fun showDatePicker() {
        val year = startCalendar.get(Calendar.YEAR)
        val month = startCalendar.get(Calendar.MONTH)
        val day = startCalendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                startCalendar.set(Calendar.YEAR, selectedYear)
                startCalendar.set(Calendar.MONTH, selectedMonth)
                startCalendar.set(Calendar.DAY_OF_MONTH, selectedDay)

                endCalendar.set(Calendar.YEAR, selectedYear)
                endCalendar.set(Calendar.MONTH, selectedMonth)
                endCalendar.set(Calendar.DAY_OF_MONTH, selectedDay)

                updateDateTimeButtons()
                updateAutoResult()
            },
            year,
            month,
            day
        ).show()
    }

    private fun showStartTimePicker() {
        val hour = startCalendar.get(Calendar.HOUR_OF_DAY)
        val minute = startCalendar.get(Calendar.MINUTE)

        TimePickerDialog(
            this,
            { _, selectedHour, selectedMinute ->
                startCalendar.set(Calendar.HOUR_OF_DAY, selectedHour)
                startCalendar.set(Calendar.MINUTE, selectedMinute)

                if (endCalendar.timeInMillis <= startCalendar.timeInMillis) {
                    endCalendar.timeInMillis = startCalendar.timeInMillis
                    endCalendar.add(Calendar.HOUR_OF_DAY, 1)
                }

                updateDateTimeButtons()
                updateAutoResult()
            },
            hour,
            minute,
            true
        ).show()
    }

    private fun showEndTimePicker() {
        val hour = endCalendar.get(Calendar.HOUR_OF_DAY)
        val minute = endCalendar.get(Calendar.MINUTE)

        TimePickerDialog(
            this,
            { _, selectedHour, selectedMinute ->
                endCalendar.set(Calendar.HOUR_OF_DAY, selectedHour)
                endCalendar.set(Calendar.MINUTE, selectedMinute)

                if (endCalendar.timeInMillis <= startCalendar.timeInMillis) {
                    endCalendar.add(Calendar.DAY_OF_MONTH, 1)
                }

                updateDateTimeButtons()
                updateAutoResult()
            },
            hour,
            minute,
            true
        ).show()
    }

    private fun getWorkoutDurationMinutes(): Double {
        val diffMillis = endCalendar.timeInMillis - startCalendar.timeInMillis
        return max(0.0, diffMillis / 60000.0)
    }

    private fun addExerciseRow() {
        val box = LinearLayout(this)
        box.orientation = LinearLayout.VERTICAL
        box.setPadding(0, 12, 0, 12)

        if (currentSport == Sport.MUSCLE) {
            box.tag = "muscle"

            val exerciseInput = createInput(getString(R.string.exercise))
            box.addView(exerciseInput)

            val setsContainer = LinearLayout(this)
            setsContainer.orientation = LinearLayout.VERTICAL
            box.addView(setsContainer)

            val addSetButton = Button(this)
            addSetButton.text = getString(R.string.add_set)
            addSetButton.setAllCaps(false)
            addSetButton.setTextColor(Color.WHITE)
            addSetButton.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#F97316"))

            val buttonParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                46.dp()
            )
            buttonParams.setMargins(0, 8, 0, 0)
            addSetButton.layoutParams = buttonParams

            addSetButton.setOnClickListener {
                addSetRow(setsContainer)
            }

            box.addView(addSetButton)
            addSetRow(setsContainer)
        } else {
            box.tag = "simple"

            val exerciseInput = createInput(getString(R.string.exercise))
            val setsInput = createInput(getString(R.string.sets))
            val repsInput = createInput(getString(R.string.repetitions))
            val extraInput = createInput(getString(R.string.minutes_intensity))

            box.addView(exerciseInput)
            box.addView(setsInput)
            box.addView(repsInput)
            box.addView(extraInput)
        }

        exercisesContainer.addView(box)
    }

    private fun addSetRow(
        setsContainer: LinearLayout,
        loadValue: String = "",
        repsValue: String = ""
    ) {
        val row = LinearLayout(this)
        row.orientation = LinearLayout.HORIZONTAL
        row.setPadding(0, 8, 0, 0)

        val loadInput = EditText(this)
        loadInput.hint = getString(R.string.load_kg)
        loadInput.setSingleLine(true)
        loadInput.setPadding(14, 10, 14, 10)
        loadInput.background = getDrawable(R.drawable.bg_input)
        loadInput.setText(loadValue)

        val repsInput = EditText(this)
        repsInput.hint = getString(R.string.reps)
        repsInput.setSingleLine(true)
        repsInput.setPadding(14, 10, 14, 10)
        repsInput.background = getDrawable(R.drawable.bg_input)
        repsInput.setText(repsValue)

        val loadParams = LinearLayout.LayoutParams(
            0,
            54.dp(),
            1f
        )
        loadParams.setMargins(0, 0, 6, 0)

        val repsParams = LinearLayout.LayoutParams(
            0,
            54.dp(),
            1f
        )
        repsParams.setMargins(6, 0, 0, 0)

        loadInput.layoutParams = loadParams
        repsInput.layoutParams = repsParams

        row.addView(loadInput)
        row.addView(repsInput)

        setsContainer.addView(row)
    }

    private fun createInput(hint: String): EditText {
        val input = EditText(this)
        input.hint = hint
        input.setSingleLine(true)
        input.setPadding(14, 10, 14, 10)
        input.background = getDrawable(R.drawable.bg_input)

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            54.dp()
        )

        params.setMargins(0, 8, 0, 0)
        input.layoutParams = params

        return input
    }

    private fun loadWorkoutForEdit(workoutId: String) {
        lifecycleScope.launch {
            try {
                val workout = workoutRepository.getWorkoutById(workoutId)

                if (workout == null) {
                    Toast.makeText(this@AddWorkoutActivity, getString(R.string.workout_not_found), Toast.LENGTH_SHORT).show()
                    finish()
                    return@launch
                }

                titleInput.setText(workout.title)
                notesInput.setText(workout.notes)

                startCalendar.timeInMillis = workout.dateMillis
                endCalendar.timeInMillis =
                    workout.dateMillis + (workout.durationMinutes * 60000).toLong()

                updateDateTimeButtons()

                when (workout.sport) {
                    Sport.MUSCLE -> {
                        sportSpinner.setSelection(0)

                        sportSpinner.postDelayed({
                            showMuscleFields()
                            titleInput.setText(workout.title)
                            notesInput.setText(workout.notes)
                            fillMuscleExercisesFromText(workout.exercise)
                        }, 200)
                    }

                    Sport.FOOTBALL -> {
                        sportSpinner.setSelection(1)

                        sportSpinner.postDelayed({
                            if (workout.intensity == "Treino" || workout.exercise.isNotBlank()) {
                                typeSpinner.setSelection(1)

                                typeSpinner.postDelayed({
                                    showFootballTrainingFields()
                                    titleInput.setText(workout.title)
                                    notesInput.setText(workout.notes)
                                    fillSimpleExercisesFromText(workout.exercise)
                                }, 200)
                            } else {
                                typeSpinner.setSelection(0)

                                typeSpinner.postDelayed({
                                    showFootballMatchFields()
                                    titleInput.setText(workout.title)
                                    notesInput.setText(workout.notes)

                                    matchResultInput.setText(
                                        workout.trainingType.removePrefix("Resultado:").trim()
                                    )

                                    field1Input.setText(workout.goals.toString())
                                    field2Input.setText(workout.assists.toString())
                                    field3Input.setText(workout.minutesPlayed.toString())
                                    field4Input.setText(workout.position)
                                }, 200)
                            }
                        }, 200)
                    }

                    Sport.RUNNING -> {
                        sportSpinner.setSelection(2)

                        sportSpinner.postDelayed({
                            if (workout.trainingType.startsWith("Prova oficial")) {
                                typeSpinner.setSelection(0)

                                typeSpinner.postDelayed({
                                    showRunningRaceFields()
                                    titleInput.setText(workout.title)
                                    notesInput.setText(workout.notes)
                                    field1Input.setText(
                                        workout.trainingType
                                            .removePrefix("Prova oficial:")
                                            .trim()
                                    )
                                    field2Input.setText(workout.distanceKm.toString())
                                    field4Input.setText(workout.position)
                                    updateAutoResult()
                                }, 200)
                            } else {
                                typeSpinner.setSelection(1)

                                typeSpinner.postDelayed({
                                    showRunningTrainingFields()
                                    titleInput.setText(workout.title)
                                    notesInput.setText(workout.notes)
                                    field1Input.setText(workout.trainingType)
                                    field2Input.setText(workout.distanceKm.toString())
                                    field4Input.setText(workout.intensity)
                                    updateAutoResult()
                                }, 200)
                            }
                        }, 200)
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@AddWorkoutActivity,
                    getString(R.string.load_workout_error),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun saveWorkout(uid: String) {
        val title = titleInput.text.toString().trim()

        if (title.isBlank()) {
            Toast.makeText(this, getString(R.string.write_workout_title), Toast.LENGTH_SHORT).show()
            return
        }

        val notes = notesInput.text.toString().trim()
        val calculatedDuration = getWorkoutDurationMinutes()

        val workout = when {
            currentSport == Sport.MUSCLE -> {
                Workout(
                    userId = uid,
                    sport = Sport.MUSCLE,
                    title = title,
                    dateMillis = startCalendar.timeInMillis,
                    durationMinutes = calculatedDuration,
                    notes = notes,
                    exercise = buildExercisesText()
                )
            }

            currentSport == Sport.FOOTBALL && currentType == typeOfficialMatch -> {
                Workout(
                    userId = uid,
                    sport = Sport.FOOTBALL,
                    title = title,
                    dateMillis = startCalendar.timeInMillis,
                    durationMinutes = calculatedDuration,
                    notes = notes,
                    trainingType = "Resultado: ${matchResultInput.text.toString().trim()}",
                    goals = field1Input.text.toString().toIntOrNull() ?: 0,
                    assists = field2Input.text.toString().toIntOrNull() ?: 0,
                    minutesPlayed = field3Input.text.toString().toIntOrNull() ?: 0,
                    position = field4Input.text.toString()
                )
            }

            currentSport == Sport.FOOTBALL && currentType == typeTraining -> {
                Workout(
                    userId = uid,
                    sport = Sport.FOOTBALL,
                    title = title,
                    dateMillis = startCalendar.timeInMillis,
                    durationMinutes = calculatedDuration,
                    notes = notes,
                    exercise = buildExercisesText(),
                    intensity = "Treino"
                )
            }

            currentSport == Sport.RUNNING && currentType == typeOfficialRace -> {
                Workout(
                    userId = uid,
                    sport = Sport.RUNNING,
                    title = title,
                    dateMillis = startCalendar.timeInMillis,
                    durationMinutes = calculatedDuration,
                    notes = notes,
                    trainingType = "Prova oficial: ${field1Input.text}",
                    distanceKm = field2Input.text.toString()
                        .replace(",", ".")
                        .toDoubleOrNull() ?: 0.0,
                    timeMinutes = calculatedDuration,
                    pace = autoResultText.text.toString(),
                    position = field4Input.text.toString()
                )
            }

            else -> {
                Workout(
                    userId = uid,
                    sport = Sport.RUNNING,
                    title = title,
                    dateMillis = startCalendar.timeInMillis,
                    durationMinutes = calculatedDuration,
                    notes = notes,
                    trainingType = field1Input.text.toString(),
                    distanceKm = field2Input.text.toString()
                        .replace(",", ".")
                        .toDoubleOrNull() ?: 0.0,
                    timeMinutes = calculatedDuration,
                    pace = autoResultText.text.toString(),
                    intensity = field4Input.text.toString()
                )
            }
        }

        if (editingWorkoutId.isNotBlank()) {
            workout.id = editingWorkoutId
        }

        lifecycleScope.launch {
            try {
                workoutRepository.saveWorkout(workout)
                setResult(Activity.RESULT_OK)
                Toast.makeText(this@AddWorkoutActivity, getString(R.string.workout_saved), Toast.LENGTH_SHORT).show()
                finish()
            } catch (e: Exception) {
                Toast.makeText(
                    this@AddWorkoutActivity,
                    e.message ?: getString(R.string.save_error),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun fillMuscleExercisesFromText(text: String) {
        exercisesContainer.removeAllViews()

        val lines = text.lines()
        var currentExerciseName = ""
        val currentSets = mutableListOf<Pair<String, String>>()

        fun flushExercise() {
            if (currentExerciseName.isNotBlank()) {
                addMuscleExerciseFromData(currentExerciseName, currentSets.toList())
            }

            currentExerciseName = ""
            currentSets.clear()
        }

        val exerciseRegex = Regex("""^\d+\.\s*(.+)$""")
        val setRegex = Regex("""Set\s+\d+:\s*([0-9.,]+)\s*kg\s*x\s*([0-9]+)\s*reps""")

        for (line in lines) {
            val cleanLine = line.trim()

            val exerciseMatch = exerciseRegex.find(cleanLine)
            val setMatch = setRegex.find(cleanLine)

            when {
                exerciseMatch != null -> {
                    flushExercise()
                    currentExerciseName = exerciseMatch.groupValues[1]
                }

                setMatch != null -> {
                    val load = setMatch.groupValues[1]
                    val reps = setMatch.groupValues[2]
                    currentSets.add(load to reps)
                }
            }
        }

        flushExercise()

        if (exercisesContainer.childCount == 0) {
            addExerciseRow()
        }
    }

    private fun addMuscleExerciseFromData(
        exerciseName: String,
        sets: List<Pair<String, String>>
    ) {
        val box = LinearLayout(this)
        box.orientation = LinearLayout.VERTICAL
        box.setPadding(0, 12, 0, 12)
        box.tag = "muscle"

        val exerciseInput = createInput(getString(R.string.exercise))
        exerciseInput.setText(exerciseName)
        box.addView(exerciseInput)

        val setsContainer = LinearLayout(this)
        setsContainer.orientation = LinearLayout.VERTICAL
        box.addView(setsContainer)

        val addSetButton = Button(this)
        addSetButton.text = getString(R.string.add_set)
        addSetButton.setAllCaps(false)
        addSetButton.setTextColor(Color.WHITE)
        addSetButton.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#F97316"))

        val buttonParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            46.dp()
        )
        buttonParams.setMargins(0, 8, 0, 0)
        addSetButton.layoutParams = buttonParams

        addSetButton.setOnClickListener {
            addSetRow(setsContainer)
        }

        box.addView(addSetButton)

        if (sets.isEmpty()) {
            addSetRow(setsContainer)
        } else {
            sets.forEach { set ->
                addSetRow(setsContainer, set.first, set.second)
            }
        }

        exercisesContainer.addView(box)
    }

    private fun fillSimpleExercisesFromText(text: String) {
        exercisesContainer.removeAllViews()

        if (text.isBlank()) {
            addExerciseRow()
            return
        }

        val lines = text.lines().filter { it.isNotBlank() }

        for (line in lines) {
            addExerciseRow()

            val box =
                exercisesContainer.getChildAt(exercisesContainer.childCount - 1) as LinearLayout
            val cleanLine = line.substringAfter(". ").trim()

            val parts = cleanLine.split(" - ")

            if (parts.isNotEmpty()) {
                (box.getChildAt(0) as EditText).setText(parts.getOrNull(0) ?: "")
            }

            if (parts.size >= 2) {
                val seriesPart = parts[1]
                val sets = Regex("""(\d+)\s*séries""")
                    .find(seriesPart)
                    ?.groupValues
                    ?.get(1) ?: ""

                val reps = Regex("""x\s*(\d+)\s*reps""")
                    .find(seriesPart)
                    ?.groupValues
                    ?.get(1) ?: ""

                (box.getChildAt(1) as EditText).setText(sets)
                (box.getChildAt(2) as EditText).setText(reps)
            }

            if (parts.size >= 3) {
                (box.getChildAt(3) as EditText).setText(parts[2])
            }
        }
    }

    private fun buildExercisesText(): String {
        val result = StringBuilder()

        for (i in 0 until exercisesContainer.childCount) {
            val box = exercisesContainer.getChildAt(i) as LinearLayout

            if (box.tag == "muscle") {
                val exerciseName = (box.getChildAt(0) as EditText).text.toString().trim()
                val setsContainer = box.getChildAt(1) as LinearLayout

                if (exerciseName.isNotBlank()) {
                    result.append("${i + 1}. $exerciseName\n")

                    for (j in 0 until setsContainer.childCount) {
                        val row = setsContainer.getChildAt(j) as LinearLayout

                        val load = (row.getChildAt(0) as EditText).text.toString().trim()
                        val reps = (row.getChildAt(1) as EditText).text.toString().trim()

                        if (load.isNotBlank() || reps.isNotBlank()) {
                            result.append(
                                "   Set ${j + 1}: ${load.ifBlank { "0" }} kg x ${reps.ifBlank { "0" }} reps\n"
                            )
                        }
                    }
                }
            } else {
                val exercise = (box.getChildAt(0) as EditText).text.toString()
                val sets = (box.getChildAt(1) as EditText).text.toString()
                val reps = (box.getChildAt(2) as EditText).text.toString()
                val extra = (box.getChildAt(3) as EditText).text.toString()

                if (exercise.isNotBlank()) {
                    val setsLabel = getString(R.string.sets).lowercase(Locale.getDefault())
                    result.append("${i + 1}. $exercise - $sets $setsLabel x $reps reps - $extra\n")
                }
            }
        }

        return result.toString().trim()
    }

    private fun updateAutoResult() {
        if (currentSport != Sport.RUNNING) return

        val distance = field2Input.text.toString()
            .replace(",", ".")
            .toDoubleOrNull()

        val time = getWorkoutDurationMinutes()

        if (distance != null && distance > 0 && time > 0) {
            val pace = time / distance
            autoResultText.text = getString(R.string.average_pace_format, pace)
        } else {
            autoResultText.text = ""
        }
    }

    private fun clearSimpleFields() {
        field1Input.setText("")
        field2Input.setText("")
        field3Input.setText("")
        field4Input.setText("")
    }

    private fun Int.dp(): Int {
        return (this * resources.displayMetrics.density).toInt()
    }
}