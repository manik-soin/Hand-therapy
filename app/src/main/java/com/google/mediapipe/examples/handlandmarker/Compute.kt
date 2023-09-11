package com.google.mediapipe.examples.handlandmarker
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark

import kotlin.math.sqrt

class Compute {

    fun angle3d(x1: Float, y1: Float, z1: Float, x2: Float, y2: Float, z2: Float, x3: Float, y3: Float, z3: Float): Float {
        val vector1X = x1 - x2
        val vector1Y = y1 - y2
        val vector1Z = z1 - z2

        val vector2X = x3 - x2
        val vector2Y = y3 - y2
        val vector2Z = z3 - z2

        val dotProduct = vector1X * vector2X + vector1Y * vector2Y + vector1Z * vector2Z
        val magnitude1 = sqrt(vector1X * vector1X + vector1Y * vector1Y + vector1Z * vector1Z)
        val magnitude2 = sqrt(vector2X * vector2X + vector2Y * vector2Y + vector2Z * vector2Z)

        val cosine = dotProduct / (magnitude1 * magnitude2)

        return Math.toDegrees(Math.acos(cosine.toDouble())).toFloat()
    }


    fun angle3ds(landmark: MutableList<NormalizedLandmark>, a: Int, b: Int, c: Int): Float {

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





    fun distance3d(x1: Float, y1: Float, z1: Float, x2: Float, y2: Float, z2: Float): Float {
        val diffX = x2 - x1
        val diffY = y2 - y1
        val diffZ = z2 - z1

        val squaredDistance = diffX * diffX + diffY * diffY + diffZ * diffZ
        //println(results)

        return sqrt(squaredDistance)
    }

    fun distance3ds(landmark: MutableList<NormalizedLandmark>, a: Int, b: Int, w: Int, h: Int, scaleFactor: Float): Float {
        val diffX = (landmark[a].x() - landmark[b].x())*w*scaleFactor
        val diffY = (landmark[a].y() - landmark[b].y())*h*scaleFactor
        val diffZ = landmark[a].z() - landmark[b].z()*scaleFactor

        val squaredDistance = diffX * diffX + diffY * diffY + diffZ * diffZ
        //println(results)

        return sqrt(squaredDistance)
    }




}
