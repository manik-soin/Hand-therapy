package com.google.mediapipe.examples.handlandmarker.handexercise

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import com.google.mediapipe.examples.handlandmarker.Compute
import com.google.mediapipe.examples.handlandmarker.StatsActivity
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import java.io.Serializable

class H6(private val context: Context) : HandExercise(context) {

    private var reps =0

    fun startExercise(canvas: Canvas, landmark: MutableList<NormalizedLandmark>,w: Int, h: Int, scaleFactor: Float) {
        val c = Compute()
        canvas.drawText(" REPS: $reps", canvas.width-400f, 200f, textPaint2)


        val distanceInCm = (c.distance3ds(
            landmark,
            4,
            8,
            w,
            h,
            scaleFactor
        ) * 2 / c.distance3ds(landmark, 7, 8, w, h, scaleFactor)) - 1


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
