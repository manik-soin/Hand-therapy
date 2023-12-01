package com.google.mediapipe.examples.handlandmarker.handexercise
import android.content.Context

import com.google.mediapipe.tasks.components.containers.NormalizedLandmark

import com.google.mediapipe.examples.handlandmarker.utils.CanvasProperties
import com.google.mediapipe.examples.handlandmarker.MainActivity

/**
 * Represents an abstract class for hand exercises.
 *
 * @param context The context of the exercise.
 */
abstract class HandExercise(context: Context?) {

    /**
     * Paint properties for displaying text on the canvas
     */

    val canvasProperties = CanvasProperties()
    val textPaint = canvasProperties.getTextPaint()
    val textPaint2 = canvasProperties.getTextPaint2()

    /**
     * Getting context value from MainActivity that was passed by the previous activity (Exercise Selection)
     */

    private val mainActivity = context as? MainActivity
    private val exerciseValue = mainActivity?.getExerciseValue() // Exercise value
    private val difficultyValue = mainActivity?.getDifficultyValue() // Difficulty value

    // Flags and counter for the exercise
    /**
     * Flag indicating whether it's the first value in the exercise.
     */
    var isFirstValue = true
    /**
     * Flag indicating whether to start a new repetition.
     */
    var startNewRep = true
    /**
     * Flag indicating whether the angle is below the threshold.
     */
    var angleBelowThreshold = true
    /**
     * Counter for the number of repetitions.
     */
    var reps = 0

    // Flags and variables for distance calculations in the exercise
    /**
     * Flag indicating whether the distance is below the threshold.
     */
    var distanceBelowThreshold = true
    /**
     * Maximum distance recorded during the exercise.
     */
    var maxDistance = 0f
    /**
     * Threshold value for the distance.
     */
    var distanceThreshold = 5f
    /**
     * Minimum distance recorded during the exercise.
     */
    var minDistance = 9999f
    /**
     * Map to store distances recorded during the exercise.
     */
    var distances1 = mutableMapOf<String, Float>()

    // Flags and variables for angle calculations in the exercise
    /**
     * Maximum angle recorded during the exercise.
     */
    var maxAngle = 0f
    /**
     * Threshold value for the angle.
     */
    var angleThreshold = 30f
    /**
     * Minimum angle recorded during the exercise.
     */
    var minAngle = 9999f

    /**
     * List to store statistics of the exercise, containing two lists: one for minimum angles and another for maximum angles.
     */
    val stats = mutableListOf(
        mutableListOf<Float>(),
        mutableListOf<Float>(),
    )

    /**
     * List to store the initial landmarks of the hand.
     */
    lateinit var landmark1: MutableList<NormalizedLandmark>
}
