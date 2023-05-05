package com.erensekkeli.gradconnect.guitools

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.erensekkeli.gradconnect.R

class PasswordChangeDialog(context: Context, private val listener: PasswordChangeDialogListener) : Dialog(context) {

    interface PasswordChangeDialogListener{
        fun onDialogOkButtonClicked(input: String, inputAgain: String)
        fun onDialogCancelButtonClicked()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.password_change_dialog)

        // Set the dialog title
        val newPassword = findViewById<TextView>(R.id.newPassword)
        val newPasswordAgain = findViewById<TextView>(R.id.newPasswordAgain)


        // Set up the OK button
        findViewById<Button>(R.id.changePasswordButton).setOnClickListener {
            listener.onDialogOkButtonClicked(newPassword.text.toString(), newPasswordAgain.text.toString())
            dismiss()
        }

        // Set up the Cancel button
        findViewById<ImageView>(R.id.dialog_cancel_button).setOnClickListener {
            listener.onDialogCancelButtonClicked()
            dismiss()
        }
    }

}