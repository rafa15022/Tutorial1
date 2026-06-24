package cm.a15022.athletelab.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import cm.a15022.athletelab.R
import cm.a15022.athletelab.model.ProgressData
import cm.a15022.athletelab.repository.AuthRepository
import cm.a15022.athletelab.repository.WorkoutRepository
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ProgressFragment : Fragment() {

    private val authRepository = AuthRepository()
    private val workoutRepository = WorkoutRepository()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_progress, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val uid = authRepository.currentUser()?.uid ?: return

        val chart = view.findViewById<SimpleChartView>(R.id.chartView)
        val summary = view.findViewById<TextView>(R.id.progressSummaryText)

        workoutRepository.observeMyWorkouts(uid) { workouts ->

            val weeklyData = buildWeeklyWorkoutCounts(
                workouts.map { it.dateMillis }
            )

            val chartData = weeklyData.map {
                ProgressData(it.label, it.count.toFloat())
            }

            chart.setChartData(getString(R.string.weekly_workouts_chart), chartData)

            val totalTreinos = weeklyData.sumOf { it.count }

            summary.text = getString(R.string.progress_chart_summary, totalTreinos)
        }
    }

    private fun buildWeeklyWorkoutCounts(workoutDates: List<Long>): List<WeekData> {
        val result = mutableListOf<WeekData>()

        val currentWeekStart = getStartOfWeek(System.currentTimeMillis())

        for (i in 7 downTo 0) {
            val startCalendar = Calendar.getInstance()
            startCalendar.timeInMillis = currentWeekStart
            startCalendar.add(Calendar.WEEK_OF_YEAR, -i)

            val startMillis = startCalendar.timeInMillis

            val endCalendar = Calendar.getInstance()
            endCalendar.timeInMillis = startMillis
            endCalendar.add(Calendar.DAY_OF_YEAR, 7)

            val endMillis = endCalendar.timeInMillis

            val count = workoutDates.count {
                it >= startMillis && it < endMillis
            }

            result.add(
                WeekData(
                    label = formatWeekLabel(startMillis),
                    count = count
                )
            )
        }

        return result
    }

    private fun getStartOfWeek(millis: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = millis
        calendar.firstDayOfWeek = Calendar.MONDAY

        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

        val diff = if (dayOfWeek == Calendar.SUNDAY) {
            -6
        } else {
            Calendar.MONDAY - dayOfWeek
        }

        calendar.add(Calendar.DAY_OF_MONTH, diff)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        return calendar.timeInMillis
    }

    private fun formatWeekLabel(millis: Long): String {
        val formatter = SimpleDateFormat("dd/MM", Locale("pt", "PT"))
        return formatter.format(millis)
    }

    data class WeekData(
        val label: String,
        val count: Int
    )
}