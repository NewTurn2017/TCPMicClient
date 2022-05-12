package com.gvkorea.tcpmicclient.utils

import android.content.SharedPreferences
import com.gvkorea.tcpmicclient.MainActivity.Companion.CALIBRATION

class PrefSetting(val pref: SharedPreferences) {
    private var editor = pref.edit()

    fun loadCalib() {
        CALIBRATION = pref.getFloat("calibration", 0f)
    }

    fun saveCalib() {
        editor.putFloat("calibration", CALIBRATION)
        editor.apply()
    }

    fun saveIsCalib(isCalib: Boolean) {
        editor.putBoolean("isCalib", isCalib)
        editor.apply()
    }
}