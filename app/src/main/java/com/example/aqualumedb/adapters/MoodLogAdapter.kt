package com.example.aqualumedb.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.aqualumedb.databinding.ItemMoodLogBinding
import com.example.aqualumedb.models.MoodLog
import java.text.SimpleDateFormat
import java.util.*

class MoodLogAdapter(
    private val onEditClick: (MoodLog) -> Unit,
    private val onDeleteClick: (MoodLog) -> Unit
) : ListAdapter<MoodLog, MoodLogAdapter.MoodLogViewHolder>(MoodLogDiffCallback()) {

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoodLogViewHolder {
        val binding = ItemMoodLogBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MoodLogViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MoodLogViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    inner class MoodLogViewHolder(
        private val binding: ItemMoodLogBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(moodLog: MoodLog, position: Int) {
            // Show date header if this is the first item or date changed from previous item
            if (position == 0 || !isSameDate(moodLog.timestamp, getItem(position - 1).timestamp)) {
                binding.tvDateHeader.visibility = View.VISIBLE

                // Format date header
                val today = Calendar.getInstance()

                val headerText = when {
                    isSameDate(moodLog.timestamp, today.timeInMillis) -> "Today"
                    isYesterday(moodLog.timestamp) -> "Yesterday"
                    else -> dateFormat.format(Date(moodLog.timestamp))
                }
                binding.tvDateHeader.text = headerText
            } else {
                binding.tvDateHeader.visibility = View.GONE
            }

            // Set mood emoji image
            binding.tvEmoji.setImageResource(moodLog.moodDrawable)

            // Set mood name with capitalization
            binding.tvMood.text = moodLog.mood.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
            }

            // Format and set time
            binding.tvTime.text = timeFormat.format(Date(moodLog.timestamp))

            // Show note if available
            if (moodLog.note.isNotEmpty()) {
                binding.tvNote.visibility = View.VISIBLE
                binding.tvNote.text = moodLog.note
            } else {
                binding.tvNote.visibility = View.GONE
            }

            // Edit button click
            binding.ivEdit.setOnClickListener {
                onEditClick(moodLog)
            }

            // Delete button click
            binding.ivDelete.setOnClickListener {
                onDeleteClick(moodLog)
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

    class MoodLogDiffCallback : DiffUtil.ItemCallback<MoodLog>() {
        override fun areItemsTheSame(oldItem: MoodLog, newItem: MoodLog): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: MoodLog, newItem: MoodLog): Boolean {
            return oldItem == newItem
        }
    }
}