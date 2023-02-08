package com.gvkorea.tcpmicclient.utils

enum class MSGProtocol(val value: Int) {
    MSG_CONN(1),
    MSG_SEND(2),
    MSG_QUIT(3),
    MSG_REVERB_REQUEST(4),
    MSG_REVERB_RESULT(5),
    MSG_MIC_START(6),
    MSG_MIC_VELOCITY(7),
    MSG_COUNT(8)
}