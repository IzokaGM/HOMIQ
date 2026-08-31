package com.homiq.app

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.homiq.app.ui.HomiqApp
import com.homiq.app.ui.theme.HomiqTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            HomiqTheme {
                HomiqApp()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val homiqApplication =
            application as HomiqApplication
        homiqApplication.container
            .appLockService
            .onAppForeground()
        if (!homiqApplication.container.appLockService.state.value.locked) {
            homiqApplication.container
                .syncService
                .onAppForeground()
        }
    }

    override fun onStop() {
        if (!isChangingConfigurations) {
            val homiqApplication =
                application as HomiqApplication
            homiqApplication.container
                .appLockService
                .onAppBackground()
        }
        super.onStop()
    }
}
