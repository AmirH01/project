package com.example.mytempapplication.notificationscheduling

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.mytempapplication.R
import com.example.mytempapplication.databinding.ItemNotificationBinding

class NotificationAdapter(
    var notifications : List<Notification>,
    private val timeSelectedListener: OnTimeSelectedListener
) : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {
    inner class NotificationViewHolder(itemView: View) : ViewHolder(itemView)
    {
        val binding = ItemNotificationBinding.bind(itemView)
    }

    interface OnTimeSelectedListener {
        fun onTimeSelected(position: Int, hour: Int, minute: Int)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_notification, parent, false)
        return NotificationViewHolder(view)
    }

    override fun getItemCount(): Int {
        return notifications.size
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.binding.apply {
            tvNotification.text = notifications[position].title
            tpNotification.setOnTimeChangedListener { _, hour, minute ->
                timeSelectedListener.onTimeSelected(position, hour, minute)
            }
        }
    }


}