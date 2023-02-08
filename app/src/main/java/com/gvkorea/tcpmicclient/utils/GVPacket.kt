package com.gvkorea.tcpmicclient.utils

import java.io.DataOutputStream
import java.io.IOException
import java.io.OutputStream
import java.net.Socket
import java.nio.ByteBuffer

class GVPacket {

    private val CHECKINTERVAL = 10L
    private lateinit var tx_buff: ByteArray
    private lateinit var outputStream: OutputStream
    private var dataOutputStream: DataOutputStream? = null

    fun sendPacketAudio(socket: Socket?, channel: Int, spldB: FloatArray, audio: FloatArray) {
        if (socket != null) {
            try {
                tx_buff = packetAudio(channel, spldB, audio)
                outputStream = socket.getOutputStream()
                dataOutputStream = DataOutputStream(outputStream)

            } catch (e: Exception) {
                e.printStackTrace()
            }

            Thread {

                try {
                    dataOutputStream?.write(tx_buff, 0, tx_buff.size)
                    dataOutputStream?.flush()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }.start()

            try {
                Thread.sleep(CHECKINTERVAL)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

            Thread.currentThread()
            Thread.interrupted()
        } else {
        }
    }

    fun packetAudio(para2: Int, spl: FloatArray, data: FloatArray): ByteArray {
        val commandID = 'S'
        val para1 = 'A'
        val spldB = floatToByte(spl)
        val audioByteArray = floatToByte(data)

        val tx_buff = ByteArray(133)

        tx_buff[0] = (tx_buff.size - 1).toByte()
        tx_buff[1] = commandID.code.toByte()
        tx_buff[2] = para1.code.toByte()
        tx_buff[3] = para2.toByte()

        for (i in 4..131) {
            if(i < 8){
                tx_buff[i] = spldB[i-4]
            }else{
                tx_buff[i] = audioByteArray[i-8]
            }
        }
        tx_buff[132] = (tx_buff.sum() - tx_buff[0]).toByte()

        return tx_buff
    }

    private fun floatToByte(input: FloatArray): ByteArray {
        val ret = ByteArray(input.size * 4)
        for (i in input.indices) {
            ByteBuffer.wrap(ret, i * 4, 4).putFloat(input[i])
        }
        return ret
    }

    fun sendPacketReverb(socket: Socket?, reverb: FloatArray) {
        if (socket != null) {
            try {
                tx_buff = packetReverbResult(reverb)
                outputStream = socket.getOutputStream()
                dataOutputStream = DataOutputStream(outputStream)

            } catch (e: Exception) {
                e.printStackTrace()
            }

            Thread {

                try {
                    dataOutputStream?.write(tx_buff, 0, tx_buff.size)
                    dataOutputStream?.flush()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }.start()

            try {
                Thread.sleep(CHECKINTERVAL)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

            Thread.currentThread()
            Thread.interrupted()
        } else {
        }
    }

    fun packetReverbResult(data: FloatArray): ByteArray {
        val commandID = 'S'
        val para1 = 'J'
        val reverbResultArray = floatToByte(data)

        val tx_buff = ByteArray(28)

        tx_buff[0] = (tx_buff.size - 1).toByte()
        tx_buff[1] = commandID.code.toByte()
        tx_buff[2] = para1.code.toByte()

        for (i in 3..26) {
            tx_buff[i] = reverbResultArray[i-3]
        }
        tx_buff[27] = (tx_buff.sum() - tx_buff[0]).toByte()

        return tx_buff
    }


    fun sendPacketTest(socket: Socket?, channel: Int) {
        if (socket != null) {
            try {
                tx_buff = packetTest(channel)
                outputStream = socket.getOutputStream()
                dataOutputStream = DataOutputStream(outputStream)

            } catch (e: Exception) {
                e.printStackTrace()
            }

            Thread {

                try {
                    dataOutputStream?.write(tx_buff, 0, tx_buff.size)
                    dataOutputStream?.flush()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }.start()

            try {
                Thread.sleep(CHECKINTERVAL)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

            Thread.currentThread()
            Thread.interrupted()
        } else {
        }
    }

    private fun packetTest(channel: Int): ByteArray {

        val commandID = 'T'
        val para1 = channel

        val mCmd = IntArray(4)
        val tx_buff = ByteArray(mCmd.size)


        mCmd[0] = (mCmd.size - 1)
        mCmd[1] = commandID.code
        mCmd[2] = para1
        mCmd[3] = mCmd.sum() - mCmd[0]

        for (i in mCmd.indices) {
            tx_buff[i] = mCmd[i].toByte()
        }

        return tx_buff
    }

}