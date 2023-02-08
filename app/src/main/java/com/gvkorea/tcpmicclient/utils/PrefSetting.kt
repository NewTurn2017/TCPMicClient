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

    fun loadCalibMic(): String {
        return pref.getString("selectedMic", "isemic_725tr_calib_freefield_1411902.csv")!!
    }

    fun saveCalibMic(selectedMic: String) {
        editor.putString("selectedMic", selectedMic)
        editor.apply()
    }

    fun saveDeviceId(selectedId: Int) {
        editor.putInt("selectedId", selectedId)
        editor.apply()
    }

    fun loadDeviceId(): Int {
        return pref.getInt("selectedId", 1)
    }

}