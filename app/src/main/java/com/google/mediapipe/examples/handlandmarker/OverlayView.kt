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
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
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
    private val mainActivity = context as? MainActivity
    private val exerciseValue = mainActivity?.getExerciseValue()//exercise value

    private val difficultyValue = mainActivity?.getDifficultyValue()//difficulty value

    private val exerciseID = mainActivity?.getExerciseID()//exercise id


    val difficultyID = mainActivity?.getDifficultyID()//difficulty id


    init {
        initPaints()




        val imageResId = if(exerciseID==1) {
            R.drawable.palm_down
        }
        else{
            R.drawable.palm_left
            }

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



        textPaint.color = Color.WHITE
        textPaint.textSize = 42f
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textPaint.setShadowLayer(10f, 0f, 0f, Color.BLACK)


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
    private var aflag=true
    private var validation_complete=false
    private var touch_flag=true
    private var reps =0
    private var maxdistance =0f
    private var dvalue =5f
    private var mindistance =9999f
    private var distances1 =mutableMapOf<String, Float>()


    private var maxAngle =0f
    private var avalue =30f
    private var minAngle =9999f

    val stats = mutableListOf(
        mutableListOf<Float>(),
        mutableListOf<Float>(),
    )
    private lateinit var landmark1: MutableList<NormalizedLandmark>
    private var e1_ft=true
    private var e5_ft=true
    private var fingersClosedBegin=false
    private var flexPoint = mutableMapOf<String, Boolean>().apply {
        this["Thumb"] = false
        this["Index"] = false
        this["Middle"] = false
        this["Right"] = false
        this["Pinky"] = false
    }

    private var e5_touch_flag=true







    override fun draw(canvas: Canvas) {
        super.draw(canvas)
//        var sf2 = if (canvas.height<canvas.width){
//            canvas.height/imageBitmap.height
//        } else{
//            canvas.width/imageBitmap.width
//        }
        var imagesize = if (canvas.height<canvas.width){
        (canvas.height/1.4).toInt()
        } else{
            (canvas.width/1.4).toInt()
        }
//        canvas.drawText("h: ${canvas.height}",100f,150f,textPaint)
//        canvas.drawText("w: ${canvas.width}",100f,200f,textPaint)


//        // Calculate the position to draw the image
//        val x1 = (canvas.width - imageBitmap.width *sf2 ) / 2f - 10f
//        val y1 = (canvas.height - imageBitmap.height*sf2  ) / 2f - 10f
//        val x2 = (canvas.width + imageBitmap.width *sf2 ) / 2f + 10f
//        val y2 = (canvas.height + imageBitmap.height*sf2  ) / 2f + 10f

        // Calculate the position to draw the image
        val x1 = (canvas.width - imagesize ) / 2f - 10f
        val y1 = (canvas.height - imagesize  ) / 2f - 10f
        val x2 = (canvas.width + imagesize ) / 2f + 10f
        val y2 = (canvas.height + imagesize  ) / 2f + 10f
//        canvas.drawText("($x1,$y1)",x1,y1,textPaint)


        //red rectangle coordinates
        val x3 = x1 + (x2 - x1) * .25
        val y3 = y1 + (y2 - y1) * .25
        val x4 = x2 - (x2 - x1) * .25
        val y4 = y2 - (y2 - y1) * .25

        //getting the extra intent from EXERCISE SELECTION


//        // Scale the bitmap
//        val scaledBitmap = Bitmap.createScaledBitmap(
//            imageBitmap,
//            imageBitmap.width *sf2 ,
//            imageBitmap.height *sf2 ,
//            true
//        )
        // Scale the bitmap
        val scaledBitmap = Bitmap.createScaledBitmap(
            imageBitmap,
            imagesize ,
            imagesize ,
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






                val pointsToConsider = arrayOf(4,1,8)//currently only to color differently
                val points = mutableListOf<Triple<Float, Float, Float>>()







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

                val validated=if(exerciseID==1){
                    v.validate_1(landmark, y1, y2,imageWidth,imageHeight,scaleFactor)}
                else{
                    v.validate(landmark, x1,y1, x2, y2,imageWidth,imageHeight,scaleFactor)
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


                    if(exerciseID==1){

                        if(e1_ft){
                            landmark1 = landmark
                        }
                        e1_ft=false

                        println(landmark1)
                        var calculatedAngle = c.angle3ds_static(landmark1,landmark, 17, 0, 17)


                        val angleText = "Angle: %.2f".format(calculatedAngle)
                        //c.angle(landmark)

                        canvas.drawText(angleText, x2, y2 - 50, textPaint)

                        if (calculatedAngle < minAngle) {
                            minAngle = calculatedAngle
                        }
                        if (calculatedAngle > maxAngle) {
                            maxAngle = calculatedAngle
                        }

                        touch_flag = if (calculatedAngle > 60f) {//touched?


                            if (touch_flag && aflag) {

                                reps++

                                stats[0].add(minAngle)

                                stats[1].add(maxAngle)
                                aflag=false

                                minAngle = 9999f//reset for new rep
                                maxAngle = 0f
                            }
                            false


                        } else {
                            true
                        }

                        if (calculatedAngle < 20f) {//this is basically a reset for the new turn to make sure we did not just move a little bit
                            aflag=true
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
                            val intent =
                                Intent(context, StatsActivity::class.java)//EXERCISE SELECTION
                            intent.putExtra("listValues", stats as Serializable)
                            intent.putExtra("min", "Minimum Angle")
                            intent.putExtra("max", "Maximum Angle")
                            intent.putExtra("unit", "deg")

                            // Start the activity
                            context.startActivity(intent)
                        }

                    }
                    if(exerciseID==2){

                        if(e1_ft){
                            landmark1 = landmark
                        }
                        e1_ft=false

                        println(landmark1)
                        var calculatedAngle = c.angle3ds_static(landmark1,landmark, 4, 9, 4)


                        val angleText = "Angle: %.2f".format(calculatedAngle)
                        //c.angle(landmark)

                        canvas.drawText(angleText, x2, y2 - 50, textPaint)

                        if (calculatedAngle < minAngle) {
                            minAngle = calculatedAngle
                        }
                        if (calculatedAngle > maxAngle) {
                            maxAngle = calculatedAngle
                        }

                        touch_flag = if (calculatedAngle > 100f) {//touched?


                            if (touch_flag && aflag) {

                                reps++

                                stats[0].add(minAngle)

                                stats[1].add(maxAngle)
                                aflag=false

                                minAngle = 9999f//reset for new rep
                                maxAngle = 0f
                            }
                            false


                        } else {
                            true
                        }

                        if (calculatedAngle < 20f) {//this is basically a reset for the new turn to make sure we did not just move a little bit
                            aflag=true
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
                            val intent =
                                Intent(context, StatsActivity::class.java)//EXERCISE SELECTION
                            intent.putExtra("listValues", stats as Serializable)
                            intent.putExtra("min", "Minimum Angle")
                            intent.putExtra("max", "Maximum Angle")
                            intent.putExtra("unit", "deg")

                            // Start the activity
                            context.startActivity(intent)
                        }

                    }







                    if(exerciseID==3) {

                        var calculatedAngle = c.angle3ds(landmark, 4, 1, 8)


                        val angleText = "Angle: %.2f".format(calculatedAngle)
                        //c.angle(landmark)

                        canvas.drawText(angleText, x2, y2 - 50, textPaint)

                        if (calculatedAngle < minAngle) {
                            minAngle = calculatedAngle
                        }
                        if (calculatedAngle > maxAngle) {
                            maxAngle = calculatedAngle
                        }

                        touch_flag = if (calculatedAngle < 10f) {//touched?


                            if (touch_flag && aflag) {

                                reps++

                                stats[0].add(minAngle)
                                stats[1].add(maxAngle)
                                aflag=false

                                minAngle = 9999f//reset for new rep
                                maxAngle = 0f
                            }
                            false


                        } else {
                            true
                        }

                        if (calculatedAngle > avalue) {//this is basically a reset for the new turn to make sure we did not just move a little bit
                            aflag=true
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
                            intent.putExtra("min","Minimum Angle")
                            intent.putExtra("max","Maximum Angle")
                            intent.putExtra("unit","deg")

                            // Start the activity
                            context.startActivity(intent)

                        }





                    }

                    if(exerciseID==4 || exerciseID==5){




                        var distances =mutableMapOf<String, Float>()
                        distances["Thumb"]=c.distance3ds(landmark,0,4,imageWidth,imageHeight, scaleFactor)
                        distances["Index"]=c.distance3ds(landmark,0,8,imageWidth,imageHeight, scaleFactor)
                        distances["Middle"]=c.distance3ds(landmark,0,12,imageWidth,imageHeight, scaleFactor)
                        distances["Right"]=c.distance3ds(landmark,0,16,imageWidth,imageHeight, scaleFactor)
                        distances["Pinky"]=c.distance3ds(landmark,0,20,imageWidth,imageHeight, scaleFactor)





                        if(e5_ft){
                            distances1=distances
                        }
                        e5_ft=false

                        var fingerClosed =mutableMapOf<String, Boolean>()
                        fingerClosed["Thumb"]=c.distance3ds(landmark,0,4,imageWidth,imageHeight, scaleFactor)< distances1["Thumb"]!! *0.85
                        fingerClosed["Index"]=c.distance3ds(landmark,0,8,imageWidth,imageHeight, scaleFactor)< distances1["Index"]!! *0.85
                        fingerClosed["Middle"]=c.distance3ds(landmark,0,12,imageWidth,imageHeight, scaleFactor)< distances1["Middle"]!!*0.85
                        fingerClosed["Right"]=c.distance3ds(landmark,0,16,imageWidth,imageHeight, scaleFactor)< distances1["Right"]!!*0.85
                        fingerClosed["Pinky"]=c.distance3ds(landmark,0,20,imageWidth,imageHeight, scaleFactor)< distances1["Pinky"]!!*0.85




                        var y = 320f
                        fingerClosed.forEach { (key, value) ->
                            val text = "$key: $value"
                            canvas.drawText(text, 20f, y, textPaint)
                            y += 40f
                        }
                        val allFingersClosed = fingerClosed.values.all { it }
                        val allFingersFlexed = fingerClosed.values.all { !it }

                        if (allFingersClosed) {
                            canvas.drawText("All fingers Closed",900f, 180f, textPaint)
                            fingersClosedBegin=true
                            e5_touch_flag=true

                        }else if(!fingersClosedBegin){
                            canvas.drawText("close all your fingers",400f, 180f, textPaint2)
                        }
                        if(fingersClosedBegin){
                            if(fingerClosed["Thumb"]==true) {
                                canvas.drawText("Flex thumb", 400f, 180f, textPaint2)
                            }
                            else if(fingerClosed["Thumb"]==false
                                &&fingerClosed["Index"]==true
                                &&fingerClosed["Middle"]==true
                                &&fingerClosed["Right"]==true
                                &&fingerClosed["Pinky"]==true
                                ){
                                canvas.drawText("Flex Index", 400f, 180f, textPaint2)
                                flexPoint["Thumb"]=true
                            }
                            else if(fingerClosed["Thumb"]==false
                                &&fingerClosed["Index"]==false
                                &&fingerClosed["Middle"]==true
                                &&fingerClosed["Right"]==true
                                &&fingerClosed["Pinky"]==true
                            ){
                                canvas.drawText("Flex Middle", 400f, 180f, textPaint2)
                                flexPoint["Index"]=true
                            }
                            else if(fingerClosed["Thumb"]==false
                                &&fingerClosed["Index"]==false
                                &&fingerClosed["Middle"]==false
                                &&fingerClosed["Right"]==true
                                &&fingerClosed["Pinky"]==true
                            ){
                                canvas.drawText("Flex Right", 400f, 180f, textPaint2)
                                flexPoint["Middle"]=true
                            }
                            else if(fingerClosed["Thumb"]==false
                                &&fingerClosed["Index"]==false
                                &&fingerClosed["Middle"]==false
                                &&fingerClosed["Right"]==false
                                &&fingerClosed["Pinky"]==true
                            ){
                                canvas.drawText("Flex Pinky", 400f, 180f, textPaint2)
                                flexPoint["Right"]=true
                            }
                            else if(allFingersFlexed){
                                flexPoint["Pinky"]=true

                                if(flexPoint.values.all { it }){
                                canvas.drawText("Complete", 400f, 180f, textPaint2)
                                    if(e5_touch_flag) {
                                        reps++
                                        e5_touch_flag=false

                                    }
                                }

                            }


                        }
                        canvas.drawText("comp val $allFingersFlexed, $flexPoint", 300f, 1000f, textPaint)

                        if (reps == 2) {

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
                            intent.putExtra("min","Minimum Angle")
                            intent.putExtra("max","Maximum Angle")
                            intent.putExtra("unit","deg")

                            // Start the activity
                            context.startActivity(intent)

                        }









                    }



                    if(exerciseID==6){

                        val distanceInCm = (c.distance3ds(
                            landmark,
                            4,
                            8,
                            imageWidth,
                            imageHeight,
                            scaleFactor
                        ) * 2 / c.distance3ds(landmark, 7, 8, imageWidth, imageHeight, scaleFactor)) - 1


                        val distanceText = "Distance: %.2f cm".format(distanceInCm)

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

                            intent.putExtra("min","Minimum Distance")
                            intent.putExtra("max","Maximum Distance")
                            intent.putExtra("unit","cm")

                            // Start the activity
                            context.startActivity(intent)

                        }


                        canvas.drawText(distanceText, (x1 + x3) / 2, (y1 + y3) / 2, textPaint)


                    }


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
