package com.example.mytempapplication.medicationmanagement

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.mytempapplication.R
import com.example.mytempapplication.databinding.ItemMedicationBinding
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar


object NotificationItemAdapter: RecyclerView.Adapter<NotificationItemAdapter.NotificationItemViewHolder>() {

    private val notifications: MutableList<NotificationItem> = mutableListOf()
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

    private fun getTime(hour: Int, minute: Int): String = Calendar.getInstance().run {
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        val df = DateFormat.getTimeInstance()
        val timeFormatted = df.format(time)
        Log.d("FORMATTED TIME FOR NOTIFICATION ITEM", timeFormatted)
        timeFormatted
    }
}