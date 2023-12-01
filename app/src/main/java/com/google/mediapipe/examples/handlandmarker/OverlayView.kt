
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
import com.google.mediapipe.examples.handlandmarker.handexercise.H1
import com.google.mediapipe.examples.handlandmarker.handexercise.H2
import com.google.mediapipe.examples.handlandmarker.handexercise.H3
import com.google.mediapipe.examples.handlandmarker.handexercise.H5
import com.google.mediapipe.examples.handlandmarker.handexercise.H6
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import java.io.Serializable


class OverlayView(context: Context?, attrs: AttributeSet?) :
    View(context, attrs) {

    //Properties
    private var imageBitmap: Bitmap
    private var results: HandLandmarkerResult? = null

    //Paint properties
    private val canvasProperties = CanvasProperties()
    private val rectPaint = canvasProperties.getRectPaint()
    private val linePaint = canvasProperties.getLinePaint()
    private val linePaint2 = canvasProperties.getLinePaint2()
    private val pointPaint = canvasProperties.getPointPaint()
    private val pointPaint2 = canvasProperties.getPointPaint2()
    private val textPaint = canvasProperties.getTextPaint()
    private val textPaint2 = canvasProperties.getTextPaint2()


    //Image Scaling
    private var scaleFactor: Float = 1f
    private var imageWidth: Int = 1
    private var imageHeight: Int = 1

    //getting context value from main activity that was passed by the previous activity(Exercise Selection)
    private val mainActivity = context as? MainActivity
    private val exerciseValue = mainActivity?.getExerciseValue()//exercise value
    private val difficultyValue = mainActivity?.getDifficultyValue()//difficulty value

    private val exerciseID = mainActivity?.getExerciseID()//exercise id
    private val difficultyID = mainActivity?.getDifficultyID()//difficulty id


    //Initialise the calibration image
    init {

        //choose the calibration image
        val imageResId = if(exerciseID==1) {
            R.drawable.palm_down
        }
        else{
            R.drawable.palm_left
        }

        imageBitmap = BitmapFactory.decodeResource(resources, imageResId)
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
    }



    private var timerText=""
    private val timer=object : CountDownTimer(4000, 100) {

        override fun onTick(millisUntilFinished: Long) {
            timerText=("seconds remaining: " + millisUntilFinished / 1000)
        }

        override fun onFinish() {
            timerText=("done!")
            validationComplete=true
        }
    }

    //flags and exercise related
    private var validationFlag=true//calibration

    private var validationComplete=false


    private val h1 = context?.let { H1(it) }
    private val h2 = context?.let { H2(it) }
    private val h3 = context?.let { H3(it) }
    private val h5 = context?.let { H5(it) }
    private val h6 = context?.let { H6(it) }




    override fun draw(canvas: Canvas) {
        super.draw(canvas)


        //deciding the size of the image
        var imagesize = if (canvas.height<canvas.width){
        (canvas.height/1.4).toInt()
        } else{
            (canvas.width/1.4).toInt()
        }

        // Calculate the position to draw the image
        val x1 = (canvas.width - imagesize ) / 2f - 10f
        val y1 = (canvas.height - imagesize  ) / 2f - 10f
        val x2 = (canvas.width + imagesize ) / 2f + 10f
        val y2 = (canvas.height + imagesize  ) / 2f + 10f


        //red rectangle coordinates
        val x3 = x1 + (x2 - x1) * .25
        val y3 = y1 + (y2 - y1) * .25
        val x4 = x2 - (x2 - x1) * .25
        val y4 = y2 - (y2 - y1) * .25

        //getting the extra intent from EXERCISE SELECTION


        // Scale the bitmap
        val scaledBitmap = Bitmap.createScaledBitmap(
            imageBitmap,
            imagesize ,
            imagesize ,
            true
        )

        //VALIDATION INCOMPLETE
        if(!validationComplete){

        // Draw the scaled image on the canvas
            canvas.drawText("CALIBRATION: Place your left hand on the given mark", 100f, 100f, textPaint)


            canvas.drawBitmap(scaledBitmap, x1, y1, rectPaint)
            val rect = Rect(x1.toInt(), y1.toInt(), x2.toInt(), y2.toInt())
            canvas.drawRect(rect, rectPaint)

            val rect2 = Rect(x3.toInt(), y3.toInt(), x4.toInt(), y4.toInt())
            canvas.drawRect(rect2, rectPaint)

        }else{
            //canvas.drawText(" REPS: $reps", canvas.width-400f, 200f, textPaint2)

            if (exerciseValue != null) {
                canvas.drawText(exerciseValue, 100f, 100f, textPaint)
            }
            if (difficultyValue != null) {
                canvas.drawText("LEVEL: $difficultyValue", 100f, 200f, textPaint)
            }
        }

        val v = Calibrate()



        //iterate landmark results
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

                if(validated && !validationComplete){
                    if(validationFlag){
                    timer.start()}
                    validationFlag=false

                    canvas.drawText("VALIDATED", 100f, 300f, textPaint2)
                    canvas.drawText(timerText, 100f, 500f, textPaint)

                }
                else{
                    timer.cancel()
                    validationFlag=true
                }

                if(validationComplete) {

                    //Drawing the angle line
                    val (x1, y1, z1) = points[1]
                    val (x2, y2, z2) = points[0]
                    val (x3, y3, z3) = points[2]
                    canvas.drawLine(x1, y1, x2, y2, linePaint2)
                    canvas.drawLine(x2, y2, x3, y3, linePaint2)



                    //Dashed Line between fingers
                    canvasProperties.drawDashedLine(canvas, x1, y1, x3, y3)




                    if(exerciseID==1){
                        h1?.startExercise(canvas,landmark)
                    }


                    if(exerciseID==2) {
                        h2?.startExercise(canvas, landmark)

                    }

                    if(exerciseID==3) {

                        h3?.startExercise(canvas, landmark)

                    }

                    if(exerciseID==4 || exerciseID==5){


                        h5?.startExercise(canvas, landmark, imageWidth,imageHeight,scaleFactor)


                    }



                    if(exerciseID==6){

                        h6?.startExercise(canvas, landmark, imageWidth,imageHeight,scaleFactor)


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
