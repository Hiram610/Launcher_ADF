package com.example.launcher_adf_2

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import androidx.core.net.toUri
import androidx.fragment.app.Fragment

class UpdateFragment : Fragment () {

    private lateinit var txtCurrentVersion: TextView
    private lateinit var txtNewestVersion: TextView
    private lateinit var txtSameVersion: TextView
    private lateinit var btnActualizar: Button

    private val url = "https://raw.githubusercontent.com/Hiram610/Launcher_ADF/refs/heads/main/version.json"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.update_panel, container, false)

        txtCurrentVersion = view.findViewById(R.id.currentVersion_txt)
        txtNewestVersion = view.findViewById(R.id.newestVersion_txt)
        txtSameVersion = view.findViewById(R.id.sameVersion_msg)
        btnActualizar = view.findViewById(R.id.update_btn)

        btnActualizar.visibility = View.GONE
        txtSameVersion.visibility = View.GONE

        checkForUpdate()

        return view
    }


    private fun isNewestVersion(lastest: String, current: String) : Boolean {
        return lastest > current
    }

    fun getVersionName() : String {
        val pInfo = context?.packageManager?.getPackageInfo(context?.packageName!!, 0)
        return pInfo?.versionName.toString()
    }

    fun checkForUpdate() {
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object: Callback{
            override fun onFailure(call: Call, e: IOException) {
                activity?.runOnUiThread {
                    Toast.makeText(context, "Error al verificar la actualización", Toast.LENGTH_SHORT).show()
                }
            }

            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call, response: Response) {
                val json = response.body?.string()
                if(json != null) {
                    val obj = JSONObject(json)
                    val lastVersion = obj.getString("versionName")
                    val apkUrl = obj.getString("apkUrl")
                    val currentVersion = getVersionName()

                    activity?.runOnUiThread {
                        txtCurrentVersion.text = "Actual: v$currentVersion"
                        txtNewestVersion.text = "Nueva: v$lastVersion"

                        if(isNewestVersion(lastVersion, currentVersion)) {
                            btnActualizar.visibility = View.VISIBLE
                            btnActualizar.setOnClickListener {
                                downloadAndInstallApk(apkUrl)
                            }
                        } else {
                            txtNewestVersion.visibility = View.GONE
                            txtSameVersion.visibility = View.VISIBLE
                        }
                    }
                }
            }
        })
    }

    fun downloadAndInstallApk(apkUrl: String) {
        val request = DownloadManager.Request(apkUrl.toUri()).apply {
            setTitle("Descargando Actualización")
            setDescription("Espere mientras se descarga la actualizacion")
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            setDestinationInExternalFilesDir(requireContext(), Environment.DIRECTORY_DOWNLOADS, "update.apk")
            setMimeType("application/vnd.android.package-archive")
        }

        val downloadManager = requireContext().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadId = downloadManager.enqueue(request)

        val onComplete = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val uri = downloadManager.getUriForDownloadedFile(downloadId)
                uri?.let {
                    installApk(it)
                }
                requireContext().unregisterReceiver(this)
            }
        }
        ContextCompat.registerReceiver(requireContext(), onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE), ContextCompat.RECEIVER_NOT_EXPORTED)
    }

    @SuppressLint("QueryPermissionsNeeded")
    fun installApk(uri: Uri) {
        val installIntent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/vnd.android.package-archive")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
        }

        val packageInstaller = requireContext().packageManager
        if (installIntent.resolveActivity(packageInstaller) != null) {
            startActivity(installIntent)
        } else {
            Toast.makeText(requireContext(), "No se puede instalar la actualización", Toast.LENGTH_SHORT).show()
        }
    }
}