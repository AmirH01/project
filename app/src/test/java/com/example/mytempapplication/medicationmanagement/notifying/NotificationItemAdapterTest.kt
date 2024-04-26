package com.example.mytempapplication.medicationmanagement.notifying

import androidx.recyclerview.widget.RecyclerView.Adapter
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.verify
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.ClassOrderer.OrderAnnotation
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class NotificationItemAdapterTest {
    private val notificationItemAdapter: NotificationItemAdapter = NotificationItemAdapter


    @Test
    @Order(1)
    fun addNotificationItem() {
        val notificationItem = NotificationItem(
            "amlodipine",
            "eat with food",
            2,
            40
        )
        notificationItemAdapter.addNotification(notificationItem)

        assertEquals(1, notificationItemAdapter.notifications.size)
    }

    @Test
    @Order(2)
    fun getItemCount() {
        assertEquals(1, notificationItemAdapter.itemCount) // Object, so size carried over from previous test
    }
}