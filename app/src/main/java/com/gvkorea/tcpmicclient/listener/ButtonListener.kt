package com.gvkorea.tcpmicclient.listener

import android.view.View
import com.gvkorea.tcpmicclient.presenter.MainPresenter
import com.gvkorea.tcpmicclient.R

class ButtonListener(val presenter: MainPresenter): View.OnClickListener {
    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.btnMic -> presenter.micControl()
            R.id.btnCalib -> presenter.calibMode()
            R.id.btnAutoCalib -> presenter.autoCalib()
            R.id.btnResetCalib -> presenter.resetCalib()
            R.id.btnShowCalibSelection -> presenter.showCalibFile()
            R.id.btnReverb -> presenter.reverbMeasureStart()
        }
    }
}