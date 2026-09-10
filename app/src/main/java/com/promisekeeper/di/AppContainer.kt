package com.promisekeeper.di

import android.content.Context
import com.promisekeeper.data.db.PromiseKeeperDatabase
import com.promisekeeper.data.repository.PromiseKeeperRepository

class AppContainer(context: Context) {
    val database = PromiseKeeperDatabase.getInstance(context)
    val repository = PromiseKeeperRepository(database.dao())
}
