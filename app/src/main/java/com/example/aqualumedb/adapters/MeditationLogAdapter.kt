package com.example.aqualumedb.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.aqualumedb.databinding.ItemMeditationLogBinding
import com.example.aqualumedb.models.MeditationLog
import java.text.SimpleDateFormat
import java.util.*

class MeditationLogAdapter(
    private val onEditClick: (MeditationLog) -> Unit,
    private val onDeleteClick: (MeditationLog) -> Unit
) : ListAdapter<MeditationLog, MeditationLogAdapter.MeditationLogViewHolder>(MeditationLogDiffCallback()) {

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeditationLogViewHolder {
        val binding = ItemMeditationLogBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MeditationLogViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MeditationLogViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    inner class MeditationLogViewHolder(
        private val binding: ItemMeditationLogBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(meditationLog: MeditationLog, position: Int) {
            // Show date header if this is the first item or date changed from previous item
            if (position == 0 || !isSameDate(meditationLog.timestamp, getItem(position - 1).timestamp)) {
                binding.tvDateHeader.visibility = View.VISIBLE

                // Format date header
                val today = Calendar.getInstance()
                val logDate = Calendar.getInstance().apply { timeInMillis = meditationLog.timestamp }

                val headerText = when {
                    isSameDate(meditationLog.timestamp, today.timeInMillis) -> "Today"
                    isYesterday(meditationLog.timestamp) -> "Yesterday"
                    else -> dateFormat.format(Date(meditationLog.timestamp))
                }
                binding.tvDateHeader.text = headerText
            } else {
                binding.tvDateHeader.visibility = View.GONE
            }

            // Set duration
            binding.tvDuration.text = "${meditationLog.duration} min"

            // Set time
            binding.tvTime.text = timeFormat.format(Date(meditationLog.timestamp))



            // Set click listeners
            binding.ivEdit.setOnClickListener {
                onEditClick(meditationLog)
            }

            binding.ivDelete.setOnClickListener {
                onDeleteClick(meditationLog)
            }
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

    class MeditationLogDiffCallback : DiffUtil.ItemCallback<MeditationLog>() {
        override fun areItemsTheSame(oldItem: MeditationLog, newItem: MeditationLog): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: MeditationLog, newItem: MeditationLog): Boolean {
            return oldItem == newItem
        }
    }
}