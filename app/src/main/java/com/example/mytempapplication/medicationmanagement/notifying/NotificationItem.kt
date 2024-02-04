package com.example.mytempapplication.medicationmanagement.notifying

data class NotificationItem(
    val medication: String,
    val description: String,
    val hour: Int,
    val minute: Int,
)