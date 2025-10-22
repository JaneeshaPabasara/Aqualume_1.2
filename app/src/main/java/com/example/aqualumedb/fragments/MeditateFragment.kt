package com.example.aqualumedb.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.aqualumedb.R
import com.example.aqualumedb.databinding.FragmentMeditateBinding
import com.example.aqualumedb.utils.PreferencesManager
import com.example.aqualumedb.workers.ReminderWorker
import java.util.concurrent.TimeUnit
import android.widget.Toast

class MeditateFragment : Fragment() {

    private var _binding: FragmentMeditateBinding? = null
    private val binding get() = _binding!!
    private lateinit var prefsManager: PreferencesManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMeditateBinding.inflate(inflater, container, false)
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
        // Setup goal spinner
        val goals = (5..120 step 5).map { it }.toTypedArray()
        val goalAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, goals)
        goalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerGoal.adapter = goalAdapter

        // Setup unit spinner
        val unitAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, arrayOf("min"))
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Setup time spinners
        val hours = (1..12).map { String.format("%02d", it) }.toTypedArray()
        val hourAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, hours)
        hourAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerHour.adapter = hourAdapter

        val amPm = arrayOf("AM", "PM")
        val amPmAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, amPm)
        amPmAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerAmPm.adapter = amPmAdapter
    }

    private fun setupClickListeners() {
        // Back button
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        // Save button
        binding.btnAddMeditate.setOnClickListener {
            saveSettings()
            Toast.makeText(requireContext(), "Settings saved!", Toast.LENGTH_SHORT).show()
        }
        binding.btnUpdateTime.setOnClickListener {
            findNavController().navigate(R.id.action_meditate_to_addMeditation)
        }

        binding.btnViewHistory.setOnClickListener {
            findNavController().navigate(R.id.action_meditate_to_meditationHistory)
        }

        binding.switchReminder.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                scheduleMeditationReminder()
            } else {
                cancelMeditationReminder()
            }

            val settings = prefsManager.getUserSettings()
            prefsManager.saveUserSettings(settings.copy(meditationReminderEnabled = isChecked))
        }

        binding.spinnerGoal.setOnItemSelectedListener(object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
                val goal = (position + 1) * 5
                prefsManager.setMeditationGoal(goal)

                // Update settings with new goal
                val settings = prefsManager.getUserSettings()
                prefsManager.saveUserSettings(settings.copy(meditationGoal = goal))
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        })
    }

    private fun saveSettings() {
        val hour = binding.spinnerHour.selectedItem.toString().toInt()
        val amPm = binding.spinnerAmPm.selectedItem.toString()
        val timeString = "$hour:00 $amPm"

        val goal = binding.spinnerGoal.selectedItem.toString().toInt()
        prefsManager.setMeditationGoal(goal)

        val settings = prefsManager.getUserSettings()
        prefsManager.saveUserSettings(settings.copy(meditationGoal = goal,meditationReminderTime = timeString))

        if (binding.switchReminder.isChecked) {
            scheduleMeditationReminder()
        }
    }

    private fun loadSettings() {
        val settings = prefsManager.getUserSettings()
        val goalIndex = (settings.meditationGoal / 5) - 1
        if (goalIndex >= 0 && goalIndex < binding.spinnerGoal.adapter.count) {
            binding.spinnerGoal.setSelection(goalIndex)
        }
        binding.switchReminder.isChecked = settings.meditationReminderEnabled

        // Parse saved time
        val time = settings.meditationReminderTime.split(" ")
        if (time.size == 2) {
            val hourMin = time[0].split(":")
            if (hourMin.size == 2) {
                val hour = hourMin[0].toIntOrNull() ?: 9
                binding.spinnerHour.setSelection(hour - 1)
                binding.spinnerAmPm.setSelection(if (time[1] == "AM") 0 else 1)
            }
        }
    }

    private fun scheduleMeditationReminder() {
        val hour = binding.spinnerHour.selectedItem.toString().toInt()
        val amPm = binding.spinnerAmPm.selectedItem.toString()
        val timeString = "$hour:00 $amPm"

        val settings = prefsManager.getUserSettings()
        prefsManager.saveUserSettings(settings.copy(meditationReminderTime = timeString))

        val data = Data.Builder()
            .putString("type", "meditation")
            .build()

        val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(1, TimeUnit.HOURS)
            .setInputData(data)
            .build()

        WorkManager.getInstance(requireContext()).enqueueUniqueWork(
            "meditation_reminder",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
        
    }

    private fun cancelMeditationReminder() {
        WorkManager.getInstance(requireContext()).cancelUniqueWork("meditation_reminder")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}