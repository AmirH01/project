package com.example.mytempapplication.notificationscheduling

import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class NotificationAdapterTest {

    private lateinit var notificationAdapter: NotificationAdapter
    private val timeSelectedListener: NotificationAdapter.OnTimeSelectedListener = mockk<NotificationAdapter.OnTimeSelectedListener>()

    @Test
    fun getItemCountIsZero() {
        val expected = 0
        notificationAdapter = NotificationAdapter(createNotifications(expected), timeSelectedListener)
        assertEquals(expected, notificationAdapter.itemCount)
    }
    @Test
    fun getItemCountIsTwo() {
        val expected = 2
        notificationAdapter = NotificationAdapter(createNotifications(expected), timeSelectedListener)
        assertEquals(expected, notificationAdapter.itemCount)
    }

    @Test
    fun getNotifications() {
        val notification = Notification("amlodipine", 5, 45)
        notificationAdapter = NotificationAdapter(mutableListOf(notification), timeSelectedListener)

        assertEquals(mutableListOf(notification), notificationAdapter.notifications)
    }

    private fun createNotifications(size: Int): List<Notification>{
        val notifications = mutableListOf<Notification>()
        for (i in 0..<size){
            notifications.add(Notification("amlodipine", i, i))
        }
        return notifications
    }

}