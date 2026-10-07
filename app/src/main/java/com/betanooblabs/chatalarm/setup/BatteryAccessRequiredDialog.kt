package com.betanooblabs.chatalarm.setup

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun BatteryAccessRequiredDialog(
    onEnable: () -> Unit,
    onExit: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onExit,
        title = {
            Text("Unrestricted Battery Usage Required")
        },
        text = {
            Text(
                "Chat Alarm needs unrestricted battery usage " +
                        "to detect Google Chat messages when the app " +
                        "is not open."
            )
        },
        confirmButton = {
            TextButton(onClick = onEnable) {
                Text("Enable")
            }
        },
        dismissButton = {
            TextButton(onClick = onExit) {
                Text("Exit")
            }
        }
    )
}