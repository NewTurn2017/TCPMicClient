package com.gvkorea.tcpmicclient.chart

import android.graphics.Color
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

class ChartLayoutLineChart(var lineChart: LineChart) {

    lateinit var xAxis: XAxis

    fun initLineChartLayout(yAxisMax: Float, yAxisMin: Float) {
        with(lineChart) {
            dragDecelerationFrictionCoef = 0.9f
            setDrawGridBackground(false)
            description.isEnabled = false
            setPinchZoom(false)
            setDrawBorders(false)
            setBackgroundColor(Color.WHITE)
            isDragEnabled = false
            setScaleEnabled(false)
        }
        xAxis = lineChart.xAxis
        val freqArray = arrayOf(
            "", "", "", "", "50", "", "", "100", "", "", "200", "", "", "", "500",
            "", "", "1k", "", "", "2k", "", "", "4k", "", "", "8k", "", "", "16k", ""
        )

        with(xAxis){
            position = XAxis.XAxisPosition.BOTTOM
            setDrawGridLines(false)
            textSize = 8.0f
            labelCount = 31
            valueFormatter = IndexAxisValueFormatter(freqArray)
        }

        val leftAxis = lineChart.axisLeft
        with(leftAxis) {
            removeAllLimitLines()
            setDrawZeroLine(true)
            axisMaximum = yAxisMax
            axisMinimum = yAxisMin
        }
        lineChart.axisRight.isEnabled = false
    }

    fun initGraph(values: FloatArray?, label: String, mColor: Int) {
        val valuesArray: ArrayList<Entry> = ArrayList()

        if (values != null) {
            for (i in 0..30) {
                valuesArray.add(Entry(i.toFloat(), values[i]))
            }
        } else {
            for (i in 0..30) {
                valuesArray.add(Entry(i.toFloat(), 0.toFloat()))
            }
        }

        val lineDataSet = LineDataSet(valuesArray, label)

        with(lineDataSet){
            color = mColor
            setDrawCircles(false)
            lineWidth = 8f
            valueTextColor = mColor
            valueTextSize = 10.0f
            setDrawValues(false)
            mode = LineDataSet.Mode.CUBIC_BEZIER
        }

        val data = LineData(lineDataSet)

        xAxis.axisMaximum = data.xMax + 0.4f
        xAxis.axisMinimum = data.xMin - 0.4f

        lineChart.data = data
        lineChart.data.notifyDataChanged()
        lineChart.notifyDataSetChanged()
        lineChart.invalidate()

    }
}