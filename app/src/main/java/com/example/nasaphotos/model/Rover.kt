package com.example.nasaphotos.model

import com.google.gson.annotations.SerializedName

data class Rover(

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("launch_date")
    val launchDate: String? = null,

    @SerializedName("landing_date")
    val landingDate: String? = null,

    @SerializedName("status")
    val status: String? = null
)