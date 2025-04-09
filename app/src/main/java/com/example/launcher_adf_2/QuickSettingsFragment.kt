package com.example.launcher_adf_2

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context.WIFI_SERVICE
import android.content.Intent
import android.net.wifi.WifiManager
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.widget.ImageButton
import androidx.fragment.app.DialogFragment

class QuickSettingsFragment : DialogFragment() {
    lateinit var wifiManager : WifiManager
    lateinit var  btnWifi : ImageButton
    @SuppressLint("UseGetLayoutInflater")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = AlertDialog.Builder(requireContext())
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.quick_panel, null)

        btnWifi = view.findViewById(R.id.wifi_button)

        wifiManager = requireContext().applicationContext.getSystemService(WIFI_SERVICE) as WifiManager

        verificarColorWifi()

        btnWifi.setOnClickListener {
            if(wifiManager.isWifiEnabled) {
                wifiManager.setWifiEnabled(false)
                btnWifi.setBackgroundResource(R.mipmap.wifi_state_2)
            }
            else {
                wifiManager.setWifiEnabled(true)
                btnWifi.setBackgroundResource(R.mipmap.wifi_state_1)
            }
        }

        btnWifi.setOnLongClickListener {
            startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
            true
        }

        builder.setView(view)

        return builder.create()
    }

    @SuppressLint("ResourceAsColor")
    private fun verificarColorWifi() {

        when(wifiManager.isWifiEnabled) {
            true -> {
                btnWifi.setBackgroundResource(R.mipmap.wifi_state_1)
            }
            false -> {
                btnWifi.setBackgroundResource(R.mipmap.wifi_state_2)
            }
        }

    }
}