package com.example.launcher_adf_2

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Context.WIFI_SERVICE
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.SupplicantState
import android.net.wifi.WifiManager
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.DialogFragment

class QuickSettingsFragment : DialogFragment() {
    private lateinit var wifiManager : WifiManager
    private lateinit var connectivityManager : ConnectivityManager
    private lateinit var netCallback : ConnectivityManager.NetworkCallback
    private lateinit var btnWifi : ImageButton
    private lateinit var textNameWifi : TextView

    @SuppressLint("UseGetLayoutInflater")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = AlertDialog.Builder(requireContext())
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.quick_panel, null)

        btnWifi = view.findViewById(R.id.wifi_button)
        textNameWifi = view.findViewById(R.id.wifi_name)

        wifiManager = requireContext().applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        connectivityManager = requireContext().applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

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

        val request = NetworkRequest.Builder().addTransportType(NetworkCapabilities.TRANSPORT_WIFI).build()

        connectivityManager.registerNetworkCallback(request, netCallback)

        verificarWifi()
        actualizarStatusWifi(textNameWifi)


        btnWifi.setOnClickListener {

            if(wifiManager.isWifiEnabled) {
                wifiManager.isWifiEnabled = false
                actualizarStatusWifi(textNameWifi)
                btnWifi.setBackgroundResource(R.mipmap.wifi_state_2)
            }
            else {
                wifiManager.isWifiEnabled = true
                actualizarStatusWifi(textNameWifi)
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
    private fun verificarWifi() {

        when(wifiManager.isWifiEnabled) {
            true -> {
                btnWifi.setBackgroundResource(R.mipmap.wifi_state_1)
            }
            false -> {
                btnWifi.setBackgroundResource(R.mipmap.wifi_state_2)
            }
        }
    }

    private fun actualizarStatusWifi(textView: TextView) {
        requireActivity().runOnUiThread {
            if(!wifiManager.isWifiEnabled) {
                textView.text = "No Conectado"
                return@runOnUiThread
            }
        }

        val network = connectivityManager.activeNetwork
        val compatibilities = connectivityManager.getNetworkCapabilities(network)

        if(network != null && compatibilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true) {
            var wifiInfo = wifiManager.connectionInfo
            var ssid = wifiInfo.ssid.replace("\"", "")
            textView.text = ssid
        } else {
            textView.text = "Conectando..."
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        connectivityManager.unregisterNetworkCallback(netCallback)
    }
}