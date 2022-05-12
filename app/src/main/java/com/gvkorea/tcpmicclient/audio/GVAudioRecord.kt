package com.gvkorea.tcpmicclient.audio

import android.annotation.SuppressLint
import android.media.AudioRecord
import android.media.AudioTrack
import android.media.MediaRecorder
import com.gvkorea.tcpmicclient.presenter.MainPresenter
import com.gvkorea.tcpmicclient.utils.GVPath
import java.io.*
import java.nio.ByteBuffer
import java.nio.ByteOrder

class GVAudioRecord(val mPath: GVPath, val presenter: MainPresenter) {

    private val mAudioSource = MediaRecorder.AudioSource.VOICE_RECOGNITION
    private val mSampleRate = 44100
    private val mChannalCount: Int = android.media.AudioFormat.CHANNEL_IN_STEREO
    private val mAudioFormat: Int = android.media.AudioFormat.ENCODING_PCM_16BIT
    private val mBufferSize =
        AudioTrack.getMinBufferSize(mSampleRate, mChannalCount, mAudioFormat)

    private var mAudioRecord: AudioRecord? = null
    private var isRecording = false

    @SuppressLint("MissingPermission")
    fun startRecord() {
        isRecording = true
        if (mAudioRecord == null) {
            mAudioRecord =
                AudioRecord(mAudioSource, mSampleRate, mChannalCount, mAudioFormat, mBufferSize)
        }
        mAudioRecord?.startRecording()
        Thread({
            val readData = ByteArray(mBufferSize)
            var fos: FileOutputStream? = null
            try {
                fos = FileOutputStream(mPath.getNewFilePath())
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }

            while (isRecording) {
                mAudioRecord!!.read(
                    readData,
                    0,
                    mBufferSize
                ) //  AudioRecord의 read 함수를 통해 pcm data 를 읽어옴
                try {
                    fos?.write(readData, 0, mBufferSize) //  읽어온 readData 를 파일에 write 함
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            mAudioRecord!!.stop()
            try {
                fos?.close()
                rawToWave(File(mPath.getNewFilePath()!!), File(mPath.getWavFilePath()!!))
                presenter.handler.post {
                    presenter.calculateRT60()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }, "RECODE-WORKER").start()
    }


    fun stopRecord() {
        isRecording = false
    }




    @Throws(IOException::class)
    private fun rawToWave(rawFile: File, waveFile: File) {
        val rawData = ByteArray(rawFile.length().toInt())
        var input: DataInputStream? = null
        try {
            input = DataInputStream(FileInputStream(rawFile))
            input.read(rawData)
        } finally {
            if (input != null) {
                input.close()
            }
        }
        var output: DataOutputStream? = null
        try {
            output = DataOutputStream(FileOutputStream(waveFile))
            // WAVE header
            // see http://ccrma.stanford.edu/courses/422/projects/WaveFormat/
            writeString(output, "RIFF") // chunk id
            writeInt(output, 36 + rawData.size) // chunk size
            writeString(output, "WAVE") // format
            writeString(output, "fmt ") // subchunk 1 id
            writeInt(output, 16) // subchunk 1 size
            writeShort(output, 1.toShort()) // audio format (1 = PCM)
            writeShort(output, 1.toShort()) // number of channels
            writeInt(output, 44100) // sample rate
            writeInt(output, 44100 * 2) // byte rate
            writeShort(output, 2.toShort()) // block align
            writeShort(output, 16.toShort()) // bits per sample
            writeString(output, "data") // subchunk 2 id
            writeInt(output, rawData.size) // subchunk 2 size
            // Audio data (conversion big endian -> little endian)
            val shorts = ShortArray(rawData.size / 2)
            ByteBuffer.wrap(rawData).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shorts)
            val bytes: ByteBuffer = ByteBuffer.allocate(shorts.size * 2)
            for (s in shorts) {
                bytes.putShort(s)
            }
            output.write(fullyReadFileToBytes(rawFile))
        } finally {
            output?.close()
        }
    }

    @Throws(IOException::class)
    fun fullyReadFileToBytes(f: File): ByteArray? {
        val size = f.length().toInt()
        val bytes = ByteArray(size)
        val tmpBuff = ByteArray(size)
        val fis = FileInputStream(f)
        try {
            var read: Int = fis.read(bytes, 0, size)
            if (read < size) {
                var remain = size - read
                while (remain > 0) {
                    read = fis.read(tmpBuff, 0, remain)
                    System.arraycopy(tmpBuff, 0, bytes, size - remain, read)
                    remain -= read
                }
            }
        } catch (e: IOException) {
            throw e
        } finally {
            fis.close()
        }
        return bytes
    }

    @Throws(IOException::class)
    private fun writeInt(output: DataOutputStream, value: Int) {
        output.write(value shr 0)
        output.write(value shr 8)
        output.write(value shr 16)
        output.write(value shr 24)
    }

    @Throws(IOException::class)
    private fun writeShort(output: DataOutputStream, value: Short) {
        output.write(value.toInt() shr 0)
        output.write(value.toInt() shr 8)
    }

    @Throws(IOException::class)
    private fun writeString(output: DataOutputStream, value: String) {
        for (element in value) {
            output.write(element.code)
        }
    }
}