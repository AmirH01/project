package com.example.mytempapplication.medicationmanagement.notifying

import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.mytempapplication.R
import com.example.mytempapplication.databinding.ItemMedicationBinding
import com.example.mytempapplication.medicationmanagement.logging.MedicationItem
import com.example.mytempapplication.medicationmanagement.logging.MedicationItemAdapter
import java.text.DateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date


object NotificationItemAdapter: RecyclerView.Adapter<NotificationItemAdapter.NotificationItemViewHolder>() {

    @RequiresApi(Build.VERSION_CODES.O)
    private val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
    val notifications: MutableList<NotificationItem> = mutableListOf()
    private val medicationItemAdapter: MedicationItemAdapter = MedicationItemAdapter
    class NotificationItemViewHolder(itemView: View) : ViewHolder(itemView) {
        val binding = ItemMedicationBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationItemViewHolder {
        return NotificationItemViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_medication,
                parent,
                false
            )
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addNotificationItemAndNotify(notificationItem: NotificationItem) {
        addNotification(notificationItem)
        notifications.sortBy { rawTime(it.hour, it.minute) }
        notifyItemRangeChanged(0, notifications.size - 1)
    }

    fun addNotification(notificationItem: NotificationItem){
        notifications.add(notificationItem)
    }

    override fun getItemCount(): Int {
        return notifications.size
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: NotificationItemViewHolder, position: Int) {
        val notificationItem = notifications[position]
        holder.binding.apply {
            tvMedicationName.text = notificationItem.medication
            tvMedicationDescription.text = notificationItem.description
            tvMedicationTime.text = getTime(notificationItem.hour, notificationItem.minute)
            bConsumed.setOnClickListener {
                medicationItemAdapter.run{

                    addMedicationItemAndNotify(MedicationItem(tvMedicationName.text as String, createLogDate()))
                    Log.wtf("tvMEDICATIONNAME text", tvMedicationName.text as String)
//                    log.add(MedicationItem(notificationItem.medication, createLogDate()))
//                    medicationLoggerAdapter.notifyItemInserted(this.itemCount)
                }

                Log.d("LOG SIZE AFTER INSERTING:", medicationItemAdapter.itemCount.toString())
                Log.d("DEBUG", "Notification item added. Size: $itemCount")

                notifications.removeAt(position)
                Log.d("LIST SIZE AFTER REMOVING:", notifications.size.toString())
                Log.d("POSITION REMOVED:", position.toString())

//                notifyDataSetChanged() if removing items no longer works uncomment this line
//                notifyItemRemoved(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, notifications.size)
                Log.d("LIST SIZE AFTER NOTIFYING:", notifications.size.toString())

            }
            bCancelled.setOnClickListener {
                notifications.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, notifications.size)
                Log.d("LIST SIZE AFTER REMOVING:", notifications.size.toString())
                Log.d("POSITION REMOVED:", position.toString())
                Log.d("LIST SIZE AFTER NOTIFYING:", notifications.size.toString())
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createLogDate(): String = LocalDateTime.now().format(formatter)

    private fun getTime(hour: Int, minute: Int): String = Calendar.getInstance().run {
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
        val df = DateFormat.getTimeInstance()
        val timeFormatted = df.format(time)
        Log.d("FORMATTED TIME FOR NOTIFICATION ITEM", timeFormatted)
        timeFormatted
    }

    private fun rawTime(hour: Int, minute: Int): Date {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, hour)
        cal.set(Calendar.MINUTE, minute)
        cal.set(Calendar.SECOND, 0)
        return cal.time
    }
}