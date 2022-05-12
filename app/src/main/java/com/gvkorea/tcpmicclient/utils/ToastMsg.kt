package com.gvkorea.tcpmicclient.utils

import android.view.Gravity
import android.widget.TextView
import android.widget.Toast
import com.gvkorea.tcpmicclient.MainActivity
import com.gvkorea.tcpmicclient.R

class ToastMsg(val view: MainActivity) {
    fun msg(msg: String){
        val inflater = view.layoutInflater
        val toastDesign = inflater.inflate(R.layout.toast_design_lay, view.findViewById(R.id.toast_design_root))
        toastDesign.findViewById<TextView>(R.id.tv_toast_design).text = msg
        val toast = Toast(view)
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = toastDesign
        toast.show()
    }
}