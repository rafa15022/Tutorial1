package cm.a15022.athletelab.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import cm.a15022.athletelab.model.ProgressData

class SimpleChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {
    private var data: List<ProgressData> = emptyList()
    private var title: String = context.getString(cm.a15022.athletelab.R.string.chart_default_title)

    private val axisPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(148, 163, 184)
        strokeWidth = 3f
    }
    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(249, 115, 22)
        strokeWidth = 7f
        style = Paint.Style.STROKE
    }
    private val pointPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(37, 99, 235)
        style = Paint.Style.FILL
    }
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(17, 24, 39)
        textSize = 34f
        isFakeBoldText = true
    }
    private val smallTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(100, 116, 139)
        textSize = 24f
    }

    fun setChartData(title: String, data: List<ProgressData>) {
        this.title = title
        this.data = data
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val left = 60f
        val top = 70f
        val right = width - 35f
        val bottom = height - 55f
        canvas.drawText(title, left, 42f, textPaint)
        canvas.drawLine(left, bottom, right, bottom, axisPaint)
        canvas.drawLine(left, top, left, bottom, axisPaint)

        if (data.isEmpty()) {
            canvas.drawText(context.getString(cm.a15022.athletelab.R.string.no_data_enough), left + 20f, height / 2f, smallTextPaint)
            return
        }

        val max = data.maxOf { it.value }.coerceAtLeast(1f)
        val stepX = if (data.size == 1) 0f else (right - left) / (data.size - 1)
        var previousX = left
        var previousY = bottom - (data.first().value / max) * (bottom - top)

        data.forEachIndexed { index, item ->
            val x = left + stepX * index
            val y = bottom - (item.value / max) * (bottom - top)
            if (index > 0) canvas.drawLine(previousX, previousY, x, y, linePaint)
            canvas.drawCircle(x, y, 9f, pointPaint)
            canvas.drawText(item.label, x - 15f, bottom + 32f, smallTextPaint)
            previousX = x
            previousY = y
        }
    }
}
