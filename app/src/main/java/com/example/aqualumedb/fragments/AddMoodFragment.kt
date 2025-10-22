// fragments/AddMoodFragment.kt
package com.example.aqualumedb.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.aqualumedb.R
import com.example.aqualumedb.models.MoodLog
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.widget.ImageView
import com.example.aqualumedb.utils.PreferencesManager

class AddMoodFragment : Fragment() {

    private lateinit var btnSaveMood: Button
    private lateinit var etNote: EditText
    private lateinit var ivBack: ImageView
    private lateinit var prefsManager: PreferencesManager

    private var mood: String = ""
    private var emoji: String = ""
    private var moodDrawable: Int = 0
    private var note: String = ""
    private var timestamp: Long = 0L
    private var timeString: String = ""
    private var logId: Long? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_mood, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefsManager = PreferencesManager(requireContext())

        // Initialize views
        btnSaveMood = view.findViewById(R.id.btn_add_mood)
        etNote = view.findViewById(R.id.et_thoughts)
        ivBack = view.findViewById(R.id.iv_back)

        setupBackButton()
        setupMoodClicks(view)
        setupSaveButton()

    }

    private fun setupBackButton() {
        ivBack.setOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }
    }

    private fun setupMoodClicks(root: View) {
        fun select(name: String, drawableRes: Int, emojiStr: String) {
            mood = name
            moodDrawable = drawableRes
            emoji = emojiStr
            Toast.makeText(requireContext(), "$name selected", Toast.LENGTH_SHORT).show()
        }

        root.findViewById<View>(R.id.card_happy).setOnClickListener {
            select("Happy", R.drawable.hapimg, "😊")
        }
        root.findViewById<View>(R.id.card_sad).setOnClickListener {
            select("Sad", R.drawable.sadimg, "😢")
        }
        root.findViewById<View>(R.id.card_angry).setOnClickListener {
            select("Angry", R.drawable.angimg, "😠")
        }
        root.findViewById<View>(R.id.card_calm).setOnClickListener {
            select("Calm", R.drawable.calmimg, "😌")
        }
        root.findViewById<View>(R.id.card_neutral).setOnClickListener {
            select("Neutral", R.drawable.neimg, "😐")
        }
        root.findViewById<View>(R.id.card_energetic).setOnClickListener {
            select("Energetic", R.drawable.eneimg, "🤗")
        }
        root.findViewById<View>(R.id.card_tired).setOnClickListener {
            select("Tired", R.drawable.tiredimg, "😴")
        }
        root.findViewById<View>(R.id.card_stressed).setOnClickListener {
            select("Stressed", R.drawable.scaimg, "😰")
        }
    }

    private fun setupSaveButton() {
        btnSaveMood.setOnClickListener {
            note = etNote.text.toString().trim()

            if (mood.isEmpty() || moodDrawable == 0) {
                Toast.makeText(requireContext(), "Please select a mood", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            timestamp = System.currentTimeMillis()
            timeString = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(timestamp))

            if (logId != null) {
                prefsManager.updateMoodLog(logId = logId!!, moodName = mood, moodEmoji = moodDrawable)
                Toast.makeText(requireContext(), "Mood updated: $mood", Toast.LENGTH_SHORT).show()
            } else {
                val moodLog = MoodLog(
                    mood = mood,
                    moodDrawable = moodDrawable,
                    timeString = timeString,
                    note = note,
                    emoji = emoji
                )

                prefsManager.saveMoodLog(moodLog)
                Toast.makeText(requireContext(), "Mood saved successfully!", Toast.LENGTH_SHORT).show()
            }

        }
    }
}