package com.betanooblabs.chatalarm.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlarmContactViewModel @Inject constructor(
    private val repository: AlarmContactRepository
) : ViewModel() {

    val contactNames: StateFlow<Set<String>> =
        repository.contactNames.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptySet()
        )

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