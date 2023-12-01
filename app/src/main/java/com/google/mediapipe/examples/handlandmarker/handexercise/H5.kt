package com.google.mediapipe.examples.handlandmarker.handexercise

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import com.google.mediapipe.examples.handlandmarker.utils.Compute
import com.google.mediapipe.examples.handlandmarker.StatsActivity
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import java.io.Serializable
/**
 * This class represents the Active Finger Flexion & (isolated) Finger Extension exercise, which involves
 * first closing all fingers and then flexing them one by one. The exercise can be started by calling the
 * startExercise() method, which takes a Canvas and a list of NormalizedLandmarks as parameters. The current
 * number of repetitions is displayed on the canvas using textPaint2. The distances between specific landmarks
 * are calculated using an instance of the Compute class to determine if the fingers are closed or flexed.
 * The exercise proceeds through a series of steps, directing the user to flex each finger one by one.
 * When the number of reps reaches 2, confetti is displayed on the canvas and an Intent is created to start the
 * StatsActivity, passing in the relevant statistical data.
 *
 * @param context The context in which this class is being used.
 */
class H5(private val context: Context) : HandExercise(context) {


    /**
     * A flag indicating if this is the first time all fingers are closed during the exercise.
     */
    private var isFirstTimeClosed = true

    /**
     * A flag indicating if the fingers have been closed at the beginning of the exercise.
     */
    private var fingersClosedBegin = false

    /**
     * A map containing the flex state of each finger. A true value indicates that the finger is flexed,
     * and a false value indicates that the finger is not flexed.
     */
    private var flexPoint = mutableMapOf<String, Boolean>().apply {
        this["Thumb"] = false
        this["Index"] = false
        this["Middle"] = false
        this["Right"] = false
        this["Pinky"] = false
    }



    /**
     * This method starts the Active Finger Flexion & (isolated) Finger Extension exercise by drawing the current
     * number of reps on the canvas, and then calculating the distances between specific landmarks using the
     * distance3ds() method of an instance of the Compute class. The exercise proceeds through a series of steps,
     * directing the user to flex each finger one by one. When the number of reps reaches 2, confetti is displayed
     * on the canvas and an Intent is created to start the StatsActivity, passing in the relevant statistical data.
     *
     * @param canvas The canvas on which to draw the current state of the exercise.
     * @param landmark A list of NormalizedLandmarks representing the current state of the hand.
     * @param w The width of the canvas.
     * @param h The height of the canvas.
     * @param scaleFactor The scale factor to apply to the distances.
     */
    fun startExercise(canvas: Canvas, landmark: MutableList<NormalizedLandmark>,w: Int, h: Int, scaleFactor: Float) {
        val c = Compute()
        canvas.drawText(" REPS: $reps", canvas.width-400f, 200f, textPaint2)


        var ref=  2 / c.distance3ds(landmark, 7, 8, w, h, scaleFactor)


        var distances =mutableMapOf<String, Float>()
        distances["Thumb"]=c.distance3ds(landmark,0,4,w,h, scaleFactor)
        distances["Index"]=c.distance3ds(landmark,0,8,w,h, scaleFactor)
        distances["Middle"]=c.distance3ds(landmark,0,12,w,h, scaleFactor)
        distances["Right"]=c.distance3ds(landmark,0,16,w,h, scaleFactor)
        distances["Pinky"]=c.distance3ds(landmark,0,20,w,h, scaleFactor)





        if(isFirstValue){
            distances1=distances

            for (finger in distances1.keys) {
                distances1[finger]?.times(ref)?.let { stats[1].add(it) }

            }

        }
        isFirstValue=false

        var fingerClosed =mutableMapOf<String, Boolean>()
        fingerClosed["Thumb"]=c.distance3ds(landmark,0,4,w,h, scaleFactor)< distances1["Thumb"]!! *0.9
        fingerClosed["Index"]=c.distance3ds(landmark,0,8,w,h, scaleFactor)< distances1["Index"]!! *0.9
        fingerClosed["Middle"]=c.distance3ds(landmark,0,12,w,h, scaleFactor)< distances1["Middle"]!!*0.9
        fingerClosed["Right"]=c.distance3ds(landmark,0,16,w,h, scaleFactor)< distances1["Right"]!!*0.9
        fingerClosed["Pinky"]=c.distance3ds(landmark,0,20,w,h, scaleFactor)< distances1["Pinky"]!!*0.9



        var y = 320f
        fingerClosed.forEach { (key, value) ->
            val text = "$key: $value"
            canvas.drawText(text, 20f, y, textPaint)
            y += 40f
        }
        val allFingersClosed = fingerClosed.values.all { it }
        val allFingersFlexed = fingerClosed.values.all { !it }

        if (allFingersClosed) {
            if(isFirstTimeClosed){
                for (finger in distances.keys) {
                    distances[finger]?.times(ref)?.let { stats[0].add(it) }

                }}
            isFirstTimeClosed=false
            canvas.drawText("All fingers Closed",900f, 180f, textPaint)
            fingersClosedBegin=true
            startNewRep=true



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

                    if(startNewRep) {
                        reps++
                        startNewRep=false
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
                500f,
                500f,
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
}
