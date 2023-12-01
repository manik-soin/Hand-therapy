package com.google.mediapipe.examples.handlandmarker.handexercise

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import com.google.mediapipe.examples.handlandmarker.Compute
import com.google.mediapipe.examples.handlandmarker.StatsActivity
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import java.io.Serializable

class H5(private val context: Context) : HandExercise(context) {


    private var isFirstTimeClosed=true
    private var fingersClosedBegin=false
    private var flexPoint = mutableMapOf<String, Boolean>().apply {
        this["Thumb"] = false
        this["Index"] = false
        this["Middle"] = false
        this["Right"] = false
        this["Pinky"] = false
    }




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
