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

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarker
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult
import kotlin.math.max
import kotlin.math.min
import kotlin.Triple
import android.graphics.DashPathEffect

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.os.CountDownTimer
import java.io.Serializable


class OverlayView(context: Context?, attrs: AttributeSet?) :
    View(context, attrs) {
    private val rectPaint = Paint()
    private var imageBitmap: Bitmap
    private var results: HandLandmarkerResult? = null
    private var linePaint = Paint()
    private var linePaint2 = Paint()
    private var pointPaint = Paint()
    private var pointPaint2 = Paint()
    private var textPaint = Paint()
    private var textPaint2 = Paint()

    private var scaleFactor: Float = 1f
    private var imageWidth: Int = 1
    private var imageHeight: Int = 1



    init {
        initPaints()
        val imageResId = R.drawable.palm_left
        imageBitmap = BitmapFactory.decodeResource(resources, imageResId)
    }




    private fun drawDashedLine(canvas: Canvas, startX: Float, startY: Float, stopX: Float, stopY: Float) {
        val paint = Paint()
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 5f
        paint.pathEffect = DashPathEffect(floatArrayOf(10f, 10f), 0f)

        canvas.drawLine(startX, startY, stopX, stopY, paint)
    }

    fun clear() {
        results = null
        linePaint.reset()
        linePaint2.reset()
        pointPaint.reset()
        pointPaint2.reset()
        textPaint.reset()
        textPaint2.reset()
        invalidate()
        initPaints()
    }

    private fun initPaints() {
        linePaint.color =
            ContextCompat.getColor(context!!, R.color.mp_color_primary)
        linePaint.strokeWidth = LANDMARK_STROKE_WIDTH
        linePaint.style = Paint.Style.STROKE



        linePaint2.color =
            Color.BLACK
        linePaint2.strokeWidth = LANDMARK_STROKE_WIDTH
        linePaint2.style = Paint.Style.STROKE

        pointPaint.color = Color.YELLOW
        pointPaint.strokeWidth = LANDMARK_STROKE_WIDTH
        pointPaint.style = Paint.Style.FILL


        pointPaint2.color = Color.RED
        pointPaint2.strokeWidth = 32F
        pointPaint2.style = Paint.Style.FILL



        textPaint2.color = Color.YELLOW
        textPaint2.textSize = 72f

        textPaint2.strokeWidth = 10f
        textPaint2.style = Paint.Style.FILL_AND_STROKE
        textPaint2.setShadowLayer(20f, 0f, 0f, Color.BLACK)



        textPaint.color = Color.BLACK
        textPaint.textSize = 42f
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textPaint.setShadowLayer(10f, 0f, 0f, Color.WHITE)


        rectPaint.color = Color.RED
        rectPaint.style = Paint.Style.FILL
        rectPaint.style = Paint.Style.STROKE
        rectPaint.strokeWidth = 12f




    }


    private fun displayConfetti(canvas: Canvas) {
        fun randomColor() : Paint {
            val paint = Paint()
            val random = (0..255).random()
            val red = (0..255).random()
            val green = (0..255).random()
            val blue = (0..255).random()
            paint.color = Color.argb(255, red, green, blue)
            paint.strokeWidth = 0.5f
            paint.style = Paint.Style.FILL
            return paint
        }


        for (i in 0..10) {
            val x = (0..canvas.width).random().toFloat()
            val y = (0..canvas.height).random().toFloat()
            canvas.drawCircle(x, y, 15f, randomColor())
        }
    }
    private var timertext=""
    private val timer=object : CountDownTimer(4000, 100) {

        override fun onTick(millisUntilFinished: Long) {
            timertext=("seconds remaining: " + millisUntilFinished / 1000)
        }

        override fun onFinish() {
            timertext=("done!")
            validation_complete=true
        }
    }
    private var ft=true
    private var dflag=true
    private var validation_complete=false
    private var touch_flag=true
    private var reps =0
    private var maxdistance =0f
    private var dvalue =5f
    private var mindistance =9999f
    val stats = mutableListOf(
        mutableListOf<Float>(),
        mutableListOf<Float>(),
    )



    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        var sf2 = if (canvas.height<canvas.width){
            canvas.height/imageBitmap.height
        } else{
            canvas.width/imageBitmap.width
        }


        // Calculate the position to draw the image
        val x1 = (canvas.width - imageBitmap.width *sf2 ) / 2f - 10f
        val y1 = (canvas.height - imageBitmap.height*sf2  ) / 2f - 10f
        val x2 = (canvas.width + imageBitmap.width *sf2 ) / 2f + 10f
        val y2 = (canvas.height + imageBitmap.height*sf2  ) / 2f + 10f

        //red rectangle coordinates
        val x3 = x1 + (x2 - x1) * .25
        val y3 = y1 + (y2 - y1) * .25
        val x4 = x2 - (x2 - x1) * .25
        val y4 = y2 - (y2 - y1) * .25

        //getting the extra intent from EXERCISE SELECTION
        val mainActivity = context as? MainActivity
        val exerciseValue = mainActivity?.getExerciseValue()//exercise value

        val difficultyValue = mainActivity?.getDifficultyValue()//difficulty value

//        val exerciseID = mainActivity?.getExerciseID()//exercise value
//        if (exerciseID != null) {
//            canvas.drawText(exerciseID.toString(), 400f, 500f, textPaint)
//        }
//
//        val difficultyID = mainActivity?.getDifficultyID()//exercise value
//        if (exerciseID != null) {
//            canvas.drawText(difficultyID.toString(), 500f, 500f, textPaint)
//        }






        // Scale the bitmap
        val scaledBitmap = Bitmap.createScaledBitmap(
            imageBitmap,
            imageBitmap.width *sf2 ,
            imageBitmap.height *sf2 ,
            true
        )

        //VALIDATION INCOMPLETE
        if(!validation_complete){

        // Draw the scaled image on the canvas
            canvas.drawText("CALIBRATION: Place your left hand on the given mark", 100f, 100f, textPaint)


            canvas.drawBitmap(scaledBitmap, x1, y1, rectPaint)
            val rect = Rect(x1.toInt(), y1.toInt(), x2.toInt(), y2.toInt())
            canvas.drawRect(rect, rectPaint)

            val rect2 = Rect(x3.toInt(), y3.toInt(), x4.toInt(), y4.toInt())
            canvas.drawRect(rect2, rectPaint)

        }else{
            canvas.drawText(" REPS: $reps", canvas.width-400f, 200f, textPaint2)

            if (exerciseValue != null) {
                canvas.drawText(exerciseValue, 100f, 100f, textPaint)
            }
            if (difficultyValue != null) {
                canvas.drawText("LEVEL: $difficultyValue", 100f, 200f, textPaint)
            }
        }


        val c = Compute()
        val v = Calibrate()





        results?.let { handLandmarkerResult ->
            for (landmark in handLandmarkerResult.landmarks()) {
                //val d=DataInput(landmark)
                val validated=v.validate(landmark, x1,y1, x2, y2,imageWidth,imageHeight,scaleFactor)

                val pointsToConsider = arrayOf(4,1,8)
                //val pointsToConsider2 = arrayOf(0,5)//this is for the longest line reference
                val points = mutableListOf<Triple<Float, Float, Float>>()
                val points2 = mutableListOf<Triple<Float, Float, Float>>()


                val ang=c.angle3ds(landmark,4,1,8)

                for ((a, normalizedLandmark) in landmark.withIndex()) {//consider all points in landmark
                    when (a) {//a goes from 0 to 21
                        in pointsToConsider -> {
                            val x = normalizedLandmark.x() * imageWidth * scaleFactor
                            val y = normalizedLandmark.y() * imageHeight * scaleFactor
                            val z = normalizedLandmark.z() * scaleFactor
                            canvas.drawPoint(x, y, pointPaint2)
                            points.add(Triple(x, y, z))

                        }
                        else -> {
                            canvas.drawPoint(
                                normalizedLandmark.x() * imageWidth * scaleFactor,
                                normalizedLandmark.y() * imageHeight * scaleFactor,
                                pointPaint
                            )
                        }
                    }
                }


                if(validated && !validation_complete){


                    if(ft){
                    timer.start()}
                    ft=false


                    canvas.drawText("VALIDATED", 100f, 300f, textPaint2)
                    canvas.drawText(timertext, 100f, 500f, textPaint)


                }
                else{
                    timer.cancel()
                    ft=true
                }

                if(validation_complete) {

                    val (x1, y1, z1) = points[1]
                    val (x2, y2, z2) = points[0]
                    val (x3, y3, z3) = points[2]
                    canvas.drawLine(x1, y1, x2, y2, linePaint2)
                    canvas.drawLine(x2, y2, x3, y3, linePaint2)
                    drawDashedLine(canvas, x1, y1, x3, y3)


                    val angletext = "Angle: %.2f".format(ang)
                    //c.angle(landmark)

                    canvas.drawText(angletext, x2, y2 - 50, textPaint)


                    val distanceInCm = (c.distance3ds(
                        landmark,
                        4,
                        8,
                        imageWidth,
                        imageHeight,
                        scaleFactor
                    ) * 2 / c.distance3ds(landmark, 7, 8, imageWidth, imageHeight, scaleFactor)) - 1


                    val text2 = "Distance: %.2f cm".format(distanceInCm)

                    //for stats
                    if (distanceInCm < mindistance) {
                        mindistance = distanceInCm
                    }
                    if (distanceInCm > maxdistance) {
                        maxdistance = distanceInCm
                    }


                    touch_flag = if (distanceInCm < 1f) {//touched?


                        if (touch_flag && dflag) {

                            reps++

                            stats[0].add(mindistance)

                            stats[1].add(maxdistance)
                            dflag=false

                            mindistance = 9999f//reset for new rep
                            maxdistance = 0f
                        }
                        false


                    } else {
                        true
                    }

                    if (distanceInCm > dvalue) {
                        dflag=true
                    }



                    if (reps == 5) {

                        displayConfetti(canvas)
                        canvas.drawText(
                            "W E L L   D O N E !  : )",
                            ((x1 + x3) / 2) + 100,
                            ((y1 + y3) / 2) + 100,
                            textPaint2
                        )

                        val context = context

                        // Create an Intent for the target activity
                        val intent = Intent(context, StatsActivity::class.java)//EXERCISE SELECTION
                        intent.putExtra("listValues", stats as Serializable)

                        // Start the activity
                        context.startActivity(intent)

                    }


                    canvas.drawText(text2, (x1 + x3) / 2, (y1 + y3) / 2, textPaint)
                }


                HandLandmarker.HAND_CONNECTIONS.forEach {
                    canvas.drawLine(
                        handLandmarkerResult.landmarks().get(0).get(it!!.start())
                            .x() * imageWidth * scaleFactor,
                        handLandmarkerResult.landmarks().get(0).get(it.start())
                            .y() * imageHeight * scaleFactor,
                        handLandmarkerResult.landmarks().get(0).get(it.end())
                            .x() * imageWidth * scaleFactor,
                        handLandmarkerResult.landmarks().get(0).get(it.end())
                            .y() * imageHeight * scaleFactor,
                        linePaint
                    )
                }
            }





        }
    }



    fun setResults(
        handLandmarkerResults: HandLandmarkerResult,
        imageHeight: Int,
        imageWidth: Int,
        runningMode: RunningMode = RunningMode.IMAGE
    ) {
        results = handLandmarkerResults

        this.imageHeight = imageHeight
        this.imageWidth = imageWidth

        scaleFactor = when (runningMode) {
            RunningMode.IMAGE,
            RunningMode.VIDEO -> {
                min(width * 1f / imageWidth, height * 1f / imageHeight)
            }
            RunningMode.LIVE_STREAM -> {
                // PreviewView is in FILL_START mode. So we need to scale up the
                // landmarks to match with the size that the captured images will be
                // displayed.
                max(width * 1f / imageWidth, height * 1f / imageHeight)
            }
        }
        invalidate()
    }

    companion object {
        private const val LANDMARK_STROKE_WIDTH = 8F
    }
}
