package com.example.launcher_adf_2

import android.annotation.SuppressLint
import android.app.Dialog
import androidx.fragment.app.DialogFragment
import androidx.appcompat.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class UpdateFragment : DialogFragment () {

    lateinit var txtCurrentVersion: TextView
    lateinit var txtNewestVersion: TextView
    lateinit var txtSameVersion: TextView
    lateinit var btnActualizar: Button


    @SuppressLint("UseGetLayoutInflater")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = AlertDialog.Builder(requireContext())
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.update_panel, null)

        txtCurrentVersion = view.findViewById(R.id.currentVersion_txt)
        txtNewestVersion = view.findViewById(R.id.newestVersion_txt)
        txtSameVersion = view.findViewById(R.id.SameVersion_msg)
        btnActualizar = view.findViewById(R.id.update_btn)

        try {
            checkUpdate()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        builder.setView(view)
        return builder.create()
    }

    private fun checkUpdate() {
        val url = "https://raw.githubusercontent.com/Hiram610/Launcher_ADF/refs/heads/main/version.json"
        val connection = URL(url).openConnection() as HttpURLConnection
        connection.connect()

        val input = connection.inputStream.bufferedReader().use { it.readText() }

        val json = JSONObject(input)
        val lastVersion = json.getString("versionName")
        val apkUrl = json.getString("apkUrl")

        val currentVersion = getVersionName()

        txtCurrentVersion.text = currentVersion
        txtNewestVersion.text = lastVersion

//        if (isNewestVersion(lastVersion, currentVersion)) {
//            txtNewestVersion.visibility = TextView.VISIBLE
//            txtSameVersion.visibility = TextView.INVISIBLE
//            btnActualizar.isEnabled = true
//        } else {
//            txtNewestVersion.visibility = TextView.INVISIBLE
//            txtSameVersion.visibility = TextView.VISIBLE
//            btnActualizar.isEnabled = false
//        }

    }

    private fun isNewestVersion(lastest: String, current: String) : Boolean {
        return lastest > current
    }

    fun getVersionName() : String {
        val pInfo = context?.packageManager?.getPackageInfo(context?.packageName!!, 0)
        return pInfo?.versionName.toString()
    }
}