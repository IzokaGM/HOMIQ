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
        val application =
            application as HomiqApplication
        application.container
            .syncService
            .onAppForeground()
    }
}
