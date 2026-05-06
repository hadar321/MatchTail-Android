package com.example.matchtail.utils

import android.app.AlertDialog
import android.content.Context

class BaseAlert(private val title: String, private val message: String, private val context: Context) {
    fun show() {
        buildAlertDialog().show()
    }

    private fun buildAlertDialog(): AlertDialog {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }

        return builder.create()
    }
}