package com.google.mediapipe.examples.handlandmarker

import com.google.mediapipe.tasks.components.containers.NormalizedLandmark

class Calibrate {
    fun validate(landmark: MutableList<NormalizedLandmark>,x1: Float, y1: Float, x2: Float, y2: Float, w: Int, h: Int, scaleFactor: Float):Boolean {
        val x3 = x1 + (x2 - x1) * .2
        val y3 = y1 + (y2 - y1) * .2
        val x4 = x2 - (x2 - x1) * .2
        val y4 = y2 - (y2 - y1) * .2
        if(y1<landmark[12].y()*h*scaleFactor && landmark[12].y()*h*scaleFactor<y3
            && y4<landmark[0].y()*h*scaleFactor && landmark[0].y()*h*scaleFactor<y2
            && x1<landmark[20].x()*w*scaleFactor && landmark[20].x()*w*scaleFactor<x3
            && x4<landmark[4].x()*w*scaleFactor && landmark[4].x()*w*scaleFactor<x2)
        {

            return true
        }



        return false
    }
}
