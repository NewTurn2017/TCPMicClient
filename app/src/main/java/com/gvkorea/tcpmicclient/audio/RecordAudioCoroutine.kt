package com.gvkorea.tcpmicclient.audio

import android.annotation.SuppressLint
import android.graphics.Color
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import com.github.mikephil.charting.data.*
import com.gvkorea.tcpmicclient.MainActivity
import com.gvkorea.tcpmicclient.fft.RealDoubleFFT
import com.gvkorea.tcpmicclient.utils.CSVRead
import com.gvkorea.tcpmicclient.utils.Frequency_8192
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.roundToInt

class RecordAudioCoroutine(val view: MainActivity) {

    private var count = 0

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


    @SuppressLint("MissingPermission")
    fun startAudioRecord() {
        CoroutineScope(Dispatchers.Default).launch {
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
                while (MainActivity.isStarted) {
                    val bufferReadResult = audioRecord.read(buffer, 0, blockSize)

                    var i = 0
                    while (i < blockSize && i < bufferReadResult) {
                        toTransform[i] = buffer[i].toDouble() / java.lang.Short.MAX_VALUE // 32,768
                        i++
                    }
                    transformer.ft(toTransform)
                    publishProgress(toTransform)
                }
                audioRecord.stop()
                audioRecord.release()

            } catch (e: Throwable) {
                Log.e("AudioRecord", "Recording Failed")
            }
        }
    }

    private fun publishProgress(values: DoubleArray) {
        CoroutineScope(Dispatchers.Default).launch {
            chartValue = ArrayList()
            for (i in 0 until blockSize) {
                if (values[i] < 0) {
                    toTransformAvg[i] = -values[i]
                } else {
                    toTransformAvg[i] = values[i]
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
                    if (!MainActivity.isCalib) {
                        if (count % 300 == 0) {
                            CoroutineScope(Dispatchers.Main).launch {
                                RecordAudioTune.spldB = calculate_SPL(RecordAudioTune.rmsValues)
                                view.binding.tvSPL.text = "SPL : ${RecordAudioTune.spldB} dB"
                                updateChart(RecordAudioTune.rmsValues)
                            }

                        }
                    } else {
                        if (count % 300 == 0) {
                            CoroutineScope(Dispatchers.Main).launch {
                                RecordAudioTune.spldB = (RecordAudioTune.rmsValues[17] * 10.0).roundToInt() / 10.0 // 1khz.value
                                view.binding.tvSPL.text = "1khz.value : ${spldB} dB"
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
        }
    }

    private fun calculate_SPL(rmsValues: DoubleArray): Double {
        var sum = 0.0
        for (i in rmsValues) {
            sum += 10.0.pow((i / 10))
        }
        return ((10 + 10 * log10(sum / 10)) * 10).roundToInt() / 10.0
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

    private fun freq_value_31(i: Int, toTransform: DoubleArray, arrayNum: Int) {
        val dbfsFinal = freqAverageCalcForOneOctave_31(i, toTransform)
        RecordAudioTune.rmsValues[arrayNum] = dbfsFinal
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
            Math.round((20.0 * Math.log10(toTransform[i]) + 50.5 + MainActivity.CALIBRATION ) * 100.0) / 100.0 / 10
        ) // calib 수정

    }

    private fun dbfsAvg(dbfsSum: Double): Double {
        return Math.round(10.0 * log10(dbfsSum) * 100.0) / 100.0
    }

    companion object {
        var rmsValues = DoubleArray(31)
        var spldB = 0.0
    }

}