package com.example.launcher_adf_2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class UpdateActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update)

        if(savedInstanceState == null) {
            val fragment = UpdateFragment()
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit()
        }
    }
}