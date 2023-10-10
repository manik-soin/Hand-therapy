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


class StatsActivity : AppCompatActivity() {
    private lateinit var buttonNext: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)
        buttonNext = findViewById(R.id.btnExerciseSelection)


        println("STATS")
        val receivedList = intent.getSerializableExtra("listValues") as? MutableList<MutableList<Float>>

        if (receivedList != null) {
            val tableLayout: TableLayout = findViewById(R.id.tableLayout)

            // Create the title row
            val titleRow = TableRow(this)

            // Add an empty cell as the first column header
            titleRow.addView(createBoldTextView(""))

            // Add the column headers
            for (i in receivedList[0].indices) {
                val title = "Rep ${i + 1}"
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
                    0 -> "Min Distance"
                    1 -> "Max Distance"
                    else -> "Rep ${i}"
                }
                dataRow.addView(createBoldTextView(rowTitle))

                // Add the data values
                for (value in rowList) {
                    dataRow.addView(createTextView(value.toString()))
                }

                tableLayout.addView(dataRow)
            }
        }


        buttonNext.setOnClickListener {


            // Create an intent to start the next activity

            val intent = Intent(this@StatsActivity, ExerciseSelection::class.java)


            startActivity(intent)
        }

    }

    private fun createBoldTextView(text: String): TextView {
        val textView = TextView(this)
        textView.text = text
        textView.setPadding(10, 10, 10, 10)
        textView.setTypeface(null, Typeface.BOLD)
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
        return textView
    }

    private fun createTextView(text: String): TextView {
        val textView = TextView(this)
        textView.text = text
        textView.setPadding(10, 10, 10, 10)
        return textView
    }


    override fun onBackPressed() {
        finish()
    }
}

