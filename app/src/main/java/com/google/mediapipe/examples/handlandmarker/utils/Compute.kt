package com.google.mediapipe.examples.handlandmarker.utils
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark

import kotlin.math.sqrt
/**
 * This class provides functions to calculate distances and angles between given landmark coordinates.
 */
class Compute {


    /**
     * Computes the angle between three points in 3D space by calculating the dot product and magnitudes of the vectors formed between the points, and then computing the inverse cosine of their ratio
     *
     * @param landmark A list of NormalizedLandmarks representing the points.
     * @param a Index of the first point.
     * @param b Index of the second point.
     * @param c Index of the third point.
     * @return The angle in degrees between the three points.
     */

    fun computeAngle3d(landmark: MutableList<NormalizedLandmark>, a: Int, b: Int, c: Int): Float {

        val vector1X = landmark[a].x() - landmark[b].x()
        val vector1Y = landmark[a].y() - landmark[b].y()
        val vector1Z = landmark[a].z() - landmark[b].z()

        val vector2X = landmark[c].x() - landmark[b].x()
        val vector2Y = landmark[c].y() - landmark[b].y()
        val vector2Z = landmark[c].z() - landmark[b].z()

        val dotProduct = vector1X * vector2X + vector1Y * vector2Y + vector1Z * vector2Z
        val magnitude1 = sqrt(vector1X * vector1X + vector1Y * vector1Y + vector1Z * vector1Z)
        val magnitude2 = sqrt(vector2X * vector2X + vector2Y * vector2Y + vector2Z * vector2Z)

        val cosine = dotProduct / (magnitude1 * magnitude2)

        return Math.toDegrees(Math.acos(cosine.toDouble())).toFloat()


    }

    /**
     * Calculates the angle between two vectors with one static point and two dynamic points in 3D space by calculating the dot product and magnitudes of the vectors formed between the points, and then computing the inverse cosine of their ratio.
     *
     * @param landmark1 A list of NormalizedLandmarks representing the static point.
     * @param landmark A list of NormalizedLandmarks representing the dynamic points.
     * @param a Index of the first dynamic point.
     * @param b Index of the static point.
     * @param c Index of the second dynamic point.
     * @return The angle in degrees between the two vectors.
     */
    fun computeAngle3StaticPoint(landmark1: MutableList<NormalizedLandmark>,landmark: MutableList<NormalizedLandmark>, a: Int, b: Int, c: Int): Float {

        val vector1X = landmark[a].x() - landmark1[b].x()
        val vector1Y = landmark[a].y() - landmark1[b].y()
        val vector1Z = landmark[a].z() - landmark1[b].z()

        val vector2X = landmark1[c].x() - landmark1[b].x()
        val vector2Y = landmark1[c].y() - landmark1[b].y()
        val vector2Z = landmark1[c].z() - landmark1[b].z()

        val dotProduct = vector1X * vector2X + vector1Y * vector2Y + vector1Z * vector2Z
        val magnitude1 = sqrt(vector1X * vector1X + vector1Y * vector1Y + vector1Z * vector1Z)
        val magnitude2 = sqrt(vector2X * vector2X + vector2Y * vector2Y + vector2Z * vector2Z)

        val cosine = dotProduct / (magnitude1 * magnitude2)

        return Math.toDegrees(Math.acos(cosine.toDouble())).toFloat()


    }

    /**
     * Computes the 3D distance between two points using their landmark coordinates by calculating the squared differences between their x, y, and z coordinates, summing them, and then taking the square root of the result.
     *
     * @param landmark A list of NormalizedLandmarks representing the points.
     * @param a Index of the first point.
     * @param b Index of the second point.
     * @param w The width of the canvas.
     * @param h The height of the canvas.
     * @param scaleFactor The scale factor to apply to the coordinates.
     * @return The 3D distance between the two points.
     */


    fun computeDistance3d(landmark: MutableList<NormalizedLandmark>, a: Int, b: Int, w: Int, h: Int, scaleFactor: Float): Float {
        val diffX = (landmark[a].x() - landmark[b].x())*w*scaleFactor
        val diffY = (landmark[a].y() - landmark[b].y())*h*scaleFactor
        val diffZ = landmark[a].z() - landmark[b].z()*scaleFactor

        val squaredDistance = diffX * diffX + diffY * diffY + diffZ * diffZ


        return sqrt(squaredDistance)
    }




}
