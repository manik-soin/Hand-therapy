package com.google.mediapipe.examples.handlandmarker


import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class ExerciseSelection : AppCompatActivity() {

    private lateinit var buttonNext: Button

    private lateinit var radioGroup: RadioGroup
    private lateinit var radioButton: RadioButton
    private lateinit var textView: TextView
    private lateinit var textView2: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selection)
        radioGroup = findViewById(R.id.radioGroupLevel)
        textView = findViewById(R.id.text_view_selected)
        textView2 = findViewById(R.id.text_view_selected2)

        // Initialize views
        buttonNext = findViewById(R.id.buttonNext)



//        buttonApply.setOnClickListener {
//            val radioId = radioGroup.checkedRadioButtonId
//            radioButton = findViewById(radioId)
//            textView.text = "Your choice: " + radioButton.text
//        }

        val radioGroup: RadioGroup = findViewById(R.id.radioGroupExercise)

        for (i in 0 until radioGroup.childCount) {
            val radioButton: RadioButton = radioGroup.getChildAt(i) as RadioButton
            radioButton.setOnClickListener {
                // Perform actions when any RadioButton is clicked
                // For example, you can retrieve the text of the clicked RadioButton:
                val selectedText: String = radioButton.text.toString()
                textView.text = "Selected Exercise: $selectedText"
                // Or you can call a function or perform any other actions you need.
            }
        }


        val radioGroup2: RadioGroup = findViewById(R.id.radioGroupLevel)
        for (i in 0 until radioGroup2.childCount) {
            val radioButton: RadioButton = radioGroup2.getChildAt(i) as RadioButton
            radioButton.setOnClickListener {
                // Perform actions when any RadioButton is clicked
                // For example, you can retrieve the text of the clicked RadioButton:
                val selectedText: String = radioButton.text.toString()
                textView2.text = "Selected Difficulty: $selectedText"
                // Or you can call a function or perform any other actions you need.
            }
        }



        // Set click listener for Next button
        buttonNext.setOnClickListener {
            val exerciseValue = textView.text.toString()
            val difficultyValue = textView2.text.toString()

            // Create an intent to start the next activity
            val intent = Intent(this@ExerciseSelection, MainActivity::class.java)
            intent.putExtra("exercise", exerciseValue)
            intent.putExtra("difficulty", difficultyValue)

            startActivity(intent)
        }
    }
}
