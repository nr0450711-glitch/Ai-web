package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.MainViewModel
import com.example.data.model.ReminderItem
import com.example.ui.theme.CyanBright
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.IndigoNight
import com.example.ui.theme.IndigoSurface
import com.example.ui.theme.IndigoSurfaceBright
import com.example.ui.theme.IndigoSurfaceElevated
import com.example.ui.theme.RedDestructive
import com.example.ui.theme.SaffronGold
import com.example.ui.theme.SaffronPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemindersScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val reminders by viewModel.reminders.collectAsState()
    var selectedCategory by remember { mutableStateOf("सभी") }
    var showAddDialog by remember { mutableStateOf(false) }

    val categories = listOf("सभी", "दवाई", "काम", "मीटिंग", "स्वास्थ्य", "बिल", "अन्य")

    val filteredReminders = if (selectedCategory == "सभी") {
        reminders
    } else {
        reminders.filter { it.category == selectedCategory }
    }

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
                                .background(CyanGlow.copy(alpha = 0.2f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = null,
                                tint = CyanGlow,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "स्मार्ट रिमाइंडर्स",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Text(
                                text = "दवाई, काम और ज़रूरी सूचनाएँ",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = IndigoNight)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = SaffronPrimary,
                contentColor = Color.Black,
                modifier = Modifier.testTag("fab_add_reminder")
            ) {
                Icon(Icons.Default.Add, contentDescription = "नया रिमाइंडर जोड़ें")
            }
        },
        containerColor = IndigoNight,
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Voice tip banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(IndigoSurfaceElevated)
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "💡",
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "बोलकर रिमाइंडर लगाएं: \"शाम 7 बजे दवाई का रिमाइंडर लगाओ\"",
                        style = MaterialTheme.typography.bodySmall,
                        color = SaffronGold
                    )
                }
            }

            // Category filter chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat, fontSize = 13.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = IndigoSurface,
                            labelColor = Color.White,
                            selectedContainerColor = SaffronPrimary,
                            selectedLabelColor = Color.Black
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = selectedCategory == cat,
                            borderColor = IndigoSurfaceBright,
                            selectedBorderColor = SaffronPrimary
                        )
                    )
                }
            }

            // Reminders list
            if (filteredReminders.isEmpty()) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (selectedCategory == "सभी") "कोई रिमाइंडर नहीं है" else "$selectedCategory में कोई रिमाइंडर नहीं है",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "नीचे + बटन दबाकर नया रिमाइंडर जोड़ें या माइक से बोलें।",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray,
                            lineHeight = 20.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredReminders, key = { it.id }) { reminder ->
                        ReminderCard(
                            item = reminder,
                            onToggle = { viewModel.toggleReminderCompletion(reminder) },
                            onDelete = { viewModel.deleteReminder(reminder) }
                        )
                    }
                }
            }
        }

        // Add Reminder Dialog
        if (showAddDialog) {
            AddReminderDialog(
                onDismiss = { showAddDialog = false },
                onAdd = { title, timeDesc, category ->
                    viewModel.addManualReminder(title, timeDesc, category)
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
fun ReminderCard(
    item: ReminderItem,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (item.isCompleted) IndigoSurface.copy(alpha = 0.6f) else IndigoSurfaceElevated
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() }
            .testTag("reminder_item_${item.id}")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(14.dp)
        ) {
            IconButton(
                onClick = onToggle,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = if (item.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                    contentDescription = if (item.isCompleted) "पूरा हुआ" else "बाकी है",
                    tint = if (item.isCompleted) GreenSuccess else Color.Gray,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                when (item.category) {
                                    "दवाई" -> RedDestructive.copy(alpha = 0.2f)
                                    "मीटिंग" -> CyanGlow.copy(alpha = 0.2f)
                                    else -> SaffronPrimary.copy(alpha = 0.2f)
                                }
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = item.category,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = when (item.category) {
                                "दवाई" -> Color(0xFFFCA5A5)
                                "मीटिंग" -> CyanBright
                                else -> SaffronGold
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        textDecoration = if (item.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                    ),
                    color = if (item.isCompleted) Color.Gray else Color.White
                )

                Text(
                    text = item.formattedTime,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (item.isCompleted) Color.Gray else SaffronGold
                )
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier.testTag("btn_delete_reminder_${item.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "डिलीट करें",
                    tint = Color.White.copy(alpha = 0.4f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun AddReminderDialog(
    onDismiss: () -> Unit,
    onAdd: (title: String, timeDesc: String, category: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var timeDesc by remember { mutableStateOf("आज शाम 8:00 बजे") }
    var category by remember { mutableStateOf("दवाई") }

    val categoryOptions = listOf("दवाई", "काम", "मीटिंग", "स्वास्थ्य", "बिल")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("नया रिमाइंडर जोड़ें", color = Color.White, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("रिमाइंडर का नाम") },
                    placeholder = { Text("जैसे: शाम की दवाई, राहुल को कॉल") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = SaffronPrimary,
                        unfocusedBorderColor = IndigoSurfaceBright
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = timeDesc,
                    onValueChange = { timeDesc = it },
                    label = { Text("समय") },
                    placeholder = { Text("जैसे: कल सुबह 9 बजे, आज शाम 7:30") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = SaffronPrimary,
                        unfocusedBorderColor = IndigoSurfaceBright
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Text("श्रेणी चुनें:", style = MaterialTheme.typography.labelMedium, color = Color.Gray)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categoryOptions.forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = IndigoSurface,
                                labelColor = Color.White,
                                selectedContainerColor = SaffronPrimary,
                                selectedLabelColor = Color.Black
                            )
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onAdd(title, timeDesc, category)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary)
            ) {
                Text("जोड़ें", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("रद्द करें", color = Color.Gray)
            }
        },
        containerColor = IndigoSurfaceElevated
    )
}
