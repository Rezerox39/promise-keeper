package com.promisekeeper

import android.app.Application
import com.promisekeeper.di.AppContainer

class PromiseKeeperApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
