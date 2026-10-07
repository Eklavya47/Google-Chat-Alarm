package com.betanooblabs.chatalarm.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlarmContactViewModel @Inject constructor(
    private val repository: ChatAlarmPreferencesRepository
) : ViewModel() {

    val userChatName: StateFlow<String?> =
        repository.userChatName.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    val contactNames: StateFlow<Set<String>> =
        repository.contactNames.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptySet()
        )

    val alarmSoundUri: StateFlow<String?> =
        repository.alarmSoundUri.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    fun setAlarmSoundUri(uri: String) {
        viewModelScope.launch {
            repository.setAlarmSoundUri(uri)
        }
    }

    fun validateAlarmSound() {
        viewModelScope.launch {
            val uri = repository.alarmSoundUri.first()
                ?: return@launch

            if (!repository.isAlarmSoundAvailable(uri)) {
                repository.clearAlarmSoundUri()
            }
        }
    }

    fun setUserChatName(name: String) {
        viewModelScope.launch {
            repository.setUserChatName(name)
        }
    }

    fun addContact(name: String) {
        viewModelScope.launch {
            repository.addContact(name)
        }
    }

    fun removeContact(name: String) {
        viewModelScope.launch {
            repository.removeContact(name)
        }
    }
}