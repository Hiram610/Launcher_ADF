package com.example.launcher_adf_2

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.gson.internal.GsonBuildConfig
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class UpdateFragment : DialogFragment () {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState)
    }

    private fun checkUpdate() {
        val url = "https://github.com/Hiram610/Launcher_ADF/releases/download/v1.0.0/version.json"
        val connection = URL(url).openConnection() as HttpURLConnection
        connection.connect()

        val input = connection.inputStream.bufferedReader().use { it.readText() }

        val json = JSONObject(input)
        val lastVersion = json.getString("version")
        val apkUrl = json.getString("apkUrl")

        val currentVersion = getVersionName()

        if (isNewestVersion(lastVersion, currentVersion)) {

        }

    }

    private fun isNewestVersion(lastest: String, current: String) : Boolean {
        return lastest > current
    }

    fun getVersionName() : String {
        val pInfo = context?.packageManager?.getPackageInfo(context?.packageName!!, 0)
        return pInfo?.versionName.toString()
    }
}