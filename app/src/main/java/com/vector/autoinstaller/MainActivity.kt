package com.vector.autoinstaller

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.vector.autoinstaller.presentation.InstallerScreen
import com.vector.autoinstaller.presentation.InstallerViewModel
import com.vector.autoinstaller.presentation.theme.VectorAutoInstallerTheme

class MainActivity : ComponentActivity() {
    private val viewModel: InstallerViewModel by viewModels {
        InstallerViewModel.Factory(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VectorAutoInstallerTheme {
                InstallerScreen(viewModel = viewModel)
            }
        }
    }
}
