package com.gvkorea.tcpmicclient

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.AudioFormat
import android.media.MediaRecorder
import android.os.*
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.WindowManager
import androidx.annotation.RequiresApi
import com.gvkorea.tcpmicclient.audio.RecordAudioTune
import com.gvkorea.tcpmicclient.audio.RecordAudioTune.Companion.rmsValues
import com.gvkorea.tcpmicclient.chart.ChartLayoutBarChart
import com.gvkorea.tcpmicclient.chart.ChartLayoutLineChart
import com.gvkorea.tcpmicclient.databinding.ActivityMainBinding
import com.gvkorea.tcpmicclient.listener.ButtonListener
import com.gvkorea.tcpmicclient.listener.SelectedMicIdListener
import com.gvkorea.tcpmicclient.presenter.MainPresenter
import com.gvkorea.tcpmicclient.utils.*
import com.scichart.charting.model.dataSeries.XyDataSeries
import com.scichart.charting.visuals.SciChartSurface
import com.scichart.extensions.builders.SciChartBuilder
import omrecorder.*
import java.io.File
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.SocketException
import java.net.SocketTimeoutException
import java.util.*

class MainActivity : AppCompatActivity() {

    var isMeasureDeay = false
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
    lateinit var recorder: Recorder




    val mHandler = object : Handler(Looper.myLooper()!!) {
        @SuppressLint("NewApi")
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MSGProtocol.MSG_CONN.value -> {
                    presenter.updateConnect(msg)

                }
                MSGProtocol.MSG_SEND.value -> {
                    presenter.sendPacket(msg)
//                    updateBetteryCapacity()
                }
                MSGProtocol.MSG_QUIT.value -> {
//                    presenter.sendPacketReset()
                }
                MSGProtocol.MSG_REVERB_REQUEST.value -> {
                    presenter.reverbMeasureStart()
                }
                MSGProtocol.MSG_MIC_START.value -> {
                    presenter.micControl()
                }
                MSGProtocol.MSG_MIC_VELOCITY.value -> {
                    presenter.changeVelocity()
                }
                MSGProtocol.MSG_COUNT.value -> {
                    presenter.updateTextView(msg)
                }

            }
        }
    }

    private fun updateBetteryCapacity() {
        val bm = applicationContext.getSystemService(BATTERY_SERVICE) as BatteryManager
        val batteryLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        binding.tvBettery.text = "Battery\n$batteryLevel%"
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root

        setContentView(view)
        checkPermission()

        initialSetting()
        setupNoiseRecorder()

        pref = applicationContext.getSharedPreferences(PREF_SETUP_KEY, Context.MODE_PRIVATE)
        prefSetting = PrefSetting(pref)
        prefSetting.loadCalib()
        presenter = MainPresenter(this, mHandler)
        initListener()
        toastMsg = ToastMsg(this)
        presenter.initializeMicNo()
        initChartLayout()
        selectedMicName = prefSetting.loadCalibMic()
        presenter.setMicName()
        binding.spMicNo.setSelection(prefSetting.loadDeviceId())

        startServer()
    }




    @RequiresApi(Build.VERSION_CODES.Q)
    private fun mic(): PullableSource {
        return PullableSource.Default(
            AudioRecordConfig.Default(
                MediaRecorder.AudioSource.VOICE_PERFORMANCE, AudioFormat.ENCODING_PCM_16BIT,
                AudioFormat.CHANNEL_IN_MONO, 44100
            )
        )
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
        binding.spMicNo.onItemSelectedListener = SelectedMicIdListener(this)
        binding.btnVelocity.setOnClickListener(ButtonListener(presenter))
        binding.btnDelay.setOnClickListener(ButtonListener(presenter))

    }


    private fun startServer() {
        val port = 60001

        try {
            serverThread = ServerThread(port)
            serverThread.start()
        } catch (e: IOException) {
        }
    }

    private fun initialSetting() {
        val folder = File(
            this.getExternalFilesDir(null)?.absolutePath.toString() + "/" + resources.getString(
                R.string.app_name)
        )
        if (!folder.exists()) {
            folder.mkdir()
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun setupNoiseRecorder() {
        recorder = OmRecorder.wav(
            PullTransport.Noise(
                mic(),
                WriteAction.Default(),
                { }, 200
            ), file()
        )
    }

    private fun file(): File {
        return File(
            this.getExternalFilesDir(null)?.absolutePath.toString() + "/" + resources.getString(
               R.string.app_name
            ), "rt.wav"
        )
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
                    var receivedData = ""
                    if (dp.data != null) {
                        for (i in dp.data.indices) {
                            receivedData += String.format("0x%02x ", dp.data[i])
                        }
                        if(receivedData.contains("0x07 0x53 0x42")){
                            val ip = dp.address.toString().replace("/", "")
                            presenter.sendMessage(ip)
                        }
                        if(receivedData.contains("0x03 0x53 0x4a")){
                            val m = Message()
                            m.what = MSGProtocol.MSG_REVERB_REQUEST.value
                            mHandler.sendMessage(m)
                        }
                        if(receivedData.contains("0x03 0x53 0x4d")){
                            val m = Message()
                            m.what = MSGProtocol.MSG_MIC_START.value
                            mHandler.sendMessage(m)
                        }

                        if(receivedData.contains("0x03 0x53 0x56")){
                            val m = Message()
                            m.what = MSGProtocol.MSG_MIC_VELOCITY.value
                            mHandler.sendMessage(m)
                        }

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
                recordAudioTune =
                    RecordAudioTune(this, mHandler)
                recordAudioTune.isTest = true
                mHandler.postDelayed({
                    recordAudioTune.isTest = false

                }, 10000)
                binding.btnShowCalibSelection.isEnabled = true
                recordAudioTune.execute()
                Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO)
            }
        }, 100)
    }

    fun recordTaskStop() {
        if (isStarted) {
            isStarted = false
            binding.btnMic.setImageResource(R.drawable.button_off)
            binding.btnShowCalibSelection.isEnabled = true
            recordAudioTune.cancel(true)
            recordAudioTune.isLowFreqStarted = false
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
        var velocity = 5

    }

}