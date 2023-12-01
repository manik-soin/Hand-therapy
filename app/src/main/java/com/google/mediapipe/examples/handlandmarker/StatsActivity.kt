package com.google.mediapipe.examples.handlandmarker

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.widget.Button
import android.widget.EditText
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.Serializable
import androidx.core.content.ContextCompat

/**
 * This activity displays statistical information for the user's exercise performance.
 * The statistics are shown in a table format and include minimum and maximum values for each exercise.
 */
class StatsActivity : AppCompatActivity() {
    private lateinit var buttonNext: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)
        buttonNext = findViewById(R.id.btnExerciseSelection)

        println("STATS")
        // Retrieve statistical data from the previous activity
        val receivedList = intent.getSerializableExtra("listValues") as? MutableList<MutableList<Float>>
        val minText = intent.getStringExtra("min") ?: "minimum"
        val maxText = intent.getStringExtra("max") ?: "maximum"
        val unitText = intent.getStringExtra("unit") ?: "units"

        if (receivedList != null) {
            val tableLayout: TableLayout = findViewById(R.id.tableLayout)

            // Create the title row
            val titleRow = TableRow(this)

            // Add an empty cell as the first column header
            titleRow.addView(createBoldTextView(""))

            // Add the column headers
            for (i in receivedList[0].indices) {
                val title = "${i + 1}"
                titleRow.addView(createBoldTextView(title))
            }

            // Add the title row to the table
            tableLayout.addView(titleRow)

            // Create the data rows
            for (i in receivedList.indices) {
                val rowList = receivedList[i]
                val dataRow = TableRow(this)

                // Set the row title
                val rowTitle = when (i) {
                    0 -> minText
                    1 -> maxText
                    else -> ""
                }
                dataRow.addView(createBoldTextView(rowTitle))

                // Add the data values
                for (value in rowList) {
                    dataRow.addView(createTextView("%.2f $unitText".format(value)))
                }

                tableLayout.addView(dataRow)
            }
        }

        // Set click listener for the Next button
        buttonNext.setOnClickListener {
            // Create an intent to start the Exercise Selection activity
            val intent = Intent(this@StatsActivity, ExerciseSelection::class.java)
            startActivity(intent)
        }
    }

    /**
     * Creates a TextView with bold text style and returns it.
     * @param text The text to be displayed in the TextView.
     * @return A TextView with bold text style.
     */

    private fun createBoldTextView(text: String): TextView {
        val textView = TextView(this)
        textView.text = text
        textView.setPadding(10, 10, 10, 10)
        textView.setTypeface(null, Typeface.BOLD)
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
        return textView
    }



    /**
     * Creates a TextView with normal text style and returns it.
     * @param text The text to be displayed in the TextView.
     * @return A TextView with normal text style.
     */

    private fun createTextView(text: String): TextView {
        val textView = TextView(this)
        textView.text = text
        textView.setPadding(10, 10, 10, 10)
        return textView
    }

    /**
     * Override the onBackPressed function to finish the activity
     */

    override fun onBackPressed() {
        finish()
    }
}
