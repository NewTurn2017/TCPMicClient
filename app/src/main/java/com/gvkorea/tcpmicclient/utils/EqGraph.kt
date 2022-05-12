package com.gvkorea.tcpmicclient.utils

import kotlin.math.*

class EqGraph {

    val samplerate = 44100

    val frequencyArrays = floatArrayOf(
        20f, 25f, 31.5f, 40f, 50f, 63f, 80f, 100f, 125f, 160f,
        200f, 250f, 315f, 400f, 500f, 630f, 800f, 1000f, 1250f, 1600f,
        2000f, 2500f, 3150f, 4000f, 5000f, 6300f, 8000f, 10000f, 12500f, 16000f, 20000f
    )

    fun calculateGEQGraph(gain: Float, freq: Float): FloatArray {
        return applyCoeff(gain.toDouble(), freq.toDouble())
    }

    private fun applyCoeff(gain: Double, freq: Double): FloatArray {
        val valueArrays = FloatArray(31)
        val a = 10.0.pow((gain / 40))
        val w0 = 2 * PI * (freq / samplerate)
        val cosW = cos(w0)
        val alpha = sin(w0) / (2 * 4.5)

        val eB0 = 1 + alpha * a
        val eB1 = -2 * cosW
        val eB2 = 1 - alpha * a
        val eA0 = 1 + alpha / a
        val eA1 = -2 * cosW
        val eA2 = 1 - alpha / a

        val b0 = eB0 / eA0
        val b1 = eB1 / eA0
        val b2 = eB2 / eA0
        val a0 = 1
        val a1 = eA1 / eA0
        val a2 = eA2 / eA0

        for (i in valueArrays.indices) {
            val w = 2 * PI * frequencyArrays[i] / samplerate
            val phi = sin(w / 2).pow(2)
            val pre =
                (b0 + b1 + b2).pow(2) - 4 * (b0 * b1 + 4 * b0 * b2 + b1 * b2) * phi + 16 * b0 * b2 * phi.pow(
                    2
                )
            val post =
                (a0 + a1 + a2).pow(2) - 4 * (a0 * a1 + 4 * a0 * a2 + a1 * a2) * phi + 16 * a0 * a2 * phi.pow(
                    2
                )
            val outPre = 10 * log10(pre)
            val outPost = 10 * log10(post)
            valueArrays[i] = (outPre - outPost).toFloat()
        }
        return valueArrays
    }
}