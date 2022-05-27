package com.elnemr.runningtracker.presentation.util

import android.R
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView

fun AutoCompleteTextView.setItems(items: List<Any>?) {
    items?.let {
        val adapter = ArrayAdapter(context, R.layout.simple_spinner_dropdown_item, it)
        this.setAdapter(adapter)
    }
}

fun AutoCompleteTextView.atIndex(index: Int) {
    adapter?.let {
        val itemAtPosition = it.getItem(index) as String
        this.setText(itemAtPosition, false)
    }
}