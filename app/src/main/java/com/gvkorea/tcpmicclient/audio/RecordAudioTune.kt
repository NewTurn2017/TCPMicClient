package com.gvkorea.tcpmicclient.audio

import android.annotation.SuppressLint
import android.graphics.Color
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.AsyncTask
import android.os.Build
import android.os.Handler
import android.os.Message
import android.util.Log
import com.github.mikephil.charting.data.*
import com.gvkorea.tcpmicclient.MainActivity
import com.gvkorea.tcpmicclient.MainActivity.Companion.CALIBRATION
import com.gvkorea.tcpmicclient.MainActivity.Companion.isCalib
import com.gvkorea.tcpmicclient.MainActivity.Companion.isStarted
import com.gvkorea.tcpmicclient.MainActivity.Companion.selectedMicName
import com.gvkorea.tcpmicclient.MainActivity.Companion.velocity
import com.gvkorea.tcpmicclient.fft.RealDoubleFFT
import com.gvkorea.tcpmicclient.utils.CSVRead
import com.gvkorea.tcpmicclient.utils.FrequencyRange16K
import com.gvkorea.tcpmicclient.utils.FrequencyRange8K
import com.gvkorea.tcpmicclient.utils.MSGProtocol
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.roundToInt

class RecordAudioTune(
    val view: MainActivity,
    val handler: Handler
) :
    AsyncTask<Unit, DoubleArray, Unit>() {

    var isTest = false

    var isLowFreqStarted = false
    private val frequency = 44100
    private val channelConfiguration = AudioFormat.CHANNEL_IN_MONO
    private val audioEncoding = AudioFormat.ENCODING_PCM_FLOAT
    private val blockSize = 8192
    private val transformer = RealDoubleFFT(blockSize)
    private var chartValue = ArrayList<BarEntry>()
    private var barDataSet = BarDataSet(chartValue, null)
    private val MIC1COLOR = Color.RED
    private val MIC2COLOR = Color.BLUE
    private val MIC3COLOR = Color.BLACK
    private val MIC4COLOR = Color.MAGENTA

    val arrayDequeList = List(31) { ArrayDeque<Double>() }

    var calibData = CSVRead(view).readCalibCsv(view.assets!!, selectedMicName)

    private var count = 0

    val frequencyRangeList8K = listOf(
        FrequencyRange8K.INDEX_20HZ, FrequencyRange8K.INDEX_25HZ, FrequencyRange8K.INDEX_32HZ,
        FrequencyRange8K.INDEX_40HZ, FrequencyRange8K.INDEX_50HZ, FrequencyRange8K.INDEX_63HZ,
        FrequencyRange8K.INDEX_80HZ, FrequencyRange8K.INDEX_100HZ, FrequencyRange8K.INDEX_125HZ,
        FrequencyRange8K.INDEX_160HZ, FrequencyRange8K.INDEX_200HZ, FrequencyRange8K.INDEX_250HZ,
        FrequencyRange8K.INDEX_315HZ, FrequencyRange8K.INDEX_400HZ, FrequencyRange8K.INDEX_500HZ,
        FrequencyRange8K.INDEX_630HZ, FrequencyRange8K.INDEX_800HZ, FrequencyRange8K.INDEX_1000HZ,
        FrequencyRange8K.INDEX_1250HZ, FrequencyRange8K.INDEX_1600HZ, FrequencyRange8K.INDEX_2000HZ,
        FrequencyRange8K.INDEX_2500HZ, FrequencyRange8K.INDEX_3150HZ, FrequencyRange8K.INDEX_4000HZ,
        FrequencyRange8K.INDEX_5000HZ, FrequencyRange8K.INDEX_6300HZ, FrequencyRange8K.INDEX_8000HZ,
        FrequencyRange8K.INDEX_10000HZ, FrequencyRange8K.INDEX_12500HZ, FrequencyRange8K.INDEX_16000HZ,
        FrequencyRange8K.INDEX_20000HZ
    )

    val frequencyRangeList16K = listOf(
        FrequencyRange16K.INDEX_20HZ, FrequencyRange16K.INDEX_25HZ, FrequencyRange16K.INDEX_32HZ,
        FrequencyRange16K.INDEX_40HZ, FrequencyRange16K.INDEX_50HZ, FrequencyRange16K.INDEX_63HZ,
        FrequencyRange16K.INDEX_80HZ, FrequencyRange16K.INDEX_100HZ, FrequencyRange16K.INDEX_125HZ,
        FrequencyRange16K.INDEX_160HZ, FrequencyRange16K.INDEX_200HZ, FrequencyRange16K.INDEX_250HZ,
        FrequencyRange16K.INDEX_315HZ, FrequencyRange16K.INDEX_400HZ, FrequencyRange16K.INDEX_500HZ,
        FrequencyRange16K.INDEX_630HZ, FrequencyRange16K.INDEX_800HZ, FrequencyRange16K.INDEX_1000HZ,
        FrequencyRange16K.INDEX_1250HZ, FrequencyRange16K.INDEX_1600HZ, FrequencyRange16K.INDEX_2000HZ,
        FrequencyRange16K.INDEX_2500HZ, FrequencyRange16K.INDEX_3150HZ, FrequencyRange16K.INDEX_4000HZ,
        FrequencyRange16K.INDEX_5000HZ, FrequencyRange16K.INDEX_6300HZ, FrequencyRange16K.INDEX_8000HZ,
        FrequencyRange16K.INDEX_10000HZ, FrequencyRange16K.INDEX_12500HZ, FrequencyRange16K.INDEX_16000HZ,
        FrequencyRange16K.INDEX_20000HZ
    )

    @SuppressLint("MissingPermission")
    override fun doInBackground(vararg p0: Unit?): Unit? {
        try {
            val bufferSize = AudioRecord.getMinBufferSize(
                frequency,
                channelConfiguration, audioEncoding
            )

            val audioRecord = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                AudioRecord(
                    MediaRecorder.AudioSource.VOICE_PERFORMANCE, frequency,
                    channelConfiguration, audioEncoding, bufferSize
                )
            } else {
                AudioRecord(
                    MediaRecorder.AudioSource.VOICE_RECOGNITION, frequency,
                    channelConfiguration, audioEncoding, bufferSize
                )
            }


            val buffer = FloatArray(blockSize)
            val toTransform = DoubleArray(blockSize)

            audioRecord.startRecording()

            while (isStarted) {
                val bufferReadResult = audioRecord.read(buffer, 0, blockSize, AudioRecord.READ_BLOCKING)

                var i = 0
                while (i < blockSize && i < bufferReadResult) {
                    toTransform[i] = buffer[i].toDouble() // / java.lang.Short.MAX_VALUE // 32,768
                    i++
                }
                transformer.ft(toTransform)
                publishProgress(toTransform)
            }
            audioRecord.stop()
            audioRecord.release()
        } catch (t: Throwable) {
            Log.e("AudioRecord", "Recording Failed")
        }
        return null
    }

    override fun onProgressUpdate(vararg values: DoubleArray) {
        count++
        Log.d("count_progress", "$count")
        chartValue = ArrayList()

        for (i in frequencyRangeList8K.indices) {
            calculateFrequencyValues(values[0], frequencyRangeList8K[i])
        }

        updateChart()
    }

    private fun updateChart() {

        if (!isCalib) {
            spldB = calculate_SPL(rmsValues)
            view.binding.tvSPL.text = "SPL : $spldB dB"

            if (!isLowFreqStarted) {
                initialDeque()
                LowFreqAvg()
                isLowFreqStarted = true
            } else {
                addDequeAndAvg()
            }
            sendAudioMessage()
            Log.d("rmsValues", "${rmsValues.toList()}")
            updateChart(rmsValues)
        } else {
            spldB = (rmsValues[17] * 10.0).roundToInt() / 10.0 // 1khz.value
            view.binding.tvSPL.text = "1khz.value : $spldB dB"
            if (view.binding.barChartCalib.data != null && view.binding.barChartCalib.data.dataSetCount > 0) {
                chartValue.add(BarEntry(0f, spldB.toFloat()))
                barDataSet.values = chartValue
                barDataSet.setDrawValues(true)
                barDataSet.valueTextColor = Color.RED
                barDataSet.valueTextSize = 12.0f
                view.binding.barChartCalib.data.notifyDataChanged()
                view.binding.barChartCalib.notifyDataSetChanged()
                view.binding.barChartCalib.invalidate()
            } else {
                barDataSet = BarDataSet(chartValue, "1khz.value(dB)")
                barDataSet.setDrawIcons(false)
                barDataSet.setGradientColor(Color.GREEN, Color.RED)
                barDataSet.setDrawValues(false)
                barDataSet.formLineWidth = 1f
                val data = BarData(barDataSet)
                view.binding.barChartCalib.data = data
                view.binding.barChartCalib.invalidate()
            }
        }
    }

    private fun sendAudioMessage() {
        val msg = Message()
        msg.what = MSGProtocol.MSG_SEND.value
        msg.obj =
            view.binding.spMicNo.selectedItem.toString() + "/" + spldB.toString() + "/" + rmsValues.toList()
                .toString()

        handler.sendMessage(msg)
    }


    private fun addDequeAndAvg() {
        removeArrayDequeFirst()
        addArrayDeque()
        LowFreqAvg()
    }

    private fun addArrayDeque() {
        for (i in arrayDequeList.indices) {
            arrayDequeList[i].add(rmsValues[i])
        }
    }

    private fun removeArrayDequeFirst() {
        for (i in arrayDequeList.indices) {
            arrayDequeList[i].removeFirst()
        }
    }

    private fun LowFreqAvg() {
        for (i in arrayDequeList.indices) {
            rmsValues[i] = arrayDequeList[i].average()
        }
    }


    private fun initialDeque() {
        var count = 0
        for (i in 1..velocity) {
            count++
            addArrayDeque()
        }
    }

    private fun updateChart(rmsValues: DoubleArray) {
        val barValues = ArrayList<BarEntry>()
        val lineValues = ArrayList<Entry>()
        for (i in 0 until 31) {
            barValues.add(BarEntry(i.toFloat(), rmsValues[i].toFloat()))
            lineValues.add(Entry(i.toFloat(), rmsValues[i].toFloat()))
        }

        val barDataset = BarDataSet(barValues, "실시간 스펙트럼")
        barDataset.setDrawIcons(false)
        barDataset.setGradientColor(Color.GREEN, Color.RED)
        barDataset.setDrawValues(false)
        view.binding.chartRms.description.isEnabled = false
        barDataset.formLineWidth = 1f
        val data = BarData(barDataset)
        // set data
        view.binding.chartRms.data = data
        view.binding.chartRms.invalidate()

        val lineDataSet1 = LineDataSet(lineValues, "실시간 스펙트럼")
        lineDataSet1.color = loadMicColor()
        lineDataSet1.setDrawCircles(false)
        lineDataSet1.lineWidth = 2f
        lineDataSet1.setDrawValues(false)
        lineDataSet1.mode = LineDataSet.Mode.CUBIC_BEZIER

        val data2 = LineData(lineDataSet1)
        view.binding.chartLine.data = data2
        view.binding.chartLine.data.notifyDataChanged()
        view.binding.chartLine.notifyDataSetChanged()
        view.binding.chartLine.invalidate()


    }

    private fun loadMicColor(): Int {
        val micNo = view.binding.spMicNo.selectedItem.toString()
        var color = 1
        when (micNo) {
            "1" -> color = MIC1COLOR
            "2" -> color = MIC2COLOR
            "3" -> color = MIC3COLOR
            "4" -> color = MIC4COLOR
        }
        return color
    }


    private fun calculate_SPL(rmsValues: DoubleArray): Double {
        var sum = 0.0
        for (i in rmsValues) {
            sum += 10.0.pow((i / 10))
        }
        return ((10 + 10 * log10(sum / 10)) * 10).roundToInt() / 10.0
    }


    fun calculateFrequencyValues(toTransform: DoubleArray, frequencyRange: FrequencyRange8K) {
        val dbfsFinal = averageDbfs(toTransform, frequencyRange)
        rmsValues[frequencyRange.index] = dbfsFinal
    }

    private fun averageDbfs(toTransform: DoubleArray, frequencyRange: FrequencyRange8K): Double {
        var dbfs = 0.0
        for (j in frequencyRange.lowIndex..frequencyRange.highIndex) {
            dbfs += toTransformToDbfs(toTransform, j)
        }
        return dbfsAvg(dbfs)
    }

    private fun toTransformToDbfs(toTransform: DoubleArray, i: Int): Double {

        return Math.pow(
            10.0,
            Math.round((20.0 * Math.log10(abs(toTransform[i])) + 50.5 + CALIBRATION ) * 100.0) / 100.0 / 10
        ) // calib 수정
//        return Math.pow(
//            10.0,
//            Math.round((20.0 * Math.log10(abs(toTransform[i])) + 50.5 + CALIBRATION + readMicCalib(i)) * 100.0) / 100.0 / 10
//        ) // calib 수정

    }

    private fun dbfsAvg(dbfsSum: Double): Double {
        return Math.round(10.0 * log10(dbfsSum) * 100.0) / 100.0
    }

//    private fun readMicCalib(i: Int): Double {
//        val delta = calibData[i][1]
//        return delta.toDouble()

//    }

    companion object {
        var rmsValues = DoubleArray(31)
        var spldB = 0.0
    }


}



