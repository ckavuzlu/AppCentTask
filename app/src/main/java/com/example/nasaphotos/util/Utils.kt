package com.example.nasaphotos.util

import com.example.nasaphotos.util.Constant.ALL

object Utils {
    val roverList = listOf(Constant.CURIOSITY, Constant.OPPORTUNITY, Constant.SPIRIT)

    fun getRoverCameras(roverName: String): List<String> {
        return when (roverName) {
            Constant.CURIOSITY -> {
                listOf(ALL, "FHAZ", "MAST", "CHEMCAM", "MAHLI", "MARDI", "NAVCAM")
            }
            Constant.OPPORTUNITY -> {
                listOf(ALL, "FHAZ", "RHAZ", "MINITES", "PANCAM", "NAVCAM")
            }
            Constant.SPIRIT -> {
                listOf(ALL, "FHAZ", "RHAZ", "MINITES", "PANCAM", "NAVCAM")
            }
            else -> {
                emptyList()
            }
        }
    }
}