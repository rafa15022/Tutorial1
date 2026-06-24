package cm.a15022.athletelab.ui

import android.content.Intent
import android.widget.Button
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cm.a15022.athletelab.R
import cm.a15022.athletelab.repository.AuthRepository
import cm.a15022.athletelab.repository.WorkoutRepository
import cm.a15022.athletelab.ui.adapters.WorkoutAdapter
import cm.a15022.athletelab.utils.DateUtils
import kotlinx.coroutines.launch
import java.util.Calendar

class CalendarFragment : Fragment() {

    private var selectedDateMillis: Long = System.currentTimeMillis()
    private val authRepository = AuthRepository()
    private val workoutRepository = WorkoutRepository()
    private val adapter = WorkoutAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_calendar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val uid = authRepository.currentUser()?.uid ?: return
        val recycler = view.findViewById<RecyclerView>(R.id.dayWorkoutsRecycler)
        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = adapter
        val title = view.findViewById<TextView>(R.id.dayTitleText)
        val calendarView = view.findViewById<CalendarView>(R.id.calendarView)

        view.findViewById<Button>(R.id.addWorkoutSelectedDayButton).setOnClickListener {
            val uid = authRepository.currentUser()?.uid ?: return@setOnClickListener

            val intent = Intent(requireContext(), AddWorkoutActivity::class.java)
            intent.putExtra("userId", uid)
            intent.putExtra("selectedDateMillis", selectedDateMillis)

            startActivity(intent)
        }

        fun loadDay(millis: Long) {
            title.text = getString(R.string.workouts_of_day, DateUtils.formatDay(millis))
            lifecycleScope.launch {
                val list = workoutRepository.getWorkoutsByDay(uid, DateUtils.startOfDay(millis), DateUtils.endOfDay(millis))
                adapter.submitList(list)
            }
        }

        calendarView.setOnDateChangeListener { _, year, month, day ->
            val c = Calendar.getInstance()
            c.set(Calendar.YEAR, year)
            c.set(Calendar.MONTH, month)
            c.set(Calendar.DAY_OF_MONTH, day)
            c.set(Calendar.HOUR_OF_DAY, 18)
            c.set(Calendar.MINUTE, 0)
            c.set(Calendar.SECOND, 0)
            c.set(Calendar.MILLISECOND, 0)

            selectedDateMillis = c.timeInMillis

            loadDay(c.timeInMillis)
        }
        selectedDateMillis = System.currentTimeMillis()
        loadDay(selectedDateMillis)
    }
}
