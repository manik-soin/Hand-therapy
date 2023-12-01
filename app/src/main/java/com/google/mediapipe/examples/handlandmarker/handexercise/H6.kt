package com.google.mediapipe.examples.handlandmarker.handexercise

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import com.google.mediapipe.examples.handlandmarker.utils.Compute
import com.google.mediapipe.examples.handlandmarker.StatsActivity
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import java.io.Serializable
/**
 * This class represents the Active Finger Opposition exercise, which involves touching the fingers together
 * and measuring the distance between them. The exercise can be started by calling the startExercise() method,
 * which takes a Canvas and a list of NormalizedLandmarks as parameters. The current number of repetitions is
 * displayed on the canvas using textPaint2. The distances between specific landmarks are calculated using an
 * instance of the Compute class. The minimum and maximum distances are updated based on the current distance,
 * and the number of reps is incremented if the distance is less than 1 cm. When the number of reps reaches 5,
 * confetti is displayed on the canvas and an Intent is created to start the StatsActivity, passing in the
 * relevant statistical data.
 *
 * @param context The context in which this class is being used.
 */
class H6(private val context: Context) : HandExercise(context) {

    /**
     * This method starts the Active Finger Opposition exercise by drawing the current number of reps on the canvas,
     * and then calculating the distances between specific landmarks using the computeDistance3d() method of an instance
     * of the Compute class. The minimum and maximum distances are updated based on the current distance, and the
     * number of reps is incremented if the distance is less than 1 cm. When the number of reps reaches 5, confetti
     * is displayed on the canvas and an Intent is created to start the StatsActivity, passing in the relevant
     * statistical data.
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


        val distanceInCm = (c.computeDistance3d(
            landmark,
            4,
            8,
            w,
            h,
            scaleFactor
        ) * 2 / c.computeDistance3d(landmark, 7, 8, w, h, scaleFactor)) - 1


        val distanceText = "Distance: %.2f cm".format(distanceInCm)

        //for stats
        if (distanceInCm < minDistance) {
            minDistance = distanceInCm
        }
        if (distanceInCm > maxDistance) {
            maxDistance = distanceInCm
        }


        startNewRep = if (distanceInCm < 1f) {//touched?


            if (startNewRep && distanceBelowThreshold) {

                reps++

                stats[0].add(minDistance)

                stats[1].add(maxDistance)
                distanceBelowThreshold=false

                minDistance = 9999f//reset for new rep
                maxDistance = 0f
            }
            false


        } else {
            true
        }

        if (distanceInCm > distanceThreshold) {
            distanceBelowThreshold=true
        }



        if (reps == 5) {

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

            intent.putExtra("min","Minimum Distance")
            intent.putExtra("max","Maximum Distance")
            intent.putExtra("unit","cm")

            // Start the activity
            context.startActivity(intent)

        }


        canvas.drawText(distanceText, 100f, 500f, textPaint)


    }
}
