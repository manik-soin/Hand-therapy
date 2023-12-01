package com.google.mediapipe.examples.handlandmarker.utils

import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
/**
 * This class provides methods to check if the landmark coordinates are within the defined bounding box.
 */
class Calibrate {

    /**
     * Validates if the landmarks are within the given bounding box by comparing their coordinates
     * with the provided x and y values.
     *
     * @param landmark A list of NormalizedLandmarks representing the current state of the hand.
     * @param x1 The left boundary of the bounding box.
     * @param y1 The top boundary of the bounding box.
     * @param x2 The right boundary of the bounding box.
     * @param y2 The bottom boundary of the bounding box.
     * @param w The width of the canvas.
     * @param h The height of the canvas.
     * @param scaleFactor The scale factor to apply to the coordinates.
     * @return A boolean value indicating if the landmarks are within the bounding box.
     */
    fun validate(landmark: MutableList<NormalizedLandmark>, x1: Float, y1: Float, x2: Float, y2: Float, w: Int, h: Int, scaleFactor: Float): Boolean {
        val x3 = x1 + (x2 - x1) * .2
        val y3 = y1 + (y2 - y1) * .2
        val x4 = x2 - (x2 - x1) * .2
        val y4 = y2 - (y2 - y1) * .2
        if (y1 < landmark[12].y() * h * scaleFactor && landmark[12].y() * h * scaleFactor < y3
            && y4 < landmark[0].y() * h * scaleFactor && landmark[0].y() * h * scaleFactor < y2
            && x1 < landmark[20].x() * w * scaleFactor && landmark[20].x() * w * scaleFactor < x3
            && x4 < landmark[4].x() * w * scaleFactor && landmark[4].x() * w * scaleFactor < x2) {

            return true
        }

        return false
    }

    /**
     * Validates if the landmarks are within the given bounding box by comparing their coordinates
     * with the provided y values.
     *
     * @param landmark A list of NormalizedLandmarks representing the current state of the hand.
     * @param y1 The top boundary of the bounding box.
     * @param y2 The bottom boundary of the bounding box.
     * @param w The width of the canvas.
     * @param h The height of the canvas.
     * @param scaleFactor The scale factor to apply to the coordinates.
     * @return A boolean value indicating if the landmarks are within the bounding box.
     */
    fun validate(landmark: MutableList<NormalizedLandmark>, y1: Float,  y2: Float, w: Int, h: Int, scaleFactor: Float):Boolean {

        val y3 = y1 + (y2 - y1) * .2

        val y4 = y2 - (y2 - y1) * .2
        if(y1<landmark[0].y()*h*scaleFactor && landmark[0].y()*h*scaleFactor<y3
            && y4<landmark[12].y()*h*scaleFactor && landmark[12].y()*h*scaleFactor<y2)

        {

            return true
        }

        return false
    }
}
