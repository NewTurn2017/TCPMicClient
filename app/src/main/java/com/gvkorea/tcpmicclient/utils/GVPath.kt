package com.gvkorea.tcpmicclient.utils

import android.media.MediaScannerConnection
import com.gvkorea.tcpmicclient.MainActivity
import java.io.File
import java.io.IOException

class GVPath(val view: MainActivity) {


    private val mRecodeFilePath: String =
        view.getExternalFilesDir(null)?.absolutePath
            .toString() + "/" + view.resources.getString(
            com.gvkorea.tcpmicclient.R.string.app_name) + "/"

    fun scanFile(file: File) {
        MediaScannerConnection.scanFile(view, arrayOf(file.toString()), arrayOf(file.name), null)
    }

    fun checkDownloadFolder() {
        val path = mRecodeFilePath
        val file = File(path)
        if (!file.exists()) {
            file.mkdirs()
        }

        scanFile(file)

//        val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
//        intent.data = Uri.fromFile(file)
//        sInstance.sendBroadcast(intent)
    }

    fun getNewFilePath(): String? {
//        val path =
//            mRecodeFilePath + SimpleDateFormat("yyyy-MM-dd-HH:mm:ss").format(Date())
//                .toString() + ".pcm"

        val path = "${mRecodeFilePath}rt.pcm"
        val file = File(path)
        if (!file.exists()) {
            try {
                file.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        scanFile(file)
//        val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
//        intent.data = Uri.fromFile(file)
//        sInstance.sendBroadcast(intent)
        return path
    }

    fun getWavFilePath(): String? {
        val path = "${mRecodeFilePath}rt.wav"
        val file = File(path)
        if (!file.exists()) {
            try {
                file.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        scanFile(file)
//        val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
//        intent.data = Uri.fromFile(file)
//        sInstance.sendBroadcast(intent)
        return path
    }

    fun getRecodeFileArray(): Array<File?>? {
        val directory = File(mRecodeFilePath)
        return directory.listFiles()
    }
}