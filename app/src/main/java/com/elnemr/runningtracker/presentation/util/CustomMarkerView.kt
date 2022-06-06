package com.elnemr.runningtracker.presentation.util

import android.content.Context
import com.elnemr.runningtracker.data.db.Run
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import kotlinx.android.synthetic.main.marker_view.view.*
import java.text.SimpleDateFormat
import java.util.*

class CustomMarkerView(
    val runs: List<Run>,
    context: Context,
    layoutId: Int
) : MarkerView(context, layoutId) {

    override fun getOffset(): MPPointF {
        return MPPointF(-width / 2f, -height.toFloat())

    }

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        super.refreshContent(e, highlight)
        if (e == null) {
            return
        }

        val curRunId = e.x.toInt()
        val run = runs[curRunId]

        tvAvgSpeed.text = "${run.avgSpeedInKMH}km/h"
        tvCaloriesBurned.text = "${run.caloriesBurned}kcal"
        tvDuration.text = LocationUtils.getFormattedStopWatchTime(run.timeInMillis)
        tvDistance.text = "${run.distanceInMeters / 1000f}km"

        val calender = Calendar.getInstance().apply {
            timeInMillis = run.timestamp
        }
        val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
        tvDate.text = dateFormat.format(calender.time)

    }
}