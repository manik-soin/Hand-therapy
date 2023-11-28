
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

    //flags and exercise related
    private var validation_flag=true//calibration
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
    private var e5_ft2=true
    private var fingersClosedBegin=false
    private var flexPoint = mutableMapOf<String, Boolean>().apply {
        this["Thumb"] = false
        this["Index"] = false
        this["Middle"] = false
        this["Right"] = false
        this["Pinky"] = false
    }

    private var e5_touch_flag=true

    private var lmk_ft=true
    private lateinit var landmark_stop: MutableList<NormalizedLandmark>
    private val h = context?.let { H1(it) }







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
        if(!validation_complete){

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


        val c = Compute()
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

                if(validated && !validation_complete){
                    if(validation_flag){
                    timer.start()}
                    validation_flag=false

                    canvas.drawText("VALIDATED", 100f, 300f, textPaint2)
                    canvas.drawText(timertext, 100f, 500f, textPaint)

                }
                else{
                    timer.cancel()
                    validation_flag=true
                }

                if(validation_complete) {

                    //Drawing the angle line
                    val (x1, y1, z1) = points[1]
                    val (x2, y2, z2) = points[0]
                    val (x3, y3, z3) = points[2]
                    canvas.drawLine(x1, y1, x2, y2, linePaint2)
                    canvas.drawLine(x2, y2, x3, y3, linePaint2)



                    //Dashed Line between fingers
                    canvasProperties.drawDashedLine(canvas, x1, y1, x3, y3)




                    if(exerciseID==1){

                        h?.startExercise(canvas,landmark)


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

                            canvasProperties.displayConfetti(canvas)
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

                            canvasProperties.displayConfetti(canvas)
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

                        var ref=  2 / c.distance3ds(landmark, 7, 8, imageWidth, imageHeight, scaleFactor)


                        var distances =mutableMapOf<String, Float>()
                        distances["Thumb"]=c.distance3ds(landmark,0,4,imageWidth,imageHeight, scaleFactor)
                        distances["Index"]=c.distance3ds(landmark,0,8,imageWidth,imageHeight, scaleFactor)
                        distances["Middle"]=c.distance3ds(landmark,0,12,imageWidth,imageHeight, scaleFactor)
                        distances["Right"]=c.distance3ds(landmark,0,16,imageWidth,imageHeight, scaleFactor)
                        distances["Pinky"]=c.distance3ds(landmark,0,20,imageWidth,imageHeight, scaleFactor)





                        if(e5_ft){
                            distances1=distances

                            for (finger in distances1.keys) {
                                distances1[finger]?.times(ref)?.let { stats[1].add(it) }

                            }

                        }
                        e5_ft=false

                        var fingerClosed =mutableMapOf<String, Boolean>()
                        fingerClosed["Thumb"]=c.distance3ds(landmark,0,4,imageWidth,imageHeight, scaleFactor)< distances1["Thumb"]!! *0.9
                        fingerClosed["Index"]=c.distance3ds(landmark,0,8,imageWidth,imageHeight, scaleFactor)< distances1["Index"]!! *0.9
                        fingerClosed["Middle"]=c.distance3ds(landmark,0,12,imageWidth,imageHeight, scaleFactor)< distances1["Middle"]!!*0.9
                        fingerClosed["Right"]=c.distance3ds(landmark,0,16,imageWidth,imageHeight, scaleFactor)< distances1["Right"]!!*0.9
                        fingerClosed["Pinky"]=c.distance3ds(landmark,0,20,imageWidth,imageHeight, scaleFactor)< distances1["Pinky"]!!*0.9
                        //canvas.drawText("${c.distance3ds(landmark,0,20,imageWidth,imageHeight, scaleFactor)},${distances1["Pinky"]!!*0.8}",1000f,1000f,textPaint2)




                        var y = 320f
                        fingerClosed.forEach { (key, value) ->
                            val text = "$key: $value"
                            canvas.drawText(text, 20f, y, textPaint)
                            y += 40f
                        }
                        val allFingersClosed = fingerClosed.values.all { it }
                        val allFingersFlexed = fingerClosed.values.all { !it }

                        if (allFingersClosed) {
                            if(e5_ft2){
                            for (finger in distances.keys) {
                                distances[finger]?.times(ref)?.let { stats[0].add(it) }

                            }}
                            e5_ft2=false
                            canvas.drawText("All fingers Closed",900f, 180f, textPaint)
                            fingersClosedBegin=true
                            e5_touch_flag=true



                        }else if(!fingersClosedBegin){
                            canvas.drawText("close all your fingers",400f, 180f, textPaint2)
                        }
                        if(fingersClosedBegin){

                            if(allFingersClosed) {

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

                                    if(e5_touch_flag) {
                                        reps++
                                        e5_touch_flag=false
                                        fingersClosedBegin=false

                                        flexPoint = mutableMapOf<String, Boolean>().apply {
                                            this["Thumb"] = false
                                            this["Index"] = false
                                            this["Middle"] = false
                                            this["Right"] = false
                                            this["Pinky"] = false
                                        }


                                    }
                                }

                            }


                        }
                        canvas.drawText("comp val $allFingersFlexed, $flexPoint", 300f, 1000f, textPaint)

                        if (reps == 2) {
                            fingersClosedBegin=true

                            canvasProperties.displayConfetti(canvas)
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
                            intent.putExtra("min","Fingers Closed Distance")
                            intent.putExtra("max","Fingers Flexed Distance")
                            intent.putExtra("unit","cm")

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

                            canvasProperties.displayConfetti(canvas)
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
