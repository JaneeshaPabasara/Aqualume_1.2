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
import com.example.aqualumedb.adapters.MoodLogAdapter
import com.example.aqualumedb.databinding.FragmentMoodHistoryBinding
import com.example.aqualumedb.utils.PreferencesManager
import java.util.*

class MoodHistoryFragment : Fragment() {

    private var _binding: FragmentMoodHistoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var prefsManager: PreferencesManager
    private lateinit var moodLogAdapter: MoodLogAdapter
    private var selectedDate: Long? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMoodHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefsManager = PreferencesManager(requireContext())

        setupRecyclerView()
        setupClickListeners()
        loadMoodLogs()
    }

    private fun setupRecyclerView() {
        moodLogAdapter = MoodLogAdapter(
            onEditClick = { log ->
                val bundle = bundleOf("logId" to log.toString())
                findNavController().navigate(R.id.action_moodHistory_to_addMood, bundle)
            },
            onDeleteClick = { log ->
                AlertDialog.Builder(requireContext())
                    .setTitle("Delete Log")
                    .setMessage("Are you sure you want to delete this mood log?")
                    .setPositiveButton("Delete") { _, _ ->
                        prefsManager.deleteMoodLog(log.id)
                        loadMoodLogs()
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        )

        binding.rvMoodLog.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = moodLogAdapter
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
                loadMoodLogs()
            },
            year,
            month,
            day
        ).show()
    }

    private fun loadMoodLogs() {
        val allLogs = prefsManager.getMoodLogs()

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

        moodLogAdapter.submitList(filteredLogs.sortedByDescending { it.timestamp })
    }
    override fun onResume() {
        super.onResume()
        loadMoodLogs()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}