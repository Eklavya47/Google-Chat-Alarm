package com.betanooblabs.chatalarm.settings

import android.content.Context
import android.content.Intent
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import androidx.core.net.toUri
import androidx.datastore.preferences.core.booleanPreferencesKey
import kotlinx.coroutines.flow.first

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

    private val alarmSoundUriKey =
        stringPreferencesKey("alarm_sound_uri")

    private val autoStartConfirmedKey =
        booleanPreferencesKey("auto_start_confirmed")

    val autoStartConfirmed: Flow<Boolean> =
        context.dataStore.data.map { preferences ->
            preferences[autoStartConfirmedKey] ?: false
        }

    val userChatName: Flow<String?> =
        context.dataStore.data.map { preferences ->
            preferences[userChatNameKey]
        }

    val contactNames: Flow<Set<String>> =
        context.dataStore.data.map { preferences ->
            preferences[alarmContactsKey] ?: emptySet()
        }

    val alarmSoundUri: Flow<String?> =
        context.dataStore.data.map { preferences ->
            preferences[alarmSoundUriKey]
        }

    suspend fun setAutoStartConfirmed() {
        context.dataStore.edit { preferences ->
            preferences[autoStartConfirmedKey] = true
        }
    }

    suspend fun setUserChatName(name: String) {
        val trimmedName = name.trim()

        if (trimmedName.isEmpty()) return

        context.dataStore.edit { preferences ->
            preferences[userChatNameKey] = trimmedName
        }
    }

    suspend fun setAlarmSoundUri(uri: String) {
        val oldUri = context.dataStore.data
            .map { preferences ->
                preferences[alarmSoundUriKey]
            }
            .first()

        if (oldUri != null && oldUri != uri) {
            try {
                context.contentResolver.releasePersistableUriPermission(
                    oldUri.toUri(),
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: SecurityException) {
                // Permission may already be unavailable.
            }
        }

        context.dataStore.edit { preferences ->
            preferences[alarmSoundUriKey] = uri
        }
    }

    suspend fun clearAlarmSoundUri() {
        context.dataStore.edit { preferences ->
            preferences.remove(alarmSoundUriKey)
        }
    }

    suspend fun addContact(name: String) {
        val trimmedName = name.trim()

        if (trimmedName.isEmpty()) return

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

    fun isAlarmSoundAvailable(uriString: String): Boolean {
        return try {
            val uri = uriString.toUri()

            context.contentResolver
                .openFileDescriptor(uri, "r")
                ?.use { true }
                ?: false

        } catch (_: Exception) {
            false
        }
    }
}