package com.gvkorea.tcpmicclient.listener

import android.view.View
import android.widget.AdapterView
import com.gvkorea.tcpmicclient.MainActivity

class SelectedMicIdListener(val view: MainActivity): AdapterView.OnItemSelectedListener {
    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        view.prefSetting.saveDeviceId(p2)
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
    }
}