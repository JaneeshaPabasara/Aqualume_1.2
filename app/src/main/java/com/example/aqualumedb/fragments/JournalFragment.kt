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
import com.example.aqualumedb.databinding.FragmentJournalBinding
import com.example.aqualumedb.utils.PreferencesManager
import com.example.aqualumedb.workers.ReminderWorker
import java.util.concurrent.TimeUnit
import android.widget.Toast

class JournalFragment : Fragment() {

    private var _binding: FragmentJournalBinding? = null
    private val binding get() = _binding!!
    private lateinit var prefsManager: PreferencesManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentJournalBinding.inflate(inflater, container, false)
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
        binding.btnAddJournal.setOnClickListener {
            saveSettings()
            Toast.makeText(requireContext(), "Settings saved!", Toast.LENGTH_SHORT).show()
        }
        binding.btnUpdateJournal.setOnClickListener {
            findNavController().navigate(R.id.action_journal_to_addMood)
        }

        binding.btnViewHistory.setOnClickListener {
            findNavController().navigate(R.id.action_journal_to_moodHistory)
        }

        binding.switchReminder.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                scheduleJournalReminder()
            } else {
                cancelJournalReminder()
            }

            val settings = prefsManager.getUserSettings()
            prefsManager.saveUserSettings(settings.copy(journalReminderEnabled = isChecked))
        }
    }

    private fun saveSettings() {
        val hour = binding.spinnerHour.selectedItem.toString().toInt()
        val amPm = binding.spinnerAmPm.selectedItem.toString()
        val timeString = "$hour:00 $amPm"

        val settings = prefsManager.getUserSettings()
        prefsManager.saveUserSettings(settings.copy(journalReminderTime = timeString))

        if (binding.switchReminder.isChecked) {
            scheduleJournalReminder()
        }
    }

    private fun loadSettings() {
        val settings = prefsManager.getUserSettings()
        binding.switchReminder.isChecked = settings.journalReminderEnabled

        // Parse saved time
        val time = settings.journalReminderTime.split(" ")
        if (time.size == 2) {
            val hourMin = time[0].split(":")
            if (hourMin.size == 2) {
                val hour = hourMin[0].toIntOrNull() ?: 9
                binding.spinnerHour.setSelection(hour - 1)
                binding.spinnerAmPm.setSelection(if (time[1] == "AM") 0 else 1)
            }
        }
    }

    private fun scheduleJournalReminder() {
        val hour = binding.spinnerHour.selectedItem.toString().toInt()
        val amPm = binding.spinnerAmPm.selectedItem.toString()
        val timeString = "$hour:00 $amPm"

        val settings = prefsManager.getUserSettings()
        prefsManager.saveUserSettings(settings.copy(journalReminderTime = timeString))

        // Calculate delay until reminder time
        val data = Data.Builder()
            .putString("type", "journal")
            .build()

        val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(1, TimeUnit.HOURS) // Simplified - should calculate actual time
            .setInputData(data)
            .build()

        WorkManager.getInstance(requireContext()).enqueueUniqueWork(
            "journal_reminder",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }

    private fun cancelJournalReminder() {
        WorkManager.getInstance(requireContext()).cancelUniqueWork("journal_reminder")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}