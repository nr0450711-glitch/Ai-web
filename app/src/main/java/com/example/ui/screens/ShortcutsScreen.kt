package com.example.ui.screens

import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DisplaySettings
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.SdStorage
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiTethering
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.MainViewModel
import com.example.ui.theme.CyanBright
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.IndigoNight
import com.example.ui.theme.IndigoSurface
import com.example.ui.theme.IndigoSurfaceBright
import com.example.ui.theme.IndigoSurfaceElevated
import com.example.ui.theme.SaffronGold
import com.example.ui.theme.SaffronPrimary

data class QuickAction(
    val title: String,
    val icon: ImageVector,
    val color: Color,
    val settingAction: String
)

data class DetailedGuide(
    val id: String,
    val title: String,
    val subtitle: String,
    val category: String,
    val steps: List<String>,
    val settingAction: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShortcutsScreen(
    viewModel: MainViewModel,
    onAskInChat: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val quickActions = listOf(
        QuickAction("वाई-फ़ाई", Icons.Default.Wifi, CyanGlow, Settings.ACTION_WIFI_SETTINGS),
        QuickAction("ब्लूटूथ", Icons.Default.Bluetooth, Color(0xFF60A5FA), Settings.ACTION_BLUETOOTH_SETTINGS),
        QuickAction("हॉटस्पॉट", Icons.Default.WifiTethering, SaffronPrimary, Settings.ACTION_WIRELESS_SETTINGS),
        QuickAction("स्टोरेज साफ़", Icons.Default.SdStorage, Color(0xFFF43F5E), Settings.ACTION_INTERNAL_STORAGE_SETTINGS),
        QuickAction("डिस्प्ले / फ़ॉन्ट", Icons.Default.DisplaySettings, Color(0xFFA855F7), Settings.ACTION_DISPLAY_SETTINGS),
        QuickAction("आवाज़ व टोन", Icons.Default.VolumeUp, Color(0xFF10B981), Settings.ACTION_SOUND_SETTINGS)
    )

    val guides = listOf(
        DetailedGuide(
            id = "wifi_guide",
            title = "घर या दफ़्तर का वाईफाई कैसे जोड़ें",
            subtitle = "फ़ोन में हाई-स्पीड इंटरनेट चलाने के लिए वाईफ़ाई कनेक्ट करें।",
            category = "इंटरनेट",
            steps = listOf(
                "स्क्रीन के ऊपर से नीचे की तरफ दो बार उंगली खींचें।",
                "'Wi-Fi' के बटन पर 2 सेकंड दबाकर रखें।",
                "वाई-फ़ाई स्विच चालू करें।",
                "अपने वाई-फ़ाई के नाम पर टैप करें।",
                "पासवर्ड डालें और 'Connect' पर क्लिक करें।"
            ),
            settingAction = Settings.ACTION_WIFI_SETTINGS
        ),
        DetailedGuide(
            id = "hotspot_guide",
            title = "पर्सनल हॉटस्पॉट चालू कैसे करें",
            subtitle = "अपने मोबाइल का इंटरनेट दूसरे फ़ोन या कंप्यूटर के साथ शेयर करें।",
            category = "शेयरिंग",
            steps = listOf(
                "फ़ोन सेटिंग्स में 'Portable Hotspot' या 'Tethering' खोलें।",
                "हॉटस्पॉट स्विच को ON (चालू) करें।",
                "'Set up hotspot' में जाकर वाईफाई नाम और पासवर्ड देख सकते हैं।"
            ),
            settingAction = Settings.ACTION_WIRELESS_SETTINGS
        ),
        DetailedGuide(
            id = "storage_guide",
            title = "फ़ोन का स्टोरेज (Memory) साफ़ कैसे करें",
            subtitle = "फ़ोन हैंग होने से बचाएं और फ़ालतू फ़ोटो-वीडियो हटाएं।",
            category = "फ़ोन स्पीड",
            steps = listOf(
                "फ़ोन में 'Files by Google' या फ़ाइल मैनेजर ऐप खोलें।",
                "'Clean' या 'साफ़ करें' विकल्प पर टैप करें।",
                "गैर-ज़रूरी जंक फ़ाइलें डिलीट करें।",
                "व्हाट्सएप की भारी वीडियो फ़ाइलें हटाएं।"
            ),
            settingAction = Settings.ACTION_INTERNAL_STORAGE_SETTINGS
        ),
        DetailedGuide(
            id = "upi_guide",
            title = "UPI (Google Pay / PhonePe / Paytm) से भुगतान कैसे करें",
            subtitle = "दुकान पर QR कोड स्कैन करके पैसे भेजने की आसान गाइड।",
            category = "डिजिटल पेमेंट",
            steps = listOf(
                "अपना पेमेंट ऐप (PhonePe, GPay, Paytm) खोलें।",
                "'Scan QR' बटन दबाएं और दुकान का QR कोड स्कैन करें।",
                "रकम दर्ज करें और अपना गुप्त 4 या 6 अंकों का UPI PIN डालें।",
                "सफल होने पर हरी टिक और आवाज़ सुनाई देगी।"
            )
        ),
        DetailedGuide(
            id = "font_guide",
            title = "स्क्रीन के अक्षर (Font) बड़े और साफ़ कैसे करें",
            subtitle = "बुजुर्गों और पढ़ने में आसानी के लिए स्क्रीन के अक्षरों का आकार बढ़ाएं।",
            category = "सुविधा",
            steps = listOf(
                "फ़ोन की सेटिंग्स (Settings) में जाएं।",
                "'Display' (डिस्प्ले) विकल्प पर टैप करें।",
                "'Font size' या 'Display size' चुनें।",
                "स्लाइडर को दाईं ओर खींचकर अक्षरों को बड़ा करें।"
            ),
            settingAction = Settings.ACTION_DISPLAY_SETTINGS
        ),
        DetailedGuide(
            id = "screenshot_guide",
            title = "स्क्रीनशॉट (फ़ोटो) कैसे खींचें",
            subtitle = "स्क्रीन पर दिखने वाली ज़रूरी रसीद या जानकारी को सुरक्षित करें।",
            category = "सुविधा",
            steps = listOf(
                "फ़ोन का पावर बटन और वॉल्यूम डाउन बटन एक साथ 1 सेकंड दबाएं।",
                "स्क्रीन हल्की सी चमकेगी और फ़ोटो गैलरी में सुरक्षित हो जाएगी।"
            )
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(SaffronPrimary.copy(alpha = 0.2f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Build,
                                contentDescription = null,
                                tint = SaffronPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "फ़ोन कंट्रोल्स व टेक गाइड्स",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Text(
                                text = "स्मार्टफोन सेटिंग्स और आसान समाधान",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = IndigoNight)
            )
        },
        containerColor = IndigoNight,
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Quick Controls Grid Header
            item {
                Text(
                    text = "त्वरित फ़ोन सेटिंग्स (Quick Controls)",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = SaffronGold
                )
            }

            // Quick Actions Grid (2 rows of 3 columns)
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        quickActions.take(3).forEach { action ->
                            QuickActionItem(
                                action = action,
                                onClick = { viewModel.actionExecutor.openSystemSettings(action.settingAction) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        quickActions.drop(3).take(3).forEach { action ->
                            QuickActionItem(
                                action = action,
                                onClick = { viewModel.actionExecutor.openSystemSettings(action.settingAction) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Guides Header
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "सरल हिंदी टेक गाइड्स (Step-by-Step Guides)",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = CyanBright
                )
            }

            // Expandable Guides List
            items(guides, key = { it.id }) { guide ->
                ExpandableGuideCard(
                    guide = guide,
                    onOpenSetting = { action ->
                        if (action != null) {
                            viewModel.actionExecutor.openSystemSettings(action)
                        }
                    },
                    onAskAssistant = {
                        onAskInChat(guide.title)
                    }
                )
            }
        }
    }
}

@Composable
fun QuickActionItem(
    action: QuickAction,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = IndigoSurfaceElevated),
        modifier = modifier
            .clickable { onClick() }
            .testTag("quick_action_${action.title}")
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 6.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(action.color.copy(alpha = 0.2f))
            ) {
                Icon(
                    imageVector = action.icon,
                    contentDescription = action.title,
                    tint = action.color,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = action.title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
        }
    }
}

@Composable
fun ExpandableGuideCard(
    guide: DetailedGuide,
    onOpenSetting: (String?) -> Unit,
    onAskAssistant: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = IndigoSurfaceElevated),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            .testTag("guide_card_${guide.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(CyanGlow.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = guide.category,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = CyanBright
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = guide.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = Color.White
                    )
                    Text(
                        text = guide.subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expanded) "बंद करें" else "खोलें",
                    tint = SaffronGold,
                    modifier = Modifier.size(24.dp)
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    guide.steps.forEachIndexed { index, step ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(IndigoSurfaceBright)
                            ) {
                                Text(
                                    text = "${index + 1}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SaffronGold
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = step,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.9f),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (guide.settingAction != null) {
                            Button(
                                onClick = { onOpenSetting(guide.settingAction) },
                                colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.OpenInNew, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("सेटिंग्स खोलें", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        OutlinedButton(
                            onClick = onAskAssistant,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("सारथी से पूछें", color = CyanBright, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}
