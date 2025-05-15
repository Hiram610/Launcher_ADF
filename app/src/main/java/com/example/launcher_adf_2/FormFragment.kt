package com.example.launcher_adf_2

import android.annotation.SuppressLint
import android.app.Dialog
import androidx.fragment.app.DialogFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.launcher_adf_2.Launcher_ADF.Companion.prefs

class FormFragment : DialogFragment() {

    lateinit var usernameTxt : EditText
    lateinit var passwordTxt : EditText
    lateinit var confirmButton : Button

    @SuppressLint("UseGetLayoutInflater", "MissingInflatedId")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = AlertDialog.Builder(requireContext())
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.form_panel, null)

        usernameTxt = view.findViewById(R.id.username_editText)
        passwordTxt = view.findViewById(R.id.password_editText)
        confirmButton = view.findViewById(R.id.confirm_btn)

        confirmButton.setOnClickListener {
            if (verificar()) {
                prefs.savePass(usernameTxt.text.toString(), passwordTxt.text.toString())
                Toast.makeText(requireContext(), "Credencial Actualizado", Toast.LENGTH_SHORT).show()
                dismiss()
            } else {
                Toast.makeText(requireContext(), "Favor de llenar los campos", Toast.LENGTH_SHORT).show()
                dismiss()
            }
        }

        builder.setView(view)

        return builder.create()
    }

    private fun verificar() : Boolean{
        var username = usernameTxt.text.toString()
        var password = passwordTxt.text.toString()

        if(username != "" && password != ""){
            return true
        } else {
            return false
        }
    }
}