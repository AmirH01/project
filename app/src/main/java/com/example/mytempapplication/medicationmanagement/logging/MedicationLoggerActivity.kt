package com.example.mytempapplication.medicationmanagement.logging

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mytempapplication.databinding.ActivityMedicationLoggerBinding

class MedicationLoggerActivity : AppCompatActivity() {

    private val adapter = MedicationItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMedicationLoggerBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.rvMedicationLog.adapter = adapter

        binding.rvMedicationLog.layoutManager = LinearLayoutManager(this)
    }


}
