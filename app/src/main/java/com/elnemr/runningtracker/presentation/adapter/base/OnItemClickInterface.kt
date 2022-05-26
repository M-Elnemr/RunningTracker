package com.elnemr.runningtracker.presentation.adapter.base

import android.view.View

interface OnItemClickInterface {
    fun onDetailsClicked(data: Any, view: View? = null)
    fun onMoreClicked(view: View, data: Any, position: Int){}
}