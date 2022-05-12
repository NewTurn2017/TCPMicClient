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
import com.gvkorea.tcpmicclient.fft.RealDoubleFFT
import com.gvkorea.tcpmicclient.utils.CSVRead
import com.gvkorea.tcpmicclient.utils.MSGProtocol
import java.text.DecimalFormat
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.roundToInt

class RecordAudioTune(
    val view: MainActivity,
    val barChart: BarChart,
    val calbBarChart: BarChart,
    val handler: Handler
) :
    AsyncTask<Unit, DoubleArray, Unit>() {

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

    var calibData = CSVRead(view).readCalibCsv(view.assets!!, selectedMicName)

    @SuppressLint("UseRequireInsteadOfGet")

    ////block size = 8192

    private val INDEX_20HZ = 7
    private val INDEX_25HZ = 9
    private val INDEX_32HZ = 11
    private val INDEX_40HZ = 14
    private val INDEX_50HZ = 18
    private val INDEX_63HZ = 23
    private val INDEX_80HZ = 29
    private val INDEX_100HZ = 37
    private val INDEX_125HZ = 46
    private val INDEX_160HZ = 59
    private val INDEX_200HZ = 74
    private val INDEX_250HZ = 92
    private val INDEX_315HZ = 117
    private val INDEX_400HZ = 148
    private val INDEX_500HZ = 185
    private val INDEX_630HZ = 234
    private val INDEX_800HZ = 297
    private val INDEX_1000HZ = 371
    private val INDEX_1250HZ = 464
    private val INDEX_1600HZ = 594
    private val INDEX_2000HZ = 743
    private val INDEX_2500HZ = 928
    private val INDEX_3150HZ = 1170
    private val INDEX_4000HZ = 1486
    private val INDEX_5000HZ = 1857
    private val INDEX_6300HZ = 2340
    private val INDEX_8000HZ = 2972
    private val INDEX_10000HZ = 3715
    private val INDEX_12500HZ = 4643
    private val INDEX_16000HZ = 5944
    private val INDEX_20000HZ = 7430


    private val LOW_20HZ = 6
    private val LOW_25HZ = 8
    private val LOW_32HZ = 10
    private val LOW_40HZ = 13
    private val LOW_50HZ = 16
    private val LOW_63HZ = 20
    private val LOW_80HZ = 26
    private val LOW_100HZ = 32
    private val LOW_125HZ = 41
    private val LOW_160HZ = 52
    private val LOW_200HZ = 65
    private val LOW_250HZ = 82
    private val LOW_315HZ = 104
    private val LOW_400HZ = 131
    private val LOW_500HZ = 165
    private val LOW_630HZ = 208
    private val LOW_800HZ = 262
    private val LOW_1000HZ = 330
    private val LOW_1250HZ = 417
    private val LOW_1600HZ = 525
    private val LOW_2000HZ = 661
    private val LOW_2500HZ = 834
    private val LOW_3150HZ = 1050
    private val LOW_4000HZ = 1323
    private val LOW_5000HZ = 1668
    private val LOW_6300HZ = 2101
    private val LOW_8000HZ = 2647
    private val LOW_10000HZ = 3336
    private val LOW_12500HZ = 4203
    private val LOW_16000HZ = 5295
    private val LOW_20000HZ = 6672

    private val HIGH_20HZ = 8
    private val HIGH_25HZ = 10
    private val HIGH_32HZ = 13
    private val HIGH_40HZ = 16
    private val HIGH_50HZ = 20
    private val HIGH_63HZ = 26
    private val HIGH_80HZ = 32
    private val HIGH_100HZ = 41
    private val HIGH_125HZ = 52
    private val HIGH_160HZ = 65
    private val HIGH_200HZ = 82
    private val HIGH_250HZ = 104
    private val HIGH_315HZ = 131
    private val HIGH_400HZ = 165
    private val HIGH_500HZ = 208
    private val HIGH_630HZ = 262
    private val HIGH_800HZ = 330
    private val HIGH_1000HZ = 417
    private val HIGH_1250HZ = 525
    private val HIGH_1600HZ = 661
    private val HIGH_2000HZ = 834
    private val HIGH_2500HZ = 1050
    private val HIGH_3150HZ = 1323
    private val HIGH_4000HZ = 1668
    private val HIGH_5000HZ = 2101
    private val HIGH_6300HZ = 2647
    private val HIGH_8000HZ = 3336
    private val HIGH_10000HZ = 4203
    private val HIGH_12500HZ = 5295
    private val HIGH_16000HZ = 6672
    private val HIGH_20000HZ = 8191

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

            if (i == INDEX_20HZ) {
                freq_value_31(i, toTransformAvg, arrayNum)
                arrayNum++
            } else if (i == INDEX_25HZ) {
                freq_value_31(i, toTransformAvg, arrayNum)
                arrayNum++
            } else if (i == INDEX_32HZ) {
                freq_value_31(i, toTransformAvg, arrayNum)
                arrayNum++
            } else if (i == INDEX_40HZ) {
                freq_value_31(i, toTransformAvg, arrayNum)
                arrayNum++
            } else if (i == INDEX_50HZ) {
                freq_value_31(i, toTransformAvg, arrayNum)
                arrayNum++
            } else if (i == INDEX_63HZ) {
                freq_value_31(i, toTransformAvg, arrayNum)
                arrayNum++
            } else if (i == INDEX_80HZ) {
                freq_value_31(i, toTransformAvg, arrayNum)
                arrayNum++
            } else if (i == INDEX_100HZ) {
                freq_value_31(i, toTransformAvg, arrayNum)
                arrayNum++
            } else if (i == INDEX_125HZ) {
                freq_value_31(i, toTransformAvg, arrayNum)
                arrayNum++
            } else if (i == INDEX_160HZ) {
                freq_value_31(i, toTransformAvg, arrayNum)
                arrayNum++
            } else if (i == INDEX_200HZ) {
                freq_value_31(i, toTransformAvg, arrayNum)
                arrayNum++
            } else if (i == INDEX_250HZ) {
                freq_value_31(i, toTransformAvg, arrayNum)
                arrayNum++
            } else if (i == INDEX_315HZ) {
                freq_value_31(i, toTransformAvg, arrayNum)
                arrayNum++
            } else if (i == INDEX_400HZ) {
                freq_value_31(i, toTransformAvg, arrayNum)
                arrayNum++
            } else if (i == INDEX_500HZ) {
                freq_value_31(i, toTransformAvg, arrayNum)
                arrayNum++
            } else if (i == INDEX_630HZ) {
                freq_value_31(i, toTransformAvg, arrayNum)
                arrayNum++
            } else if (i == INDEX_800HZ) {
                freq_value_31(i, toTransformAvg, arrayNum)
                arrayNum++
            } else if (i == INDEX_1000HZ) {
                freq_value_31(i, toTransformAvg, arrayNum)
                arrayNum++
            } else if (i == INDEX_1250HZ) {
                freq_value_31(i, toTransformAvg, arrayNum)
                arrayNum++
            } else if (i == INDEX_1600HZ) {
                freq_value_31(i, toTransformAvg, arrayNum)
                arrayNum++
            } else if (i == INDEX_2000HZ) {
                freq_value_31(i, toTransformAvg, arrayNum)
                arrayNum++
            } else if (i == INDEX_2500HZ) {
                freq_value_31(i, toTransformAvg, arrayNum)
                arrayNum++
            } else if (i == INDEX_3150HZ) {
                freq_value_31(i, toTransformAvg, arrayNum)
                arrayNum++
            } else if (i == INDEX_4000HZ) {
                freq_value_31(i, toTransformAvg, arrayNum)
                arrayNum++
            } else if (i == INDEX_5000HZ) {
                freq_value_31(i, toTransformAvg, arrayNum)
                arrayNum++
            } else if (i == INDEX_6300HZ) {
                freq_value_31(i, toTransformAvg, arrayNum)
                arrayNum++
            } else if (i == INDEX_8000HZ) {
                freq_value_31(i, toTransformAvg, arrayNum)
                arrayNum++
            } else if (i == INDEX_10000HZ) {
                freq_value_31(i, toTransformAvg, arrayNum)
                arrayNum++
            } else if (i == INDEX_12500HZ) {
                freq_value_31(i, toTransformAvg, arrayNum)
                arrayNum++
            } else if (i == INDEX_16000HZ) {
                freq_value_31(i, toTransformAvg, arrayNum)
                arrayNum++
            } else if (i == INDEX_20000HZ) {
                freq_value_31(i, toTransformAvg, arrayNum)
                arrayNum++
            }

            if (arrayNum == 31) {
                count++
                if (!isCalib) {
                    spldB = calculate_SPL(rmsValues)
                    view.binding.tvSPL.text = "SPL : $spldB dB"

                    if (count % 200 == 0) {
                        val msg = Message()
                        msg.what = MSGProtocol.MSG_SEND.value
                        msg.obj =
                            view.binding.spMicNo.selectedItem.toString() + "/" + spldB.toString() + "/" + rmsValues.toList()
                                .toString()

                        handler.sendMessage(msg)
                        updateChart(rmsValues)

                    }


                } else {
                    spldB = (rmsValues[17] * 10.0).roundToInt() /10.0 // 1khz
                    view.binding.tvSPL.text = "1khz : $spldB dB"

                    if (count % 200 == 0) {
                        if (calbBarChart.data != null && calbBarChart.data.dataSetCount > 0) {
                            chartValue.add(BarEntry(0f, spldB.toFloat()))
                            barDataSet.values = chartValue
                            barDataSet.setDrawValues(true)
                            barDataSet.valueTextColor = Color.RED
                            barDataSet.valueTextSize = 12.0f
                            calbBarChart.data.notifyDataChanged()
                            calbBarChart.notifyDataSetChanged()
                            calbBarChart.invalidate()

                        } else {
                            barDataSet = BarDataSet(chartValue, "1khz(dB)")
                            barDataSet.setDrawIcons(false)
                            barDataSet.setGradientColor(Color.GREEN, Color.RED)
                            barDataSet.setDrawValues(false)
                            barDataSet.formLineWidth = 1f
                            val data = BarData(barDataSet)
                            calbBarChart.data = data
                            calbBarChart.invalidate()
                        }
                    }

                }
            }
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
        barChart.description.isEnabled = false
        barDataset.formLineWidth = 1f
        val data = BarData(barDataset)
        // set data
        barChart.data = data
        barChart.invalidate()

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
        when(micNo){
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

        if (i == INDEX_20HZ) {
            for (j in LOW_20HZ..HIGH_20HZ) {
                dbfs += toTransfromTodbfs(toTransform, j)
            }
            dbfsFinal = dbfsAvg(dbfs)
        } else if (i == INDEX_25HZ) {
            for (j in LOW_25HZ..HIGH_25HZ) {
                dbfs += toTransfromTodbfs(toTransform, j)
            }
            dbfsFinal = dbfsAvg(dbfs)
        } else if (i == INDEX_32HZ) {
            for (j in LOW_32HZ..HIGH_32HZ) {
                dbfs += toTransfromTodbfs(toTransform, j)
            }
            dbfsFinal = dbfsAvg(dbfs)
        } else if (i == INDEX_40HZ) {
            for (j in LOW_40HZ..HIGH_40HZ) {
                dbfs += toTransfromTodbfs(toTransform, j)
            }
            dbfsFinal = dbfsAvg(dbfs)
        } else if (i == INDEX_50HZ) {
            for (j in LOW_50HZ..HIGH_50HZ) {
                dbfs += toTransfromTodbfs(toTransform, j)
            }
            dbfsFinal = dbfsAvg(dbfs)
        } else if (i == INDEX_63HZ) {
            for (j in LOW_63HZ..HIGH_63HZ) {
                dbfs += toTransfromTodbfs(toTransform, j)
            }
            dbfsFinal = dbfsAvg(dbfs)
        } else if (i == INDEX_80HZ) {
            for (j in LOW_80HZ..HIGH_80HZ) {
                dbfs += toTransfromTodbfs(toTransform, j)
            }
            dbfsFinal = dbfsAvg(dbfs)
        } else if (i == INDEX_100HZ) {
            for (j in LOW_100HZ..HIGH_100HZ) {
                dbfs += toTransfromTodbfs(toTransform, j)
            }
            dbfsFinal = dbfsAvg(dbfs)
        } else if (i == INDEX_125HZ) {
            for (j in LOW_125HZ..HIGH_125HZ) {
                dbfs += toTransfromTodbfs(toTransform, j)
            }
            dbfsFinal = dbfsAvg(dbfs)
        } else if (i == INDEX_160HZ) {
            for (j in LOW_160HZ..HIGH_160HZ) {
                dbfs += toTransfromTodbfs(toTransform, j)
            }
            dbfsFinal = dbfsAvg(dbfs)
        } else if (i == INDEX_200HZ) {
            for (j in LOW_200HZ..HIGH_200HZ) {
                dbfs += toTransfromTodbfs(toTransform, j)
            }
            dbfsFinal = dbfsAvg(dbfs)
        } else if (i == INDEX_250HZ) {
            for (j in LOW_250HZ..HIGH_250HZ) {
                dbfs += toTransfromTodbfs(toTransform, j)
            }
            dbfsFinal = dbfsAvg(dbfs)
        } else if (i == INDEX_315HZ) {
            for (j in LOW_315HZ..HIGH_315HZ) {
                dbfs += toTransfromTodbfs(toTransform, j)
            }
            dbfsFinal = dbfsAvg(dbfs)
        } else if (i == INDEX_400HZ) {
            for (j in LOW_400HZ..HIGH_400HZ) {
                dbfs += toTransfromTodbfs(toTransform, j)
            }
            dbfsFinal = dbfsAvg(dbfs)
        } else if (i == INDEX_500HZ) {
            for (j in LOW_500HZ..HIGH_500HZ) {
                dbfs += toTransfromTodbfs(toTransform, j)
            }
            dbfsFinal = dbfsAvg(dbfs)
        } else if (i == INDEX_630HZ) {
            for (j in LOW_630HZ..HIGH_630HZ) {
                dbfs += toTransfromTodbfs(toTransform, j)
            }
            dbfsFinal = dbfsAvg(dbfs)
        } else if (i == INDEX_800HZ) {
            for (j in LOW_800HZ..HIGH_800HZ) {
                dbfs += toTransfromTodbfs(toTransform, j)
            }
            dbfsFinal = dbfsAvg(dbfs)
        } else if (i == INDEX_1000HZ) {
            for (j in LOW_1000HZ..HIGH_1000HZ) {
                dbfs += toTransfromTodbfs(toTransform, j)
            }
            dbfsFinal = dbfsAvg(dbfs)
        } else if (i == INDEX_1250HZ) {
            for (j in LOW_1250HZ..HIGH_1250HZ) {
                dbfs += toTransfromTodbfs(toTransform, j)
            }
            dbfsFinal = dbfsAvg(dbfs)
        } else if (i == INDEX_1600HZ) {
            for (j in LOW_1600HZ..HIGH_1600HZ) {
                dbfs += toTransfromTodbfs(toTransform, j)
            }
            dbfsFinal = dbfsAvg(dbfs)
        } else if (i == INDEX_2000HZ) {
            for (j in LOW_2000HZ..HIGH_2000HZ) {
                dbfs += toTransfromTodbfs(toTransform, j)
            }
            dbfsFinal = dbfsAvg(dbfs)
        } else if (i == INDEX_2500HZ) {
            for (j in LOW_2500HZ..HIGH_2500HZ) {
                dbfs += toTransfromTodbfs(toTransform, j)
            }
            dbfsFinal = dbfsAvg(dbfs)
        } else if (i == INDEX_3150HZ) {
            for (j in LOW_3150HZ..HIGH_3150HZ) {
                dbfs += toTransfromTodbfs(toTransform, j)
            }
            dbfsFinal = dbfsAvg(dbfs)
        } else if (i == INDEX_4000HZ) {
            for (j in LOW_4000HZ..HIGH_4000HZ) {
                dbfs += toTransfromTodbfs(toTransform, j)
            }
            dbfsFinal = dbfsAvg(dbfs)
        } else if (i == INDEX_5000HZ) {
            for (j in LOW_5000HZ..HIGH_5000HZ) {
                dbfs += toTransfromTodbfs(toTransform, j)
            }
            dbfsFinal = dbfsAvg(dbfs)
        } else if (i == INDEX_6300HZ) {
            for (j in LOW_6300HZ..HIGH_6300HZ) {
                dbfs += toTransfromTodbfs(toTransform, j)
            }
            dbfsFinal = dbfsAvg(dbfs)
        } else if (i == INDEX_8000HZ) {
            for (j in LOW_8000HZ..HIGH_8000HZ) {
                dbfs += toTransfromTodbfs(toTransform, j)
            }
            dbfsFinal = dbfsAvg(dbfs)
        } else if (i == INDEX_10000HZ) {
            for (j in LOW_10000HZ..HIGH_10000HZ) {
                dbfs += toTransfromTodbfs(toTransform, j)
            }
            dbfsFinal = dbfsAvg(dbfs)
        } else if (i == INDEX_12500HZ) {
            for (j in LOW_12500HZ..HIGH_12500HZ) {
                dbfs += toTransfromTodbfs(toTransform, j)
            }
            dbfsFinal = dbfsAvg(dbfs)
        } else if (i == INDEX_16000HZ) {
            for (j in LOW_16000HZ..HIGH_16000HZ) {
                dbfs += toTransfromTodbfs(toTransform, j)
            }
            dbfsFinal = dbfsAvg(dbfs)
        } else if (i == INDEX_20000HZ) {
            for (j in LOW_20000HZ..HIGH_20000HZ) {
                dbfs += toTransfromTodbfs(toTransform, j)
            }
            dbfsFinal = dbfsAvg(dbfs)
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


