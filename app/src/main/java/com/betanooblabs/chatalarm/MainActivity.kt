package com.betanooblabs.chatalarm


import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import com.betanooblabs.chatalarm.settings.ChatAlarmPreferencesRepository
import com.betanooblabs.chatalarm.setup.BackgroundSettingsManager

import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var backgroundSettingsManager: BackgroundSettingsManager
    @Inject
    lateinit var preferencesRepository: ChatAlarmPreferencesRepository

    private val notificationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(
                Manifest.permission.POST_NOTIFICATIONS
            )
        }

        enableEdgeToEdge()
        setContent {
            ChatAlarmApp(
                backgroundSettingsManager = backgroundSettingsManager,
                preferencesRepository = preferencesRepository
            )
        }
    }
}