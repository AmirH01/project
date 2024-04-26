package com.example.mytempapplication.medicationmanagement.logging

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mytempapplication.R
import com.example.mytempapplication.databinding.ItemLoggedMedicationBinding

object MedicationItemAdapter :
    RecyclerView.Adapter<MedicationItemAdapter.LoggedMedicationItemViewHolder>() {

    private val log = mutableListOf<MedicationItem>()
    private var count = 1

    class LoggedMedicationItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemLoggedMedicationBinding.bind(itemView)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LoggedMedicationItemViewHolder {
        return LoggedMedicationItemViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_logged_medication,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return log.size
    }

    override fun onBindViewHolder(holder: LoggedMedicationItemViewHolder, position: Int) {
        val logItem = log[position]
        holder.binding.apply {
            tvMedicationName.text = logItem.name
            tvMedicationDateTime.text = logItem.dateTime
        }
    }

    fun addMedicationItemAndNotify(medicationItem: MedicationItem) {
        addMedication(medicationItem)
        print(log)
        notifyItemInserted(log.size - 1)

//        notifyItemRangeChanged(log.size - 1, 1)
        Log.d("ADDING MEDICATION ITEM", count.toString())
        count++
    }

    fun addMedication(medicationItem: MedicationItem) {
        log.add(medicationItem)

    }
}