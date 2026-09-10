package com.promisekeeper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.promisekeeper.ui.navigation.PromiseKeeperNavHost
import com.promisekeeper.ui.theme.PromiseKeeperTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PromiseKeeperTheme {
                PromiseKeeperNavHost()
            }
        }
    }
}
