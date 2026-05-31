package com.xclub.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import net.zetetic.database.sqlcipher.SQLiteDatabase

@HiltAndroidApp
class XclubApp : Application() {
    override fun onCreate() {
        super.onCreate()
        SQLiteDatabase.loadLibs(this)
    }
}
