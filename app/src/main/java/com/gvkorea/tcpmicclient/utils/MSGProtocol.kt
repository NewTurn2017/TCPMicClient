package com.gvkorea.tcpmicclient.utils

enum class MSGProtocol(val value: Int) {
    MSG_CONN(1),
    MSG_SEND(2),
    MSG_QUIT(3)
}