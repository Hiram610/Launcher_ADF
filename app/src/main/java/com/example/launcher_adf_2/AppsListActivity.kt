package com.example.launcher_adf_2

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.GridView
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_apps_list)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val backButton : Button = findViewById(R.id.user_Button)

        backButton.setOnClickListener {
            var intent = Intent(this, MainActivity::class.java)
            this.startActivity(intent)
            finish()
        }

        loadApps()
        adapterApps()
        addClickListener()
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
            popMenu.inflate(R.menu.popup_apps_menu)
            popMenu.menu.findItem(R.id.delete_option).isVisible = false

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

