package com.kheteebaadi.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.kheteebaadi.app.ui.AppRoot
import com.kheteebaadi.app.ui.theme.KheteebaadiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KheteebaadiTheme { AppRoot() }
        }
    }
}
