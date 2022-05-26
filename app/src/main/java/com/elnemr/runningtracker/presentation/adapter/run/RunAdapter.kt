package com.elnemr.runningtracker.presentation.adapter.run

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import com.elnemr.runningtracker.data.db.Run
import com.elnemr.runningtracker.databinding.ItemRunBinding
import com.elnemr.runningtracker.presentation.adapter.base.BaseAdapter
import com.elnemr.runningtracker.presentation.adapter.base.BaseViewHolder
import com.elnemr.runningtracker.presentation.adapter.base.OnItemClickInterface

class RunAdapter(private val onClickListener: OnItemClickInterface) :
    BaseAdapter<Run>() {

    private val mDiffer = AsyncListDiffer(this, RunDiffCallBack)

    override fun setDataList(dataList: List<Run>) {
        mDiffer.submitList(dataList)
    }

    override fun addDataList(dataList: List<Run>) {
        mDiffer.currentList.addAll(dataList)
    }

    override fun clearDataList() {
        mDiffer.currentList.clear()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<Run> =
        RunViewHolder(
            ItemRunBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )


    override fun onBindViewHolder(holder: BaseViewHolder<Run>, position: Int) =
        holder.bind(mDiffer.currentList[position])

    override fun getItemCount(): Int = mDiffer.currentList.size


}