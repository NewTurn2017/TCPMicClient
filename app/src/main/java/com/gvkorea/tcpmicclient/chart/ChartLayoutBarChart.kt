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

    fun initBarChartLayout(yAxisMax: Float, yAxisMin: Float){
        mBarChart.setDrawBarShadow(false)
        mBarChart.setDrawValueAboveBar(true)
        mBarChart.setTouchEnabled(true)
        mBarChart.dragDecelerationFrictionCoef = 0.9f
        mBarChart.setDrawGridBackground(false)
        mBarChart.isHighlightPerDragEnabled = true
        mBarChart.description.isEnabled = false
        mBarChart.setPinchZoom(false)
        mBarChart.setDrawBorders(false)
        mBarChart.setBackgroundColor(Color.WHITE)
        mBarChart.isDragEnabled = false
        mBarChart.setScaleEnabled(false)

        // x-axis limit line

        val xAxis = mBarChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.setDrawLabels(false)

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

    fun initBarChartLayout_31(yAxisMax: Float, yAxisMin: Float){
        mBarChart.setDrawBarShadow(false)
        mBarChart.setDrawValueAboveBar(true)
        mBarChart.setTouchEnabled(true)
        mBarChart.dragDecelerationFrictionCoef = 0.9f
        mBarChart.setDrawGridBackground(true)
        mBarChart.isHighlightPerDragEnabled = true
        mBarChart.description.isEnabled = false
        mBarChart.setPinchZoom(false)
        mBarChart.setDrawBorders(false)
        mBarChart.setBackgroundColor(Color.WHITE)
        mBarChart.isDragEnabled = false
        mBarChart.setScaleEnabled(false)
        mBarChart.setGridBackgroundColor(fillColor)

        val freqArray = arrayOf("","", "", "","50", "", "", "100", "", "", "200", "", "", "", "500",
            "", "", "1k", "", "", "2k", "", "", "4k", "", "", "8k", "", "", "16k", "")

        xAxisCompLine = mBarChart.xAxis
        xAxisCompLine.position = XAxis.XAxisPosition.BOTTOM
        xAxisCompLine.setDrawGridLines(false)
        xAxisCompLine.textSize = 8.0f
        xAxisCompLine.labelCount = 31
        xAxisCompLine.valueFormatter = IndexAxisValueFormatter(freqArray)


        val leftAxis = mBarChart.axisLeft
        leftAxis.removeAllLimitLines() // reset all limit lines to avoid overlapping lines
        leftAxis.axisMaximum = yAxisMax
        leftAxis.axisMinimum = yAxisMin
        leftAxis.setDrawZeroLine(true)

        mBarChart.axisRight.isEnabled = false
        val l = mBarChart.legend
        l.form = Legend.LegendForm.LINE
    }



    fun initChart(){
        val barValues = ArrayList<BarEntry>()
        for ( i in 0 until 31){
            barValues.add(BarEntry(i.toFloat(), 0.toFloat()))
        }

        val barDataset = BarDataSet(barValues, "실시간 스펙트럼")
        barDataset.setDrawIcons(false)
        barDataset.setGradientColor(Color.GREEN, Color.RED)
        barDataset.setDrawValues(false)
        mBarChart.description.isEnabled = false
        barDataset.formLineWidth = 1f
        val data = BarData(barDataset)
        // set data
        mBarChart.data = data
        mBarChart.invalidate()
    }

}