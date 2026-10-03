package com.betanooblabs.chatalarm.settings

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(
    name = "chat_alarm_preferences"
)

class AlarmContactRepository(
    private val context: Context
) {

    private val alarmContactsKey =
        stringSetPreferencesKey("alarm_contact_names")

    val contactNames: Flow<Set<String>> =
        context.dataStore.data.map { preferences ->
            preferences[alarmContactsKey] ?: emptySet()
        }

    suspend fun addContact(name: String) {
        context.dataStore.edit { preferences ->
            val contacts = preferences[alarmContactsKey] ?: emptySet()
            preferences[alarmContactsKey] = contacts + name
        }
    }

    suspend fun removeContact(name: String) {
        context.dataStore.edit { preferences ->
            val contacts = preferences[alarmContactsKey] ?: emptySet()
            preferences[alarmContactsKey] = contacts - name
        }
    }
}