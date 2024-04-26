package com.example.mytempapplication

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mytempapplication.databinding.ActivityMainBinding
import com.example.mytempapplication.imageprocessor.ImageProcessorMain
import com.example.mytempapplication.medicationinformation.MedicationInformationActivity
import com.example.mytempapplication.medicationmanagement.logging.MedicationLoggerActivity
import com.example.mytempapplication.medicationmanagement.notifying.NotificationItem
import com.example.mytempapplication.medicationmanagement.notifying.NotificationItemAdapter
import com.example.mytempapplication.notificationscheduling.NotificationSchedulerActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var notificationItemAdapter: NotificationItemAdapter

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        notificationItemAdapter = NotificationItemAdapter

        binding.rvUpcomingMedication.apply {
            adapter = notificationItemAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
        createNotificationChannel()

        val startForResult = registerForActivityResult(
            StartActivityForResult()
        ) { result: ActivityResult ->
            Log.d("RESULT CODE", result.resultCode.toString())
            if (result.resultCode != RESULT_CANCELED) {
                if (result.resultCode == RESULT_OK) {
                    val intent = result.data
//                    Log.d("RETURNED INTENT HOURS", intent.toString())
                    val hours = intent?.getIntegerArrayListExtra("hours")
                    val minutes = intent?.getIntegerArrayListExtra("minutes")
                    val len = hours?.size?.minus(1)
                    Log.d("HOURS", hours.toString())
                    Log.d("MINUTES", minutes.toString())
                    for (i in 0..len!!) {
                        notificationItemAdapter.addNotificationItemAndNotify(
                            NotificationItem(
                                intent.getStringExtra("Medication Name").toString(),
                                intent.getStringExtra("Description").toString(),
                                hours[i],
                                minutes?.get(i) ?: 0,
                            )
                        )
                    }
                }
            }
        }

        binding.BMedLogs.setOnClickListener {
            Intent(this, MedicationLoggerActivity::class.java).also {
                startActivity(it)
            }

        }

        binding.submitButton.setOnClickListener {
            if (!binding.medNameET.text?.isEmpty()!!  && !binding.frequencyET.text?.isEmpty()!! && binding.frequencyET.text?.toString()?.toInt()!! > 0){
                startForResult.launch(Intent(this, NotificationSchedulerActivity::class.java).also {
                    it.putExtra("Medication Name", binding.medNameET.text.toString())
                    it.putExtra("Frequency", binding.frequencyET.text.toString())
                    it.putExtra("Description", binding.descriptionET.text.toString())
                })
                binding.medNameET.text!!.clear()
                binding.frequencyET.text!!.clear()
                binding.descriptionET.text!!.clear()
            }
//            scheduleNotification()
//            startActivityForResult()
        }

        binding.BMedicationInformation.setOnClickListener {
            startForResult.launch(Intent(this, MedicationInformationActivity::class.java))
        }

        val imageSelector = createImageSelector()
        binding.BScanImage.setOnClickListener {
            getUriFromSelectedImage(imageSelector)
        }

    }

    private fun getUriFromSelectedImage(selectImage: ActivityResultLauncher<String>) {
        selectImage.launch("image/*")
    }

    private fun createImageSelector(): ActivityResultLauncher<String> {
//        val intent = Intent(Intent.ACTION_PICK)
//        intent.type = "image/*"

        val imageSelector = registerForActivityResult(
            ActivityResultContracts.GetContent(),
            ActivityResultCallback {
                it?.let { uri ->
                    ImageProcessorMain(applicationContext, uri, binding).also { imageProcessor ->
                        imageProcessor()
                    }

                }
//                binding.image.setImageURI(it)
            }
        )
        return imageSelector
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val name = "Notification Channel"
        val description = "Description: Medication Reminder Channel"
        val importance = NotificationManager.IMPORTANCE_HIGH

        val channel = NotificationChannel(channelID, name, importance)
        channel.description = description
        val notificationManager = getSystemService((NOTIFICATION_SERVICE)) as NotificationManager

        notificationManager.createNotificationChannel(channel)
    }
}