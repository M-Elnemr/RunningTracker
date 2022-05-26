package com.elnemr.runningtracker.presentation.adapter.run

import coil.load
import com.elnemr.runningtracker.data.db.Run
import com.elnemr.runningtracker.databinding.ItemRunBinding
import com.elnemr.runningtracker.presentation.adapter.base.BaseViewHolder
import com.elnemr.runningtracker.presentation.util.LocationUtils
import java.text.SimpleDateFormat
import java.util.*


class RunViewHolder(private val binding: ItemRunBinding) :
    BaseViewHolder<Run>(binding) {

    override fun bind(result: Run) {
        binding.tvAvgSpeed.text = "${result.avgSpeedInKMH}km/h"
        binding.tvCalories.text = "${result.caloriesBurned}kcal"
        binding.tvTime.text = LocationUtils.getFormattedStopWatchTime(result.timeInMillis)
        binding.tvDistance.text = "${result.distanceInMeters / 1000f}km"

        val calender = Calendar.getInstance().apply {
            timeInMillis = result.timestamp
        }
        val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
        binding.tvDate.text = dateFormat.format(calender.time)

        result.img?.let {
            binding.ivRunImage.load(it) {
                crossfade(true)
            }
        }
    }
}