package com.example.launcher_adf_2

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class LoginFragment : DialogFragment () {

    lateinit var userTxt : EditText
    lateinit var passTxt : EditText
    lateinit var enterButton : Button

    @SuppressLint("UseGetLayoutInflater")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = AlertDialog.Builder(requireContext())
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.login_panel, null)

        userTxt = view.findViewById(R.id.user_editText)
        passTxt = view.findViewById(R.id.pass_editText)
        enterButton = view.findViewById(R.id.enter_btn)

        enterButton.setOnClickListener {
            if (verificar()) {
                val intent = Intent(requireContext(), AppsListActivity::class.java)
                startActivity(intent)
                Toast.makeText(requireContext(), "Modo Administrador", Toast.LENGTH_SHORT).show()
                dismiss()
                activity?.finish()
            } else {
                Toast.makeText(requireContext(), "Usuario y Contraseña Incorrectos", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setView(view)

        return builder.create()
    }

    private fun verificar() : Boolean{
        var username = userTxt.text.toString()
        var password = passTxt.text.toString()

        return (username.equals("ADF") && password.equals("123456"))
    }

}