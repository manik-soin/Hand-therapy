package com.google.mediapipe.examples.handlandmarker

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Typeface

class CanvasProperties {

    private val rectPaint = Paint()
    private var linePaint = Paint()
    private var linePaint2 = Paint()
    private var pointPaint = Paint()
    private var pointPaint2 = Paint()
    private var textPaint = Paint()
    private var textPaint2 = Paint()
    private val strokeWidth = 5f

    init {
        initPaints()
    }

    private fun initPaints() {
        linePaint.color = Color.BLUE
        linePaint.strokeWidth = strokeWidth
        linePaint.style = Paint.Style.STROKE

        linePaint2.color = Color.BLACK
        linePaint2.strokeWidth = strokeWidth
        linePaint2.style = Paint.Style.STROKE

        pointPaint.color = Color.YELLOW
        pointPaint.strokeWidth = strokeWidth
        pointPaint.style = Paint.Style.FILL

        pointPaint2.color = Color.RED
        pointPaint2.strokeWidth = 32F
        pointPaint2.style = Paint.Style.FILL

        textPaint2.color = Color.YELLOW
        textPaint2.textSize = 72f
        textPaint2.strokeWidth = 10f
        textPaint2.style = Paint.Style.FILL_AND_STROKE
        textPaint2.setShadowLayer(20f, 0f, 0f, Color.BLACK)

        textPaint.color = Color.WHITE
        textPaint.textSize = 42f
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textPaint.setShadowLayer(10f, 0f, 0f, Color.BLACK)

        rectPaint.color = Color.RED
        rectPaint.style = Paint.Style.FILL
        rectPaint.style = Paint.Style.STROKE
        rectPaint.strokeWidth = 12f
    }

    fun getRectPaint(): Paint {
        return rectPaint
    }

    fun getLinePaint(): Paint {
        return linePaint
    }

    fun getLinePaint2(): Paint {
        return linePaint2
    }

    fun getPointPaint(): Paint {
        return pointPaint
    }

    fun getPointPaint2(): Paint {
        return pointPaint2
    }

    fun getTextPaint(): Paint {
        return textPaint
    }

    fun getTextPaint2(): Paint {
        return textPaint2
    }

    fun drawDashedLine(canvas: Canvas, startX: Float, startY: Float, stopX: Float, stopY: Float) {
        val paint = Paint()
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 5f
        paint.pathEffect = DashPathEffect(floatArrayOf(10f, 10f), 0f)

        canvas.drawLine(startX, startY, stopX, stopY, paint)
    }

    fun displayConfetti(canvas: Canvas) {
        fun randomColor(): Paint {
            val paint = Paint()
            val red = (0..255).random()
            val green = (0..255).random()
            val blue = (0..255).random()
            paint.color = Color.argb(255, red, green, blue)
            paint.strokeWidth = 0.5f
            paint.style = Paint.Style.FILL
            return paint
        }

        for (i in 0..10) {
            val x = (0..canvas.width).random().toFloat()
            val y = (0..canvas.height).random().toFloat()
            canvas.drawCircle(x, y, 15f, randomColor())
        }
    }
}
