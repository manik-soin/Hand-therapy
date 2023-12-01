
package com.google.mediapipe.examples.handlandmarker

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarker
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult
import kotlin.math.max
import kotlin.math.min
import kotlin.Triple

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.os.CountDownTimer
import com.google.mediapipe.examples.handlandmarker.handexercise.H1
import com.google.mediapipe.examples.handlandmarker.handexercise.H2
import com.google.mediapipe.examples.handlandmarker.handexercise.H3
import com.google.mediapipe.examples.handlandmarker.handexercise.H5
import com.google.mediapipe.examples.handlandmarker.handexercise.H6
import com.google.mediapipe.examples.handlandmarker.utils.Calibrate
import com.google.mediapipe.examples.handlandmarker.utils.CanvasProperties


/**
 * Custom view class for displaying hand landmarks, connections, and exercise information.
 *
 * @param context The context in which the view is created.
 * @param attrs The AttributeSet for the view.
 */
class OverlayView(context: Context?, attrs: AttributeSet?) :
    View(context, attrs) {

    // Properties
    /**
     * The bitmap for the calibration image.
     */
    private var imageBitmap: Bitmap

    /**
     * The result object containing the hand landmarks.
     */
    private var results: HandLandmarkerResult? = null

    // Paint properties
    /**
     * Canvas Properties Object
     */
    private val canvasProperties = CanvasProperties()
    /**
     * Paint object for drawing rectangles.
     */
    private val rectPaint = canvasProperties.getRectPaint()

    /**
     * Paint object for drawing lines.
     */
    private val linePaint = canvasProperties.getLinePaint()

    /**
     * Paint object for drawing alternate lines.
     */
    private val linePaint2 = canvasProperties.getLinePaint2()

    /**
     * Paint object for drawing points.
     */
    private val pointPaint = canvasProperties.getPointPaint()

    /**
     * Paint object for drawing alternate points.
     */
    private val pointPaint2 = canvasProperties.getPointPaint2()

    /**
     * Paint object for drawing text.
     */
    private val textPaint = canvasProperties.getTextPaint()

    /**
     * Paint object for drawing alternate text.
     */
    private val textPaint2 = canvasProperties.getTextPaint2()

    // Image Scaling
    /**
     * Scale factor for resizing the image.
     */
    private var scaleFactor: Float = 1f

    /**
     * Width of the image.
     */
    private var imageWidth: Int = 1

    /**
     * Height of the image.
     */
    private var imageHeight: Int = 1

    // Getting context value from main activity that was passed by the previous activity (Exercise Selection)
    /**
     * The MainActivity object for accessing exercise and difficulty values.
     */
    private val mainActivity = context as? MainActivity

    /**
     * The selected exercise value.
     */
    private val exerciseValue = mainActivity?.getExerciseValue()

    /**
     * The selected difficulty value.
     */
    private val difficultyValue = mainActivity?.getDifficultyValue()

    /**
     * The selected exercise ID.
     */
    private val exerciseID = mainActivity?.getExerciseID()

    /**
     * The selected difficulty ID.
     */
    private val difficultyID = mainActivity?.getDifficultyID()

    // Initialize the calibration image
    init {
        // Choose the calibration image
        val imageResId = if (exerciseID == 1) {
            R.drawable.palm_down
        } else {
            R.drawable.palm_left
        }

        imageBitmap = BitmapFactory.decodeResource(resources, imageResId)
    }

    /**
     * Clears the view by resetting the result object and paint objects.
     */
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

    /**
     * Text to display the timer countdown.
     */
    private var timerText = ""

    /**
     * Timer object for counting down during the calibration phase.
     */
    private val timer = object : CountDownTimer(4000, 100) {
        override fun onTick(millisUntilFinished: Long) {
            timerText = "seconds remaining: " + millisUntilFinished / 1000
        }

        override fun onFinish() {
            timerText = "done!"
            validationComplete = true
        }
    }

    // Flags and exercise-related variables
    /**
     * Flag to indicate if the calibration is in progress.
     */
    private var validationFlag = true

    /**
     * Flag to indicate if the calibration is complete.
     */
    private var validationComplete = false

    /**
     * Instance of the H1 exercise class.
     */
    private val h1 = context?.let { H1(it) }

    /**
     * Instance of the H2 exercise class.
     */
    private val h2 = context?.let { H2(it) }

    /**
     * Instance of the H3 exercise class.
     */
    private val h3 = context?.let { H3(it) }

    /**
     * Instance of the H5 exercise class.
     */
    private val h5 = context?.let { H5(it) }

    /**
     * Instance of the H6 exercise class.
     */
    private val h6 = context?.let { H6(it) }

    /**
     * This function is responsible for drawing the hand landmarks on a canvas and providing visual feedback for calibration and exercise execution. When the validation is not complete, it draws a calibration image and instructs the user to place their left hand on the mark. The hand landmarks are detected and analyzed, and if the user's hand is correctly placed, a "VALIDATED" message is shown along with a timer. Once the validation is complete, the code proceeds to draw hand landmarks and guide the user through the selected exercise. Different exercises and difficulty levels are supported, and the code adjusts the hand landmarks and visual guidance accordingly.
     * @param canvas The drawing canvas that is overlaid
     */
    override fun draw(canvas: Canvas) {
        super.draw(canvas)


        //deciding the size of the image
        var imageSize = if (canvas.height<canvas.width){
        (canvas.height/1.4).toInt()
        } else{
            (canvas.width/1.4).toInt()
        }

        // Calculate the position to draw the image
        val x1 = (canvas.width - imageSize ) / 2f - 10f
        val y1 = (canvas.height - imageSize  ) / 2f - 10f
        val x2 = (canvas.width + imageSize ) / 2f + 10f
        val y2 = (canvas.height + imageSize  ) / 2f + 10f


        //red rectangle coordinates
        val x3 = x1 + (x2 - x1) * .25
        val y3 = y1 + (y2 - y1) * .25
        val x4 = x2 - (x2 - x1) * .25
        val y4 = y2 - (y2 - y1) * .25

        //getting the extra intent from EXERCISE SELECTION


        // Scale the bitmap
        val scaledBitmap = Bitmap.createScaledBitmap(
            imageBitmap,
            imageSize ,
            imageSize ,
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

                val validated = when (exerciseID) {
                    1 -> v.validate(landmark, y1, y2, imageWidth, imageHeight, scaleFactor)
                    else -> v.validate(landmark, x1, y1, x2, y2, imageWidth, imageHeight, scaleFactor)
                }

                if (validated && !validationComplete) {
                    if (validationFlag) {
                        timer.start()
                        validationFlag = false
                    }
                    canvas.drawText("VALIDATED", 100f, 300f, textPaint2)
                    canvas.drawText(timerText, 100f, 500f, textPaint)
                } else {
                    timer.cancel()
                    validationFlag = true
                }

                if (validationComplete) {
                    val (x1, y1, z1) = points[1]
                    val (x2, y2, z2) = points[0]
                    val (x3, y3, z3) = points[2]
                    canvas.drawLine(x1, y1, x2, y2, linePaint2)
                    canvas.drawLine(x2, y2, x3, y3, linePaint2)

                    canvasProperties.drawDashedLine(canvas, x1, y1, x3, y3)

                    when (exerciseID) {
                        1 -> h1?.startExercise(canvas, landmark)
                        2 -> h2?.startExercise(canvas, landmark)
                        3 -> h3?.startExercise(canvas, landmark)
                        4, 5 -> h5?.startExercise(canvas, landmark, imageWidth, imageHeight, scaleFactor)
                        6 -> h6?.startExercise(canvas, landmark, imageWidth, imageHeight, scaleFactor)
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
