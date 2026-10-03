package com.betanooblabs.chatalarm.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun AlarmContactsScreen(
    viewModel: AlarmContactViewModel = hiltViewModel()
) {
    val contacts by viewModel.contactNames.collectAsStateWithLifecycle()

    var name by remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "DM Alarm Contacts"
        )

        Text(
            text = "Direct messages from these contacts will trigger the alarm."
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.weight(1f),
                label = {
                    Text("Chat name")
                },
                singleLine = true
            )

            Button(
                onClick = {
                    val trimmedName = name.trim()

                    if (trimmedName.isNotEmpty()) {
                        viewModel.addContact(trimmedName)
                        name = ""
                    }
                }
            ) {
                Text("Add")
            }
        }

        LazyColumn {
            items(
                items = contacts.toList(),
                key = { it }
            ) { contact ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(contact)

                    Button(
                        onClick = {
                            viewModel.removeContact(contact)
                        }
                    ) {
                        Text("Remove")
                    }
                }
            }
        }
    }
}