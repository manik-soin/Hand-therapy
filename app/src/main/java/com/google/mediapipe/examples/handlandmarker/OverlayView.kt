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






import android.graphics.Path



import android.view.ViewAnimationUtils
import android.view.animation.DecelerateInterpolator
import java.util.Random
import java.util.Timer
import kotlin.concurrent.schedule


class OverlayView(context: Context?, attrs: AttributeSet?) :
    View(context, attrs) {

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
    }




    fun drawDashedLine(canvas: Canvas, startX: Float, startY: Float, stopX: Float, stopY: Float) {
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

        textPaint2.strokeWidth = 15f
        textPaint2.style = Paint.Style.FILL_AND_STROKE



        textPaint.color = Color.BLACK
        textPaint.textSize = 36f
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
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
            canvas.drawCircle(x, y, 20f, randomColor())
        }
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        val c = Compute






        results?.let { handLandmarkerResult ->
            for (landmark in handLandmarkerResult.landmarks()) {
                val pointsToConsider = arrayOf(4,1,8)
                val pointsToConsider2 = arrayOf(0,5)//this is for the longest line reference
                val points = mutableListOf<Triple<Float, Float, Float>>()
                val points2 = mutableListOf<Triple<Float, Float, Float>>()
                println(landmark[0].x())
                for ((a, normalizedLandmark) in landmark.withIndex()) {
                    when (a) {
                        in pointsToConsider -> {
                            val x = normalizedLandmark.x() * imageWidth * scaleFactor
                            val y = normalizedLandmark.y() * imageHeight * scaleFactor
                            val z = normalizedLandmark.z() * scaleFactor
                            canvas.drawPoint(x, y, pointPaint2)
                            points.add(Triple(x, y, z))
                        }
                        in pointsToConsider2 -> {
                            val x = normalizedLandmark.x() * imageWidth * scaleFactor
                            val y = normalizedLandmark.y() * imageHeight * scaleFactor
                            val z = normalizedLandmark.z() * scaleFactor
                            canvas.drawPoint(x, y, pointPaint)
                            points2.add(Triple(x, y, z))
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

                if (points.size >= 1) {
                    val (x1, y1, z1) = points[1]
                    val (x2, y2, z2) = points[0]
                    val (x3, y3, z3) = points[2]
                    canvas.drawLine(x1, y1, x2, y2, linePaint2)
                    canvas.drawLine(x2, y2, x3, y3, linePaint2)
                    drawDashedLine(canvas,x1, y1, x3, y3)

                    val v1 = floatArrayOf(x1 - x2, y1 - y2, z1 - z2)
                    val v2 = floatArrayOf(x3 - x2, y3 - y2, z3 - z2)
                    val dotProduct = v1[0] * v2[0] + v1[1] * v2[1] + v1[2] * v2[2]
                    val magnitudeV1 = Math.sqrt((v1[0] * v1[0] + v1[1] * v1[1] + v1[2] * v1[2]).toDouble())
                    val magnitudeV2 = Math.sqrt((v2[0] * v2[0] + v2[1] * v2[1] + v2[2] * v2[2]).toDouble())
                    val angle = Math.toDegrees(Math.acos(dotProduct / (magnitudeV1 * magnitudeV2)))
                    val angle2 = c.angle3d(x1, y1, z1,x2, y2, z2,x3, y3, z3)


                    val angletext = "Angle: %.2f, %.2f".format(angle,angle2)



                    canvas.drawText(angletext, x2, y2 - 50, textPaint)




                    val (a1, b1, c1) = points2[0]
                    val (a2, b2, c2) = points2[1]

                    val distance = Math.sqrt(((x1 - x3) * (x1 - x3) + (y1 - y3) * (y1 - y3) + (z1 - z3) * (z1 - z3)).toDouble())
                    val distance2 = Math.sqrt(((a1 - a2) * (a1 - a2) + (b1 - b2) * (b1 - b2) + (c1 - c2) * (c1 - c2)).toDouble())

                    val distanceInCm = (distance*11/distance2)-1

                    val distanceInCm2 = (c.distance3d(x1, y1, z1,x3, y3, z3)*11/distance2)-1
                    val text2 = "Distance: %.2f cm,%.2f cm".format(distanceInCm,distanceInCm2)

                    if(distanceInCm<1f){

                        //Timer().schedule(1000) {
                        displayConfetti(canvas)



                        canvas.drawText("W E L L   D O N E !  : )", ((x1 + x3) / 2)+100, ((y1 + y3) / 2)+100, textPaint2)



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
