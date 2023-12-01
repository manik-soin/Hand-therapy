package com.google.mediapipe.examples.handlandmarker.handexercise

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import com.google.mediapipe.examples.handlandmarker.Compute
import com.google.mediapipe.examples.handlandmarker.StatsActivity
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import java.io.Serializable

class H2(private val context: Context) : HandExercise(context) {



    fun startExercise(canvas: Canvas, landmark: MutableList<NormalizedLandmark>) {
        val c = Compute()
        canvas.drawText(" REPS: $reps", canvas.width-400f, 200f, textPaint2)



        if(isFirstValue){
            landmark1 = landmark
        }
        isFirstValue=false

        println(landmark1)
        var calculatedAngle = c.angle3ds_static(landmark1,landmark, 4, 9, 4)


        val angleText = "Angle: %.2f".format(calculatedAngle)
        //c.angle(landmark)

        canvas.drawText(angleText, 100f , 500f, textPaint)

        if (calculatedAngle < minAngle) {
            minAngle = calculatedAngle
        }
        if (calculatedAngle > maxAngle) {
            maxAngle = calculatedAngle
        }

        repFlag = if (calculatedAngle > 100f) {//touched?


            if (repFlag && aflag) {

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
                500f,
                500f,
                textPaint2
            )



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
}
