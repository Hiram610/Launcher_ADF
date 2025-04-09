package com.example.launcher_adf_2

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context.WIFI_SERVICE
import android.content.Intent
import android.net.wifi.WifiManager
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.widget.Button
import androidx.fragment.app.DialogFragment

class QuickSettingsFragment : DialogFragment() {
    lateinit var wifiManager : WifiManager

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = AlertDialog.Builder(requireContext())
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.quick_panel, null)

        val  btnWifi : Button = view.findViewById(R.id.wifi_button)

        wifiManager = requireContext().applicationContext.getSystemService(WIFI_SERVICE) as WifiManager

        btnWifi.setOnClickListener {
            if(wifiManager.isWifiEnabled) {
                    wifiManager.setWifiEnabled(false)
                    //Toast.makeText(requireContext(), "Wifi Apagado", Toast.LENGTH_SHORT).show()
                } else {
                    wifiManager.setWifiEnabled(true)
                    //Toast.makeText(requireContext(), "Wifi Escendido", Toast.LENGTH_SHORT).show()
                }
        }

        btnWifi.setOnLongClickListener {
            startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
            true
        }

        builder.setView(view)

        return builder.create()
    }
}