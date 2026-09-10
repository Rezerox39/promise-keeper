package com.promisekeeper.ui.screens.promise

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.promisekeeper.PromiseKeeperApp
import com.promisekeeper.data.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

class PromiseViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = (application as PromiseKeeperApp).container.repository

    val companions: StateFlow<List<CompanionCore>> =
        repo.observeAllCompanions()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _created = MutableSharedFlow<Boolean>()
    val created = _created

    fun createPromise(
        title: String,
        description: String?,
        companionId: String,
        hour: Int = 9,
        minute: Int = 0
    ) {
        viewModelScope.launch {
            val promise = Promise(
                id = UUID.randomUUID().toString(),
                title = title,
                description = description,
                schedule = Schedule(ScheduleType.DAILY, hour, minute),
                companionId = companionId,
                reward = Reward(3, 2, 2, 10),
                consequence = Consequence(4, 3, 1),
                gracePeriodMinutes = 0,
                active = true,
                createdAt = System.currentTimeMillis(),
                startDate = System.currentTimeMillis()
            )
            repo.createPromise(promise)
            _created.emit(true)
        }
    }
}
