package com.elnemr.runningtracker.presentation.util

import android.content.Context
import android.content.DialogInterface
import com.elnemr.runningtracker.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

fun showDialog(
    context: Context,
    title: Int,
    message: Int,
    icon: Int,
    yes: (dialog: DialogInterface) -> Unit
) {
    val dialog = MaterialAlertDialogBuilder(context, R.style.AlertDialogTheme)
        .setTitle(title)
        .setMessage(message)
        .setIcon(icon)
        .setPositiveButton("Yes") { dialog, _ ->
            yes(dialog)
        }
        .setNegativeButton("No") { dialog, _ ->
            dialog.cancel()
        }.create()

    dialog.show()
}