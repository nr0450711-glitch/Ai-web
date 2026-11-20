package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminders")
data class ReminderItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val timeMillis: Long,
    val formattedTime: String,
    val category: String = "दवाई", // दवाई, मीटिंग, कॉल, काम, आदि
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
