package com.example.aqualumedb.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.aqualumedb.R
import com.example.aqualumedb.databinding.FragmentWaterBinding
import com.example.aqualumedb.workers.ReminderWorker
import com.example.aqualumedb.utils.PreferencesManager
import java.util.concurrent.TimeUnit

class WaterFragment : Fragment() {

    private var _binding: FragmentWaterBinding? = null
    private val binding get() = _binding!!
    private lateinit var prefsManager: PreferencesManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWaterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefsManager = PreferencesManager(requireContext())

        setupUI()
        setupClickListeners()
        loadSettings()
    }

    private fun setupUI() {
        // Setup goal spinner (1-10 liters)
        val goals = (1..10).map { it }.toTypedArray()
        val goalAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, goals)
        goalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerGoal.adapter = goalAdapter

        // Setup duration spinner (1-12 hours)
        val durations = (1..12).map { it }.toTypedArray()
        val durationAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, durations)
        durationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerDuration.adapter = durationAdapter

        // Setup time unit spinner
        val timeUnitAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, arrayOf("hrs", "min"))
        timeUnitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTimeUnit.adapter = timeUnitAdapter
    }

    private fun setupClickListeners() {
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnViewHistory.setOnClickListener {
            findNavController().navigate(R.id.action_water_to_hydrationHistory)
        }

        binding.btnAddWater.setOnClickListener {
            saveSettings()
        }

        binding.switchReminder.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                scheduleWaterReminders()
            } else {
                cancelWaterReminders()
            }

            val settings = prefsManager.getUserSettings()
            prefsManager.saveUserSettings(settings.copy(waterReminderEnabled = isChecked))
        }

        binding.spinnerGoal.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
                val goal = (position + 1) * 1000
                val settings = prefsManager.getUserSettings()
                prefsManager.saveUserSettings(settings.copy(waterGoal = goal))
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        }
    }

    private fun saveSettings() {
        val goal = (binding.spinnerGoal.selectedItemPosition + 1) * 1000
        val duration = binding.spinnerDuration.selectedItem.toString().toLong()
        val reminderEnabled = binding.switchReminder.isChecked

        val settings = prefsManager.getUserSettings()
        prefsManager.saveUserSettings(settings.copy(
            waterGoal = goal,
            waterReminderInterval = TimeUnit.HOURS.toMillis(duration),
            waterReminderEnabled = reminderEnabled
        ))

        Toast.makeText(requireContext(), "Settings saved!", Toast.LENGTH_SHORT).show()
        findNavController().navigateUp()
    }

    private fun loadSettings() {
        val settings = prefsManager.getUserSettings()
        val goalInLiters = (settings.waterGoal / 1000).coerceIn(1, 10)
        binding.spinnerGoal.setSelection(goalInLiters - 1)
        binding.switchReminder.isChecked = settings.waterReminderEnabled
        val hours = TimeUnit.MILLISECONDS.toHours(settings.waterReminderInterval).toInt().coerceIn(1, 12)
        binding.spinnerDuration.setSelection(hours - 1)
    }

    private fun scheduleWaterReminders() {
        val duration = binding.spinnerDuration.selectedItem.toString().toLong()

        val data = Data.Builder()
            .putString("type", "water")
            .build()
        //repeat hours
        val workRequest = PeriodicWorkRequestBuilder<ReminderWorker>(
            duration, TimeUnit.HOURS
        ).setInputData(data).build()

        //rplace period
        WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork(
            "water_reminder",
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )

        val settings = prefsManager.getUserSettings()
        prefsManager.saveUserSettings(settings.copy(
            waterReminderInterval = TimeUnit.HOURS.toMillis(duration)
        ))
    }

    private fun cancelWaterReminders() {
        WorkManager.getInstance(requireContext()).cancelUniqueWork("water_reminder")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}