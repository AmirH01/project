package com.example.mytempapplication

import android.app.AlertDialog
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
import androidx.navigation.ui.AppBarConfiguration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mytempapplication.databinding.ActivityMainBinding
import com.example.mytempapplication.imageprocessor.ImageProcessorMain
import com.example.mytempapplication.medicationmanagement.logging.MedicationLoggerActivity
import com.example.mytempapplication.medicationmanagement.notifying.NotificationItem
import com.example.mytempapplication.medicationmanagement.notifying.NotificationItemAdapter
import com.example.mytempapplication.notificationscheduling.NotificationSchedulerActivity
import java.util.Calendar
import java.util.Date

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var uri: String
    private lateinit var notificationItemAdapter: NotificationItemAdapter

    private var medicationName: String? = null
    private var frequency: String? = null


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


        val startForResult = registerForActivityResult(
            StartActivityForResult()
        ) { result: ActivityResult ->
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
                        notificationItemAdapter.addNotificationItem(
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

        createNotificationChannel()
        binding.submitButton.setOnClickListener {
//            scheduleNotification()
//            startActivityForResult()

            startForResult.launch(Intent(this, NotificationSchedulerActivity::class.java).also {
                it.putExtra("Medication Name", binding.medNameET.text.toString())
                it.putExtra("Frequency", binding.frequencyET.text.toString())
                it.putExtra("Description", binding.descriptionET.text.toString())
//                startActivity(it)
            })


        }

        binding.BMedicationInformation.setOnClickListener {
            startForResult.launch(Intent(this, MedicationInformationActivity::class.java))
//                startActivity(it)

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