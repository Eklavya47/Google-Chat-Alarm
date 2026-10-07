package com.betanooblabs.chatalarm.setup

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import javax.inject.Inject
import androidx.core.net.toUri

class BackgroundSettingsManager @Inject constructor(
    private val context: Context
) {

    fun isBatteryUnrestricted(): Boolean {

        val powerManager =
            context.getSystemService(Context.POWER_SERVICE) as PowerManager

        return powerManager.isIgnoringBatteryOptimizations(
            context.packageName
        )
    }

    fun openBatterySettings() {

        val intent = Intent(
            Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
        ).apply {
            data = "package:${context.packageName}".toUri()
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            val fallbackIntent = Intent(
                Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            context.startActivity(fallbackIntent)
        }
    }

    fun openAutoStartSettings() {
        val intent = when {
            Build.MANUFACTURER.equals("xiaomi", ignoreCase = true) -> {
                Intent("miui.intent.action.OP_AUTO_START")
                    .addCategory(Intent.CATEGORY_DEFAULT)
            }

            Build.MANUFACTURER.equals("oppo", ignoreCase = true) -> {
                Intent().setComponent(
                    ComponentName(
                        "com.coloros.safecenter",
                        "com.coloros.safecenter.permission.startup.StartupAppListActivity"
                    )
                )
            }

            Build.MANUFACTURER.equals("vivo", ignoreCase = true) -> {
                Intent().setComponent(
                    ComponentName(
                        "com.iqoo.secure",
                        "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity"
                    )
                )
            }

            else -> {
                Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                ).apply {
                    data = "package:${context.packageName}".toUri()
                }
            }
        }.apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        try {
            context.startActivity(intent)
        } catch (_: Exception) {
            val fallbackIntent = Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            ).apply {
                data = "package:${context.packageName}".toUri()
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            context.startActivity(fallbackIntent)
        }
    }

    fun requiresAutoStartSetup(): Boolean {
        return Build.MANUFACTURER.equals("xiaomi", ignoreCase = true) ||
                Build.MANUFACTURER.equals("oppo", ignoreCase = true) ||
                Build.MANUFACTURER.equals("vivo", ignoreCase = true)
    }
}