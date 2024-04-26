package com.example.mytempapplication.medicationmanagement.logging

import com.example.mytempapplication.medicationmanagement.notifying.NotificationItemAdapter
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class MedicationItemAdapterTest {

    private val medicationItemAdapter = MedicationItemAdapter
    private val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")

    @Test
    @Order(1)
    fun getItemCount() {
        assertEquals(0, medicationItemAdapter.itemCount)
    }

    @Test
    @Order(2)
    fun addMedicationItem() {
        val medicationItem = MedicationItem("amlodipine", LocalDateTime.now().format(formatter))
        medicationItemAdapter.addMedication(medicationItem)
        assertEquals(1, medicationItemAdapter.itemCount)
    }
}