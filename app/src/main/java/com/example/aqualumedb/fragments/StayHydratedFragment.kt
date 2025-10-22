package com.example.aqualumedb.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.aqualumedb.R
import com.example.aqualumedb.databinding.FragmentStayHydratedBinding
import com.example.aqualumedb.models.WaterLog
import com.example.aqualumedb.utils.PreferencesManager
import java.text.SimpleDateFormat
import java.util.*

class StayHydratedFragment : Fragment() {

    private var _binding: FragmentStayHydratedBinding? = null
    private val binding get() = _binding!!
    private lateinit var prefsManager: PreferencesManager
    private var selectedAmount = 250
    private var editingLogId: Long? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStayHydratedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefsManager = PreferencesManager(requireContext())

        // Check if editing existing log
        arguments?.get("logId")?.let { arg ->
            when (arg) {
                is Long -> {
                    editingLogId = arg
                    loadExistingLog(arg)
                }
                is String -> {
                    arg.toLongOrNull()?.let { id ->
                        editingLogId = id
                        loadExistingLog(id)
                    }
                }
                else-> {
                    Toast.makeText(requireContext(), "Invalid log id", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }
            }
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
    }

    private fun setupClickListeners() {
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.card150ml.setOnClickListener {
            selectedAmount = 150
            updateSelectedWaterType()
        }

        binding.card250ml.setOnClickListener {
            selectedAmount = 250
            updateSelectedWaterType()
        }

        binding.card500ml.setOnClickListener {
            selectedAmount = 500
            updateSelectedWaterType()
        }

        binding.btnAddWater.setOnClickListener {
            addOrUpdateWater()
        }

        binding.etCustomAmount.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                selectedAmount = 0
                updateSelectedWaterType()
            }
        }
    }

    private fun updateSelectedWaterType() {
        binding.card150ml.strokeWidth = if (selectedAmount == 150) 4 else 2
        binding.card250ml.strokeWidth = if (selectedAmount == 250) 4 else 2
        binding.card500ml.strokeWidth = if (selectedAmount == 500) 4 else 2
    }

    private fun addOrUpdateWater() {
        var amount = selectedAmount

        if (binding.etCustomAmount.text.isNotEmpty()) {
            amount = binding.etCustomAmount.text.toString().toIntOrNull() ?: 0
        }

        if (amount <= 0) {
            Toast.makeText(requireContext(), "Please select or enter water amount", Toast.LENGTH_SHORT).show()
            return
        }

        val timestamp = System.currentTimeMillis()

        if (editingLogId != null) {
            val updatedLog = WaterLog(
                id = editingLogId!!,
                amount = amount,
                timestamp = timestamp
            )
            prefsManager.updateWaterLog(updatedLog)
            Toast.makeText(requireContext(), "Water log updated!", Toast.LENGTH_SHORT).show()
        } else {
            val waterLog = WaterLog(
                amount = amount,
                timestamp = timestamp
            )
            prefsManager.saveWaterLog(waterLog)
            Toast.makeText(requireContext(), "Water added successfully!", Toast.LENGTH_SHORT).show()
        }

        findNavController().navigateUp()
    }

    private fun loadExistingLog(logId: Long) {
        val log = prefsManager.getWaterLogs().find { it.id == logId }
        log?.let {
            selectedAmount = it.amount
            binding.etCustomAmount.setText(it.amount.toString())
            binding.btnAddWater.text = "Update Water"
            updateSelectedWaterType()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}