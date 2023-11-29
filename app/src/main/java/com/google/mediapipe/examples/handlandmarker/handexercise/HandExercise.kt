package com.google.mediapipe.examples.handlandmarker.handexercise
import android.content.Context
import android.graphics.Canvas
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import android.graphics.Paint
import android.util.AttributeSet
import com.google.mediapipe.examples.handlandmarker.CanvasProperties
import com.google.mediapipe.examples.handlandmarker.MainActivity


abstract class HandExercise(context: Context?) {

    //Paint properties
    val canvasProperties = CanvasProperties()


    val textPaint = canvasProperties.getTextPaint()
    val textPaint2 = canvasProperties.getTextPaint2()
    //getting context value from main activity that was passed by the previous activity(Exercise Selection)
    private val mainActivity = context as? MainActivity
    private val exerciseValue = mainActivity?.getExerciseValue()//exercise value
    private val difficultyValue = mainActivity?.getDifficultyValue()//difficulty value


    var dflag=true
    //var aflag=true
    var validation_complete=false
    var touch_flag=true
    //var reps =0
    var maxdistance =0f
    var dvalue =5f
    var mindistance =9999f
    var distances1 =mutableMapOf<String, Float>()


    var maxAngle =0f
    var avalue =30f
    var minAngle =9999f

    val stats = mutableListOf(
        mutableListOf<Float>(),
        mutableListOf<Float>(),
    )
    lateinit var landmark1: MutableList<NormalizedLandmark>
    var e1_ft=true
    var e5_ft=true
    var e5_ft2=true
    var fingersClosedBegin=false
    var flexPoint = mutableMapOf<String, Boolean>().apply {
        this["Thumb"] = false
        this["Index"] = false
        this["Middle"] = false
        this["Right"] = false
        this["Pinky"] = false
    }

    var e5TouchFlag=true

}

