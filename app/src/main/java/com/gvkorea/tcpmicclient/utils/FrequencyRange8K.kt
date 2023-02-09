package com.gvkorea.tcpmicclient.utils

import com.gvkorea.tcpmicclient.utils.Frequency8K.*

enum class FrequencyRange8K(val index: Int, val lowIndex: Int, val highIndex: Int) {
    INDEX_20HZ(0, LOW_20HZ.value, HIGH_20HZ.value),
    INDEX_25HZ(1, LOW_25HZ.value, HIGH_25HZ.value),
    INDEX_32HZ(2, LOW_32HZ.value, HIGH_32HZ.value),
    INDEX_40HZ(3, LOW_40HZ.value, HIGH_40HZ.value),
    INDEX_50HZ(4, LOW_50HZ.value, HIGH_50HZ.value),
    INDEX_63HZ(5, LOW_63HZ.value, HIGH_63HZ.value),
    INDEX_80HZ(6, LOW_80HZ.value, HIGH_80HZ.value),
    INDEX_100HZ(7, LOW_100HZ.value, HIGH_100HZ.value),
    INDEX_125HZ(8, LOW_125HZ.value, HIGH_125HZ.value),
    INDEX_160HZ(9, LOW_160HZ.value, HIGH_160HZ.value),
    INDEX_200HZ(10, LOW_200HZ.value, HIGH_200HZ.value),
    INDEX_250HZ(11, LOW_250HZ.value, HIGH_250HZ.value),
    INDEX_315HZ(12, LOW_315HZ.value, HIGH_315HZ.value),
    INDEX_400HZ(13, LOW_400HZ.value, HIGH_400HZ.value),
    INDEX_500HZ(14, LOW_500HZ.value, HIGH_500HZ.value),
    INDEX_630HZ(15, LOW_630HZ.value, HIGH_630HZ.value),
    INDEX_800HZ(16, LOW_800HZ.value, HIGH_800HZ.value),
    INDEX_1000HZ(17, LOW_1000HZ.value, HIGH_1000HZ.value),
    INDEX_1250HZ(18, LOW_1250HZ.value, HIGH_1250HZ.value),
    INDEX_1600HZ(19, LOW_1600HZ.value, HIGH_1600HZ.value),
    INDEX_2000HZ(20, LOW_2000HZ.value, HIGH_2000HZ.value),
    INDEX_2500HZ(21, LOW_2500HZ.value, HIGH_2500HZ.value),
    INDEX_3150HZ(22, LOW_3150HZ.value, HIGH_3150HZ.value),
    INDEX_4000HZ(23, LOW_4000HZ.value, HIGH_4000HZ.value),
    INDEX_5000HZ(24, LOW_5000HZ.value, HIGH_5000HZ.value),
    INDEX_6300HZ(25, LOW_6300HZ.value, HIGH_6300HZ.value),
    INDEX_8000HZ(26, LOW_8000HZ.value, HIGH_8000HZ.value),
    INDEX_10000HZ(27, LOW_10000HZ.value, HIGH_10000HZ.value),
    INDEX_12500HZ(28, LOW_12500HZ.value, HIGH_12500HZ.value),
    INDEX_16000HZ(29, LOW_16000HZ.value, HIGH_16000HZ.value),
    INDEX_20000HZ(30, LOW_20000HZ.value, HIGH_20000HZ.value)
}