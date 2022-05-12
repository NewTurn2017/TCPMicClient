package com.gvkorea.tcpmicclient.utils

import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket

class ConnectThreadMain(socket: Socket?, val ip: String): Thread() {

    var socket: Socket? = null
    val port = 50001

    init {
        this.socket = socket
    }

    override fun run() {
        connect()
    }

    private fun connect() {

        socket = Socket()
        val mainController = InetSocketAddress(ip, port)
        try {
            socket?.connect(mainController)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}