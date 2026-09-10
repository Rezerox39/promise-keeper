package com.promisekeeper.ui.screens.companion

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.promisekeeper.PromiseKeeperApp
import com.promisekeeper.data.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CompanionViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = (application as PromiseKeeperApp).container.repository

    val companions: StateFlow<List<CompanionCore>> =
        repo.observeAllCompanions()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _created = MutableSharedFlow<Boolean>()
    val created = _created

    fun createCompanion(type: CompanionType, name: String) {
        viewModelScope.launch {
            repo.createCompanion(type, name)
            _created.emit(true)
        }
    }
}
