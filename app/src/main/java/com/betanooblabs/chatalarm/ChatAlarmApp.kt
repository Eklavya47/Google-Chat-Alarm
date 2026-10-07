package com.betanooblabs.chatalarm

import android.app.Activity
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.betanooblabs.chatalarm.notification.NotificationListenerStatus
import com.betanooblabs.chatalarm.settings.ChatAlarmPreferencesRepository
import com.betanooblabs.chatalarm.setup.BackgroundSettingsManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun ChatAlarmApp(
    backgroundSettingsManager: BackgroundSettingsManager,
    preferencesRepository: ChatAlarmPreferencesRepository
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var batteryUnrestricted by remember {
        mutableStateOf(
            backgroundSettingsManager.isBatteryUnrestricted()
        )
    }

    var autoStartConfirmed by remember {
        mutableStateOf(
            !backgroundSettingsManager.requiresAutoStartSetup()
        )
    }

    var showPermissionDialog by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        if (backgroundSettingsManager.requiresAutoStartSetup()) {
            autoStartConfirmed =
                preferencesRepository.autoStartConfirmed.first()
        }
    }

    LaunchedEffect(Unit) {
        showPermissionDialog =
            !NotificationListenerStatus.isListenerEnabled(context)
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                showPermissionDialog =
                    !NotificationListenerStatus.isListenerEnabled(context)

                batteryUnrestricted =
                    backgroundSettingsManager.isBatteryUnrestricted()

            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    when {

        // 1. Notification listener
        showPermissionDialog -> {

            AlertDialog(
                onDismissRequest = {
                    (context as? Activity)?.finishAffinity()
                },
                title = {
                    Text("Notification Read Access Required")
                },
                text = {
                    Text(
                        "Chat Alarm needs notification read access to detect " +
                                "Google Chat messages and mentions."
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            NotificationListenerStatus.openSettings(context)
                        }
                    ) {
                        Text("Enable")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            (context as? Activity)?.finishAffinity()
                        }
                    ) {
                        Text("Exit")
                    }
                }
            )
        }

        // 2. Battery optimization
        !batteryUnrestricted -> {

            AlertDialog(
                onDismissRequest = {
                    (context as? Activity)?.finishAffinity()
                },
                title = {
                    Text("Unrestricted Battery Usage Required")
                },
                text = {
                    Text(
                        "Chat Alarm needs unrestricted battery usage so it " +
                                "can continue detecting Google Chat messages " +
                                "when the app is not open."
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            backgroundSettingsManager.openBatterySettings()
                        }
                    ) {
                        Text("Enable")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            (context as? Activity)?.finishAffinity()
                        }
                    ) {
                        Text("Exit")
                    }
                }
            )
        }

        // 3. Auto-start
        !autoStartConfirmed -> {

            AlertDialog(
                onDismissRequest = {
                    (context as? Activity)?.finishAffinity()
                },
                title = {
                    Text("Background Auto-start Required")
                },
                text = {
                    Text(
                        "Chat Alarm needs to be allowed to start in the " +
                                "background so it can continue detecting " +
                                "Google Chat messages when the app is closed."
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            backgroundSettingsManager.openAutoStartSettings()
                        }
                    ) {
                        Text("Open Settings")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            scope.launch {
                                preferencesRepository.setAutoStartConfirmed()
                                autoStartConfirmed = true
                            }
                        }
                    ) {
                        Text("I've Enabled It")
                    }
                }
            )
        }

        else -> {
            AppNavigation()
        }
    }
}