package com.example.aqualumedb.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.aqualumedb.databinding.ItemCustomTaskBinding
import com.example.aqualumedb.models.CustomTask

class CustomTaskAdapter(
    private val onEditClick: (CustomTask) -> Unit,
    private val onDeleteClick: (CustomTask) -> Unit
) : ListAdapter<CustomTask, CustomTaskAdapter.CustomTaskViewHolder>(CustomTaskDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomTaskViewHolder {
        val binding = ItemCustomTaskBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CustomTaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CustomTaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CustomTaskViewHolder(
        private val binding: ItemCustomTaskBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(customTask: CustomTask) {
            binding.tvTaskName.text = customTask.taskName
            binding.tvTaskTime.text = customTask.timeDuration

            binding.ivEdit.setOnClickListener {
                onEditClick(customTask)
            }

            binding.ivDelete.setOnClickListener {
                onDeleteClick(customTask)
            }
        }
    }

    class CustomTaskDiffCallback : DiffUtil.ItemCallback<CustomTask>() {
        override fun areItemsTheSame(oldItem: CustomTask, newItem: CustomTask): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CustomTask, newItem: CustomTask): Boolean {
            return oldItem == newItem
        }
    }
}