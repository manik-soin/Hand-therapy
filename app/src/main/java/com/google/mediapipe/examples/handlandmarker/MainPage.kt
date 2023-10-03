package com.google.mediapipe.examples.handlandmarker

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.mediapipe.examples.handlandmarker.presentation.handtherapy.HandTherapyActivity

class MainPage : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)

        var btn: Button = findViewById(R.id.button)
        btn.setOnClickListener {
            val intent = Intent(this, HandTherapyActivity::class.java)
            startActivity(intent)
        }
    }
}