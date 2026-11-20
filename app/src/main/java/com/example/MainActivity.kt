package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ai.configureAppCheck
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.RemindersScreen
import com.example.ui.screens.ShortcutsScreen
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.IndigoDark
import com.example.ui.theme.IndigoNight
import com.example.ui.theme.IndigoSurface
import com.example.ui.theme.SaarthiTheme
import com.example.ui.theme.SaffronGold
import com.example.ui.theme.SaffronPrimary

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configureAppCheck(this, intent)
        enableEdgeToEdge()
        setContent {
            SaarthiTheme {
                SaarthiMainApp(
                    onExitApp = { finish() }
                )
            }
        }
    }
}

@Composable
fun SaarthiMainApp(
    viewModel: MainViewModel = viewModel(),
    onExitApp: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentTab by viewModel.currentTab.collectAsState()

    // Permission launcher for Microphone audio recording
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Toast.makeText(context, "माइक की अनुमति मिल गई है। अब आप बोल सकते हैं!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "आवाज़ पहचानने के लिए माइक की अनुमति आवश्यक है।", Toast.LENGTH_LONG).show()
        }
    }

    // Check permission on startup
    LaunchedEffect(Unit) {
        val permissionCheck = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        )
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    // Predictive back handler: if on Reminders or Guides, return to Chat
    BackHandler {
        if (currentTab != 0) {
            viewModel.setTab(0)
        } else {
            onExitApp()
        }
    }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = IndigoDark,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .navigationBarsPadding()
                    .testTag("saarthi_bottom_nav")
            ) {
                NavigationBarItem(
                    selected = currentTab == 0,
                    onClick = { viewModel.setTab(0) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.ChatBubble,
                            contentDescription = "सारथी AI",
                            tint = if (currentTab == 0) SaffronPrimary else Color.Gray
                        )
                    },
                    label = {
                        Text(
                            text = "सारथी AI",
                            fontSize = 11.sp,
                            fontWeight = if (currentTab == 0) FontWeight.Bold else FontWeight.Normal,
                            color = if (currentTab == 0) SaffronGold else Color.Gray
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = IndigoSurface
                    ),
                    modifier = Modifier.testTag("nav_tab_chat")
                )

                NavigationBarItem(
                    selected = currentTab == 1,
                    onClick = { viewModel.setTab(1) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "रिमाइंडर्स",
                            tint = if (currentTab == 1) CyanGlow else Color.Gray
                        )
                    },
                    label = {
                        Text(
                            text = "रिमाइंडर्स",
                            fontSize = 11.sp,
                            fontWeight = if (currentTab == 1) FontWeight.Bold else FontWeight.Normal,
                            color = if (currentTab == 1) CyanGlow else Color.Gray
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = IndigoSurface
                    ),
                    modifier = Modifier.testTag("nav_tab_reminders")
                )

                NavigationBarItem(
                    selected = currentTab == 2,
                    onClick = { viewModel.setTab(2) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Build,
                            contentDescription = "टेक गाइड्स",
                            tint = if (currentTab == 2) SaffronGold else Color.Gray
                        )
                    },
                    label = {
                        Text(
                            text = "टेक गाइड्स",
                            fontSize = 11.sp,
                            fontWeight = if (currentTab == 2) FontWeight.Bold else FontWeight.Normal,
                            color = if (currentTab == 2) SaffronGold else Color.Gray
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = IndigoSurface
                    ),
                    modifier = Modifier.testTag("nav_tab_shortcuts")
                )
            }
        },
        containerColor = IndigoNight,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(IndigoNight)
        ) {
            Crossfade(
                targetState = currentTab,
                label = "tab_transition"
            ) { tab ->
                when (tab) {
                    0 -> ChatScreen(
                        viewModel = viewModel,
                        onNavigateToGuides = { viewModel.setTab(2) }
                    )
                    1 -> RemindersScreen(
                        viewModel = viewModel
                    )
                    2 -> ShortcutsScreen(
                        viewModel = viewModel,
                        onAskInChat = { question ->
                            viewModel.setTab(0)
                            viewModel.processUserInput(question)
                        }
                    )
                }
            }
        }
    }
}
