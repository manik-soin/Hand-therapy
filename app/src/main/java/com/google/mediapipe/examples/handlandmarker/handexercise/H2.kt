package com.google.mediapipe.examples.handlandmarker.handexercise

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import com.google.mediapipe.examples.handlandmarker.utils.Compute
import com.google.mediapipe.examples.handlandmarker.StatsActivity
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import java.io.Serializable
/**
 * This class represents the Active & Assisted Forearm Supination exercise, which involves
 * measuring the angle between two landmarks on the hand. The exercise can be started by calling the
 * startExercise() method, which takes a Canvas and a list of NormalizedLandmarks as parameters. The current
 * number of repetitions is displayed on the canvas using textPaint2. The angle between the landmarks is
 * calculated using an instance of the Compute class. The minimum and maximum angles are updated based on the
 * current angle, and the number of reps is incremented if the angle is greater than 100 degrees. When the
 * number of reps reaches 5, confetti is displayed on the canvas and an Intent is created to start the
 * StatsActivity, passing in the relevant statistical data.
 *
 * @param context The context in which this class is being used.
 */
class H2(private val context: Context) : HandExercise(context) {

    /**
     * This method starts the Active & Assisted Forearm Supination exercise by drawing the current
     * number of reps on the canvas, and then calculating the angle between the two landmarks using the
     * computeAngle3StaticPoint() method of an instance of the Compute class. The minimum and maximum angles are updated
     * based on the current angle, and the number of reps is incremented if the angle is greater than 100 degrees.
     * When the number of reps reaches 5, confetti is displayed on the canvas and an Intent is created to start
     * the StatsActivity, passing in the relevant statistical data.
     *
     * @param canvas The canvas on which to draw the current state of the exercise.
     * @param landmark A list of NormalizedLandmarks representing the current state of the hand.
     */

    fun startExercise(canvas: Canvas, landmark: MutableList<NormalizedLandmark>) {
        val c = Compute()
        canvas.drawText(" REPS: $reps", canvas.width-400f, 200f, textPaint2)



        if(isFirstValue){
            landmark1 = landmark
        }
        isFirstValue=false

        println(landmark1)
        var calculatedAngle = c.computeAngle3StaticPoint(landmark1,landmark, 4, 9, 4)


        val angleText = "Angle: %.2f".format(calculatedAngle)
        //c.angle(landmark)

        canvas.drawText(angleText, 100f , 500f, textPaint)

        if (calculatedAngle < minAngle) {
            minAngle = calculatedAngle
        }
        if (calculatedAngle > maxAngle) {
            maxAngle = calculatedAngle
        }

        startNewRep = if (calculatedAngle > 100f) {//touched?


            if (startNewRep && angleBelowThreshold) {

                reps++

                stats[0].add(minAngle)

                stats[1].add(maxAngle)
                angleBelowThreshold=false

                minAngle = 9999f//reset for new rep
                maxAngle = 0f
            }
            false


        } else {
            true
        }

        if (calculatedAngle < 20f) {//this is basically a reset for the new turn to make sure we did not just move a little bit
            angleBelowThreshold=true
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
