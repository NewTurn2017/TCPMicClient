package com.gvkorea.tcpmicclient.presenter

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.media.MediaPlayer
import android.os.Handler
import android.os.Message
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import com.chaquo.python.PyObject
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.gvkorea.tcpmicclient.MainActivity
import com.gvkorea.tcpmicclient.MainActivity.Companion.CALIBRATION
import com.gvkorea.tcpmicclient.MainActivity.Companion.isCalib
import com.gvkorea.tcpmicclient.MainActivity.Companion.isStarted
import com.gvkorea.tcpmicclient.MainActivity.Companion.selectedMicName
import com.gvkorea.tcpmicclient.MainActivity.Companion.toastMsg
import com.gvkorea.tcpmicclient.R
import com.gvkorea.tcpmicclient.audio.GVAudioRecord
import com.gvkorea.tcpmicclient.audio.RecordAudioTune.Companion.spldB
import com.gvkorea.tcpmicclient.utils.*
import java.lang.Math.round
import java.net.Socket
import kotlin.math.roundToInt

class MainPresenter(val view: MainActivity, val handler: Handler) {
    private var socket: Socket? = null
    private val packet = GVPacket()

    var audioRecord: GVAudioRecord
    var rt60Arrays = ArrayList<FloatArray>(5)
    var nameList = ArrayList<String>()
    var noiseVolumeList = ArrayList<String>()
    val noiseVolumeArray = arrayOf("0", "-2", "-4", "-6", "-8", "-10", "-12")
    val FREQARRAYS = arrayOf("125 hz", "250 hz", "500 hz", "1000 hz", "2000 hz", "4000 hz")
    var reverbCount = 0

    init {
        val path = GVPath(view)
        path.checkDownloadFolder()
        audioRecord = GVAudioRecord(path, this)
        for (i in noiseVolumeArray) {
            noiseVolumeList.add(i)
        }
    }

    fun sendMessage(msg: String) {
        val m = Message()
        m.what = MSGProtocol.MSG_CONN.value
        m.obj = msg
        handler.sendMessage(m)
    }

    private fun disConnectStatus() {

        view.binding.tvConnectStatus.text = "접속안됨."
    }

    private fun connectStatus() {
        view.binding.tvConnectStatus.text = "접속됨."
    }

    fun sendPacket(msg: Message) {
        val strArrays = (msg.obj as String).split('/')
        val micNo = strArrays[0].toInt()
        val spldB = strArrays[1].toFloat()
        val spldBArray = FloatArray(1)
        spldBArray[0] = spldB
        val audioArrays = strArrays[2].split(',')
        val audioData = strToFloatArray(audioArrays)

        packet.sendPacketAudio(socket, micNo, spldBArray, audioData)

    }

    private fun strToFloatArray(tempArray: List<String>): FloatArray {


        val rmsValues = FloatArray(31)
        for (i in 0..30) {
            when (i) {

                0 -> {
                    rmsValues[i] = tempArray[i].replace("[", "").toFloat()
                }
                30 -> {
                    rmsValues[i] = tempArray[i].replace("]", "").toFloat()
                }
                else -> {
                    rmsValues[i] = tempArray[i].toFloat()
                }
            }
        }
        return rmsValues
    }

    fun micControl() {
        if (!isStarted) {
            view.recordTaskStart()
        } else {
            view.recordTaskStop()
        }

    }

    fun calibMode() {
        if (!isCalib) {
            isCalib = true
            view.binding.btnCalib.text = "일반 모드"
            showCalibMenu()
        } else {
            isCalib = false
            view.binding.btnCalib.text = "캘리브레이션 모드"
            hideCalibMenu()
        }
    }

    private fun showCalibMenu() {
        view.binding.layCalib.visibility = View.VISIBLE
        view.binding.btnAutoCalib.visibility = View.VISIBLE
        view.binding.btnShowCalibSelection.visibility = View.VISIBLE
        view.binding.btnResetCalib.visibility = View.VISIBLE
        view.binding.barChartCalib.visibility = View.VISIBLE
    }

    private fun hideCalibMenu() {
        view.binding.layCalib.visibility = View.GONE
        view.binding.btnAutoCalib.visibility = View.GONE
        view.binding.btnResetCalib.visibility = View.GONE
        view.binding.barChartCalib.visibility = View.GONE
        view.binding.btnShowCalibSelection.visibility = View.GONE

    }

    fun autoCalib() {
        view.prefSetting.loadCalib()
        CALIBRATION += (94 - spldB).toFloat()
        view.prefSetting.saveIsCalib(true)
        view.prefSetting.saveCalib()
        msg("설정이 완료되었습니다.")
    }

    fun resetCalib() {
        AlertDialog.Builder(view)
            .setTitle("Calibration 초기화")
            .setMessage("Calibration을 초기화 하면 다시 설정해야 합니다.")
            .setPositiveButton(
                android.R.string.yes
            ) { dialog, _ ->
                dialog.dismiss()
                CALIBRATION = 0f
                view.prefSetting.saveIsCalib(false)
                msg("CALIBRATION 값이 초기화 되었습니다.")
            }
            .setNegativeButton(
                android.R.string.no
            ) { dialog, _ ->
                dialog.dismiss()
            }
            .setIcon(android.R.drawable.ic_dialog_info)
            .show()
    }

    private fun msg(msg: String) {
        Toast.makeText(view.applicationContext, msg, Toast.LENGTH_SHORT).show()
    }

    fun updateConnect(msg: Message) {
        val ip = msg.obj as String
        val micNo = view.binding.spMicNo.selectedItem.toString().toInt()
        view.connect = ConnectThreadMain(socket, ip)
        view.connect.start()

        handler.postDelayed({
            socket = view.connect.socket
            if (socket?.isConnected!!) {

                view.binding.textView.text = ip
                connectStatus()
                packet.sendPacketTest(socket, micNo)
            } else {
                view.binding.textView.text = ""
                disConnectStatus()
            }
        }, 200)
    }

    fun initializeMicNo() {
        val micList = ArrayList<String>()
        for (i in 1..4) {
            micList.add(i.toString())
        }
        registerAdapter(view.applicationContext, micList, view.binding.spMicNo)
    }

    private fun registerAdapter(
        context: Context,
        list: ArrayList<String>,
        spinner: Spinner
    ) {
        val adapter = ArrayAdapter(context, R.layout.spinner_dropdown, list)
        adapter.setDropDownViewResource(R.layout.spinner_dropdown)
        adapter.notifyDataSetChanged()
        spinner.adapter = adapter
    }

    fun showCalibFile() {
        val calibFileList = view.resources.getStringArray(R.array.calib_file)
        val builder = AlertDialog.Builder(view)
        builder.setTitle("현재 MIC의 No.를 확인하여 아래 목록에서 선택하세요.")
        builder.setItems(
            calibFileList
        ) { dialog, which ->
            dialog.dismiss()
            Toast.makeText(view, "${calibFileList[which]} 선택됨.", Toast.LENGTH_SHORT).show()
            setCalibFile(calibFileList[which])
        }
        val alertDialog = builder.create()
        alertDialog.show()
    }

    private fun setCalibFile(fileName: String) {
        if (view.recordAudioTune != null) {
            view.recordAudioTune.calibData = CSVRead(view).readCalibCsv(view.assets!!, fileName)
            selectedMicName = fileName
            setMicName()
        } else {
            Toast.makeText(view, "전원 버튼을 누른 후 실행하세요.", Toast.LENGTH_SHORT).show()
        }

    }

    fun setMicName() {
        val fileName = "Caib. File: $selectedMicName"
        view.binding.tvCalibMicNo.text = fileName
    }

    fun reverbMeasureStart() {
        startRecord()
    }

    private fun startRecord() {
        toastMsg.msg("측정을 시작합니다.")
        audioRecord.startRecord()
        handler.postDelayed({
            stopRecord()
        }, 4000)
    }

    private fun stopRecord() {
        toastMsg.msg("잠시만 기다려 주세요...")
        audioRecord.stopRecord()
    }

    fun calculateRT60() {
        toastMsg.msg("잔향 측정을 준비하고 있습니다. 잠시만 기다려 주세요.")
        reverbCount++
        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(view.applicationContext))
        }
        val py = Python.getInstance()
        val pyf = py.getModule("myscript")
        val wavPath =
            view.getExternalFilesDir(null)?.absolutePath + "/" + view.resources.getString(R.string.app_name) + "/rt.wav"
        val obj = pyf.callAttr("rt60", wavPath)
        val arr = pyObjectToArray(obj)
        var testResult = ""
        toastMsg.msg("${arr.toList()}")


    }


    fun pyObjectToArray(obj: PyObject): FloatArray {
        val arr = FloatArray(obj.asList().size)
        for (i in obj.asList().indices) {
            arr[i] = obj.asList()[i].toFloat()
        }
        return arr
    }


}