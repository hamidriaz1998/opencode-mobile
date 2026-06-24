package com.example.opencode_mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.opencode_mobile.data.api.ApiServiceProvider
import com.example.opencode_mobile.data.local.ConnectionManager
import com.example.opencode_mobile.data.local.ConnectionStore
import com.example.opencode_mobile.ui.navigation.AppNavigation
import com.example.opencode_mobile.ui.theme.Opencode_mobileTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var apiServiceProvider: ApiServiceProvider

    @Inject
    lateinit var connectionStore: ConnectionStore

    @Inject
    lateinit var connectionManager: ConnectionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Opencode_mobileTheme {
                AppNavigation(
                    apiServiceProvider = apiServiceProvider,
                    connectionStore = connectionStore,
                    connectionManager = connectionManager
                )
            }
        }
    }
}
