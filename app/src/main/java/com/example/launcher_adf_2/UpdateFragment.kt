package com.example.launcher_adf_2

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import androidx.fragment.app.DialogFragment
import androidx.appcompat.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import com.example.launcher_adf_2.VersionInfo
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

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
            checkForUpdate(requireContext()) { updateAvailable, versionInfo -> }


        } catch (e: Exception) {
            e.printStackTrace()
        }

        builder.setView(view)
        return builder.create()
    }

    private fun isNewestVersion(lastest: String, current: String) : Boolean {
        return lastest > current
    }

    fun getVersionName() : String {
        val pInfo = context?.packageManager?.getPackageInfo(context?.packageName!!, 0)
        return pInfo?.versionName.toString()
    }

    fun checkForUpdate(context: Context, onResult: (Boolean, VersionInfo?) -> Unit) {
        val url = "https://raw.githubusercontent.com/Hiram610/Launcher_ADF/refs/heads/main/version.json"
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object: Callback{
            override fun onFailure(call: Call, e: IOException) {
                onResult(false, null)
            }

            override fun onResponse(call: Call, response: Response) {
                val json = response.body?.string()
                val obj = JSONObject(json)
                val versionInfo = VersionInfo(obj.getString("versionName"), obj.getString("apkUrl"))
                val currentVersion = getVersionName()
                val lastVersion = versionInfo.versionName
                val apkUrl = versionInfo.apkUrl

                txtCurrentVersion.text = currentVersion
                txtNewestVersion.text = lastVersion

                if(isNewestVersion(lastVersion, currentVersion)) {
                    onResult(true, versionInfo)
                } else {
                    onResult(false, null)
                }

            }
        })
    }
}