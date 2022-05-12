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
        lineChart.dragDecelerationFrictionCoef = 0.9f
        lineChart.setDrawGridBackground(false)
        lineChart.description.isEnabled = false
        lineChart.setPinchZoom(false)
        lineChart.setDrawBorders(false)
        lineChart.setBackgroundColor(Color.WHITE)
        lineChart.isDragEnabled = false

        lineChart.setScaleEnabled(false)
        xAxis = lineChart.xAxis
        val freqArray = arrayOf(
            "", "", "", "", "50", "", "", "100", "", "", "200", "", "", "", "500",
            "", "", "1k", "", "", "2k", "", "", "4k", "", "", "8k", "", "", "16k", ""
        )


        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.textSize = 8.0f
        xAxis.labelCount = 31
        xAxis.valueFormatter = IndexAxisValueFormatter(freqArray)

        val leftAxis = lineChart.axisLeft
        leftAxis.removeAllLimitLines()
        leftAxis.setDrawZeroLine(true)
        leftAxis.axisMaximum = yAxisMax
        leftAxis.axisMinimum = yAxisMin
        lineChart.axisRight.isEnabled = false

    }

    fun initGraph(values: FloatArray?, label: String, color: Int) {
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
        lineDataSet.color = color
        lineDataSet.setDrawCircles(false)
        lineDataSet.lineWidth = 8f
        lineDataSet.valueTextColor = color
        lineDataSet.valueTextSize = 10.0f
        lineDataSet.setDrawValues(false)

        lineDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER


        val data = LineData(lineDataSet)

        xAxis.axisMaximum = data.xMax + 0.4f
        xAxis.axisMinimum = data.xMin - 0.4f

        lineChart.data = data
        lineChart.data.notifyDataChanged()
        lineChart.notifyDataSetChanged()
        lineChart.invalidate()

    }
}