package com.example.aqualumedb.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aqualumedb.adapters.CustomTaskAdapter
import com.example.aqualumedb.databinding.FragmentAddTaskBinding
import com.example.aqualumedb.models.CustomTask
import com.example.aqualumedb.utils.PreferencesManager

class AddTaskFragment : Fragment() {

    private var _binding: FragmentAddTaskBinding? = null
    private val binding get() = _binding!!
    private lateinit var prefsManager: PreferencesManager
    private lateinit var customTaskAdapter: CustomTaskAdapter
    private var editingTaskId: Long? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefsManager = PreferencesManager(requireContext())

        setupRecyclerView()
        setupClickListeners()
        loadCustomTasks()
    }

    private fun setupRecyclerView() {
        customTaskAdapter = CustomTaskAdapter(
            onEditClick = { customTask ->
                // Load task details into form
                binding.etTask.setText(customTask.taskName)
                binding.etTime.setText(customTask.timeDuration)
                editingTaskId = customTask.id
                binding.btnSave.text = "Update"

                // Scroll to top
                binding.root.smoothScrollTo(0, 0)
            },
            onDeleteClick = { customTask ->
                // Show delete confirmation
                AlertDialog.Builder(requireContext())
                    .setTitle("Delete Task")
                    .setMessage("Are you sure you want to delete this task?")
                    .setPositiveButton("Delete") { _, _ ->
                        prefsManager.deleteCustomTask(customTask.id)
                        loadCustomTasks()
                        Toast.makeText(requireContext(), "Task deleted", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        )

        binding.rvCustomTasks.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = customTaskAdapter
        }
    }

    private fun setupClickListeners() {
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnSave.setOnClickListener {
            saveOrUpdateTask()
        }
    }

    private fun saveOrUpdateTask() {
        val taskName = binding.etTask.text.toString().trim()
        val timeDuration = binding.etTime.text.toString().trim()

        if (taskName.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter task name", Toast.LENGTH_SHORT).show()
            return
        }

        if (timeDuration.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter time duration", Toast.LENGTH_SHORT).show()
            return
        }

        if (editingTaskId != null) {
            // Update existing task
            val updatedTask = CustomTask(
                id = editingTaskId!!,
                taskName = taskName,
                timeDuration = timeDuration
            )
            prefsManager.updateCustomTask(updatedTask)
            Toast.makeText(requireContext(), "Task updated successfully!", Toast.LENGTH_SHORT).show()
            editingTaskId = null
            binding.btnSave.text = "Save"
        } else {
            // Create new task
            val customTask = CustomTask(
                taskName = taskName,
                timeDuration = timeDuration
            )
            prefsManager.saveCustomTask(customTask)
            Toast.makeText(requireContext(), "Task added successfully!", Toast.LENGTH_SHORT).show()
        }

        // Clear form
        binding.etTask.text.clear()
        binding.etTime.text.clear()

        // Reload tasks
        loadCustomTasks()
    }

    private fun loadCustomTasks() {
        val customTasks = prefsManager.getAllCustomTasks()
        customTaskAdapter.submitList(customTasks.sortedByDescending { it.timestamp })

        // Show/hide empty state
        if (customTasks.isEmpty()) {
            binding.rvCustomTasks.visibility = View.GONE
        } else {
            binding.rvCustomTasks.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}