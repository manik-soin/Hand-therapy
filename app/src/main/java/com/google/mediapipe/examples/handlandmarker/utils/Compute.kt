package com.google.mediapipe.examples.handlandmarker.utils
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark

import kotlin.math.sqrt

class Compute {




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
    fun angle3ds_static(landmark1: MutableList<NormalizedLandmark>,landmark: MutableList<NormalizedLandmark>, a: Int, b: Int, c: Int): Float {

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



    fun distance3ds(landmark: MutableList<NormalizedLandmark>, a: Int, b: Int, w: Int, h: Int, scaleFactor: Float): Float {
        val diffX = (landmark[a].x() - landmark[b].x())*w*scaleFactor
        val diffY = (landmark[a].y() - landmark[b].y())*h*scaleFactor
        val diffZ = landmark[a].z() - landmark[b].z()*scaleFactor

        val squaredDistance = diffX * diffX + diffY * diffY + diffZ * diffZ
        //println(results)

        return sqrt(squaredDistance)
    }




}
