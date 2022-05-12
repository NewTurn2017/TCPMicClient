package com.gvkorea.tcpmicclient

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.WindowManager
import android.widget.Button
import com.gvkorea.tcpmicclient.audio.RecordAudioTune
import com.gvkorea.tcpmicclient.chart.ChartLayoutBarChart
import com.gvkorea.tcpmicclient.chart.ChartLayoutLineChart
import com.gvkorea.tcpmicclient.databinding.ActivityMainBinding
import com.gvkorea.tcpmicclient.listener.ButtonListener
import com.gvkorea.tcpmicclient.presenter.MainPresenter
import com.gvkorea.tcpmicclient.utils.ConnectThreadMain
import com.gvkorea.tcpmicclient.utils.MSGProtocol
import com.gvkorea.tcpmicclient.utils.PrefSetting
import com.gvkorea.tcpmicclient.utils.ToastMsg
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.SocketException
import java.net.SocketTimeoutException
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var serverThread: ServerThread

    lateinit var binding: ActivityMainBinding
    lateinit var presenter: MainPresenter
    lateinit var connect: ConnectThreadMain

    private var permission_list = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.RECORD_AUDIO
    )
    lateinit var recordAudioTune: RecordAudioTune
    lateinit var pref: SharedPreferences
    lateinit var prefSetting: PrefSetting
    val PREF_SETUP_KEY = "Settings"

    val mHandler = object: Handler(Looper.myLooper()!!) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MSGProtocol.MSG_CONN.value -> {
                    presenter.updateConnect(msg)

                }
                MSGProtocol.MSG_SEND.value -> {
                    presenter.sendPacket(msg)
                }
                MSGProtocol.MSG_QUIT.value -> {
//                    presenter.sendPacketReset()
                }
            }
        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        checkPermission()

        pref = applicationContext.getSharedPreferences(PREF_SETUP_KEY, Context.MODE_PRIVATE)
        prefSetting = PrefSetting(pref)
        prefSetting.loadCalib()
        presenter = MainPresenter(this, mHandler)
        initListener()
        toastMsg = ToastMsg(this)
        presenter.initializeMicNo()
        initChartLayout()
        presenter.setMicName()

        startServer()
    }

    private fun initChartLayout() {
        val chartLayout = ChartLayoutBarChart(binding.barChartCalib)
        chartLayout.initBarChartLayout(120f, 20f)

        val chartLayoutRms = ChartLayoutBarChart(binding.chartRms)
        chartLayoutRms.initBarChartLayout_31(120f, 20f)
        chartLayoutRms.initChart()

        val lineChart = ChartLayoutLineChart(binding.chartLine)
        lineChart.initLineChartLayout(100f, 20f)
        lineChart.initGraph(null, "실시간 스펙트럼", Color.GREEN)

    }

    private fun initListener() {
        binding.btnMic.setOnClickListener(ButtonListener(presenter))
        binding.btnCalib.setOnClickListener(ButtonListener(presenter))
        binding.btnAutoCalib.setOnClickListener(ButtonListener(presenter))
        binding.btnResetCalib.setOnClickListener(ButtonListener(presenter))
        binding.btnShowCalibSelection.setOnClickListener(ButtonListener(presenter))
        binding.btnReverb.setOnClickListener(ButtonListener(presenter))
    }


    private fun startServer() {
        val port = 60001

        try {
            serverThread = ServerThread(port)
            serverThread.start()
        } catch (e: IOException){}
    }


    inner class ServerThread @Throws(IOException::class)
    constructor(port: Int) : Thread() {
        private var loop: Boolean = false
        private val ds: DatagramSocket = DatagramSocket(port)

        init {
            ds.soTimeout = 3000
        }

        override fun run() {
            val buffer = ByteArray(8)
            val dp = DatagramPacket(buffer, buffer.size)
            loop = true

            while (loop) {
                try {
                    ds.receive(dp)
                    if (dp.data != null) {
                        val ip = dp.address.toString().replace("/", "")
                        presenter.sendMessage(ip)
                    }

                    Arrays.fill(buffer, 0.toByte())
                    dp.length = buffer.size
                } catch (e: SocketTimeoutException) {
                } catch (e: SocketException) {
                    break
                } catch (e: Exception) {
                    System.out.printf("S: Error \n", e)
                    e.printStackTrace()
                    break
                }
            }
            if (loop) ds.close()
        }

        fun quit() {
            loop = false
            ds.close()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
//        if(connect.socket != null){
//            connect.socket?.close()
//            connect.socket = null
//        }

        recordTaskStop()

    }

    override fun onStop() {
        super.onStop()
        recordTaskStop()
    }

    fun recordTaskStart() {
        mHandler.postDelayed({
            if (!isStarted) {
                isStarted = true
                binding.barChartCalib.clear()
                binding.btnMic.setImageResource(R.drawable.button_on)
                recordAudioTune = RecordAudioTune(this, binding.chartRms, binding.barChartCalib,  mHandler)
                binding.btnShowCalibSelection.isEnabled = true
                recordAudioTune.execute()
            }
        }, 100)
    }

    fun recordTaskStop() {
        if (isStarted) {
            isStarted = false
            binding.btnMic.setImageResource(R.drawable.button_off)
            binding.btnShowCalibSelection.isEnabled = true
            recordAudioTune.cancel(true)

        }
    }


    private fun checkPermission() {

        for (permission: String in permission_list) {

            val chk = checkCallingOrSelfPermission(permission)

            if (chk == PackageManager.PERMISSION_DENIED) {
                requestPermissions(permission_list, 0)
                break
            }
        }

    }

    companion object {
        var isStarted = false

        var isCalib = false
        var CALIBRATION = 0F
        var selectedMicName = "isemic_725tr_calib_freefield_1411902.csv"
        lateinit var toastMsg: ToastMsg

    }

}