package com.example.mytempapplication.medicationmanagement

data class NotificationItem(
    val medication: String,
    val description: String,
    val hour: Int,
    val minute: Int,
)