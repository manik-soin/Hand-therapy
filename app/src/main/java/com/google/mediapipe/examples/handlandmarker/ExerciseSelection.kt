package com.google.mediapipe.examples.handlandmarker


import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class ExerciseSelection : AppCompatActivity() {
    private lateinit var editTextSetting: EditText
    private lateinit var buttonNext: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selection)

        // Initialize views
        editTextSetting = findViewById(R.id.editTextSetting)
        buttonNext = findViewById(R.id.buttonNext)

        // Set click listener for Next button
        buttonNext.setOnClickListener {
            val settingValue = editTextSetting.text.toString()

            // Create an intent to start the next activity
            val intent = Intent(this@ExerciseSelection, MainActivity::class.java)
            intent.putExtra("settingValue", settingValue)
            startActivity(intent)
        }
    }
}
