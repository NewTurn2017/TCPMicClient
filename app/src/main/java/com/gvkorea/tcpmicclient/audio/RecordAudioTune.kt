package com.gvkorea.tcpmicclient.audio

import android.annotation.SuppressLint
import android.graphics.Color
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.AsyncTask
import android.os.Handler
import android.os.Message
import android.util.Log
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.*
import com.gvkorea.tcpmicclient.MainActivity
import com.gvkorea.tcpmicclient.MainActivity.Companion.CALIBRATION
import com.gvkorea.tcpmicclient.MainActivity.Companion.isCalib
import com.gvkorea.tcpmicclient.MainActivity.Companion.isStarted
import com.gvkorea.tcpmicclient.MainActivity.Companion.selectedMicName
import com.gvkorea.tcpmicclient.MainActivity.Companion.velocity
import com.gvkorea.tcpmicclient.fft.RealDoubleFFT
import com.gvkorea.tcpmicclient.utils.CSVRead
import com.gvkorea.tcpmicclient.utils.Frequency_8192
import com.gvkorea.tcpmicclient.utils.MSGProtocol
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.roundToInt

class RecordAudioTune(
    val view: MainActivity,
    val handler: Handler
) :
    AsyncTask<Unit, DoubleArray, Unit>() {

    var time = 0L
    var sendCount = 0

    var isTest = false

    var isLowFreqStarted = false
    private val frequency = 44100
    private val channelConfiguration = AudioFormat.CHANNEL_IN_MONO
    private val audioEncoding = AudioFormat.ENCODING_PCM_16BIT
    private val blockSize = 8192
    private val transformer = RealDoubleFFT(blockSize)
    private var toTransformAvg = DoubleArray(blockSize)
    private var chartValue = ArrayList<BarEntry>()
    private var barDataSet = BarDataSet(chartValue, null)
    private val MIC1COLOR = Color.RED
    private val MIC2COLOR = Color.BLUE
    private val MIC3COLOR = Color.BLACK
    private val MIC4COLOR = Color.MAGENTA

    val arrayDeque0 = ArrayDeque<Double>()
    val arrayDeque1 = ArrayDeque<Double>()
    val arrayDeque2 = ArrayDeque<Double>()
    val arrayDeque3 = ArrayDeque<Double>()
    val arrayDeque4 = ArrayDeque<Double>()
    val arrayDeque5 = ArrayDeque<Double>()
    val arrayDeque6 = ArrayDeque<Double>()
    val arrayDeque7 = ArrayDeque<Double>()
    val arrayDeque8 = ArrayDeque<Double>()
    val arrayDeque9 = ArrayDeque<Double>()
    val arrayDeque10 = ArrayDeque<Double>()
    val arrayDeque11 = ArrayDeque<Double>()
    val arrayDeque12 = ArrayDeque<Double>()
    val arrayDeque13 = ArrayDeque<Double>()
    val arrayDeque14 = ArrayDeque<Double>()
    val arrayDeque15 = ArrayDeque<Double>()
    val arrayDeque16 = ArrayDeque<Double>()
    val arrayDeque17 = ArrayDeque<Double>()
    val arrayDeque18 = ArrayDeque<Double>()
    val arrayDeque19 = ArrayDeque<Double>()
    val arrayDeque20 = ArrayDeque<Double>()
    val arrayDeque21 = ArrayDeque<Double>()
    val arrayDeque22 = ArrayDeque<Double>()
    val arrayDeque23 = ArrayDeque<Double>()
    val arrayDeque24 = ArrayDeque<Double>()
    val arrayDeque25 = ArrayDeque<Double>()
    val arrayDeque26 = ArrayDeque<Double>()
    val arrayDeque27 = ArrayDeque<Double>()
    val arrayDeque28 = ArrayDeque<Double>()
    val arrayDeque29 = ArrayDeque<Double>()
    val arrayDeque30 = ArrayDeque<Double>()


    var calibData = CSVRead(view).readCalibCsv(view.assets!!, selectedMicName)

    private var count = 0

    @SuppressLint("MissingPermission")
    override fun doInBackground(vararg p0: Unit?): Unit? {
        try {
            val bufferSize = AudioRecord.getMinBufferSize(
                frequency,
                channelConfiguration, audioEncoding
            )

            val audioRecord = AudioRecord(
                MediaRecorder.AudioSource.VOICE_PERFORMANCE, frequency,
                channelConfiguration, audioEncoding, bufferSize
            )


            val buffer = ShortArray(blockSize)
            val toTransform = DoubleArray(blockSize)

            audioRecord.startRecording()

            while (isStarted) {
                val bufferReadResult = audioRecord.read(buffer, 0, blockSize)

                var i = 0
                while (i < blockSize && i < bufferReadResult) {
                    toTransform[i] = buffer[i].toDouble() / java.lang.Short.MAX_VALUE // 32,768
                    i++
                }
                transformer.ft(toTransform)
                time = System.currentTimeMillis()
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

        chartValue = ArrayList()
        for (i in 0 until blockSize) {
            if (values[0][i] < 0) {
                toTransformAvg[i] = -values[0][i]
            } else {
                toTransformAvg[i] = values[0][i]
            }
        }

        var arrayNum = 0

        for (i in 1 until toTransformAvg.size) {

            when (i) {
                Frequency_8192.INDEX_20HZ.value -> {
                    freq_value_31(i, toTransformAvg, arrayNum)
                    arrayNum++
                }
                Frequency_8192.INDEX_25HZ.value -> {
                    freq_value_31(i, toTransformAvg, arrayNum)
                    arrayNum++
                }
                Frequency_8192.INDEX_32HZ.value -> {
                    freq_value_31(i, toTransformAvg, arrayNum)
                    arrayNum++
                }
                Frequency_8192.INDEX_40HZ.value -> {
                    freq_value_31(i, toTransformAvg, arrayNum)
                    arrayNum++
                }
                Frequency_8192.INDEX_50HZ.value -> {
                    freq_value_31(i, toTransformAvg, arrayNum)
                    arrayNum++
                }
                Frequency_8192.INDEX_63HZ.value -> {
                    freq_value_31(i, toTransformAvg, arrayNum)
                    arrayNum++
                }
                Frequency_8192.INDEX_80HZ.value -> {
                    freq_value_31(i, toTransformAvg, arrayNum)
                    arrayNum++
                }
                Frequency_8192.INDEX_100HZ.value -> {
                    freq_value_31(i, toTransformAvg, arrayNum)
                    arrayNum++
                }
                Frequency_8192.INDEX_125HZ.value -> {
                    freq_value_31(i, toTransformAvg, arrayNum)
                    arrayNum++
                }
                Frequency_8192.INDEX_160HZ.value -> {
                    freq_value_31(i, toTransformAvg, arrayNum)
                    arrayNum++
                }
                Frequency_8192.INDEX_200HZ.value -> {
                    freq_value_31(i, toTransformAvg, arrayNum)
                    arrayNum++
                }
                Frequency_8192.INDEX_250HZ.value -> {
                    freq_value_31(i, toTransformAvg, arrayNum)
                    arrayNum++
                }
                Frequency_8192.INDEX_315HZ.value -> {
                    freq_value_31(i, toTransformAvg, arrayNum)
                    arrayNum++
                }
                Frequency_8192.INDEX_400HZ.value -> {
                    freq_value_31(i, toTransformAvg, arrayNum)
                    arrayNum++
                }
                Frequency_8192.INDEX_500HZ.value -> {
                    freq_value_31(i, toTransformAvg, arrayNum)
                    arrayNum++
                }
                Frequency_8192.INDEX_630HZ.value -> {
                    freq_value_31(i, toTransformAvg, arrayNum)
                    arrayNum++
                }
                Frequency_8192.INDEX_800HZ.value -> {
                    freq_value_31(i, toTransformAvg, arrayNum)
                    arrayNum++
                }
                Frequency_8192.INDEX_1000HZ.value -> {
                    freq_value_31(i, toTransformAvg, arrayNum)
                    arrayNum++
                }
                Frequency_8192.INDEX_1250HZ.value -> {
                    freq_value_31(i, toTransformAvg, arrayNum)
                    arrayNum++
                }
                Frequency_8192.INDEX_1600HZ.value -> {
                    freq_value_31(i, toTransformAvg, arrayNum)
                    arrayNum++
                }
                Frequency_8192.INDEX_2000HZ.value -> {
                    freq_value_31(i, toTransformAvg, arrayNum)
                    arrayNum++
                }
                Frequency_8192.INDEX_2500HZ.value -> {
                    freq_value_31(i, toTransformAvg, arrayNum)
                    arrayNum++
                }
                Frequency_8192.INDEX_3150HZ.value -> {
                    freq_value_31(i, toTransformAvg, arrayNum)
                    arrayNum++
                }
                Frequency_8192.INDEX_4000HZ.value -> {
                    freq_value_31(i, toTransformAvg, arrayNum)
                    arrayNum++
                }
                Frequency_8192.INDEX_5000HZ.value -> {
                    freq_value_31(i, toTransformAvg, arrayNum)
                    arrayNum++
                }
                Frequency_8192.INDEX_6300HZ.value -> {
                    freq_value_31(i, toTransformAvg, arrayNum)
                    arrayNum++
                }
                Frequency_8192.INDEX_8000HZ.value -> {
                    freq_value_31(i, toTransformAvg, arrayNum)
                    arrayNum++
                }
                Frequency_8192.INDEX_10000HZ.value -> {
                    freq_value_31(i, toTransformAvg, arrayNum)
                    arrayNum++
                }
                Frequency_8192.INDEX_12500HZ.value -> {
                    freq_value_31(i, toTransformAvg, arrayNum)
                    arrayNum++
                }
                Frequency_8192.INDEX_16000HZ.value -> {
                    freq_value_31(i, toTransformAvg, arrayNum)
                    arrayNum++
                }
                Frequency_8192.INDEX_20000HZ.value -> {
                    freq_value_31(i, toTransformAvg, arrayNum)
                    arrayNum++
                }
            }

            if (arrayNum == 31) {
                count++
                if (!isCalib) {
                    if (count % 300 == 0) {
                        sendCount++
                        spldB = calculate_SPL(rmsValues)
                        view.binding.tvSPL.text = "SPL : $spldB dB"
                        if(view.isMeasureDeay) {
                            view.binding.tvDelay.text = getDelay(spldB)
                        }else {
                            view.binding.tvDelay.text = "None"
                        }

                        if (!isLowFreqStarted) {
                            initialDeque()
                            LowFreqAvg()
                            isLowFreqStarted = true
                        } else {
                            addDequeAndAvg()
                        }
                        sendAudioMessage()
                        updateChart(rmsValues)
                    }
                } else {
                    if (count % 300 == 0) {
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
            }
        }
    }

    private fun getDelay(spldB: Double): String {
        var result = ""
        val distance = getMicDistance(spldB)
        val delay = (distance / 0.342 * 100.0).roundToInt() / 100.0
        result += "Distance: ${(distance *100.0).roundToInt()/100.0} m \nDelay: ${delay} ms"
        return result
    }

    private fun getMicDistance(spldB: Double): Double {
        return 0.4f * 10.0.pow(-(spldB - 80.0) / 10)
    }

    private fun sendAudioMessage() {
        val msg = Message()
        msg.what = MSGProtocol.MSG_SEND.value
        msg.obj =
            view.binding.spMicNo.selectedItem.toString() + "/" + spldB.toString() + "/" + rmsValues.toList()
                .toString()

        handler.sendMessage(msg)
    }

    private fun checkTime() {
        val time2 = System.currentTimeMillis() - time
        val msg1 = Message()
        msg1.what = MSGProtocol.MSG_COUNT.value
        msg1.obj = time2.toString()
        handler.sendMessage(msg1)
    }

    private fun addDequeAndAvg() {
        removeArrayDequeFirst()
        addArrayDeque()
        LowFreqAvg()
    }

    private fun addArrayDeque() {
        arrayDeque0.add(rmsValues[0])
        arrayDeque1.add(rmsValues[1])
        arrayDeque2.add(rmsValues[2])
        arrayDeque3.add(rmsValues[3])
        arrayDeque4.add(rmsValues[4])
        arrayDeque5.add(rmsValues[5])
        arrayDeque6.add(rmsValues[6])
        arrayDeque7.add(rmsValues[7])
        arrayDeque8.add(rmsValues[8])
        arrayDeque9.add(rmsValues[9])
        arrayDeque10.add(rmsValues[10])
        arrayDeque11.add(rmsValues[11])
        arrayDeque12.add(rmsValues[12])
        arrayDeque13.add(rmsValues[13])
        arrayDeque14.add(rmsValues[14])
        arrayDeque15.add(rmsValues[15])
        arrayDeque16.add(rmsValues[16])
        arrayDeque17.add(rmsValues[17])
        arrayDeque18.add(rmsValues[18])
        arrayDeque19.add(rmsValues[19])
        arrayDeque20.add(rmsValues[20])
        arrayDeque21.add(rmsValues[21])
        arrayDeque22.add(rmsValues[22])
        arrayDeque23.add(rmsValues[23])
        arrayDeque24.add(rmsValues[24])
        arrayDeque25.add(rmsValues[25])
        arrayDeque26.add(rmsValues[26])
        arrayDeque27.add(rmsValues[27])
        arrayDeque28.add(rmsValues[28])
        arrayDeque29.add(rmsValues[29])
        arrayDeque30.add(rmsValues[30])
    }

    private fun removeArrayDequeFirst() {
        arrayDeque0.removeFirst()
        arrayDeque1.removeFirst()
        arrayDeque2.removeFirst()
        arrayDeque3.removeFirst()
        arrayDeque4.removeFirst()
        arrayDeque5.removeFirst()
        arrayDeque6.removeFirst()
        arrayDeque7.removeFirst()
        arrayDeque8.removeFirst()
        arrayDeque9.removeFirst()
        arrayDeque10.removeFirst()
        arrayDeque11.removeFirst()
        arrayDeque12.removeFirst()
        arrayDeque13.removeFirst()
        arrayDeque14.removeFirst()
        arrayDeque15.removeFirst()
        arrayDeque16.removeFirst()
        arrayDeque17.removeFirst()
        arrayDeque18.removeFirst()
        arrayDeque19.removeFirst()
        arrayDeque20.removeFirst()
        arrayDeque21.removeFirst()
        arrayDeque22.removeFirst()
        arrayDeque23.removeFirst()
        arrayDeque24.removeFirst()
        arrayDeque25.removeFirst()
        arrayDeque26.removeFirst()
        arrayDeque27.removeFirst()
        arrayDeque28.removeFirst()
        arrayDeque29.removeFirst()
        arrayDeque30.removeFirst()
    }

    private fun LowFreqAvg() {
        rmsValues[0] = arrayDeque0.average()
        rmsValues[1] = arrayDeque1.average()
        rmsValues[2] = arrayDeque2.average()
        rmsValues[3] = arrayDeque3.average()
        rmsValues[4] = arrayDeque4.average()
        rmsValues[5] = arrayDeque5.average()
        rmsValues[6] = arrayDeque6.average()
        rmsValues[7] = arrayDeque7.average()
        rmsValues[8] = arrayDeque8.average()
        rmsValues[9] = arrayDeque9.average()
        rmsValues[10] = arrayDeque10.average()
        rmsValues[11] = arrayDeque11.average()
        rmsValues[12] = arrayDeque12.average()
        rmsValues[13] = arrayDeque13.average()
        rmsValues[14] = arrayDeque14.average()
        rmsValues[15] = arrayDeque15.average()
        rmsValues[16] = arrayDeque16.average()
        rmsValues[17] = arrayDeque17.average()
        rmsValues[18] = arrayDeque18.average()
        rmsValues[19] = arrayDeque19.average()
        rmsValues[20] = arrayDeque20.average()
        rmsValues[21] = arrayDeque21.average()
        rmsValues[22] = arrayDeque22.average()
        rmsValues[23] = arrayDeque23.average()
        rmsValues[24] = arrayDeque24.average()
        rmsValues[25] = arrayDeque25.average()
        rmsValues[26] = arrayDeque26.average()
        rmsValues[27] = arrayDeque27.average()
        rmsValues[28] = arrayDeque28.average()
        rmsValues[29] = arrayDeque29.average()
        rmsValues[30] = arrayDeque30.average()
    }


    private fun initialDeque() {

        for (i in 1..velocity) {
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

    private fun doubleToString(value: Double): String {
        val format = DecimalFormat("##.#")
        return format.format(value)
    }


    fun freq_value_31(i: Int, toTransform: DoubleArray, arrayNum: Int) {
        val dbfsFinal = freqAverageCalcForOneOctave_31(i, toTransform)
        rmsValues[arrayNum] = dbfsFinal
    }

    private fun freqAverageCalcForOneOctave_31(i: Int, toTransform: DoubleArray): Double {
        var dbfs = 0.0
        var dbfsFinal = 0.0

        when (i) {
            Frequency_8192.INDEX_20HZ.value -> {
                for (j in Frequency_8192.LOW_20HZ.value..Frequency_8192.HIGH_20HZ.value) {
                    dbfs += toTransfromTodbfs(toTransform, j)
                }
                dbfsFinal = dbfsAvg(dbfs)
            }
            Frequency_8192.INDEX_25HZ.value -> {
                for (j in Frequency_8192.LOW_25HZ.value..Frequency_8192.HIGH_25HZ.value) {
                    dbfs += toTransfromTodbfs(toTransform, j)
                }
                dbfsFinal = dbfsAvg(dbfs)
            }
            Frequency_8192.INDEX_32HZ.value -> {
                for (j in Frequency_8192.LOW_32HZ.value..Frequency_8192.HIGH_32HZ.value) {
                    dbfs += toTransfromTodbfs(toTransform, j)
                }
                dbfsFinal = dbfsAvg(dbfs)
            }
            Frequency_8192.INDEX_40HZ.value -> {
                for (j in Frequency_8192.LOW_40HZ.value..Frequency_8192.HIGH_40HZ.value) {
                    dbfs += toTransfromTodbfs(toTransform, j)
                }
                dbfsFinal = dbfsAvg(dbfs)
            }
            Frequency_8192.INDEX_50HZ.value -> {
                for (j in Frequency_8192.LOW_50HZ.value..Frequency_8192.HIGH_50HZ.value) {
                    dbfs += toTransfromTodbfs(toTransform, j)
                }
                dbfsFinal = dbfsAvg(dbfs)
            }
            Frequency_8192.INDEX_63HZ.value -> {
                for (j in Frequency_8192.LOW_63HZ.value..Frequency_8192.HIGH_63HZ.value) {
                    dbfs += toTransfromTodbfs(toTransform, j)
                }
                dbfsFinal = dbfsAvg(dbfs)
            }
            Frequency_8192.INDEX_80HZ.value -> {
                for (j in Frequency_8192.LOW_80HZ.value..Frequency_8192.HIGH_80HZ.value) {
                    dbfs += toTransfromTodbfs(toTransform, j)
                }
                dbfsFinal = dbfsAvg(dbfs)
            }
            Frequency_8192.INDEX_100HZ.value -> {
                for (j in Frequency_8192.LOW_100HZ.value..Frequency_8192.HIGH_100HZ.value) {
                    dbfs += toTransfromTodbfs(toTransform, j)
                }
                dbfsFinal = dbfsAvg(dbfs)
            }
            Frequency_8192.INDEX_125HZ.value -> {
                for (j in Frequency_8192.LOW_125HZ.value..Frequency_8192.HIGH_125HZ.value) {
                    dbfs += toTransfromTodbfs(toTransform, j)
                }
                dbfsFinal = dbfsAvg(dbfs)
            }
            Frequency_8192.INDEX_160HZ.value -> {
                for (j in Frequency_8192.LOW_160HZ.value..Frequency_8192.HIGH_160HZ.value) {
                    dbfs += toTransfromTodbfs(toTransform, j)
                }
                dbfsFinal = dbfsAvg(dbfs)
            }
            Frequency_8192.INDEX_200HZ.value -> {
                for (j in Frequency_8192.LOW_200HZ.value..Frequency_8192.HIGH_200HZ.value) {
                    dbfs += toTransfromTodbfs(toTransform, j)
                }
                dbfsFinal = dbfsAvg(dbfs)
            }
            Frequency_8192.INDEX_250HZ.value -> {
                for (j in Frequency_8192.LOW_250HZ.value..Frequency_8192.HIGH_250HZ.value) {
                    dbfs += toTransfromTodbfs(toTransform, j)
                }
                dbfsFinal = dbfsAvg(dbfs)
            }
            Frequency_8192.INDEX_315HZ.value -> {
                for (j in Frequency_8192.LOW_315HZ.value..Frequency_8192.HIGH_315HZ.value) {
                    dbfs += toTransfromTodbfs(toTransform, j)
                }
                dbfsFinal = dbfsAvg(dbfs)
            }
            Frequency_8192.INDEX_400HZ.value -> {
                for (j in Frequency_8192.LOW_400HZ.value..Frequency_8192.HIGH_400HZ.value) {
                    dbfs += toTransfromTodbfs(toTransform, j)
                }
                dbfsFinal = dbfsAvg(dbfs)
            }
            Frequency_8192.INDEX_500HZ.value -> {
                for (j in Frequency_8192.LOW_500HZ.value..Frequency_8192.HIGH_500HZ.value) {
                    dbfs += toTransfromTodbfs(toTransform, j)
                }
                dbfsFinal = dbfsAvg(dbfs)
            }
            Frequency_8192.INDEX_630HZ.value -> {
                for (j in Frequency_8192.LOW_630HZ.value..Frequency_8192.HIGH_630HZ.value) {
                    dbfs += toTransfromTodbfs(toTransform, j)
                }
                dbfsFinal = dbfsAvg(dbfs)
            }
            Frequency_8192.INDEX_800HZ.value -> {
                for (j in Frequency_8192.LOW_800HZ.value..Frequency_8192.HIGH_800HZ.value) {
                    dbfs += toTransfromTodbfs(toTransform, j)
                }
                dbfsFinal = dbfsAvg(dbfs)
            }
            Frequency_8192.INDEX_1000HZ.value -> {
                for (j in Frequency_8192.LOW_1000HZ.value..Frequency_8192.HIGH_1000HZ.value) {
                    dbfs += toTransfromTodbfs(toTransform, j)
                }
                dbfsFinal = dbfsAvg(dbfs)
            }
            Frequency_8192.INDEX_1250HZ.value -> {
                for (j in Frequency_8192.LOW_1250HZ.value..Frequency_8192.HIGH_1250HZ.value) {
                    dbfs += toTransfromTodbfs(toTransform, j)
                }
                dbfsFinal = dbfsAvg(dbfs)
            }
            Frequency_8192.INDEX_1600HZ.value -> {
                for (j in Frequency_8192.LOW_1600HZ.value..Frequency_8192.HIGH_1600HZ.value) {
                    dbfs += toTransfromTodbfs(toTransform, j)
                }
                dbfsFinal = dbfsAvg(dbfs)
            }
            Frequency_8192.INDEX_2000HZ.value -> {
                for (j in Frequency_8192.LOW_2000HZ.value..Frequency_8192.HIGH_2000HZ.value) {
                    dbfs += toTransfromTodbfs(toTransform, j)
                }
                dbfsFinal = dbfsAvg(dbfs)
            }
            Frequency_8192.INDEX_2500HZ.value -> {
                for (j in Frequency_8192.LOW_2500HZ.value..Frequency_8192.HIGH_2500HZ.value) {
                    dbfs += toTransfromTodbfs(toTransform, j)
                }
                dbfsFinal = dbfsAvg(dbfs)
            }
            Frequency_8192.INDEX_3150HZ.value -> {
                for (j in Frequency_8192.LOW_3150HZ.value..Frequency_8192.HIGH_3150HZ.value) {
                    dbfs += toTransfromTodbfs(toTransform, j)
                }
                dbfsFinal = dbfsAvg(dbfs)
            }
            Frequency_8192.INDEX_4000HZ.value -> {
                for (j in Frequency_8192.LOW_4000HZ.value..Frequency_8192.HIGH_4000HZ.value) {
                    dbfs += toTransfromTodbfs(toTransform, j)
                }
                dbfsFinal = dbfsAvg(dbfs)
            }
            Frequency_8192.INDEX_5000HZ.value -> {
                for (j in Frequency_8192.LOW_5000HZ.value..Frequency_8192.HIGH_5000HZ.value) {
                    dbfs += toTransfromTodbfs(toTransform, j)
                }
                dbfsFinal = dbfsAvg(dbfs)
            }
            Frequency_8192.INDEX_6300HZ.value -> {
                for (j in Frequency_8192.LOW_6300HZ.value..Frequency_8192.HIGH_6300HZ.value) {
                    dbfs += toTransfromTodbfs(toTransform, j)
                }
                dbfsFinal = dbfsAvg(dbfs)
            }
            Frequency_8192.INDEX_8000HZ.value -> {
                for (j in Frequency_8192.LOW_8000HZ.value..Frequency_8192.HIGH_8000HZ.value) {
                    dbfs += toTransfromTodbfs(toTransform, j)
                }
                dbfsFinal = dbfsAvg(dbfs)
            }
            Frequency_8192.INDEX_10000HZ.value -> {
                for (j in Frequency_8192.LOW_10000HZ.value..Frequency_8192.HIGH_10000HZ.value) {
                    dbfs += toTransfromTodbfs(toTransform, j)
                }
                dbfsFinal = dbfsAvg(dbfs)
            }
            Frequency_8192.INDEX_12500HZ.value -> {
                for (j in Frequency_8192.LOW_12500HZ.value..Frequency_8192.HIGH_12500HZ.value) {
                    dbfs += toTransfromTodbfs(toTransform, j)
                }
                dbfsFinal = dbfsAvg(dbfs)
            }
            Frequency_8192.INDEX_16000HZ.value -> {
                for (j in Frequency_8192.LOW_16000HZ.value..Frequency_8192.HIGH_16000HZ.value) {
                    dbfs += toTransfromTodbfs(toTransform, j)
                }
                dbfsFinal = dbfsAvg(dbfs)
            }
            Frequency_8192.INDEX_20000HZ.value -> {
                for (j in Frequency_8192.LOW_20000HZ.value..Frequency_8192.HIGH_20000HZ.value) {
                    dbfs += toTransfromTodbfs(toTransform, j)
                }
                dbfsFinal = dbfsAvg(dbfs)
            }
        }
        return dbfsFinal
    }

    private fun toTransfromTodbfs(toTransform: DoubleArray, i: Int): Double {

        return Math.pow(
            10.0,
            Math.round((20.0 * Math.log10(toTransform[i]) + 50.5 + CALIBRATION + readMicCalib(i)) * 100.0) / 100.0 / 10
        ) // calib 수정

    }

    private fun dbfsAvg(dbfsSum: Double): Double {
        return Math.round(10.0 * log10(dbfsSum) * 100.0) / 100.0
    }

    private fun readMicCalib(i: Int): Double {

        val delta = calibData[i][1]
        return delta.toDouble()
    }

    companion object {
        var rmsValues = DoubleArray(31)
        var spldB = 0.0
    }


}


