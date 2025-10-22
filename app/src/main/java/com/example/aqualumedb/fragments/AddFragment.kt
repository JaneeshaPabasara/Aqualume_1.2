package com.example.aqualumedb.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aqualumedb.R
import com.example.aqualumedb.adapters.TaskAdapter
import com.example.aqualumedb.databinding.FragmentAddBinding
import com.example.aqualumedb.models.Task
import com.example.aqualumedb.utils.PreferencesManager

class AddFragment : Fragment() {

    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!
    private lateinit var prefsManager: PreferencesManager
    private lateinit var taskAdapter: TaskAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefsManager = PreferencesManager(requireContext())

        setupClickListeners()
        setupTaskRecyclerView()
        loadTasks()
    }

    private fun setupClickListeners() {
        binding.cardWater.setOnClickListener {
            findNavController().navigate(R.id.action_add_to_water)
        }

        binding.cardMeditation.setOnClickListener {
            findNavController().navigate(R.id.action_add_to_meditate)
        }

        binding.cardJournal.setOnClickListener {
            findNavController().navigate(R.id.action_add_to_journal)
        }

        binding.cardMedicine.setOnClickListener {

        }
        binding.btnAddTask.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_addTask)
        }
    }

    private fun setupTaskRecyclerView() {
        taskAdapter = TaskAdapter { task ->
            // Toggle task completion
            prefsManager.updateTaskCompletion(task.id, !task.isCompleted)
            loadTasks()
        }

        binding.rvTasks.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = taskAdapter
        }
    }

    private fun loadTasks() {
        // Get all tasks
        val allTasks = prefsManager.getAllTasks().toMutableList()

        // Get current settings
        val settings = prefsManager.getUserSettings()

        // Remove old tasks and recreate them with updated values
        allTasks.removeAll { it.title.contains("water", ignoreCase = true) ||
                it.title.contains("meditate", ignoreCase = true) ||
                it.title.contains("journal", ignoreCase = true) }

        // Create fresh tasks with current goals
        val waterTask = Task(
            title = "Drink water - ${settings.waterGoal / 1000} liters",
            isCompleted = false
        )

        val meditationTask = Task(
            title = "Meditate ${settings.meditationGoal} min",
            isCompleted = false
        )

        val journalTask = Task(
            title = "Write the journal",
            isCompleted = false
        )

        // Clear all tasks from preferences
        prefsManager.clearAllTasks()

        // Save new tasks
        prefsManager.saveTask(waterTask)
        prefsManager.saveTask(meditationTask)
        prefsManager.saveTask(journalTask)

        // Load fresh task list
        val updatedTasks = prefsManager.getAllTasks()

        taskAdapter.submitList(updatedTasks.sortedBy { it.isCompleted })
    }


    override fun onResume() {
        super.onResume()
        loadTasks()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}