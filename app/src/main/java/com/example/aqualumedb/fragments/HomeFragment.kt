package com.example.aqualumedb.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.aqualumedb.R
import com.example.aqualumedb.databinding.FragmentHomeBinding
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding?= null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setupUI()
        setupClickListeners()
    }

    private fun setupUI() {
        // Set date
        val dateFormat = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())
        binding.tvDate.text = dateFormat.format(Date())

    }

    private fun setupClickListeners() {
        binding.btnUpdateWater.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_stayHydrated)
        }

        binding.btnUpdateMeditation.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_addMeditation)
        }

        binding.btnUpdateJournal.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_addMood)
        }

        binding.ivNotification.setOnClickListener {
            // Handle notification settings
        }

        binding.ivSettings.setOnClickListener {
            // Handle app settings
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}