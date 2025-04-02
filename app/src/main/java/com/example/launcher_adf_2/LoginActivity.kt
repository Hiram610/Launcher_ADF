package com.example.launcher_adf_2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class LoginActivity : AppCompatActivity() {

    lateinit var userEditText : EditText
    lateinit var passwordEditText : EditText
    lateinit var loginBtn : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        userEditText = findViewById(R.id.user_txt)
        passwordEditText = findViewById(R.id.password_txt)
        loginBtn = findViewById(R.id.login_btn)

        loginBtn.setOnClickListener {
            if (verificarUsuario()) {
                var intent = Intent(this, AppsListActivity::class.java)
                startActivity(intent)
                Toast.makeText(this, "Modo Administrador", Toast.LENGTH_SHORT).show()
                finish()
            }
            else {
                Toast.makeText(this, "Usuario y Contraseña Incorrectos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun verificarUsuario() : Boolean {
        var usuario = userEditText.text.toString()
        var contrasena = passwordEditText.text.toString()

        if(usuario.equals("ADF")) {

            if(contrasena.equals("123456")){
                return true
            }

            else {
                return false
            }

        } else {
            return false
        }
    }
}