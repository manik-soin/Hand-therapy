package com.google.mediapipe.examples.handlandmarker

import com.google.mediapipe.tasks.components.containers.NormalizedLandmark

class Caliberate {
    fun validate(landmark: MutableList<NormalizedLandmark>,x1: Float, y1: Float, x2: Float, y2: Float):Boolean{
        val x3 = x1 + (x2-x1)*.25
        val y3 = y1 + (y2-y1)*.25
        val x4 = x2 - (x2-x1)*.25
        val y4 = y2 - (y2-y1)*.25


        if()
    }
}
