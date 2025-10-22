package com.example.aqualumedb.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.aqualumedb.R
import com.example.aqualumedb.models.WaterLog
import java.text.SimpleDateFormat
import java.util.*

class WaterLogAdapter(
    private var waterLogs: List<WaterLog>,
    private val onEditClick: (WaterLog) -> Unit,
    private val onDeleteClick: (WaterLog) -> Unit
) : RecyclerView.Adapter<WaterLogAdapter.WaterLogViewHolder>() {

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("h.mm a", Locale.getDefault())
    private val today = Calendar.getInstance()

    inner class WaterLogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDateHeader: TextView = itemView.findViewById(R.id.tvDateHeader)
        val tvAmount: TextView = itemView.findViewById(R.id.tv_amount)
        val tvTime: TextView = itemView.findViewById(R.id.tv_time)
        val tvAchievement: TextView = itemView.findViewById(R.id.tvAchievement)
        val btnEdit: ImageView = itemView.findViewById(R.id.iv_edit)
        val btnDelete: ImageView = itemView.findViewById(R.id.iv_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WaterLogViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_water_log, parent, false)
        return WaterLogViewHolder(view)
    }

    override fun onBindViewHolder(holder: WaterLogViewHolder, position: Int) {
        val waterLog = waterLogs[position]

        // Show date header if this is the first item or date changed from previous item
        if (position == 0 || !isSameDate(waterLog.timestamp, waterLogs[position - 1].timestamp)) {
            holder.tvDateHeader.visibility = View.VISIBLE

            // Format date header
            val logDate = Calendar.getInstance().apply { timeInMillis = waterLog.timestamp }
            val headerText = when {
                isSameDate(waterLog.timestamp, today.timeInMillis) -> "Today"
                isYesterday(waterLog.timestamp) -> "Yesterday"
                else -> dateFormat.format(Date(waterLog.timestamp))
            }
            holder.tvDateHeader.text = headerText
        } else {
            holder.tvDateHeader.visibility = View.GONE
        }

        // Set amount
        holder.tvAmount.text = "${waterLog.amount}ml"

        // Set time
        holder.tvTime.text = timeFormat.format(Date(waterLog.timestamp))

        // Show achievement if daily goal completed
        if (waterLog.isDailyGoalCompleted) {
            holder.tvAchievement.visibility = View.VISIBLE
            holder.tvAchievement.text = "${waterLog.dailyTotal / 1000f} liters Completed"
        } else {
            holder.tvAchievement.visibility = View.GONE
        }

        // Set click listeners
        holder.btnEdit.setOnClickListener { onEditClick(waterLog) }
        holder.btnDelete.setOnClickListener { onDeleteClick(waterLog) }
    }

    override fun getItemCount(): Int = waterLogs.size

    fun updateLogs(newLogs: List<WaterLog>) {
        waterLogs = newLogs
        notifyDataSetChanged()
    }

    private fun isSameDate(timestamp1: Long, timestamp2: Long): Boolean {
        val cal1 = Calendar.getInstance().apply { timeInMillis = timestamp1 }
        val cal2 = Calendar.getInstance().apply { timeInMillis = timestamp2 }
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    private fun isYesterday(timestamp: Long): Boolean {
        val yesterday = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -1)
        }
        return isSameDate(timestamp, yesterday.timeInMillis)
    }
}