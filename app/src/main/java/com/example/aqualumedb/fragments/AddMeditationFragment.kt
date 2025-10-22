package com.example.aqualumedb.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.aqualumedb.databinding.FragmentAddMeditationBinding
import com.example.aqualumedb.models.MeditationLog
import com.example.aqualumedb.utils.PreferencesManager

class AddMeditationFragment : Fragment() {

    private var _binding: FragmentAddMeditationBinding? = null
    private val binding get() = _binding!!
    private lateinit var prefsManager: PreferencesManager
    private var editingLogId: Long? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddMeditationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefsManager = PreferencesManager(requireContext())

        // Check if editing existing log
        if (arguments?.containsKey("logId") == true) {
            val id = requireArguments().getLong("logId")
            editingLogId = id
            loadExistingLog(id)
        }

        setupUI()
        setupClickListeners()
    }

    private fun setupUI() {
        // Setup time of day spinner
        val timesOfDay = arrayOf(
            "Morning (6AM - 8AM)",
            "Mid Morning (8AM - 12PM)",
            "Afternoon (12PM - 4PM)",
            "Evening (4PM - 8PM)",
            "Night (8PM - 12AM)"
        )
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, timesOfDay)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTimeOfDay.adapter = adapter

        // Setup duration spinner
        val durations = (5..120 step 5).map { it }.toTypedArray()
        val durationAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, durations)
        durationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerDuration.adapter = durationAdapter

        // Setup unit spinner
        val unitAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, arrayOf("min"))
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerUnit.adapter = unitAdapter
    }

    private fun setupClickListeners() {
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnAddTime.setOnClickListener {
            addOrUpdateMeditation()
        }
    }

    private fun addOrUpdateMeditation() {
        val duration = binding.spinnerDuration.selectedItem.toString().toInt()

        if (editingLogId != null) {
            prefsManager.updateMeditationLog(editingLogId!!, duration, true)
            Toast.makeText(requireContext(), "Meditation updated!", Toast.LENGTH_SHORT).show()
        } else {
            val meditationLog = MeditationLog(duration = duration)
            prefsManager.saveMeditationLog(meditationLog)
            Toast.makeText(requireContext(), "Meditation added successfully!", Toast.LENGTH_SHORT).show()
        }

        findNavController().navigateUp()
    }

    private fun loadExistingLog(logId: Long) {
        val log = prefsManager.getMeditationLogs().find { it.id == logId }
        log?.let {
            val index = (it.duration / 5) - 1
            if (index >= 0 && index < binding.spinnerDuration.adapter.count) {
                binding.spinnerDuration.setSelection(index)
            }
            binding.btnAddTime.text = "Update Time"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}