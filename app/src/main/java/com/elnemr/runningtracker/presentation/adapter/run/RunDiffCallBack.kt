package com.elnemr.runningtracker.presentation.adapter.run

import androidx.recyclerview.widget.DiffUtil
import com.elnemr.runningtracker.data.db.Run

object RunDiffCallBack : DiffUtil.ItemCallback<Run>() {
    override fun areItemsTheSame(oldItem: Run, newItem: Run): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Run, newItem: Run): Boolean {
        return oldItem.hashCode() == newItem.hashCode()
    }
}