package com.example.mytempapplication.medicationmanagement.notifying

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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


object NotificationItemAdapter: RecyclerView.Adapter<NotificationItemAdapter.NotificationItemViewHolder>() {

    private val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
    private val notifications: MutableList<NotificationItem> = mutableListOf()
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

    fun addNotificationItem(notificationItem: NotificationItem) {
        notifications.add(notificationItem)
        notifyItemInserted(notifications.size - 1)
    }

    override fun getItemCount(): Int {
        return notifications.size
    }

    override fun onBindViewHolder(holder: NotificationItemViewHolder, position: Int) {
        val notificationItem = notifications[position]
        holder.binding.apply {
            tvMedicationName.text = notificationItem.medication
            tvMedicationDescription.text = notificationItem.description
            tvMedicationTime.text = getTime(notificationItem.hour, notificationItem.minute)
            bConsumed.setOnClickListener {
                medicationItemAdapter.run{

                    addMedicationItem(MedicationItem(tvMedicationName.text as String, createLogDate()))
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
        }
    }

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
}