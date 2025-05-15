package com.example.launcher_adf_2

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.EditText
import android.widget.GridView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.launcher_adf_2.Launcher_ADF.Companion.prefs

class AppsListActivity : AppCompatActivity() {

    var apps = ArrayList<AppDetail>()
    var adapter: appAdapter? = null
    lateinit var appList: GridView
    lateinit var manager: PackageManager
    lateinit var editNumber : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_apps_list)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val backButton : ImageButton = findViewById(R.id.user_Button)
        val adminSettingsButton : ImageButton = findViewById(R.id.admin_settings_button)
        editNumber = findViewById(R.id.num_surcusal_edit)


        backButton.setOnClickListener {
            guardarNumeroSurcusal()
            var intent = Intent(this, MainActivity::class.java)
            this.startActivity(intent)
            finish()
        }

        adminSettingsButton.setOnClickListener {
            val popupMenuSettings = PopupMenu(this, it)
            popupMenuSettings.inflate(R.menu.popup_menu)
            popupMenuSettings.menu.findItem(R.id.add_option).isVisible = false
            popupMenuSettings.menu.findItem(R.id.delete_option).isVisible = false

            popupMenuSettings.setOnMenuItemClickListener { menuItem ->
                when(menuItem.itemId) {
                    R.id.pass_option -> {
                        openPassFragment()
                        true
                    }
                    else -> {
                        false
                    }
                }
            }

            popupMenuSettings.show()
            true
        }

        salirKiosco()
        loadApps()
        adapterApps()
        addClickListener()
        obtenerNumeroSurcusal()
    }

    private fun loadApps() {
        var intent: Intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)

        manager = packageManager
        var avaliableActivities = manager.queryIntentActivities(intent, 0)
        for (i in avaliableActivities) {
            apps.add(
                AppDetail(
                    i.loadLabel(manager).toString(),
                    i.activityInfo.packageName,
                    i.activityInfo.loadIcon(manager)
                )
            )
        }
    }

    private fun adapterApps() {
        adapter = appAdapter(this, apps)
        appList = findViewById(R.id.apps_list)
        appList.adapter = adapter
    }

    private fun addClickListener() {
        appList.setOnItemClickListener { parent, view, pos, id ->
            val intent = manager.getLaunchIntentForPackage(apps.get(pos).name.toString())
            intent?.let { startActivity(it) }
        }

        appList.setOnItemLongClickListener{parent, view, pos, id ->

            val popMenu = PopupMenu(this, view)
            popMenu.inflate(R.menu.popup_menu)
            popMenu.menu.findItem(R.id.delete_option).isVisible = false
            popMenu.menu.findItem(R.id.pass_option).isVisible = false

            popMenu.setOnMenuItemClickListener { menuItem ->

                when(menuItem.itemId) {
                    R.id.add_option -> {
                        prefs.saveApp(apps.get(pos).name)
                        Toast.makeText(this, "La app ${apps.get(pos).label} se ha enviado al modo usuario", Toast.LENGTH_SHORT).show()
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

    fun openPassFragment() {
        var panelForm = FormFragment()
        panelForm.show(supportFragmentManager, "Dialog Form")
    }

    private fun salirKiosco(){
        val dpm = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        val componentName = ComponentName(this, AdminReceiver::class.java)

        dpm.setStatusBarDisabled(componentName, false)
    }

    private fun guardarNumeroSurcusal(){
        val numberInput = editNumber.text.toString()
        if(numberInput.isNotEmpty()) {
            val padded = numberInput.padStart(4, '0')
            prefs.saveNumber(padded)
        } else {
            Toast.makeText(this, "Por favor ingrese un numero de surcusal", Toast.LENGTH_SHORT).show()
        }

    }

    private fun obtenerNumeroSurcusal(){
        editNumber.setText(prefs.getNumber())
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

