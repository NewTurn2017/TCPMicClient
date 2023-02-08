package com.gvkorea.tcpmicclient.chart

import android.graphics.Color
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

class ChartLayoutBarChart(var mBarChart: BarChart) {

    fun initBarChartLayout(yAxisMax: Float, yAxisMin: Float) {
        with(mBarChart) {
            setDrawBarShadow(false)
            setDrawValueAboveBar(true)
            setTouchEnabled(true)
            dragDecelerationFrictionCoef = 0.9f
            setDrawGridBackground(false)
            isHighlightPerDragEnabled = true
            description.isEnabled = false
            setPinchZoom(false)
            setDrawBorders(false)
            setBackgroundColor(Color.WHITE)
            isDragEnabled = false
            setScaleEnabled(false)
        }

        // x-axis limit line

        val xAxis = mBarChart.xAxis
        with(xAxis) {
            position = XAxis.XAxisPosition.BOTTOM
            setDrawGridLines(false)
            setDrawLabels(false)
        }


        val leftAxis = mBarChart.axisLeft
        leftAxis.removeAllLimitLines() // reset all limit lines to avoid overlapping lines
        leftAxis.axisMaximum = yAxisMax
        leftAxis.axisMinimum = yAxisMin
        leftAxis.setDrawZeroLine(true)

        mBarChart.axisRight.isEnabled = false
        val l = mBarChart.legend
        l.form = Legend.LegendForm.LINE
    }

    lateinit var xAxisCompLine: XAxis
    private val fillColor = Color.argb(150, 51, 181, 229)

    fun initBarChartLayout_31(yAxisMax: Float, yAxisMin: Float) {


        with(mBarChart) {
            setDrawBarShadow(false)
            setDrawValueAboveBar(true)
            setTouchEnabled(true)
            dragDecelerationFrictionCoef = 0.9f
            setDrawGridBackground(true)
            isHighlightPerDragEnabled = true
            description.isEnabled = false
            setPinchZoom(false)
            setDrawBorders(false)
            setBackgroundColor(Color.WHITE)
            isDragEnabled = false
            setScaleEnabled(false)
            setGridBackgroundColor(fillColor)
            axisRight.isEnabled = false
            legend.form = Legend.LegendForm.LINE
        }

        val freqArray = arrayOf(
            "", "", "", "", "50", "", "", "100", "", "", "200", "", "", "", "500",
            "", "", "1k", "", "", "2k", "", "", "4k", "", "", "8k", "", "", "16k", ""
        )
        xAxisCompLine = mBarChart.xAxis
        with(xAxisCompLine) {
            position = XAxis.XAxisPosition.BOTTOM
            setDrawGridLines(false)
            textSize = 8.0f
            labelCount = 31
            valueFormatter = IndexAxisValueFormatter(freqArray)
        }

        val leftAxis = mBarChart.axisLeft
        with(leftAxis) {
            removeAllLimitLines() // reset all limit lines to avoid overlapping lines
            axisMaximum = yAxisMax
            axisMinimum = yAxisMin
            setDrawZeroLine(true)
        }
    }


    fun initChart() {
        val barValues = ArrayList<BarEntry>()
        for (i in 0 until 31) {
            barValues.add(BarEntry(i.toFloat(), 0.toFloat()))
        }

        val barDataset = BarDataSet(barValues, "실시간 스펙트럼")
        with(barDataset) {
            setDrawIcons(false)
            setGradientColor(Color.GREEN, Color.RED)
            setDrawValues(false)
            formLineWidth = 1f
        }

        val data = BarData(barDataset)
        // set data
        mBarChart.data = data
        mBarChart.invalidate()
    }

}