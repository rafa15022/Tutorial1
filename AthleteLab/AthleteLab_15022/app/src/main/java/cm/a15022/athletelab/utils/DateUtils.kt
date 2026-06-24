package cm.a15022.athletelab.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {
    private val dayFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val shortFormat = SimpleDateFormat("dd/MM HH:mm", Locale.getDefault())

    fun formatDay(millis: Long): String = dayFormat.format(Date(millis))
    fun formatShort(millis: Long): String = shortFormat.format(Date(millis))

    fun startOfDay(millis: Long): Long {
        val c = Calendar.getInstance()
        c.timeInMillis = millis
        c.set(Calendar.HOUR_OF_DAY, 0)
        c.set(Calendar.MINUTE, 0)
        c.set(Calendar.SECOND, 0)
        c.set(Calendar.MILLISECOND, 0)
        return c.timeInMillis
    }

    fun endOfDay(millis: Long): Long = startOfDay(millis) + 24 * 60 * 60 * 1000 - 1

    fun endOfWeek(): Long {
        val c = Calendar.getInstance()
        c.add(Calendar.DAY_OF_YEAR, 7)
        return c.timeInMillis
    }
}
