package com.promisekeeper.ui.screens.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.promisekeeper.PromiseKeeperApp
import com.promisekeeper.data.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = (application as PromiseKeeperApp).container.repository

    val companions: StateFlow<List<CompanionCore>> =
        repo.observeAllCompanions()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val primaryCompanion: StateFlow<CompanionCore?> =
        companions.map { it.firstOrNull() }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val promises: StateFlow<List<Promise>> =
        repo.observeAllPromises()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun togglePromise(id: String, active: Boolean) {
        viewModelScope.launch { repo.togglePromise(id, active) }
    }

    fun deletePromise(id: String) {
        viewModelScope.launch { repo.deletePromise(id) }
    }
}
