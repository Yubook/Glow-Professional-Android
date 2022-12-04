package com.youbook.glowpros

import android.app.Application
import com.youbook.glowpros.utils.SocketConnector

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        SocketConnector.initSocket(this)
    }
}