package com.example.aqualumedb.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aqualumedb.R
import com.example.aqualumedb.adapter.WaterLogAdapter
import com.example.aqualumedb.databinding.FragmentHydrationHistoryBinding
import com.example.aqualumedb.utils.PreferencesManager
import java.util.*

class HydrationHistoryFragment : Fragment() {

    private var _binding: FragmentHydrationHistoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var prefsManager: PreferencesManager
    private lateinit var waterLogAdapter: WaterLogAdapter
    private var selectedDate: Long? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHydrationHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefsManager = PreferencesManager(requireContext())

        setupRecyclerView()
        setupClickListeners()
        loadWaterLogs()
    }

    private fun setupRecyclerView() {
        waterLogAdapter = WaterLogAdapter(
            waterLogs = emptyList(),
            onEditClick = { waterLog ->
                // Navigate to edit screen
                val bundle = bundleOf("logId" to waterLog.id.toString())
                findNavController().navigate(
                    R.id.action_hydrationHistory_to_stayHydrated,
                    bundle
                )
            },
            onDeleteClick = { waterLog ->
                // Show delete confirmation
                AlertDialog.Builder(requireContext())
                    .setTitle("Delete Log")
                    .setMessage("Are you sure you want to delete this water log?")
                    .setPositiveButton("Delete") { _, _ ->
                        prefsManager.deleteWaterLog(waterLog.id)
                        loadWaterLogs()
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        )

        binding.rvWaterLogs.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = waterLogAdapter
        }
    }

    private fun setupClickListeners() {
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnFilter.setOnClickListener {
            showDatePicker()
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(selectedYear, selectedMonth, selectedDay, 0, 0, 0)
                selectedDate = selectedCalendar.timeInMillis
                loadWaterLogs()
            },
            year,
            month,
            day
        ).show()
    }

    private fun loadWaterLogs() {
        val allLogs = prefsManager.getWaterLogs()

        val filteredLogs = if (selectedDate != null) {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = selectedDate!!
            val startOfDay = calendar.apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
            }.timeInMillis

            val endOfDay = calendar.apply {
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
            }.timeInMillis

            allLogs.filter { it.timestamp in startOfDay..endOfDay }
        } else {
            allLogs
        }

        waterLogAdapter.updateLogs(filteredLogs.sortedByDescending { it.timestamp })
    }

    override fun onResume() {
        super.onResume()
        loadWaterLogs()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}