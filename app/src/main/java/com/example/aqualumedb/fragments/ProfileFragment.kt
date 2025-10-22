package com.example.aqualumedb.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View

import android.view.ViewGroup


import androidx.fragment.app.Fragment
import com.example.aqualumedb.databinding.FragmentProfileBinding
import com.example.aqualumedb.utils.PreferencesManager

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var prefsManager: PreferencesManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefsManager = PreferencesManager(requireContext())

        loadStats()
    }

    private fun loadStats() {
        val waterLogs = prefsManager.getWaterLogs()
        val meditationLogs = prefsManager.getMeditationLogs()
        val moodLogs = prefsManager.getMoodLogs()

        // Display counts
        binding.tvWaterCount.text = waterLogs.size.toString()
        binding.tvMeditationCount.text = meditationLogs.size.toString()
        binding.tvMoodCount.text = moodLogs.size.toString()

        // Display totals
        val totalWater = waterLogs.sumOf { it.amount }
        binding.tvTotalWater.text = "${totalWater / 1000}L"

        val totalMeditation = meditationLogs.sumOf { it.duration }
        binding.tvTotalMeditation.text = "${totalMeditation}min"
    }

    override fun onResume() {
        super.onResume()
        loadStats()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}