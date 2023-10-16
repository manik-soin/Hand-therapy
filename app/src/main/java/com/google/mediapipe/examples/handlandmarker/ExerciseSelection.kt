package com.google.mediapipe.examples.handlandmarker


import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.net.IDN


class ExerciseSelection : AppCompatActivity() {

    private lateinit var buttonNext: Button
    private lateinit var radioGroupExercise: RadioGroup
    private lateinit var radioGroupLevel: RadioGroup
    private lateinit var textView: TextView
    private lateinit var textView2: TextView
    private var exerciseID=-1
    private var difficultyID=-1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selection)

        radioGroupExercise = findViewById(R.id.radioGroupExercise)
        radioGroupLevel = findViewById(R.id.radioGroupLevel)
        textView = findViewById(R.id.text_view_selected)
        textView2 = findViewById(R.id.text_view_selected2)
        buttonNext = findViewById(R.id.buttonNext)


        val exerciseMapping = mapOf(
            0 to 1,
            1 to 3,
            2 to 6
        )


        // Set the listener for the exercise RadioGroup
        radioGroupExercise.setOnCheckedChangeListener { _, checkedId ->
            val radioButton = findViewById<RadioButton>(checkedId)

            exerciseID = exerciseMapping[radioGroupExercise.indexOfChild(radioButton)]!!

            textView.text = "Selected Exercise: ${radioButton.text}"
        }

        // Set the listener for the level RadioGroup
        radioGroupLevel.setOnCheckedChangeListener { _, checkedId ->
            val radioButton = findViewById<RadioButton>(checkedId)
            difficultyID = radioGroupLevel.indexOfChild(radioButton)
            textView2.text = "Selected Difficulty: ${radioButton.text}"
        }

        // Set click listener for Next button
// Set click listener for Next button
        buttonNext.setOnClickListener {
            val exerciseValue = textView.text.toString().replace("Selected Exercise: ", "")
            val difficultyValue = textView2.text.toString().replace("Selected Difficulty: ", "")

            // Check if exerciseValue and difficultyValue are not null
            if (exerciseID!=-1 && difficultyID!=-1) {

                // Create an intent to start the next activity
                val intent = Intent(this@ExerciseSelection, MainActivity::class.java)
                intent.putExtra("exercise", exerciseValue)
                intent.putExtra("difficulty", difficultyValue)
                intent.putExtra("exerciseID", exerciseID)
                intent.putExtra("difficultyID", difficultyID)

                startActivity(intent)
            } else {
                if(exerciseID==-1 && difficultyID==-1){
                    Toast.makeText(this@ExerciseSelection, "Exercise and difficulty values are missing", Toast.LENGTH_SHORT).show()
                }else {
                    // Display toast for missing values
                    if (exerciseID == -1) {
                        Toast.makeText(
                            this@ExerciseSelection,
                            "Exercise value is missing",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    if (difficultyID == -1) {
                        Toast.makeText(
                            this@ExerciseSelection,
                            "Difficulty value is missing",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}
