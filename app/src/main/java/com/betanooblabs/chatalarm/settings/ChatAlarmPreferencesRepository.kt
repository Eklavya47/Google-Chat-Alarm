package com.betanooblabs.chatalarm.settings

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(
    name = "chat_alarm_preferences"
)

class ChatAlarmPreferencesRepository(
    private val context: Context
) {
    private val userChatNameKey =
        stringPreferencesKey("user_chat_name")

    private val alarmContactsKey =
        stringSetPreferencesKey("alarm_contact_names")

    val userChatName: Flow<String?> =
        context.dataStore.data.map { preferences ->
            preferences[userChatNameKey]
        }

    val contactNames: Flow<Set<String>> =
        context.dataStore.data.map { preferences ->
            preferences[alarmContactsKey] ?: emptySet()
        }

    suspend fun setUserChatName(name: String) {
        val trimmedName = name.trim()

        if (trimmedName.isEmpty()) {
            return
        }

        context.dataStore.edit { preferences ->
            preferences[userChatNameKey] = trimmedName
        }
    }

    suspend fun addContact(name: String) {
        val trimmedName = name.trim()

        if (trimmedName.isEmpty()) {
            return
        }

        context.dataStore.edit { preferences ->
            val contacts = preferences[alarmContactsKey] ?: emptySet()
            val alreadyExists = contacts.any {
                it.equals(trimmedName, ignoreCase = true)
            }
            if (!alreadyExists) {
                preferences[alarmContactsKey] = contacts + trimmedName
            }
        }
    }

    suspend fun removeContact(name: String) {
        context.dataStore.edit { preferences ->
            val contacts = preferences[alarmContactsKey] ?: emptySet()
            preferences[alarmContactsKey] = contacts - name
        }
    }
}