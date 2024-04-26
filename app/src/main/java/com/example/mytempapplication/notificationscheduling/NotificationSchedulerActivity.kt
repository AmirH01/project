package com.example.mytempapplication.notificationscheduling

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mytempapplication.NotificationBroadcastReceiver
import com.example.mytempapplication.NotificationBroadcastReceiver.Companion.getAndIncrementNotificationId
import com.example.mytempapplication.databinding.ActivityNotificationSchedulerBinding
import com.example.mytempapplication.descriptionExtra
import com.example.mytempapplication.medicationNameExtra
import java.time.Instant
import java.time.LocalDateTime
import java.util.Calendar
import kotlin.properties.Delegates

class NotificationSchedulerActivity : AppCompatActivity(),
    NotificationAdapter.OnTimeSelectedListener {

    private lateinit var medicationName: String
    private var numberOfNotifications by Delegates.notNull<Int>()
    private lateinit var description: String

    val list = mutableListOf<Notification>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityNotificationSchedulerBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        medicationName = this.intent.getStringExtra("Medication Name") ?: "No Medication Name"
        numberOfNotifications = this.intent.getStringExtra("Frequency")?.toInt() ?: 0
        description = this.intent.getStringExtra("Description") ?: ""

        populateList(numberOfNotifications)

        val adapter = NotificationAdapter(list, this)
        binding.notifications.adapter = adapter
        binding.notifications.layoutManager = LinearLayoutManager(this)

        val intent = Intent(applicationContext, NotificationBroadcastReceiver::class.java)
        intent.putExtra(medicationNameExtra, medicationName)
        intent.putExtra(descriptionExtra, description)

        binding.confirmButton.setOnClickListener {
//            for (i in 0..<list.size)
//            {
//                Log.d("LIST TIME", "${list[i].timePicker.hour} ${list[i].timePicker.minute}")
//            }
            list.forEach {

                val pendingIntent = PendingIntent.getBroadcast(
                    applicationContext,
                    getAndIncrementNotificationId(),
                    intent,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
                val repeatingPendingIntent = PendingIntent.getBroadcast(
                    applicationContext,
                    getAndIncrementNotificationId(),
                    intent,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
                val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
                Log.e("TITLE", it.title)
                Log.e("HOUR", "${it.hour}")
                Log.e("MINUTE", "${it.minute}")
                val time = getTimeInMilliseconds(it.hour, it.minute)

                if (time >= Instant.now().toEpochMilli()){
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        time,
                        pendingIntent
                    )
                }

                alarmManager.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    time + oneDayInMilliseconds(),
                    oneDayInMilliseconds(),
                    repeatingPendingIntent
                )
            }
            val hours = ArrayList(list.map { it.hour })

            val returnIntent = Intent().also { intent ->
                intent.putExtra("Medication Name", medicationName)
                intent.putExtra("Description", description)
                intent.putIntegerArrayListExtra("hours", hours)
                intent.putIntegerArrayListExtra("minutes", ArrayList(list.map { it.minute }))
            }
            Log.wtf("MEDICATION NAME:", medicationName)
            Log.wtf("DESCRIPTION:", description)
            Log.wtf("hours:", hours.size.toString())
            setResult(Activity.RESULT_OK, returnIntent)
            finish()
        }
    }

    private fun oneDayInMilliseconds(): Long = 86_400_000

    fun populateList(frequency: Int) {
        for (i in 1..frequency) {
            list.add(Notification("Notification $i", 0, 0))
        }
    }

    fun getTimeInMilliseconds(hour: Int, minute: Int): Long {
        val calendar = Calendar.getInstance()

        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)

        Log.e("ALARM SCHEDULER", "Alarm Scheduled for ${calendar.time}")
        Log.e("TIME IN MILLISECONDS", calendar.timeInMillis.toString())
        return calendar.timeInMillis
    }

    override fun onTimeSelected(position: Int, hour: Int, minute: Int) {
        list[position] = list[position].copy(hour = hour, minute = minute)
    }

//    override fun finish() {
//        val returnIntent = Intent().also { intent ->
//            intent.putExtra("Medication Name", medicationName)
//            intent.putExtra("Description", description)
//            intent.putIntegerArrayListExtra("hours", ArrayList(list.map{it.hour}))
//            intent.putIntegerArrayListExtra("minutes", ArrayList(list.map{it.minute}))
//        }
//        setResult(Activity.RESULT_OK, returnIntent)
//        super.finish()
//    }
}