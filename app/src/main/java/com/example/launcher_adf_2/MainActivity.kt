package com.example.launcher_adf_2

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.BaseAdapter
import android.widget.GridView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.launcher_adf_2.Launcher_ADF.Companion.prefs
import com.example.launcher_adf_2.MainActivity.LoginSuccessListenter

class MainActivity : AppCompatActivity(), LoginSuccessListenter {

    interface LoginSuccessListenter {
        fun onLoginSuccess(action: String)
    }

    var apps = ArrayList<AppDetail>() // lista de las apps instaladas
    var adapter : appAdapter? = null // Adaptador para la Lista de las apps
    lateinit var manager: PackageManager // Administrador de las apps Instaladas
    lateinit var appList : GridView // La vista de las apps
    var appPackage : String = "" // El nombre del paquete de la app
    lateinit var sucNumtxt : TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        if(!isDefaultLauncher()) {
            Toast.makeText(this, "Por favor configurar la applicacion como launcher predeterminado", Toast.LENGTH_LONG).show()
            startActivity(Intent(Settings.ACTION_HOME_SETTINGS))
        }

        var button: ImageButton = findViewById(R.id.apps_button)
        var quickSettingsButton : ImageButton = findViewById(R.id.quick_button)
        sucNumtxt = findViewById(R.id.num_surcusal_txt)

        button.setOnClickListener {
            openLoginFragment("admin")
        }

        quickSettingsButton.setOnClickListener {
            openDialogFragment()
        }

        try {
            checkLocationPermission()
            kioscoMode()
            loadApps()
            adapterApps()
            addClickListener()
            obtenerNumeroSurcusal()
        } catch (e: Exception) {
            Toast.makeText(this, "Hubo un problema con ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    fun loadApps() {

        var intent: Intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)

        manager = packageManager

        if(prefs.getApp().isNotEmpty()) {

            var prefsApps = prefs.getApp()
            for (i in prefsApps){
                var infoApp = manager.getApplicationInfo(i, 0)
                apps.add(AppDetail(manager.getApplicationLabel(infoApp).toString(), i, manager.getApplicationIcon(i)))
            }
        } else {
            Toast.makeText(this, "No hay Applicaciones Instaladas", Toast.LENGTH_SHORT).show()
        }
    }

    fun adapterApps() {
        adapter = appAdapter(this, apps)
        appList = findViewById(R.id.apps_main)
        appList.adapter = adapter
    }

    fun addClickListener() {
        appList.setOnItemClickListener { parent, view, pos, id ->
            val intent = manager.getLaunchIntentForPackage(apps.get(pos).name.toString())
            intent?.let { startActivity(it) }
        }

        appList.setOnItemLongClickListener { parent, view, pos, id ->

            val popMenu = PopupMenu(this, view)
            popMenu.inflate(R.menu.popup_menu)
            popMenu.menu.findItem(R.id.add_option).isVisible = false
            popMenu.menu.findItem(R.id.pass_option).isVisible = false

            popMenu.setOnMenuItemClickListener { menuItem ->

                when(menuItem.itemId) {
                    R.id.delete_option -> {
                        appPackage = apps.get(pos).name
                        openLoginFragment("delete")
                        true
                    }

                    else -> {
                        false
                    }
                }
            }
            popMenu.show()
            true

            }
    }

    fun kioscoMode() {
        val dpm = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        val componentName = ComponentName(this, AdminReceiver::class.java)

        if (!dpm.isAdminActive(componentName)) {
            val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName)
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Esta app requiere permisos de Administrador para ejecutar.")

            startActivityForResult(intent, 1)
        } else {
            dpm.setStatusBarDisabled(componentName, true)
        }
    }

    private fun obtenerNumeroSurcusal(){
        sucNumtxt.setText(prefs.getNumber())
    }

    fun openDialogFragment() {
        val panelQuickSettings = QuickSettingsFragment()
        panelQuickSettings.show(supportFragmentManager, "quick settings")
    }

    fun isDefaultLauncher() : Boolean {
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
        }

        val resolverInfo = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
        return resolverInfo?.activityInfo?.packageName == packageName
    }

    fun openLoginFragment(action: String) {
        var panelLogin = LoginFragment()
        panelLogin.setLoginSuccessListenter(this)

        val args = Bundle()
        args.putString("action", action)
        panelLogin.arguments = args
        panelLogin.show(supportFragmentManager, "Dialog Login")
    }

    override fun onLoginSuccess(action: String) {
        when (action) {
            "admin" -> {
                val intent = Intent(this, AppsListActivity::class.java)
                startActivity(intent)
                Toast.makeText(this, "Modo Administrador", Toast.LENGTH_SHORT).show()
                finish()
            }
            "delete" -> {
                prefs.removeApp(appPackage)
                Toast.makeText(this, "Se quito app del modo Usuario", Toast.LENGTH_SHORT).show()
                recreate()
            }
        }
    }

    fun checkLocationPermission() {
        val permissions = arrayOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
            )

        val allGranted = permissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }

        if (!allGranted) {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE)
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }

    class appAdapter : BaseAdapter {
        var context : Context? = null
        var apps = ArrayList<AppDetail>()

        constructor(context: Context, apps: ArrayList<AppDetail>){
            this.context = context
            this.apps = apps
        }

        override fun getCount(): Int {
            return apps.size
        }

        override fun getItem(p0: Int): Any {
            return apps[0]
        }

        override fun getItemId(p0: Int): Long {
            return p0.toLong()
        }

        override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
            var app = apps[p0]
            var inflator = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            var vista = inflator.inflate(R.layout.list_item, null)
            var label: TextView = vista.findViewById(R.id.item_app_label)
            var icon: ImageView = vista.findViewById(R.id.item_app_icon)

            label.setText(app.label)
            icon.setImageDrawable(app.icon)

            return vista
        }
    }
}
