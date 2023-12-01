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

/**
 * This activity displays a list of exercises and difficulty levels, allowing users to choose their desired
 * combination. After making both selections, users can proceed to the main activity where they can perform
 * the selected exercise at the chosen difficulty level.
 */
class ExerciseSelection : AppCompatActivity() {

    /**
     * UI element for the 'Next' button, used to proceed to the main activity after selecting exercise and difficulty.
     */
    private lateinit var buttonNext: Button

    /**
     * UI element for the exercise selection radio group.
     */
    private lateinit var radioGroupExercise: RadioGroup

    /**
     * UI element for the difficulty level selection radio group.
     */
    private lateinit var radioGroupLevel: RadioGroup

    /**
     * UI element for displaying the selected exercise.
     */
    private lateinit var textView: TextView

    /**
     * UI element for displaying the selected difficulty level.
     */
    private lateinit var textView2: TextView

    /**
     * Variable to store the selected exercise ID.
     */
    private var exerciseID = -1

    /**
     * Variable to store the selected difficulty ID.
     */
    private var difficultyID = -1

    /**
     * Initializes the activity, sets up the UI elements and their listeners, and handles user selections.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selection)

        // Initialize UI elements
        radioGroupExercise = findViewById(R.id.radioGroupExercise)
        radioGroupLevel = findViewById(R.id.radioGroupLevel)
        textView = findViewById(R.id.text_view_selected)
        textView2 = findViewById(R.id.text_view_selected2)
        buttonNext = findViewById(R.id.buttonNext)

        // Map exercise radio button indices to exercise IDs
        val exerciseMapping = mapOf(
            0 to 1,
            1 to 2,
            2 to 3,
            3 to 4,
            4 to 5,
            5 to 6,
            6 to 7
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
        buttonNext.setOnClickListener {
            val exerciseValue = textView.text.toString().replace("Selected Exercise: ", "")
            val difficultyValue = textView2.text.toString().replace("Selected Difficulty: ", "")

            // Check if exerciseValue and difficultyValue are not null
            if (exerciseID != -1 && difficultyID != -1) {

                // Create an intent to start the next activity
                val intent = Intent(this@ExerciseSelection, MainActivity::class.java)
                intent.putExtra("exercise", exerciseValue)
                intent.putExtra("difficulty", difficultyValue)
                intent.putExtra("exerciseID", exerciseID)
                intent.putExtra("difficultyID", difficultyID)

                startActivity(intent)
            } else {
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
