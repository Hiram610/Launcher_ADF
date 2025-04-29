package com.example.launcher_adf_2

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.launcher_adf_2.MainActivity.LoginSuccessListenter

class LoginFragment : DialogFragment() {

    private var loginSuccessListenter: LoginSuccessListenter? = null
    private var action: String = ""

    lateinit var userTxt : EditText
    lateinit var passTxt : EditText
    lateinit var enterButton : Button


    @SuppressLint("UseGetLayoutInflater")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = AlertDialog.Builder(requireContext())
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.login_panel, null)

        action = arguments?.getString("action") ?: ""

        userTxt = view.findViewById(R.id.user_editText)
        passTxt = view.findViewById(R.id.pass_editText)
        enterButton = view.findViewById(R.id.enter_btn)

        enterButton.setOnClickListener {
            if (verificar()) {
                loginSuccessListenter?.onLoginSuccess(action)
                dismiss()
            } else {
                Toast.makeText(requireContext(), "Usuario y Contraseña Incorrectos", Toast.LENGTH_SHORT).show()
                dismiss()
            }
        }

        builder.setView(view)

        return builder.create()
    }

    fun verificar() : Boolean{
        var username = userTxt.text.toString()
        var password = passTxt.text.toString()

        return (username.equals("ADF") && password.equals("123456"))
    }

    fun setLoginSuccessListenter(listener: LoginSuccessListenter) {
        loginSuccessListenter = listener
    }
}