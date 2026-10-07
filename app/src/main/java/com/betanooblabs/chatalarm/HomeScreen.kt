package com.betanooblabs.chatalarm

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.betanooblabs.chatalarm.notification.NotificationListenerStatus
import com.betanooblabs.chatalarm.settings.AlarmContactViewModel
import com.betanooblabs.chatalarm.settings.AllowedDmContactsDialog

@Composable
fun HomeScreen(
    viewModel: AlarmContactViewModel = hiltViewModel(),
    onSettingsClick: () -> Unit
) {
    val userChatName by viewModel.userChatName.collectAsStateWithLifecycle()
    val isListenerConnected by NotificationListenerStatus.isConnected.collectAsStateWithLifecycle()
    var username by remember {
        mutableStateOf(userChatName.orEmpty())
    }
    var showContactsDialog by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Chat Alarm",
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Get an alarm when important Google Chat " +
                    "messages arrive.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Before using Chat Alarm",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(15.dp))

        Text(
            text = "• Set your Chat name same as your Google Chat name\n\n" +
                    "• Allow notification access for Chat Alarm from your device's settings to read incoming notifications from Gmail and Google Chat apps.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Notification access",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (isListenerConnected) {
                        "Connected — Chat Alarm is ready"
                    } else {
                        "Not connected"
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = username,
            onValueChange = {
                username = it
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Your Google Chat name")
            },
            placeholder = {
                Text("Enter your Google Chat name")
            },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                viewModel.setUserChatName(username)
                username = ""
            },
            enabled = username.isNotBlank()
        ) {
            Text("Save")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Current Google Chat name",
            style = MaterialTheme.typography.labelLarge
        )

        Text(
            text = userChatName ?: "Not set",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedButton(
            onClick = { showContactsDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Allowed DM Contacts List")
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onSettingsClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Settings")
        }

        if (showContactsDialog) {
            AllowedDmContactsDialog(
                onDismiss = {
                    showContactsDialog = false
                }
            )
        }
    }
}