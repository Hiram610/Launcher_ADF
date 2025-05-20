package com.example.launcher_adf_2

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiManager
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.DialogFragment

/**
 *
 */
class QuickSettingsFragment : DialogFragment() {
    private lateinit var wifiManager: WifiManager
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var netCallback: ConnectivityManager.NetworkCallback
    private lateinit var btnWifi: ImageButton
    private lateinit var textNameWifi: TextView

    @SuppressLint("UseGetLayoutInflater")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        
        val builder = AlertDialog.Builder(requireContext())
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.quick_panel, null)

        btnWifi = view.findViewById(R.id.wifi_button)
        textNameWifi = view.findViewById(R.id.wifi_name)
        wifiManager =
            requireContext().applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        connectivityManager =
            requireContext().applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val request =
            NetworkRequest.Builder().addTransportType(NetworkCapabilities.TRANSPORT_WIFI).build()

        netCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                actualizarStatusWifi(textNameWifi)
            }

            override fun onLost(network: Network) {
                actualizarStatusWifi(textNameWifi)
            }

            override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
                actualizarStatusWifi(textNameWifi)
            }
        }

        actualizarStatusWifi(textNameWifi)
        connectivityManager.registerNetworkCallback(request, netCallback)

        btnWifi.setOnClickListener {
            cambiarStatusWifi()
        }

        btnWifi.setOnLongClickListener {
            startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
            true
        }

        builder.setView(view)
        return builder.create()
    }

    /**
     * Cambia el estado del wifi (lo desactiva o activa)
     */
    private fun cambiarStatusWifi() {
        if (wifiManager.isWifiEnabled) {
            wifiManager.setWifiEnabled(false)
        } else {
            wifiManager.setWifiEnabled(true)
        }
    }

    /**
     * Metodo que actualiza el nombre e icono del Wifi dependiendo si este esta activo o no.
     */
    private fun actualizarStatusWifi(textView: TextView) {

        iconoWifi()

        val network = connectivityManager.activeNetwork
        val info = connectivityManager.getNetworkCapabilities(network)

        if(!wifiManager.isWifiEnabled) {
            textView.text = "No Conectado"
            return
        }

        if(info?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true) {
            var ssid = wifiManager.connectionInfo.ssid.replace("\"", "")
            textView.text = ssid
        } else {

            textView.text = "Conectando..."
        }

    }


    /**
     * Metodo que cambia el icono del wifi dependiendo si esta activo o no.
     */
    private fun iconoWifi() {
        if (wifiManager.isWifiEnabled) {
            btnWifi.setBackgroundResource(R.mipmap.wifi_state_1)
        } else {
            btnWifi.setBackgroundResource(R.mipmap.wifi_state_2)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        connectivityManager.unregisterNetworkCallback(netCallback)
    }
}