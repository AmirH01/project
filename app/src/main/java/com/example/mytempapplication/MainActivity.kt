package com.example.mytempapplication

import android.app.AlarmManager.*
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context.*
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import com.example.mytempapplication.databinding.ActivityMainBinding
import com.example.mytempapplication.imageprocessor.ImageProcessor
import com.example.mytempapplication.notificationscheduling.NotificationScheduler
import java.util.Calendar
import java.util.Date

class MainActivity : AppCompatActivity() {


    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var uri: String

    private var medicationName: String? = null
    private var frequency: String? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        createNotificationChannel()
        binding.submitButton.setOnClickListener {
//            scheduleNotification()
            Intent(this, NotificationScheduler::class.java).also {
                it.putExtra("Medication Name", binding.medNameET.text.toString() ?: "No name")
                it.putExtra("Frequency", binding.frequencyET.text.toString() ?: "No frequency")
                it.putExtra("Description", binding.descriptionET.text.toString() ?: "No description")
                startActivity(it)
            }

        }

        val imageSelector = createImageSelector()
        binding.selectImage.setOnClickListener {
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
                    ImageProcessor(applicationContext, uri, binding).also { imageProcessor ->
                        imageProcessor()
                    }

                }
                binding.image.setImageURI(it)
            }
        )
        return imageSelector
    }
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    private fun scheduleNotification() {
//        val intent = Intent(applicationContext, NotificationBroadcastReceiver::class.java)
//        medicationName = binding.medNameET.text.toString()
//        frequency = binding.frequencyET.text.toString()
//        intent.putExtra(medicationNameExtra, medicationName)
//        intent.putExtra(frequencyExtra, frequency)
//
//        val pendingIntent = PendingIntent.getBroadcast(
//            applicationContext,
//            notificationID,
//            intent,
//            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
//        )
//        val repeatingPendingIntent = PendingIntent.getBroadcast(
//            applicationContext,
//            notificationID + 1,
//            intent,
//            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
//        )
//
//
//        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
//        val time = getTime()
//
//        alarmManager.setExactAndAllowWhileIdle(
//            RTC_WAKEUP,
//            time,
//            pendingIntent
//        )
//        alarmManager.setInexactRepeating(
//            RTC_WAKEUP,
//            time,
//            60_000,
//            repeatingPendingIntent
//        )
//
////        showAlert(time, medicationName, frequency)
//    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showAlert(time: Long, medicationName: String, frequency: String) {
        val notificationTime = Date(time)
        val timeFormat = android.text.format.DateFormat.getTimeFormat(applicationContext)

        AlertDialog.Builder(this)
            .setTitle("Notification Scheduled")
            .setMessage(
                "Medication Name: $medicationName" +
                        "\n Frequency: $frequency" +
                        "\nAt: " + timeFormat.format(notificationTime)
            )
            .setPositiveButton("Okay") { _, _ -> }
            .show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getTime(): Long {

//        val minute = binding.timePicker.minute
//        val hour = binding.timePicker.hour

        val calendar = Calendar.getInstance()

//        calendar.set(Calendar.HOUR_OF_DAY, hour)
//        calendar.set(Calendar.MINUTE, minute)
//        calendar.set(Calendar.SECOND, 0)

        Log.e("ALARM SCHEDULER", "Alarm Scheduled for ${calendar.time}")

        return calendar.timeInMillis
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