/*
 * Copyright 2022 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *             http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.mediapipe.examples.handlandmarker

import android.os.Bundle
import android.os.CountDownTimer
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.mediapipe.examples.handlandmarker.databinding.ActivityMainBinding



class MainActivity : AppCompatActivity() {
    private lateinit var activityMainBinding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private var exerciseValue: String? = null
    private var difficultyValue: String? = null
    private var exerciseID: Int? = null
    private var difficultyID: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        exerciseValue = intent.getStringExtra("exercise")
        exerciseID = intent.getIntExtra("exerciseID",0)
        difficultyValue = intent.getStringExtra("difficulty")
        difficultyID = intent.getIntExtra("difficultyID",0)
    }

    fun getExerciseValue(): String? {
        return exerciseValue
    }
    fun getDifficultyValue(): String? {
        return difficultyValue
    }
    fun getExerciseID(): Int? {
        return exerciseID
    }
    fun getDifficultyID(): Int? {
        return difficultyID
    }


    override fun onBackPressed() {
        finish()
    }
}




