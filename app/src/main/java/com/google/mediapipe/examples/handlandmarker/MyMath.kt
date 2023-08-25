package com.google.mediapipe.examples.handlandmarker

class MyMath {
    private var values = mutableListOf<Double>()

    fun add(value: Double) {
        values.add(value)
    }

    fun getSum(): Double {
        return values.sum()
    }
}
