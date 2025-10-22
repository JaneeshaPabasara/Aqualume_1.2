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
import com.example.aqualumedb.adapters.MeditationLogAdapter
import com.example.aqualumedb.databinding.FragmentMeditationHistoryBinding
import com.example.aqualumedb.utils.PreferencesManager
import java.util.*

class MeditationHistoryFragment : Fragment() {

    private var _binding: FragmentMeditationHistoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var prefsManager: PreferencesManager
    private lateinit var meditationLogAdapter: MeditationLogAdapter
    private var selectedDate: Long? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMeditationHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefsManager = PreferencesManager(requireContext())

        setupRecyclerView()
        setupClickListeners()
        loadMeditationLogs()
    }

    private fun setupRecyclerView() {
        meditationLogAdapter = MeditationLogAdapter(
            onEditClick = { log ->
                val bundle = bundleOf("logId" to log.id.toString())
                findNavController().navigate(R.id.action_meditationHistory_to_addMeditation, bundle)
            },

            onDeleteClick = { log ->
                AlertDialog.Builder(requireContext())
                    .setTitle("Delete Log")
                    .setMessage("Are you sure you want to delete this meditation log?")
                    .setPositiveButton("Delete") { _, _ ->
                        prefsManager.deleteMeditationLog(log.id)
                        loadMeditationLogs()
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        )

        binding.rvMeditationLogs.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = meditationLogAdapter
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
                loadMeditationLogs()
            },
            year,
            month,
            day
        ).show()
    }

    private fun loadMeditationLogs() {
        val allLogs = prefsManager.getMeditationLogs()

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

        meditationLogAdapter.submitList(filteredLogs.sortedByDescending { it.timestamp })
    }

    //reload logs when returning from edit
    override fun onResume() {
        super.onResume()
        loadMeditationLogs()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}