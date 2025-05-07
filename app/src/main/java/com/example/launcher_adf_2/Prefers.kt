package com.example.launcher_adf_2

import android.content.Context
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Prefers (val context: Context) {

    val DATA_APPS = "AppsDB"
    val DATA_NUMBER = "NumberDB"

    val storage = context.getSharedPreferences(DATA_APPS, 0)
    val storageNumber = context.getSharedPreferences(DATA_NUMBER, 0)

    val SHARE_APP_ADMIN = "packageApps"
    val SHARE_NUMBER_ADMIN = "numbers"

    var apps_User_List = ArrayList<String>()
    val gson = Gson()

    fun saveApp(app_Package: String) {
        apps_User_List.add(app_Package)
        storage.edit() { putString(SHARE_APP_ADMIN, gson.toJson(apps_User_List)) }
    }

    fun getApp() : ArrayList<String> {

        val type = object : TypeToken<ArrayList<String>>() {}.type
        return gson.fromJson(storage.getString(SHARE_APP_ADMIN, null), type) ?: arrayListOf()
    }

    fun removeApp(app_Package: String) {
        apps_User_List.remove(app_Package)
        storage.edit() { putString(SHARE_APP_ADMIN, gson.toJson(apps_User_List)) }
    }

    fun saveNumber(number: String) {
        storageNumber.edit() { putString(SHARE_NUMBER_ADMIN, number) }
    }

    fun getNumber() : String {
        return storageNumber.getString(SHARE_NUMBER_ADMIN, null) ?: ""
    }

    fun wipe() {
        storage.edit() { clear() }
    }
}
